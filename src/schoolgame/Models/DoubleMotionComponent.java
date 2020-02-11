/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgame.Models;

/**
 *
 * @author 14NBill
 */
public class DoubleMotionComponent implements IMotionComponent {
    public double x;
    public double y;
    public double xInterpolation;
    public double yInterpolation;
    public int frames;

    public DoubleMotionComponent(double deltaX, double deltaY, int frameDuration) {
        this.x = deltaX;
        this.y = deltaY;
        this.frames = frameDuration;
    }
}
