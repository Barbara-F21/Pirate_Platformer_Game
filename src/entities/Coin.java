package entities;

//ugyan az, mint a Spike class csak ez a Coinnak van, ugyan az a felepitese

import main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utilz.Constants.Coin_Constants.*;

public class Coin {
    private int x,y;
    private Rectangle2D.Float canClaimBox;
    private boolean isCollected = false;

    public Coin(int x,int y){
        this.x=x;
        this.y=y;
        initClaimBox();
    }

    public void initClaimBox(){
        canClaimBox = new Rectangle2D.Float((int)(x + (COIN_WIDTH*Game.SCALE)/2),(int)(y + (COIN_HEIGHT*Game.SCALE)),(int)(COIN_WIDTH_DEFAULT*Game.SCALE),(int)(COIN_HEIGHT_DEFAULT*Game.SCALE));//az a +25 szemre van
    }

    public static ArrayList<Coin> getCoinFromMap(BufferedImage lvlMap){
        ArrayList<Coin> coins = new ArrayList<>();

        for (int i = 0; i < lvlMap.getHeight(); i++) {
            for (int j = 0; j < lvlMap.getWidth(); j++) {
                Color color = new Color(lvlMap.getRGB(j, i));
                if (color.getBlue() == 100) { // 100-as kék színkód jelzi a coinokat
                    coins.add(new Coin(j * Game.TILES_SIZE, i * Game.TILES_SIZE));
                }
            }
        }
        return coins;
    }

    public void draw(Graphics g, BufferedImage coinSprite, int xLvlOffset) {
        if(!isCollected){
            g.drawImage(coinSprite, (int)(x - xLvlOffset + COIN_WIDTH/2 * Game.SCALE),(int)(y + COIN_HEIGHT*Game.SCALE), COIN_WIDTH, COIN_HEIGHT, null);//a +2 szemmertek alapjan van, az y eseten is igy jobban nez ki
//            g.setColor(Color.RED);
//            g.drawRect((int)(canClaimBox.x - xLvlOffset),(int)canClaimBox.y,(int)canClaimBox.width,(int)canClaimBox.height);
        }
    }

    public Rectangle2D.Float getCanClaimBox() {
        return canClaimBox;
    }

    public boolean getisCollected(){
        return isCollected;
    }

    public void setisCollected(boolean value){
        this.isCollected = value;
    }

    public void collectCoin(){
        isCollected = true;
    }
}
