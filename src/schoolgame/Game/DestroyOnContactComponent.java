package schoolgame.Game;

import schoolgame.Models.CollisionEventType;
import schoolgame.Models.GameObject;
import schoolgame.Models.IGameObjectComponent;

public class DestroyOnContactComponent implements IGameObjectComponent {
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
        gameObject.Destroy();
    }

    @Override
    public void GameObjectCollideEvent(GameObject gameObject, GameObject gameObjectCollidedWith, CollisionEventType type) {
        System.out.println(type);
        gameObject.Destroy();
    }
}
