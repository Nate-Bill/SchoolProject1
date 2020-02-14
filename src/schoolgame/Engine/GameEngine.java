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
    Queue<IKeyCallback> keys = new ConcurrentLinkedQueue<>();
    private RenderEngine renderer;

    public GameEngine() {
        singleton = this;
        JFrame jframe = new JFrame();
        renderer = new RenderEngine();
        jframe.add(renderer);
        jframe.setTitle("Bricks VS Block");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(800, 800);
        jframe.addKeyListener(this);
        jframe.setResizable(false);
        jframe.setVisible(true);
        new Thread(() -> {
            while (!GameEngine.singleton.cancellationToken) {
                try {
                    long timeMilis = System.currentTimeMillis();
                    GameEngine.singleton.renderer.repaint();
                    Thread.sleep(10);
                    lastFrameTime = (int) (System.currentTimeMillis() - timeMilis);
                } catch (Exception ignored) {

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
        for (IRenderable renderable : iRenderableList) {
            try {
                renderable.DoRender(g);
            } catch (Exception ex) {
                System.out.println("An error occurred while rendering an IRenderable: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
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
}
