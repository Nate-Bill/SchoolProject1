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
            textObject.text = "FPS: " + (1000 / (GameEngine.singleton.lastFrameTime) + " (" + GameEngine.singleton.lastFrameTime + "ms)");
        } catch (Exception ignored) {
            
        }
    }

    @Override
    public void Start(TextObject textObject) {
     
    }

    @Override
    public void Destroy(TextObject textObject) {
     
    }
    
}
