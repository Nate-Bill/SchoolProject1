/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgame.Game;

import schoolgame.Engine.GameEngine;
import schoolgame.Models.CollisionEventType;
import schoolgame.Models.GameObject;
import schoolgame.Models.IGameObjectComponent;
import schoolgame.Models.ITextObjectComponent;
import schoolgame.Models.TextObject;

/**
 *
 * @author 14NBill
 */
public class FrameCounterComponent implements ITextObjectComponent {

    @Override
    public void Update(TextObject textObject) {
        try {
            int fps = GameEngine.singleton.lastFrameTime == 0 ? -1 : 1000 / GameEngine.singleton.lastFrameTime;
            int tps = GameEngine.singleton.lastTickTime == 0 ? -1 : 1000 / GameEngine.singleton.lastTickTime;
            textObject.text = "FPS: " + (fps == -1 ? ">1000" : fps) + " (" + GameEngine.singleton.lastFrameTime + "ms) (" + GameEngine.singleton.frames + " frames) " +
                    (tps == -1 ? ">1000" : tps) + " TPS" + " (" + GameEngine.singleton.lastTickTime + " ms)"+ " (" + GameEngine.singleton.ticks + " ticks) " +
                    Thread.activeCount() + " threads";
        } catch (Exception ex) {
            System.out.println("Exception in frame counter!");
            ex.printStackTrace();
        }
    }

    @Override
    public void Start(TextObject textObject) {
     
    }

    @Override
    public void Destroy(TextObject textObject) {
     
    }
    
}
