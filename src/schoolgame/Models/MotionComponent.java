package schoolgame.Models;

import java.util.Vector;

public class MotionComponent {
    public int x;
    public int y;
    public int frames;

    public MotionComponent(int deltaX, int deltaY, int frameDuration) {
        this.x = deltaX;
        this.y = deltaY;
        this.frames = frameDuration;
    }
}
