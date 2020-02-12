/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgame.Game;

import schoolgame.Models.CollisionEventType;
import schoolgame.Models.GameObject;
import schoolgame.Models.IGameObjectComponent;

/**
 *
 * @author 14NBill
 */
public class DemoComponent implements IGameObjectComponent {

    @Override
    public void Update(GameObject gameObject) {
        
    }

    @Override
    public void Start(GameObject gameObject) {
        
    }

    @Override
    public void Destroy(GameObject gameObject) {
        
    }

    @Override
    public void WallCollideEvent(GameObject gameObject, CollisionEventType type) {
        
    }

    @Override
    public void GameObjectCollideEvent(GameObject gameObject, GameObject gameObjectCollidedWith, CollisionEventType type) {
        
    }
  
}
