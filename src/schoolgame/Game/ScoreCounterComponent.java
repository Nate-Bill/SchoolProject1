package schoolgame.Game;

import schoolgame.Models.ITextObjectComponent;
import schoolgame.Models.TextObject;

public class ScoreCounterComponent implements ITextObjectComponent {
    @Override
    public void Update(TextObject textObject) {
        textObject.text = "Score: " + GameController.singleton.round;
    }

    @Override
    public void Start(TextObject textObject) {

    }

    @Override
    public void Destroy(TextObject textObject) {

    }
}
