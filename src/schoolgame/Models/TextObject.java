/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgame.Models;

import schoolgame.Engine.GameEngine;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author 14NBill
 */
public class TextObject implements IRenderable {

    public final Queue<MotionComponent> pendingVectors = new ConcurrentLinkedQueue<>();
    public final Queue<ITextObjectComponent> components = new ConcurrentLinkedQueue<>();
    public double X, Y;
    public int Z;
    public String text;
    public String name;
    public int rotation = 0;
    public boolean visible = true;

    public TextObject(String name, double x, double y, int z, String text, ITextObjectComponent... components) {
        this.name = name;
        this.X = x;
        this.Y = y;
        this.Z = z;
        this.text = text;
        this.components.addAll(Arrays.asList(components));
        GameEngine.singleton.activeObjects.add(this);
        this.components.forEach(goc -> goc.Start(this));
    }

    public void Destroy() {
        this.components.forEach(goc -> goc.Destroy(this));
        this.components.clear();
        GameEngine.singleton.activeObjects.remove(this);
    }

    public void AddMotion(MotionComponent motionComponent) {
        pendingVectors.add(motionComponent);
    }

    public void AddComponent(ITextObjectComponent component) {
        components.add(component);
        component.Start(this);
    }

    @Override
    public void DoRender(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 18));
        if (visible) g.drawString(text, (int) Math.round(X), (int) Math.round(Y));
    }

    @Override
    public int GetZ() {
        return Z;
    }

    @Override
    public void Tick() {
        ArrayList<MotionComponent> toDelete = new ArrayList<>();
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
        components.forEach(goc -> goc.Update(this));
    }
}
