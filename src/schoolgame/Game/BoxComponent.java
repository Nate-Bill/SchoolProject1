package schoolgame.Game;

import schoolgame.Models.CollisionEventType;
import schoolgame.Models.GameObject;
import schoolgame.Models.IGameObjectComponent;
import schoolgame.Models.TextObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import schoolgame.Engine.GameEngine;
import schoolgame.Engine.SoundEngine;

public class BoxComponent implements IGameObjectComponent {
    private int strength;
    private long lastDamageTick = 0;
    public boolean DontChangeColour = false;

    private TextObject label;

    public BoxComponent(int strength) {
        this.strength = strength;
    }
    
    public void setStrength(int strength) {
        if (GameEngine.singleton.ticks == lastDamageTick) return;
        new SoundEngine().Play("/schoolgame/resources/HitSound.wav");
        this.strength = strength;
        this.lastDamageTick = GameEngine.singleton.ticks;
    }
    
    public int getStreangth() {
        return this.strength;
    }

    @Override
    public void Update(GameObject gameObject) {
        if (strength <= 0) {
            gameObject.Destroy();
        }
        if (label != null) {
            label.X = gameObject.X - 6;
            if (strength > 10) label.X -= 2;
            label.Y = gameObject.Y + 6;
            label.text = String.valueOf(strength);
        }
        if (!DontChangeColour) gameObject.sprite = ChangeColour(gameObject.sprite);
    }

    @Override
    public void Start(GameObject gameObject) {
        label = new TextObject("boxLabel", gameObject.X - 8, gameObject.Y - 6, 11, String.valueOf(strength));
    }

    @Override
    public void Destroy(GameObject gameObject) {
        label.Destroy();
        label = null;
    }

    @Override
    public void WallCollideEvent(GameObject gameObject, CollisionEventType type) {

    }

    @Override
    public void GameObjectCollideEvent(GameObject gameObject, GameObject gameObjectCollidedWith, CollisionEventType type) {
        gameObject.Destroy();
    }

    private Image ChangeColour(Image i) {
        BufferedImage img = toBufferedImage(i);
        int width = img.getWidth();
        int height = img.getHeight();

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                int pixel = img.getRGB(col,row);
                if ((pixel>>24) == 0x00 ) {
                    continue;
                }
                float percent = strength / 25f;
                img.setRGB(col, row, interpolateColor(Color.RED, Color.GREEN, percent));
            }
        }
        return img;
    }

    public void ChangeColourOverride(GameObject self, Color c) {
        BufferedImage img = toBufferedImage(self.sprite);
        int width = img.getWidth();
        int height = img.getHeight();

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                int pixel = img.getRGB(col,row);
                if ((pixel>>24) == 0x00 ) {
                    continue;
                }
                if (c != null) {
                    img.setRGB(col, row, c.getRGB());
                } else {
                    img.setRGB(col, row, new Color(0, 0, 0, 0).getRGB());
                }
            }
        }
    }

    private int interpolateColor(Color x, Color y, float blending) {
        float inverse_blending = 1 - blending;
        float red =   x.getRed()   * blending   +   y.getRed()   * inverse_blending;
        float green = x.getGreen() * blending   +   y.getGreen() * inverse_blending;
        float blue =  x.getBlue()  * blending   +   y.getBlue()  * inverse_blending;
        float fRed = Math.max(0, Math.min(1, red / 255));
        float fGreen = Math.max(0, Math.min(1, green / 255));
        float fBlue = Math.max(0, Math.min(1, blue / 255));
        return brighter(new Color(fRed, fGreen, fBlue), 0.6f).getRGB();
    }

    private BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }

    public Color brighter(Color c, float FACTOR) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        int alpha = c.getAlpha();

        int i = (int)(1.0/(1.0-FACTOR));
        if ( r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i, alpha);
        }
        if ( r > 0 && r < i ) r = i;
        if ( g > 0 && g < i ) g = i;
        if ( b > 0 && b < i ) b = i;

        return new Color(Math.min((int)(r/FACTOR), 255),
                Math.min((int)(g/FACTOR), 255),
                Math.min((int)(b/FACTOR), 255),
                alpha);
    }
}
