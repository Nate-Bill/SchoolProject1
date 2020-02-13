package schoolgame.Models;

public class MotionComponent {
    public double x;
    public double y;
    public int frames;

    public MotionComponent(double deltaX, double deltaY, int frameDuration) {
        this.x = deltaX;
        this.y = deltaY;
        this.frames = frameDuration;
    }
}
