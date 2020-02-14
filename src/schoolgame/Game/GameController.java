/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgame.Game;

import schoolgame.Engine.GameEngine;
import schoolgame.Models.GameObject;

/**
 *
 * @author 14cfirth
 */
public class GameController {
    
    private int round;
    private int BaseX = 400;
    private boolean basemoved = false;
    public static GameController singleton;
    public GameController(){
        singleton = this;
    }
    
    public boolean IsMoved(){
        return basemoved;
    }
    
    public void SetBase(int X){
        BaseX = X;
        this.basemoved = true;
        MoveBase();
    }
    
    public void MoveBase(){
        GameObject base = (GameObject)GameEngine.singleton.activeObjects.stream().filter(i ->  i instanceof GameObject).filter(go -> ((GameObject)go).name.equals("base")).findFirst().get();
        base.X = BaseX;
    }
    
    public void Start(){       
        GameObject base = new GameObject("base", BaseX, 700, 1, "/schoolgame/resources/cannon.png", true, new DemoComponent());
        base.X = (BaseX);
        base.Y = 700-base.sprite.getHeight(null)/2;    
        
    }  
}
