package ui;

import utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static utilz.Constants.UI.VolumeButtons.*;

public class VolumeButton extends PauseButton{

    private BufferedImage[] imgs;
    private BufferedImage slider;
    private int index=0;
    private boolean mouseOver,mousePressed;
    private int buttonX;//csuszka a slideren
    private int minX, maxX;//slider eleje es vege

    public VolumeButton(int x, int y, int width, int height) {
        super(x + width/2, y, VOLUME_WIDTH, height);//hitbox a buttonnak;
        bounds.x-=VOLUME_WIDTH/2;
        buttonX = x + width/2;
        this.x=x;
        this.width = width;
        minX = x + VOLUME_WIDTH/2;//figyelembe kell venni a csuszka szelesseget is, hogy ne fusson ki a sliderbol
        maxX = x + width - VOLUME_WIDTH/2 - 1;//igy jobban nez ki -1-el, igy lesz egyforma a ketto, hanem 1 pixel még mindig ki fog lógni
        //y ugyan az marad
        loadImgs();
    }

    private void loadImgs() {
        BufferedImage tmp = LoadSave.GetSpriteAtlas(LoadSave.VOLUME_BUTTONS);
        imgs = new BufferedImage[3];
        for (int i = 0; i < imgs.length; i++) {
            imgs[i] = tmp.getSubimage(i * VOLUME_DEFAULT_WIDTH, 0, VOLUME_DEFAULT_WIDTH, VOLUME_DEFAULT_HEIGHT);
        }

        slider = tmp.getSubimage(3*VOLUME_DEFAULT_WIDTH,0,SLIDER_DEFAULT_WIDTH,VOLUME_DEFAULT_HEIGHT);//a sheeten 3 volumbutton-al alrebb kezdodik a slider
    }

    public void update(){
         index=0;
         if(mouseOver) {
             index = 1;
         }
         if(mousePressed) {
             index=2;
         }
    }

    public void draw(Graphics g){

        g.drawImage(slider,x,y,width,height,null);
        g.drawImage(imgs[index],buttonX - VOLUME_WIDTH/2,y,VOLUME_WIDTH,height,null);//igy lesz a mouse a tenyleges kozepen a gombnak
    }

    public float changeX(int x){//void-om volt
        if(x<minX){
            buttonX=minX;
        }
        else if(x>maxX){
            buttonX = maxX;
        }
        else{
            buttonX=x;
        }
        bounds.x=buttonX - VOLUME_WIDTH/2;//mivel fent modositottuk ide is kell
        return getVolumePercent(); //soundManager.setVolume(getVolumePercent());
    }

    public void resetBools(){
        mouseOver=false;
        mousePressed=false;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public float getVolumePercent() {
        return (float) (buttonX - minX) / (maxX - minX);
    }
}
