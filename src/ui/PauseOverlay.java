package ui;

import gamestates.Gamestate;
import gamestates.Playing;
import main.Game;
import utilz.Constants;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static utilz.Constants.UI.PauseButtons.*;
import static utilz.Constants.UI.URMButtons.*;
import static utilz.Constants.UI.VolumeButtons.*;

public class PauseOverlay {

    private Playing playing;
    private BufferedImage backgroundImg;
    private int bgX,bgY,bgW,bgH;
    private SoundButton musicButton;
    private SoundButton sfxButton;
    private UrmButton menuB,replayB,unpauseB;
    private VolumeButton volumeButton;
    private SoundManager soundManager;

    public PauseOverlay(Playing playing,SoundManager soundManager){
        this.playing = playing;
        this.soundManager = soundManager;
        loadBackground();
        createSoundButtons();
        createUrmButtons();
        createVolumeButton();
    }

    private void createVolumeButton() {
        int vX = (int)(309*Game.SCALE);
        int vY = (int)(278*Game.SCALE);
        volumeButton=new VolumeButton(vX,vY,SLIDER_WIDTH,VOLUME_HEIGHT);
    }

    private void createUrmButtons() {
        int menuX = (int)(313*Game.SCALE);
        int replayX = (int)(387* Game.SCALE);
        int unpauseX = (int)(462* Game.SCALE);
        int bY = (int)(325*Game.SCALE);

        menuB=new UrmButton(menuX,bY,URM_SIZE,URM_SIZE,2);
        replayB=new UrmButton(replayX,bY,URM_SIZE,URM_SIZE,1);
        unpauseB=new UrmButton(unpauseX,bY,URM_SIZE,URM_SIZE,0);

    }

    private void createSoundButtons() {
        int soundX = (int)(450*Game.SCALE);
        int musicY = (int)(140*Game.SCALE);
        int sfxY = (int)(186*Game.SCALE);
        musicButton = new SoundButton(soundX,musicY,SOUND_SIZE,SOUND_SIZE);
        sfxButton = new SoundButton(soundX,sfxY,SOUND_SIZE,SOUND_SIZE);
    }

    private void loadBackground() {
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PAUSE_BACKGROUND);
        bgW=(int)(backgroundImg.getWidth()* Game.SCALE);
        bgH=(int)(backgroundImg.getHeight()* Game.SCALE);
        bgX=Game.GAME_WIDTH/2 - bgW/2;
        bgY=(int)(25* Game.SCALE);
    }

    public void update(){
        musicButton.update();
        sfxButton.update();
        menuB.update();
        replayB.update();
        unpauseB.update();
        volumeButton.update();
    }

    public void draw(Graphics g){
        //Background
        g.drawImage(backgroundImg,bgX,bgY,bgW,bgH,null);

        //Sound buttons
        musicButton.draw(g);
        sfxButton.draw(g);

        //URM buttons
        menuB.draw(g);
        replayB.draw(g);
        unpauseB.draw(g);

        //Volume slider
        volumeButton.draw(g);
    }

    public void mouseDragged(MouseEvent e){
         //csak a volumesliderhez kell;
        if(volumeButton.isMousePressed()){
            float volume = volumeButton.changeX(e.getX());
            soundManager.setVolume(volume);//volumeButton.changeX(e.getX(),soundManager);
        }
    }

    public void mouseReleased(MouseEvent e) {
        if(isIn(e,musicButton)){
            if(musicButton.isMousePressed()){
                musicButton.setMuted();
            }
        }
        else if(isIn(e,sfxButton)){
            if(sfxButton.isMousePressed()){
                sfxButton.setMuted();//megforditja így a ! miatt, rovidebb
            }
        }
        else if(isIn(e,menuB)){
            if(menuB.isMousePressed()){
                Gamestate.state = Gamestate.MENU;
                playing.unpauseGame();
            }
        }
        else if(isIn(e,replayB)){
            if(replayB.isMousePressed()){
                playing.resetAll();
                playing.unpauseGame();
            }
        }
        else if(isIn(e,unpauseB)){
            if(unpauseB.isMousePressed()){
                playing.unpauseGame();
            }
        }

        musicButton.resetBools();
        sfxButton.resetBools();
        menuB.resetBools();
        replayB.resetBools();
        unpauseB.resetBools();
        volumeButton.resetBools();//tulajdonkeppen ha elegendjuk nem kell sokmindent csinaljon
    }

    public void mousePressed(MouseEvent e) {
        if(isIn(e,musicButton)){
            musicButton.setMousePressed(true);
        }
        else if(isIn(e,sfxButton)){
            sfxButton.setMousePressed(true);
        }

        else if(isIn(e,menuB)){
            menuB.setMousePressed(true);
        }
        else if(isIn(e,replayB)){
            replayB.setMousePressed(true);
        }
        else if(isIn(e,unpauseB)){
            unpauseB.setMousePressed(true);
        }
        else if(isIn(e,volumeButton)){
            volumeButton.setMousePressed(true);
        }
    }

    public void mouseMoved(MouseEvent e) {
        musicButton.setMouseOver(false);
        sfxButton.setMouseOver(false);

        menuB.setMouseOver(false);
        replayB.setMouseOver(false);
        unpauseB.setMouseOver(false);
        volumeButton.setMouseOver(false);

        if(isIn(e,musicButton)){
            musicButton.setMouseOver(true);
        }
        else if(isIn(e,sfxButton)){
            sfxButton.setMouseOver(true);
        }
        else if(isIn(e,menuB)){
            menuB.setMouseOver(true);
        }
        else if(isIn(e,replayB)){
            replayB.setMouseOver(true);
        }
        else if(isIn(e,unpauseB)){
            unpauseB.setMouseOver(true);
        }
        else if(isIn(e,volumeButton)){
            volumeButton.setMouseOver(true);
        }
    }

    private boolean isIn(MouseEvent e, PauseButton b){
        return b.getBounds().contains(e.getX(),e.getY());
    }

    public SoundButton getMusicButton() {
        return musicButton;
    }

    public SoundButton getSfxButton() {
        return sfxButton;
    }
}
