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
    public int xRotationOffset = 0;
    public int yRotationOffset = 0;
    public boolean visible = true;
    private boolean destroyed = false;

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
    
    public boolean isDestroyed() {
        return destroyed;
    }

    public void Destroy() {
        GameEngine.singleton.activeObjects.remove(this);
        this.components.forEach(goc -> goc.Destroy(this));
        this.components.clear();
        destroyed = true;
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
        AffineTransform at = AffineTransform.getTranslateInstance(X - (sprite.getWidth(null) / 2f), Y - (sprite.getHeight(null) / 2f));
        at.rotate(Math.toRadians(rotation), (sprite.getWidth(null) / 2f) + xRotationOffset, (sprite.getHeight(null) / 2f + yRotationOffset));
        if (visible) ((Graphics2D) g).drawImage(sprite, at, null);
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
        if (collidable && components.size() > 0) {
            try {
                if (X + (sprite.getWidth(null) / 2f) > 780) {
                    components.forEach(goc -> goc.WallCollideEvent(this, CollisionEventType.WALLRIGHT));
                } else if (X - (sprite.getWidth(null) / 2f) < 0) {
                    components.forEach(goc -> goc.WallCollideEvent(this, CollisionEventType.WALLLEFT));
                } else if (Y - (sprite.getHeight(null) / 2f) < 0) {
                    components.forEach(goc -> goc.WallCollideEvent(this, CollisionEventType.WALLTOP));
                } else if (Y + (sprite.getHeight(null) / 2f) > 700) {
                    components.forEach(goc -> goc.WallCollideEvent(this, CollisionEventType.WALLBOTTOM));
                }
                for (IRenderable ir : GameEngine.singleton.activeObjects) {
                    if (ir instanceof GameObject) {
                        GameObject go = (GameObject) ir;
                        if (go.collidable && go != this) {
                            Rectangle myBox = createRectangle(GetCorners((int) Math.round(X), (int) Math.round(Y), sprite.getWidth(null), sprite.getHeight(null)), 0);
                            Rectangle goBottomBox = createRectangle(GetCorners((int) Math.round(go.X), (int) Math.round(go.Y), go.sprite.getWidth(null), go.sprite.getHeight(null)), 1);
                            Rectangle goTopBox = createRectangle(GetCorners((int) Math.round(go.X), (int) Math.round(go.Y), go.sprite.getWidth(null), go.sprite.getHeight(null)), 2);
                            Rectangle goLeftBox = createRectangle(GetCorners((int) Math.round(go.X), (int) Math.round(go.Y), go.sprite.getWidth(null), go.sprite.getHeight(null)), 3);
                            Rectangle goRightBox = createRectangle(GetCorners((int) Math.round(go.X), (int) Math.round(go.Y), go.sprite.getWidth(null), go.sprite.getHeight(null)), 4);
                            /*
                            DebugDraw(g, myBox);
                            DebugDraw(g, goBottomBox);
                            DebugDraw(g, goTopBox);
                            DebugDraw(g, goLeftBox);
                            DebugDraw(g, goRightBox);
                            */
                            if (myBox.intersects(goLeftBox)) {
                                components.forEach(goc -> goc.GameObjectCollideEvent(this, go, CollisionEventType.GAMEOBJECTLEFT));
                            }
                            if (myBox.intersects(goRightBox)) {
                                components.forEach(goc -> goc.GameObjectCollideEvent(this, go, CollisionEventType.GAMEOBJECTRIGHT));
                            }
                            if (myBox.intersects(goBottomBox)) {
                                components.forEach(goc -> goc.GameObjectCollideEvent(this, go, CollisionEventType.GAMEOBJECTBOTTOM));
                            }
                            if (myBox.intersects(goTopBox)) {
                                components.forEach(goc -> goc.GameObjectCollideEvent(this, go, CollisionEventType.GAMEOBJECTTOP));
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println("Couldnt pass Collision event!");
                ex.printStackTrace();
            }
        }
    }

    private void DebugDraw(Graphics g, Rectangle r) {
        g.fillRect(r.x, r.y, r.width, r.height);
    }

    private Rectangle createRectangle(ArrayList<Point> points, int mode) {
        Point topLeft = points.get(0);
        Point topRight = points.get(1);
        Point bottomLeft = points.get(2);
        Point bottomRight = points.get(3);
        int height = bottomLeft.y - topLeft.y;
        int width = topRight.x - topLeft.x;
        int widthFactor = 50;
        if (mode == 0) { //Standard box
            return new Rectangle(topLeft.x, topLeft.y, width, height);
        } else if (mode == 1) { //Bottom
            int adjustedY = bottomLeft.y - (height / widthFactor);
            return new Rectangle(topLeft.x, adjustedY, width, height / widthFactor);
        } else if (mode == 2) { //Top
            return new Rectangle(topLeft.x, topLeft.y, width, height / widthFactor);
        } else if (mode == 3) { //Left
            return new Rectangle(topLeft.x, topLeft.y , width / widthFactor, height);
        } else if (mode == 4) { //Right
            int adjustedX = bottomRight.x - (width / widthFactor);
            return new Rectangle(adjustedX, topRight.y, width / widthFactor, height);
        }
        return new Rectangle(topLeft.x, topLeft.y, width, height);
    }

    private ArrayList<Point> GetCorners(int x, int y, int width, int height) {
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(x - (width / 2), y - (height / 2)));
        points.add(new Point(x + (width / 2), y - (height / 2)));
        points.add(new Point(x - (width / 2), y + (height / 2)));
        points.add(new Point(x + (width / 2), y + (height / 2)));
        return points;
    }
}
