/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgame.Engine;

import schoolgame.Models.IKeyCallback;
import schoolgame.Models.IRenderable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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
    /*
    public static GameObject[] GetGameObjectByComponent(String name, IGameObjectComponent component) {
        synchronized (singleton.activeObjects) {
            return (GameObject[]) singleton.activeObjects.stream().filter(go -> go instanceof GameObject).filter(go -> ((GameObject)go).components.stream().anyMatch(c -> c instanceof component.class)).toArray();
        }
    } //TODO these helper
    */

    public void DoRender(Graphics g) {
        if (activeObjects.size() == 0) {
            return;
        }
        ArrayList<IRenderable> iRenderableList = new ArrayList<IRenderable>(activeObjects);
        iRenderableList.sort(Comparator.comparingInt(IRenderable::GetZ));
        if (iRenderableList.get(0).GetZ() > iRenderableList.get(iRenderableList.size() - 1).GetZ()) {
            Collections.reverse(iRenderableList);
        }
        for (IRenderable renderable : iRenderableList) {
            renderable.DoRender(g);
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
        for (IKeyCallback r : keys) r.KeyType(ke);
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        for (IKeyCallback r : keys) r.KeyPress(ke);
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        for (IKeyCallback r : keys) r.KeyRelease(ke);
    }
}
