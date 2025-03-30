package entities;

import main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utilz.Constants.Spike_Constants.SPIKE_HEIGHT_DEFAULT;

public class Spike {
    private int x, y;
    private Rectangle2D.Float attackBox;

    public Spike(int x, int y) {
        this.x = x;
        this.y = y;
        initAttackBox();
    }

    public void initAttackBox(){
        attackBox = new Rectangle2D.Float(x,(int)(y+ SPIKE_HEIGHT_DEFAULT/2 *Game.SCALE),(int)(32*Game.SCALE),(int)(16*Game.SCALE));
    }

    public static ArrayList<Spike> getSpikesFromMap(BufferedImage levelMap) {
        ArrayList<Spike> spikes = new ArrayList<>();
        for (int i = 0; i < levelMap.getHeight(); i++) {
            for (int j = 0; j < levelMap.getWidth(); j++) {
                Color color = new Color(levelMap.getRGB(j, i));
                if (color.getBlue() == 4) { // Kék szín értéke 4 jelzi a tüskéket
                    spikes.add(new Spike(j * Game.TILES_SIZE, i * Game.TILES_SIZE));
                }
            }
        }
        return spikes;
    }

    public void draw(Graphics g, BufferedImage spikeSprite, int xLvlOffset) {
        g.drawImage(spikeSprite, x - xLvlOffset, y, Game.TILES_SIZE, Game.TILES_SIZE, null);
//        g.setColor(Color.RED);
//        g.drawRect((int)(attackBox.x - xLvlOffset),(int)attackBox.y,(int)attackBox.width,(int)attackBox.height);
    }

    public Rectangle2D.Float getAttackBox() {
        return attackBox;
    }
}
