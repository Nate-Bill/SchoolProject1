/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgame.Game;

import schoolgame.Engine.GameEngine;
import schoolgame.Models.*;

import java.awt.event.KeyEvent;
import schoolgame.Engine.SoundEngine;

/**
 *
 * @author 14NBill
 */
public class BaseComponent implements IGameObjectComponent, IKeyCallback {
    
    GameObject me;

    TextObject counter;
    
    @Override
    public void Update(GameObject gameObject) {
        if (me.visible) {
            counter.visible = true;
            counter.text = "x" + GameController.singleton.ballCount;
            counter.X = me.X - 35;
            counter.Y = me.Y + 65;
        } else {
            counter.visible = false;
        }
    }

    @Override
    public void Start(GameObject gameObject) {
        this.me = gameObject;
        GameEngine.singleton.RegisterKeyListener(this);
        me.yRotationOffset = 42;
        counter = new TextObject("ballCountBase", 0, 0, 0, "x1");
    }

    @Override
    public void Destroy(GameObject gameObject) {
        GameEngine.singleton.UnregisterKeyListener(this);
    }

    @Override
    public void WallCollideEvent(GameObject gameObject, CollisionEventType type) {

    }

    @Override
    public void GameObjectCollideEvent(GameObject gameObject, GameObject gameObjectCollidedWith, CollisionEventType type) {

    }

    @Override
    public void KeyType(KeyEvent ke) {

    }

    @Override
    public void KeyPress(KeyEvent ke) {
        if (ke.getKeyCode() == 37) { //Left
            me.rotation -= 2;
        } else if (ke.getKeyCode() == 39) { //Right
            me.rotation += 2;
        } else if (ke.getKeyCode() == 38 || ke.getKeyCode() == 32) { //Up or space
            if (!GameController.singleton.canFire) return;
            new Thread (() -> {
                int speed = 6;
                double deltaY = speed * Math.sin(Math.toRadians(90 - me.rotation));
                double deltaX = speed * Math.cos(Math.toRadians(90 - me.rotation));
                GameController.singleton.isFiring = true;
                for (int i = 1; i <= GameController.singleton.ballCount; i++) {
                    try {
                        new GameObject("ball", (int) me.X, (int) me.Y + 42, 10, "/schoolgame/resources/ball2.png", true, new BallComponent()).AddMotion(new MotionComponent(deltaX, -deltaY, 3000));
                        new SoundEngine().Play("/schoolgame/resources/LauncherSound.wav");
                        Thread.sleep(50);
                    } catch (Exception ignored) {

                    }
                }
                GameController.singleton.isFiring = false;
                me.visible = false;
            }).start();
        } else if (ke.getKeyCode() == 61) {
            GameController.singleton.ballCount += 5;
        }
    }

    @Override
    public void KeyRelease(KeyEvent ke) {

    }
}
