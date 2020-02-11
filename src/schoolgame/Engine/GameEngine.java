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

/**
 *
 * @author 14NBill
 */
public class GameEngine implements KeyListener {

    public final List<IRenderable> activeObjects = Collections.synchronizedList(new ArrayList<>());

    public static GameEngine singleton;

    public Boolean cancellationToken = false;

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
                    GameEngine.singleton.renderer.repaint();
                    Thread.sleep(10);
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

    @Override
    public void keyTyped(KeyEvent ke) {

    }

    @Override
    public void keyPressed(KeyEvent ke) {

    }

    @Override
    public void keyReleased(KeyEvent ke) {

    }
}
