/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgame.Models;

import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
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

    public int X, Y, Z;

    public Image sprite;

    public String name;

    public final List<IMotionComponent> pendingVectors = Collections.synchronizedList(new ArrayList<>());

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
        synchronized (pendingVectors) {
            this.components.forEach(goc -> goc.Destroy(this));
            this.components.clear();
        }
        synchronized (GameEngine.singleton.activeObjects) {
            GameEngine.singleton.activeObjects.remove(this);
        }
    }

    public void AddMotion(IMotionComponent motionComponent) {
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
        ArrayList<IMotionComponent> toDelete = new ArrayList<>();
        synchronized (pendingVectors) {
            for (IMotionComponent imc : pendingVectors) {
                if (imc instanceof MotionComponent) {
                    MotionComponent mc = (MotionComponent)imc;
                    if (mc.frames <= 0) {
                        toDelete.add(mc);
                        continue;
                    }
                    this.X += mc.x;
                    this.Y += mc.y;
                    mc.frames--;
                } else if (imc instanceof DoubleMotionComponent) {
                    DoubleMotionComponent mc = (DoubleMotionComponent)imc;
                    if (mc.frames <= 0) {
                        toDelete.add(mc);
                        continue;
                    }
                    if (mc.xInterpolation + Math.floor(mc.x) >= Math.ceil(mc.x)) {
                        mc.xInterpolation = 0;
                        this.X += Math.ceil(mc.x);
                    } else {
                        mc.xInterpolation += mc.x;
                    }
                    if (mc.yInterpolation  + Math.floor(mc.y) >= Math.ceil(mc.y)) {
                        mc.yInterpolation = 0;
                        this.Y += Math.ceil(mc.y);
                    } else {
                        mc.yInterpolation += mc.y;
                    }
                    mc.frames--;
                }
            }
            pendingVectors.removeAll(toDelete);
        }
        synchronized (components) {
            components.forEach(goc -> goc.Update(this));
            if (collidable && components.size() > 0) {
                if (X + sprite.getWidth(null) > 800) {
                    components.forEach(goc -> goc.WallCollideEvent(this, CollisionEventType.WALLRIGHT));
                } else if (X < 0) {
                    components.forEach(goc -> goc.WallCollideEvent(this, CollisionEventType.WALLLEFT));
                } else if (Y - sprite.getHeight(null) < 0) {
                    components.forEach(goc -> goc.WallCollideEvent(this, CollisionEventType.WALLTOP));
                } else if (Y > 800) {
                    components.forEach(goc -> goc.WallCollideEvent(this, CollisionEventType.WALLBOTTOM));
                }
                for (IRenderable ir : GameEngine.singleton.activeObjects) {
                    if (ir instanceof GameObject) {
                        GameObject go = (GameObject) ir;
                        if (go.collidable) {
                            Rectangle myBox = new Rectangle(X, Y, sprite.getWidth(null), sprite.getHeight(null));
                            Rectangle goBox = new Rectangle(go.X, go.Y, go.sprite.getWidth(null), go.sprite.getHeight(null));
                            if (myBox.intersects(goBox)) {
                                components.forEach(goc -> goc.GameObjectCollideEvent(this, go, CollisionEventType.GAMEOBJECT));
                            }
                        }
                    }
                }
            }
        }
        AffineTransform at = AffineTransform.getTranslateInstance(X - (sprite.getWidth(null) / 2), Y  - (sprite.getHeight(null) / 2));
        at.rotate(Math.toRadians(rotation), X + (sprite.getWidth(null) / 2), Y + (sprite.getHeight(null) / 2));
        ((Graphics2D) g).drawImage(sprite, at, null);
    }

    @Override
    public int GetZ() {
        return Z;
    }
}
