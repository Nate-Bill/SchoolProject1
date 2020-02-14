/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgame.Engine;

import schoolgame.Models.IRenderable;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JFrame;
import schoolgame.Models.GameObject;
import schoolgame.Models.IGameObjectComponent;
import schoolgame.Models.IKeyCallback;

/**
 *
 * @author 14NBill
 */
public class GameEngine implements KeyListener {

    public final List<IRenderable> activeObjects = Collections.synchronizedList(new ArrayList<>());

    public static GameEngine singleton;

    public Boolean cancellationToken = false;

    private RenderEngine renderer;
    
    public int lastFrameTime;

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

    public void DoRender(Graphics g) {
        synchronized (activeObjects) {
            if (activeObjects.size() == 0) {
                return;
            }
            activeObjects.sort(Comparator.comparingInt(IRenderable::GetZ));
            if (activeObjects.get(0).GetZ() > activeObjects.get(activeObjects.size() - 1).GetZ()) {
                Collections.reverse(activeObjects);
            }
            for (IRenderable renderable : activeObjects) {
                renderable.DoRender(g);
            }
        }
    }
    /*
    public static GameObject[] GetGameObjectByComponent(String name, IGameObjectComponent component) {
        synchronized (singleton.activeObjects) {
            return (GameObject[]) singleton.activeObjects.stream().filter(go -> go instanceof GameObject).filter(go -> ((GameObject)go).components.stream().anyMatch(c -> c instanceof component.class)).toArray();
        }
    } //TODO these helper
    */

    ArrayList<IKeyCallback> keys = new ArrayList<>();
    
    public void RegisterKeyListener(IKeyCallback kl) {
        synchronized (keys) {
            keys.add(kl);
        }
    }
    
    public void UnregisterKeyListener(IKeyCallback kl) {
        synchronized (keys) {
            keys.remove(kl);
        }
    }
    
    @Override
    public void keyTyped(KeyEvent ke) {
        synchronized (keys) {
            for (IKeyCallback r : keys) r.KeyType(ke);
        }
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        synchronized (keys) {
            for (IKeyCallback r : keys) r.KeyPress(ke);
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        synchronized (keys) {
            for (IKeyCallback r : keys) r.KeyRelease(ke);
        }
    }
}
