package schoolgame.Models;

public interface ITextObjectComponent {
    void Update(TextObject textObject);
    void Start(TextObject textObject);
    void Destroy(TextObject textObject);
}
