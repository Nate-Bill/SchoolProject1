/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgame;

import schoolgame.Engine.GameEngine;
import schoolgame.Game.DemoComponent;
import schoolgame.Models.DoubleMotionComponent;
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
        new GameObject("bg", 400, 400, 0, "/schoolgame/resources/background.png", false);
        GameObject go = new GameObject("debug", 200, 200, 1, "/schoolgame/resources/debug.png", true);
        for (int i = 0; i <= 360; i++) {
            go.rotation = i;
            Thread.sleep(100);
        }
        //go.AddMotion(new MotionComponent(20, 20, 300));
    }
    
}
