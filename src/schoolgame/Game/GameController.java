/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgame.Game;

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
        boolean basemoved = true;
    }
    
    public void Start(){
        
        
        GameObject base = new GameObject("debug", BaseX, 700, 1, "/schoolgame/resources/cannon.png", true, new DemoComponent());

        base.X = (BaseX)-(base.sprite.getWidth(null)/2);
        base.Y = 700-base.sprite.getHeight(null);
        
        
        
        
        
    }
    
    
    
}
