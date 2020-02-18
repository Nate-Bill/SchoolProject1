package schoolgame.Game;

import schoolgame.Models.ITextObjectComponent;
import schoolgame.Models.TextObject;

public class BallCounterComponent implements ITextObjectComponent {
    @Override
    public void Update(TextObject textObject) {
        textObject.text = "Balls: " + GameController.singleton.ballCount;
    }

    @Override
    public void Start(TextObject textObject) {

    }

    @Override
    public void Destroy(TextObject textObject) {

    }
}
