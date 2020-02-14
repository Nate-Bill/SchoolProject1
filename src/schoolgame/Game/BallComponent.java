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
        MotionComponent dmc = gameObject.pendingVectors.stream().findFirst().get();
        double Xc = dmc.x;
        double Yc = dmc.y;
        double Xcoor = gameObject.X;
        gameObject.pendingVectors.clear();
        if (type == CollisionEventType.WALLRIGHT) {
            gameObject.AddMotion(new MotionComponent(-Math.abs(Xc), Yc, 9999));
        }
        if (type == CollisionEventType.WALLLEFT) {
            gameObject.AddMotion(new MotionComponent(Math.abs(Xc), Yc, 9999));
        }
        if (type == CollisionEventType.WALLTOP) {
            gameObject.AddMotion(new MotionComponent(Xc, -Yc, 999));
        }
        if (type == CollisionEventType.WALLBOTTOM) {
            gameObject.AddMotion(new MotionComponent(Xc, -Yc, 999));
            GameController.singleton.SetBase((int) Xcoor);
            gameObject.Destroy();
        }
        MotionComponent dmcc = gameObject.pendingVectors.stream().findFirst().get();
        double nXc = dmcc.x;
        double nYc = dmcc.y;

    }

    @Override
    public void GameObjectCollideEvent(GameObject gameObject, GameObject gameObjectCollidedWith, CollisionEventType type) {
        if (!gameObjectCollidedWith.name.equals("box")) return;
        MotionComponent dmc = gameObject.pendingVectors.stream().findFirst().get();
        double Xc = dmc.x;
        double Yc = dmc.y;
        double Xcoor = gameObject.X;
        gameObject.pendingVectors.clear();
        if (type == CollisionEventType.GAMEOBJECTRIGHT) {
            gameObject.AddMotion(new MotionComponent(-Xc, Yc, 9999));
        }
        if (type == CollisionEventType.GAMEOBJECTLEFT) {
            gameObject.AddMotion(new MotionComponent(-Xc, Yc, 9999));
        }
        if (type == CollisionEventType.GAMEOBJECTTOP) {
            gameObject.AddMotion(new MotionComponent(Xc, -Math.abs(Yc), 999));
        }
        if (type == CollisionEventType.GAMEOBJECTBOTTOM) {
            gameObject.AddMotion(new MotionComponent(Xc, Math.abs(Yc), 999));
        }
        MotionComponent dmcc = gameObject.pendingVectors.stream().findFirst().get();
        double nXc = dmcc.x;
        double nYc = dmcc.y;
    }
}
