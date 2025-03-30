package gamestates;

import entities.*;
import levels.LevelManager;
import main.Game;
import ui.GameOverOverLay;
import ui.GameWonOverlay;
import ui.PauseOverlay;
import ui.SoundManager;
import utilz.LoadSave;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static utilz.Constants.Environment.*;

import static main.Game.SCALE;
import static utilz.Constants.Flag_Finish.*;

public class Playing extends State implements Statemethods{
    private Player player;
    private LevelManager levelManager;
    private EnemyManager enemyManager;
    private boolean paused = false;
    private PauseOverlay pauseOverlay;
    private GameOverOverLay gameOverOveLay;

    private int xLvlOffset;
    private int leftBorder = (int)(0.2 * Game.GAME_WIDTH);//20%-a//az a vonal amit ha elhagyunk kell szamolni az uj reszert, vagyis tovabbcsuszik a map
    private int rightBorder = (int) (0.8 * Game.GAME_WIDTH);//20% + 80% = 100%
    private int lvlTilesWide = LoadSave.GetLevelData()[0].length; // ez azert kell, hogy ha vege van a map-unknek, akkor majd ne tudjunk kifutni
    private int maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH; //meg mennyi blockunk/tile-unk maradt es meg mennyit tudunk latni
    private int maxlvlOffsetX = maxTilesOffset * Game.TILES_SIZE; //pixelekbe

    private BufferedImage backGroundImg,bigCloud,smallCloud;
    private int[] smallCloudsPos;
    private Random rnd;
    private BufferedImage spikeSprite;
    private ArrayList<Spike> spikes;

    private BufferedImage flagSprite;
    private FinishFlag flag;

    private BufferedImage coinSprite;
    private ArrayList<Coin> coins;

    private boolean gameOver;
    private boolean gameWon = false;

    private GameWonOverlay gameWonOverlay;
    private SoundManager soundManager;

    private SoundManager soundManagerForSFX;

    public Playing(Game game) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        super(game);
        initClasses();

        backGroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BACKGROUND_IMG);
        bigCloud = LoadSave.GetSpriteAtlas(LoadSave.BIG_CLOUDS);
        smallCloud = LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUDS);
        smallCloudsPos = new int[40];
        rnd=new Random();
        for(int i=0;i<smallCloudsPos.length;i++){
            smallCloudsPos[i]=(int)(90*Game.SCALE) + rnd.nextInt((int)(100* SCALE));
        }
    }

    private void initClasses() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        levelManager=new LevelManager(game);
        enemyManager=new EnemyManager(this);
        soundManager = new SoundManager();
        soundManagerForSFX = new SoundManager();

        spikeSprite = LoadSave.GetSpriteAtlas(LoadSave.TRAP_SPIKES);
        spikes = Spike.getSpikesFromMap(LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ONE_DATA));
        coinSprite = LoadSave.GetSpriteAtlas(LoadSave.COIN_PIXELART);
        coins = Coin.getCoinFromMap(LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ONE_DATA));
        flagSprite = LoadSave.GetSpriteAtlas(LoadSave.FLAG_PIXELART);
        flag = new FinishFlag(FLAG_X_POSITION,FLAG_Y_POSITION,flagSprite);
        gameWonOverlay = new GameWonOverlay(this);

        player=new Player(200,200,(int)(64*SCALE),(int)(40*SCALE), this);
        player.loadLvlData(levelManager.getCurrentLevel().getLevelData());
        pauseOverlay = new PauseOverlay(this,soundManager);
        gameOverOveLay = new GameOverOverLay(this);
    }

    @Override
    public void update() {
        if(gameWon){
            soundManager.stopBackGroundMusic();
            return;
        }
        if(!paused && !gameOver){
            if(!pauseOverlay.getMusicButton().isMuted()){
                soundManager.startBackGroundMusic();
            }
            levelManager.update();
            player.update();
            enemyManager.update(levelManager.getCurrentLevel().getLevelData(), player);
            checkCloseToBorder();
            checkSpikesTouched(player);
            checkCoinTouched(player);
            checkFlagTouched(player);
        }
        else if(paused){
            soundManager.stopBackGroundMusic();
            pauseOverlay.update();
        }

        if(gameOver){
            soundManager.stopBackGroundMusic();
        }
    }

    private void checkFlagTouched(Player player) {
        if(flag.getHitbox().intersects(player.getHitbox())){
            gameWon = true;
        }
    }

    private void checkCloseToBorder() {
        int playerX = (int)player.getHitbox().x;//offset nelkul az aktualis player pozicioja
        int diff = playerX - xLvlOffset;

        if(diff>rightBorder){
            xLvlOffset += diff - rightBorder;
        }
        else if(diff<leftBorder){
            xLvlOffset += diff - leftBorder;
        }

        if(xLvlOffset > maxlvlOffsetX){
            xLvlOffset = maxlvlOffsetX;
        }
        else if(xLvlOffset < 0){
            xLvlOffset = 0;
        }
    }

    @Override
    public void draw(Graphics g) {

        g.drawImage(backGroundImg,0,0,Game.GAME_WIDTH,Game.GAME_HEIGHT,null);
        drawClouds(g);

         levelManager.draw(g, xLvlOffset);
         player.render(g, xLvlOffset);
         enemyManager.draw(g,xLvlOffset);
         for (Spike s : spikes) {
             s.draw(g, spikeSprite, xLvlOffset);
         }
         for (Coin c : coins){
             c.draw(g,coinSprite, xLvlOffset);
         }
         flag.draw(g,xLvlOffset);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + player.getScore(), 60, 21);

        if (gameWon) {
            gameWonOverlay.draw(g);
        } else if (paused) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
            pauseOverlay.draw(g);
        } else if (gameOver) {
            gameOverOveLay.draw(g);
        }
    }

    private void drawClouds(Graphics g) {

        for(int i=0;i<20; i++){
            g.drawImage(bigCloud,i * BIG_CLOUD_WIDTH - (int)(xLvlOffset * 0.2),(int)(202* Game.SCALE),BIG_CLOUD_WIDTH,BIG_CLOUD_HEIGHT,null);
        }

        for (int i=0;i<smallCloudsPos.length;i++) {
            g.drawImage(smallCloud, SMALL_CLOUD_WIDTH*4*i - (int)(xLvlOffset * 0.6), smallCloudsPos[i], SMALL_CLOUD_WIDTH, SMALL_CLOUD_HEIGHT, null);
        }
    }

    public void mouseDragged(MouseEvent e){
        if(!gameOver){
            if(paused){
                pauseOverlay.mouseDragged(e);
            }
        }
    }

    public void resetAll(){
        //reset player enemy stb;
        gameOver = false;
        gameWon = false;
        paused = false;
        //a coinoknal mind resetelni kell falsera az isCollectedet;
        coins.stream().forEach(c -> c.setisCollected(false));
        //Ez az egyik (2. féle) streamem (:

        player.resetAll();
        enemyManager.resetAllEnemies();
    }

    public void setGameOver(boolean gameOver){
        this.gameOver = gameOver;
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox){
        enemyManager.checkEnemyHit(attackBox);
        System.out.println("Checkolva!");
    }

    public void checkSpikesTouched(Player player){
        for ( Spike s : spikes){
            if(s.getAttackBox().intersects(player.getHitbox())){
                player.kill();
            }
        }
    }

    private void checkCoinTouched(Player player) {
        for ( Coin c : coins){
            if(!c.getisCollected() && c.getCanClaimBox().intersects(player.getHitbox())){
                player.increaseScore(10);
                c.collectCoin();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(!gameOver){
            if(e.getButton()==MouseEvent.BUTTON1){ //bal klick
                player.setAttacking(true);
                if(!paused && !pauseOverlay.getSfxButton().isMuted()){
                    soundManagerForSFX.startSFX();//azert van itt, mert itt van mar ellenorizve, hogy tamad-e;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(!gameOver) {
            if (paused) {
                pauseOverlay.mouseReleased(e);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(!gameOver){
           if(paused){
               pauseOverlay.mousePressed(e);
           }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(!gameOver) {
            if (paused) {
                pauseOverlay.mouseMoved(e);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if(gameOver){
            gameOverOveLay.keyPressed(e);
        }
        else {

            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    player.setLeft(true);//left
                    break;
                case KeyEvent.VK_D:
                    player.setRight(true);//right
                    break;
                case KeyEvent.VK_SPACE:
                    player.setJump(true);
                    break;
                case KeyEvent.VK_BACK_SPACE:
                    Gamestate.state = Gamestate.MENU;
                    break;
                case KeyEvent.VK_ESCAPE:
                    if (gameWon) {
                        resetAll();
                        Gamestate.state = Gamestate.MENU;
                    } else {
                        paused = !paused;
                    }
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

        if(!gameOver){
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    player.setLeft(false);
                    break;
                case KeyEvent.VK_D:
                    player.setRight(false);
                    break;
                case KeyEvent.VK_SPACE:
                    player.setJump(false);
                    break;
            }
        }

    }

    public void unpauseGame(){
        paused=false;
    }

    public Player getPlayer(){
        return player;
    }

    public void setPause(boolean value){
        paused = value;
    }

    public PauseOverlay getPauseOverlay(){
        return pauseOverlay;
    }

    public SoundManager getSoundManager(){
        return soundManager;
    }
}
