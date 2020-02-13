/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgame.Game;

import schoolgame.Models.CollisionEventType;
import schoolgame.Models.GameObject;
import schoolgame.Models.IGameObjectComponent;
import schoolgame.Models.MotionComponent;

/**
 *
 * @author 14CFirth
 */
public class BallComponent implements IGameObjectComponent {

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
        synchronized(gameObject.pendingVectors){
            MotionComponent dmc = (MotionComponent) gameObject.pendingVectors.stream().findFirst().get();
            double Xc = dmc.x;
            double Yc = dmc.y;
            double Ycoor = gameObject.Y + gameObject.sprite.getHeight(null);
            System.out.println("Start x "+Xc);
            System.out.println("Start y " +Yc);
            gameObject.pendingVectors.clear();
            if(type==CollisionEventType.WALLRIGHT){
            System.out.println("Right x "+Xc);
            System.out.println("Right y " +Yc);
                gameObject.AddMotion(new MotionComponent(-Xc, Yc, 9999));
            }
            if(type==CollisionEventType.WALLLEFT){
                System.out.println("Left x "+Xc);
            System.out.println("Left y " +Yc);
                gameObject.AddMotion(new MotionComponent(-Xc, Yc, 9999));
            }
            if(type==CollisionEventType.WALLTOP){
                System.out.println("Top x "+Xc);
            System.out.println("Top y " +Yc);
                gameObject.AddMotion(new MotionComponent(Xc, -Yc, 999));
            }
            if(type==CollisionEventType.WALLBOTTOM){
                System.out.println("Bottom x "+Xc);
                System.out.println("Bottom y " +Yc);
                System.out.println("Coor Y : "+Ycoor);
                gameObject.AddMotion(new MotionComponent(Xc, -Yc, 999));
                //FIND COORDS OF BALL AND SET BASE TO BE THERE
            }
            MotionComponent dmcc = (MotionComponent) gameObject.pendingVectors.stream().findFirst().get();
            double nXc = dmcc.x;
            double nYc = dmcc.y;
            System.out.println("Final x "+nXc);
            System.out.println("Final y "+nYc);
            
        }
        
    }

    @Override
    public void GameObjectCollideEvent(GameObject gameObject, GameObject gameObjectCollidedWith, CollisionEventType type) {
        
    }
    
}
