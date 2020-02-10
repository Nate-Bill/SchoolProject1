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
public class GameObject implements IRenderable {
    public int X,Y,Z;

    public Image sprite;

    public String name;

    final List<MotionComponent> pendingVectors = Collections.synchronizedList(new ArrayList<>());

    public Boolean collidable;

    public final List<IGameObjectComponent> components = Collections.synchronizedList(new ArrayList<>());

    public int rotation = 0;

    public GameObject(String name, int x, int y, int z, String spritePath, Boolean collidable, IGameObjectComponent... components) {
        synchronized (GameEngine.singleton.activeObjects) {
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
    }

    public void Destroy() {
        synchronized (GameEngine.singleton.activeObjects) {
            this.components.forEach(goc -> goc.Destroy(this));
            this.components.clear();
            GameEngine.singleton.activeObjects.remove(this);
        }
    }

    public void AddMotion(MotionComponent motionComponent) {
        synchronized (pendingVectors) {
            pendingVectors.add(motionComponent);
        }
    }

    public void AddComponent(IGameObjectComponent component) {
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
        AffineTransform at = AffineTransform.getTranslateInstance(X, Y);
        at.rotate(Math.toRadians(rotation));
        ((Graphics2D)g).drawImage(sprite, at, null);
        synchronized (components) {
            components.forEach(goc -> goc.Update(this));
            if (collidable && components.size() > 0) {
                if (X > 800) {
                    components.forEach(goc -> goc.WallCollideEvent(this, CollisionEventType.WALLRIGHT));
                } else if (X < 0) {
                    components.forEach(goc -> goc.WallCollideEvent(this, CollisionEventType.WALLLEFT));
                } else if (Y < 0) {
                    components.forEach(goc -> goc.WallCollideEvent(this, CollisionEventType.WALLTOP));
                } else if (Y > 800) {
                    components.forEach(goc -> goc.WallCollideEvent(this, CollisionEventType.WALLBOTTOM));
                }
                for (IRenderable ir : GameEngine.singleton.activeObjects) {
                    if (ir instanceof GameObject) {
                        GameObject go = (GameObject) ir;
                        if (go.collidable) {
                            if (X > go.X && X + sprite.getWidth(null) < go.X + go.sprite.getWidth(null)
                                    && Y > go.Y && Y + sprite.getHeight(null) < go.Y + go.sprite.getHeight(null)) {
                                components.forEach(goc -> goc.GameObjectCollideEvent(this, go, CollisionEventType.GAMEOBJECT));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public int GetZ() {
        return Z;
    }
}