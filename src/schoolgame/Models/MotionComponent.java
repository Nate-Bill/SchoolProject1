package schoolgame.Models;

public class MotionComponent implements IMotionComponent {
    public int x;
    public int y;
    public int frames;

    public MotionComponent(int deltaX, int deltaY, int frameDuration) {
        this.x = deltaX;
        this.y = deltaY;
        this.frames = frameDuration;
    }
}
