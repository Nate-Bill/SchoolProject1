/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgame.Game;

import schoolgame.Engine.GameEngine;
import schoolgame.Models.GameObject;
import schoolgame.Models.IRenderable;
import schoolgame.Models.TextObject;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author 14cfirth
 */
public class GameController {
    
    private int round;
    private int BaseX = 400;
    public static GameController singleton;
    public boolean canFire = false;
    public int ballCount = 5;
    public GameController(){
        singleton = this;
        new GameEngine();
        new GameObject("bg", 400, 400, 0, "/schoolgame/resources/background.png", false);
        new TextObject("fps", 0, 15, 100, "FPS: 0", new FrameCounterComponent());
        Start();
    }

    
    public void SetBase(int X){
        BaseX = X;
        MoveBase();
    }
    
    public void MoveBase(){
        List<GameObject> baseGOs = GameEngine.singleton.GetGameObjectsByName("base");
        if (baseGOs.size() == 0) return;
        GameObject base = baseGOs.get(0);
        base.X = BaseX;
    }
    
    public void Start(){       
        GameObject base = new GameObject("base", BaseX, 700, 1, "/schoolgame/resources/base2.png", true, new BaseComponent());
        new GameObject("box", 100, 200, 10, "/schoolgame/resources/debug.png", true);
        new GameObject("box", 300, 200, 10, "/schoolgame/resources/debug.png", true);
        new GameObject("box", 500, 200, 10, "/schoolgame/resources/debug.png", true);
        new GameObject("box", 700, 200, 10, "/schoolgame/resources/debug.png", true);
        base.X = (BaseX);
        base.Y = 700-base.sprite.getHeight(null) / 2f;
        new Thread(() -> { //Controls if can fire
            while (!GameEngine.singleton.cancellationToken) {
                canFire = !(GameEngine.singleton.GetGameObjectsByComponent(BallComponent.class).size() > 0);
                base.visible = canFire;
            }
        }).start();
        //TODO procedural boxes
    }  
}
