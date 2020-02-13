/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgame;

import schoolgame.Engine.GameEngine;
import schoolgame.Game.BallComponent;
import schoolgame.Game.DemoComponent;
import schoolgame.Models.GameObject;
import schoolgame.Models.MotionComponent;

/**
 *
 * @author 14NBill, 14CFirth, 14CBreare
 */
public class SchoolGame {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        new GameEngine();
        new GameObject("bg", 0, 0, 0, "/schoolgame/resources/background.png", false);
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
    }
    
}
