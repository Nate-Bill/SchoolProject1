/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgame.Engine;

import schoolgame.Models.GameObject;
import schoolgame.Models.IGameObjectComponent;
import schoolgame.Models.IKeyCallback;
import schoolgame.Models.IRenderable;

import javax.swing.*;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * @author 14NBill
 */
public class GameEngine implements KeyListener {

    public static GameEngine singleton;
    public final Queue<IRenderable> activeObjects = new ConcurrentLinkedQueue<>();
    public Boolean cancellationToken = false;
    public int lastFrameTime;
    public int lastTickTime;
    public long frames;
    public long ticks;
    Queue<IKeyCallback> keys = new ConcurrentLinkedQueue<>();
    private RenderEngine renderer;
    private ConcurrentHashMap<Long, Boolean> tickUpdates = new ConcurrentHashMap<>();

    public GameEngine() {
        singleton = this;
        JFrame jframe = new JFrame();
        renderer = new RenderEngine();
        jframe.add(renderer);
        jframe.setTitle("Balls VS Block");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(786, 800);
        jframe.addKeyListener(this);
        jframe.setResizable(false);
        jframe.setVisible(true);
        new Thread(() -> { //Render
            Thread.currentThread().setName("RenderThread");
            while (!GameEngine.singleton.cancellationToken) {
                try {
                    final long timeMilis = System.currentTimeMillis();
                    GameEngine.singleton.renderer.repaint();
                    Thread.sleep(1);
                    lastFrameTime = (int) (System.currentTimeMillis() - timeMilis);
                    frames++;
                    if (frames > (Long.MAX_VALUE - 10000)) frames = 0;
                } catch (Exception ex) {
                    System.out.println("Exception in render!");
                    ex.printStackTrace();
                }
            }
        }).start();
        new Thread(() -> { //Tick
            Thread.currentThread().setName("TickThread");
            short sleepTime = 10;
            while (!GameEngine.singleton.cancellationToken) {
                try {
                    final long timeMilis = System.currentTimeMillis();
                    activeObjects.parallelStream().forEach(IRenderable::Tick);
                    tickUpdates.forEach((k,v) -> {
                        if (k <= ticks) {
                            tickUpdates.replace(k, true);
                        }
                    });
                    tickUpdates.forEach((k,v) -> {
                        if (k <= ticks - 10) {
                            tickUpdates.remove(k);
                        }
                    });
                    Thread.sleep(sleepTime);
                    ticks++;
                    lastTickTime = (int) (System.currentTimeMillis() - timeMilis);
                    sleepTime += 10 - lastTickTime;
                    if (sleepTime < 8) sleepTime = 8;
                    if (sleepTime > 12) sleepTime = 12;
                    if (ticks > (Long.MAX_VALUE - 10000)) ticks = 0;
                    if (ticks % 3000 == 0) System.gc();
                } catch (Exception ex) {
                    System.out.println("Exception in tick!");
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public List<GameObject> GetGameObjectsByComponent(Class<? extends IGameObjectComponent> component) {
        List<? extends IRenderable> list = activeObjects.stream()
                .filter(go -> go instanceof GameObject)
                .filter(go -> ((GameObject) go).components.stream()
                        .anyMatch(c -> c.getClass() == component))
                .collect(Collectors.toList());
        //We have to use this due to Java type checking and type saftey as ? extends IRenderable -> GameObject bypasses type saftey and is dangerous
        List<GameObject> goList = new ArrayList<>();
        list.forEach(ir -> {
            if (ir instanceof GameObject) goList.add((GameObject)ir);
        });
        return goList;
    }

    public List<GameObject> GetGameObjectsByName(String name) {
        List<? extends IRenderable> list = activeObjects.stream()
                .filter(go -> go instanceof GameObject)
                .filter(go -> ((GameObject) go).name.equals(name))
                .collect(Collectors.toList());
        //We have to use this due to Java type checking and type saftey as ? extends IRenderable -> GameObject bypasses type saftey and is dangerous
        List<GameObject> goList = new ArrayList<>();
        list.forEach(ir -> {
            if (ir instanceof GameObject) goList.add((GameObject)ir);
        });
        return goList;
    }

    public List<IGameObjectComponent> GetComponentFromGameObject(GameObject gameObject, Class<? extends IGameObjectComponent> component) {
        return gameObject.components.stream().filter(c -> c.getClass() == component).collect(Collectors.toList());
    }

    public void DoRender(Graphics g) {
        if (activeObjects.size() == 0) {
            return;
        }
        ArrayList<IRenderable> iRenderableList = new ArrayList<>(activeObjects);
        iRenderableList.sort(Comparator.comparingInt(IRenderable::GetZ));
        if (iRenderableList.get(0).GetZ() > iRenderableList.get(iRenderableList.size() - 1).GetZ()) {
            Collections.reverse(iRenderableList);
        }
        iRenderableList.forEach(renderable -> { //Doing a parallel stream cause weird glitches so keep sync
            try {
                renderable.DoRender(g);
            } catch (Exception ex) {
                System.out.println("An error occurred while rendering an IRenderable: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    public void RegisterKeyListener(IKeyCallback kl) {
        keys.add(kl);
    }

    public void UnregisterKeyListener(IKeyCallback kl) {
        keys.remove(kl);
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        for (IKeyCallback r : keys) {
            try {
                r.KeyType(ke);
            } catch (Exception ex) {
                System.out.println("Couldnt pass KeyType event to " + r.getClass().getTypeName());
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        for (IKeyCallback r : keys) {
            try {
                r.KeyPress(ke);
            } catch (Exception ex) {
                System.out.println("Couldnt pass KeyPress event to " + r.getClass().getTypeName());
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        for (IKeyCallback r : keys) {
            try {
                r.KeyRelease(ke);
            } catch (Exception ex) {
                System.out.println("Couldnt pass KeyRelease event to " + r.getClass().getTypeName());
                ex.printStackTrace();
            }
        }
    }

    public void BlockForTicks(int ticksToWait) {
        long target = this.ticks + ticksToWait;
        tickUpdates.put(target, false);
        while (!tickUpdates.get(target)) { //Do nothing until frame target reached
        }
    }
}
