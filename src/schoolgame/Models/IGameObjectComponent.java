package schoolgame.Models;

public interface IGameObjectComponent {
    void Update(GameObject gameObject);
    void Start(GameObject gameObject);
    void Destroy(GameObject gameObject);
    void WallCollideEvent(GameObject gameObject, CollisionEventType type);
    void GameObjectCollideEvent(GameObject gameObject, GameObject gameObjectCollidedWith, CollisionEventType type);
}
