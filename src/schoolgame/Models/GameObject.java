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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author 14NBill
 */
public class GameObject implements IRenderable {

    public final Queue<MotionComponent> pendingVectors = new ConcurrentLinkedQueue<>();
    public final Queue<IGameObjectComponent> components = new ConcurrentLinkedQueue<>();
    public double X, Y;
    public int Z;
    public Image sprite;
    public String name;
    public Boolean collidable;
    public int rotation = 0;

    public GameObject(String name, int x, int y, int z, String spritePath, Boolean collidable, IGameObjectComponent... components) {
        this.name = name;
        this.X = x;
        this.Y = y;
        this.Z = z;
        sprite = new ImageIcon(this.getClass().getResource(spritePath)).getImage();
        this.collidable = collidable;
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

    public void AddComponent(IGameObjectComponent component) {
        components.add(component);
        component.Start(this);
    }

    @Override
    public void DoRender(Graphics g) {
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
        if (collidable && components.size() > 0) {
            if (X + (sprite.getWidth(null) / 2) > 800) {
                components.forEach(goc -> goc.WallCollideEvent(this, CollisionEventType.WALLRIGHT));
            } else if (X - (sprite.getWidth(null) / 2) < 0) {
                components.forEach(goc -> goc.WallCollideEvent(this, CollisionEventType.WALLLEFT));
            } else if (Y - (sprite.getHeight(null) / 2) < 0) {
                components.forEach(goc -> goc.WallCollideEvent(this, CollisionEventType.WALLTOP));
            } else if (Y + (sprite.getHeight(null) / 2) > 700) {
                components.forEach(goc -> goc.WallCollideEvent(this, CollisionEventType.WALLBOTTOM));
            }
            for (IRenderable ir : GameEngine.singleton.activeObjects) {
                if (ir instanceof GameObject) {
                    GameObject go = (GameObject) ir;
                    if (go.collidable && go != this) {
                        Rectangle myBox = new Rectangle((int) Math.round(X) - (sprite.getWidth(null) / 2), (int) Math.round(Y) - (sprite.getHeight(null) / 2), (int) Math.round(X) + (sprite.getWidth(null) / 2), (int) Math.round(Y) + (sprite.getHeight(null) / 2));
                        Rectangle goBox = new Rectangle((int) Math.round(go.X) - (go.sprite.getWidth(null) / 2), (int) Math.round(go.Y) - (go.sprite.getHeight(null) / 2), (int) Math.round(go.X) + (go.sprite.getWidth(null) / 2), (int) Math.round(go.Y) + (go.sprite.getHeight(null) / 2));
                        if (myBox.intersects(goBox)) {
                            components.forEach(goc -> goc.GameObjectCollideEvent(this, go, CollisionEventType.GAMEOBJECT));
                        }
                    }
                }
            }
        }
        AffineTransform at = AffineTransform.getTranslateInstance(X - (sprite.getWidth(null) / 2), Y - (sprite.getHeight(null) / 2));
        at.rotate(Math.toRadians(rotation), sprite.getWidth(null) / 2, sprite.getHeight(null) / 2);
        ((Graphics2D) g).drawImage(sprite, at, null);
    }

    @Override
    public int GetZ() {
        return Z;
    }
}
