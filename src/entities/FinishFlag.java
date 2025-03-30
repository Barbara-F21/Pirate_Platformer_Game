package entities;

import main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static utilz.Constants.Flag_Finish.*;

public class FinishFlag {
    private float x, y;
    private int width, height;
    private BufferedImage sprite;
    private Rectangle2D.Float hitbox;
    private boolean isTouched = false;

    public FinishFlag(float x, float y, BufferedImage sprite) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
        this.width = sprite.getWidth();
        this.height = sprite.getHeight();
        initHitbox();
        this.hitbox = new Rectangle2D.Float(x, y, width, height);
    }

    private void initHitbox() {
        hitbox = new Rectangle2D.Float((int)x,(int)y,(int)(FLAG_WIDTH*Game.SCALE),(int)(FLAG_HEIGHT*Game.SCALE));
    }

    public void draw(Graphics g, int xLvlOffset) {
        // Rajzolás a kamera eltolását figyelembe véve
        g.drawImage(sprite, (int) (x - xLvlOffset), (int) y, width, height, null);
//        g.setColor(Color.RED);
//        g.drawRect((int)(hitbox.x-xLvlOffset),(int)(hitbox.y),(int)(hitbox.width),(int)(hitbox.height));
    }

    public void checkCollision(Player player) {

        Rectangle2D.Float adjustedHitbox = new Rectangle2D.Float(x, y, width, height);

        if (adjustedHitbox.intersects(player.getHitbox()) && !isTouched) {
            isTouched = true;
            System.out.println("You won!");
        }
    }

    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }

    public boolean isTouched() {
        return isTouched;
    }
}
