/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgame;

import schoolgame.Engine.GameEngine;
import schoolgame.Game.*;
import schoolgame.Models.GameObject;
import schoolgame.Models.MotionComponent;
import schoolgame.Models.TextObject;

/**
 *
 * @author 14NBill, 14CFirth, 14CBreare
 */
public class SchoolGame {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        new GameController(); //All prev code including new GameEngine, etc, has been moved to GameController#ctor
        /*

        This code serves as a test case to check function of game engine

        GameObject go = new GameObject("debug", 200, 200, 1, "/schoolgame/resources/ball.png", true, new BallComponent());
        GameObject g1 = new GameObject("debug", 200, 200, 1, "/schoolgame/resources/box.png", true, new BallComponent());
        GameObject g2 = new GameObject("debug", 200, 200, 1, "/schoolgame/resources/cannon.png", true, new BallComponent());
        go.AddMotion(new MotionComponent(3, 3, 300));
        g1.AddMotion(new MotionComponent(1, 2, 300));
        g2.AddMotion(new MotionComponent(4, 8, 300));
        for (int i = 0; i <= 360; i++) {
            go.rotation = i;
            Thread.sleep(10);
        }
        new GameObject("test", 400, 700, 10, "/schoolgame/resources/debug.png", true);
        new GameObject("test", 400, 100, 10, "/schoolgame/resources/ball.png", true, new DestroyOnContactComponent()).AddMotion(new MotionComponent(0, 2, 300));
        new GameObject("test", 400, 100, 10, "/schoolgame/resources/ball.png", true, new DestroyOnContactComponent()).AddMotion(new MotionComponent(0, 2, 300));
        System.out.println("Count for object by name test: " + GameEngine.singleton.GetGameObjectsByName("test").size());
        System.out.println("Count for object by comp DestroyOnContact: " + GameEngine.singleton.GetGameObjectsByComponent(DestroyOnContactComponent.class).size());
        System.out.println("Count for comp DestroyOnContact in test: " + GameEngine.singleton.GetComponentFromGameObject(GameEngine.singleton.GetGameObjectsByComponent(DestroyOnContactComponent.class).get(0), DestroyOnContactComponent.class).size());
        new GameObject("test", 400, 100, 10, "/schoolgame/resources/ball2.png", true, new DestroyOnContactComponent()).AddMotion(new MotionComponent(0, 2, 300));
        */
    }
}
