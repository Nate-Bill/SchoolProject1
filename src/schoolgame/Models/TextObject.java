/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgame.Models;

import schoolgame.Engine.GameEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author 14NBill
 */
public class TextObject implements IRenderable {

    public double X, Y;
    
    public int Z;

    public String text;

    public String name;

    public final List<MotionComponent> pendingVectors = Collections.synchronizedList(new ArrayList<>());

    public final List<ITextObjectComponent> components = Collections.synchronizedList(new ArrayList<>());

    public int rotation = 0;

    public TextObject(String name, int x, int y, int z, String text, ITextObjectComponent... components) {
        synchronized (GameEngine.singleton.activeObjects) {
            this.name = name;
            this.X = x;
            this.Y = y;
            this.Z = z;
            this.text = text;
            this.components.addAll(Arrays.asList(components));
            GameEngine.singleton.activeObjects.add(this);
            this.components.forEach(goc -> goc.Start(this));
        }
    }

    public void Destroy() {
        synchronized (pendingVectors) {
            this.components.forEach(goc -> goc.Destroy(this));
            this.components.clear();
        }
        synchronized (GameEngine.singleton.activeObjects) {
            GameEngine.singleton.activeObjects.remove(this);
        }
    }

    public void AddMotion(MotionComponent motionComponent) {
        synchronized (pendingVectors) {
            pendingVectors.add(motionComponent);
        }
    }

    public void AddComponent(ITextObjectComponent component) {
        synchronized (component) {
            components.add(component);
            component.Start(this);
        }
    }

    @Override
    public void DoRender(Graphics g) {
        ArrayList<MotionComponent> toDelete = new ArrayList<>();
        synchronized (pendingVectors) {
            for (MotionComponent mc : pendingVectors) {
                if (mc.frames <= 0) {
                    toDelete.add(mc);
                    continue;
                }
                this.X += mc.x;
                this.Y += mc.y;
                mc.frames--;
            }
            pendingVectors.removeAll(toDelete);
        }
        synchronized (components) {
            components.forEach(goc -> goc.Update(this));
        }
        g.drawString(text, (int)Math.round(X), (int)Math.round(Y));
    }

    @Override
    public int GetZ() {
        return Z;
    }
}
