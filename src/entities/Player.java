package entities;

import gamestates.Playing;
import main.Game;
import utilz.LoadSave;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static utilz.Constants.PlayerConstants.*;
import static  utilz.HelpMethods.CanMoveHere;
import static utilz.HelpMethods.*;

public class Player extends Entity{

    private BufferedImage[][] animations;
    private int aniTick, aniIndex, aniSpeed = 25;
    private int playerAction=IDLE;
    private boolean moving = false, attacking=false;
    private boolean left, up, right, down, jump;
    private float playerSpeed=1.0f * Game.SCALE;
    private int[][] lvlData;
    private float xDrawOffset=21 * Game.SCALE;//a hitboxtol az aktualis karaktermeretig
    private float yDrawOffset = 4 * Game.SCALE;

    //Jumping and Gravity
    private float airSpeed = 0f;
    private float gravity = 0.04f * Game.SCALE;
    private float jumpSpeed = -2.25f * Game.SCALE;
    private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
    private boolean inAir = false;

    private BufferedImage statusBarImg;

    //Ez maga a teljes kep amin van a health
    private int statusBarWidth = (int)(192 * Game.SCALE);
    private int statusBarHeight = (int)(58 * Game.SCALE);
    private int statusBarX = (int)(34 * Game.SCALE);
    private int statusBarY = (int)(14 * Game.SCALE);
    //
    private int healthBarWidth = (int)(150 * Game.SCALE);
    private int healthBarHeight = (int)(4 * Game.SCALE);
    private int healthBarXStart = (int)(34 * Game.SCALE);
    private int healthBarYStart = (int)(14 * Game.SCALE);

    private int maxHealth = 100;
    private int currentHealth = maxHealth;
    private int healthWidth = healthBarWidth;

    //attackBox
    private Rectangle2D.Float attackBox;//a player elott lesz egy kicsivel a kardja beleer

    private int flipX = 0;
    private int flipW = 1;

    private boolean attackChecked = false;
    private Playing playing;

    private int score = 0;

    public Player(float x, float y, int width, int height, Playing playing) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        super(x, y, width, height);
        this.playing=playing;
        loadAnimations();
        initHitbox(x,y,(int)(20*Game.SCALE),(int)(27*Game.SCALE));//scaling miatt kell int-re rakni
        initAttackBox();
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x,y,(int)(20* Game.SCALE),(int)(20*Game.SCALE));
    }

    public void increaseScore(int value){
        this.score += value;
    }

    public void update(){
        updateHealthBar();
        if (currentHealth <= 0){
            playing.setGameOver(true);
            return;
        }
        updateAttackBox();

        updatePos();
        if(attacking){
            checkAttack();
        }
        updateAnimationTick();
        setAnimation();
    }

    private void checkAttack() {
        if(attackChecked || aniIndex !=1){
            return;
        }
        attackChecked = true;
        playing.checkEnemyHit(attackBox);
    }

    private void updateAttackBox() {
        if(right){
            attackBox.x = hitbox.x + hitbox.width + (int)(Game.SCALE * 10);//koveti a player hitboxot + offset
        }
        else if(left){
            attackBox.x = hitbox.x - hitbox.width - (int)(Game.SCALE * 10);
        }

        attackBox.y = hitbox.y + (Game.SCALE * 10);
    }

    private void updateHealthBar() {
        healthWidth = (int)((currentHealth/(float)maxHealth) * healthBarWidth);//float nelkul 1 vagy 0 lenne, (int/int = int, int/float = float, float/int = float);
    }

    public void render(Graphics g, int lvlOffset){
        int scaledWidth = (int)(64 * Game.SCALE);
        int scaledHeight = (int)(40 * Game.SCALE);
        g.drawImage(animations[playerAction][aniIndex], (int)(hitbox.x - xDrawOffset) - lvlOffset + flipX, (int)(hitbox.y - yDrawOffset), scaledWidth * flipW, scaledHeight, null);//ha balra megyunk a player-t "negativ width-el vesszuk" s megforditjuk a karaktert ezzel
        //mert amikor LEFT-re allitjuk akkor a flipW = -1 maskulomben 1, s akkor tukroz.

        //drawHitbox(g, lvlOffset);
        //g.drawImage(animations[playerAction][aniIndex],(int)x,(int)y,256,160,null);//karakter kb. 64x40 px
        //drawAttackBox(g,lvlOffset);
        drawUI(g);
    }

//    private void drawAttackBox(Graphics g, int lvlXOffset) {
//        g.setColor(Color.RED);
//        g.drawRect((int)(attackBox.x - lvlXOffset),(int)attackBox.y,(int)attackBox.width,(int)attackBox.height);
//    }

    private void drawUI(Graphics g){
        g.drawImage(statusBarImg, statusBarX, statusBarY,statusBarWidth,statusBarHeight,null);
        g.setColor(Color.RED);
        g.fillRect(healthBarXStart + statusBarX,healthBarYStart + statusBarY, healthWidth, healthBarHeight);//satatusBarX/Y + healthbarX/Y azert, mert at kell lepni a kort is, amiben az ikon van, hogy onnan kezdodjon a teglalap
    }

    private void updateAnimationTick() {

        aniTick++;
        if(aniTick>=aniSpeed){
            aniTick=0;
            aniIndex++;
            if(aniIndex >= GetSpriteAmount(playerAction)){
                aniIndex=0;//ujramegy a tombon/matrix megfelelo soran
                attacking = false;
                attackChecked = false;
            }
        }
    }

    private void setAnimation(){

        int startAni=playerAction;

        if(moving){
            playerAction = RUNNING;
        }
        else{
            playerAction = IDLE;
        }

        if(inAir){
            if(airSpeed < 0){//up
                playerAction = JUMP;
            }
            else{
                playerAction= FALLING;
            }
        }

        if(attacking){
            playerAction=ATTACK_1;
            if(startAni != ATTACK_1){//ezt csak azert, hogy mikor kattintasz ne legyen delay az attacking animacioban, ezert a 2-es attacking allapotbol induljon (magyarul a tomb 2. (1-edik, mert 0-tol szamolunk) elemeteol)
               aniIndex = 1;
               aniTick = 0;
            }
        }
        if(startAni !=playerAction){
            resetAniTick();
        }
    }

    private void resetAniTick(){
        aniTick=0;
        aniIndex=0;
    }

    private void updatePos(){

        moving=false;
        if(jump){
            jump();
        }
        if(!inAir){
            if((!left && !right) || (right && left)){//hogy ha mindket gombot lenyomod egyszerre ne legyen futas animacio
                return;
            }
        }

        float xSpeed=0;
        if(left){
            xSpeed -=playerSpeed;
            flipX = width;
            flipW = -1;
        }
        if(right){
            xSpeed +=playerSpeed;
            flipX = 0;
            flipW = 1;
        }

        if(!inAir){
            if(!isEntityOnFloor(hitbox, lvlData)){
                inAir=true;
            }
        }

        if(inAir){
            if(CanMoveHere(hitbox.x,hitbox.y + airSpeed, hitbox.width ,hitbox.height, lvlData)){
                hitbox.y +=airSpeed;
                airSpeed += gravity;
                updateXPos(xSpeed);
            }
            else{
                hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox,airSpeed);
                if(airSpeed > 0){
                    resetInAir();
                }
                else{
                    airSpeed = fallSpeedAfterCollision;
                }
                updateXPos(xSpeed);
            }
        }
        else{//csak x-et kell ellenorizni
            updateXPos(xSpeed);
        }

        moving=true;
    }

    private void jump() {
        if(inAir){
            return;
        }
        inAir=true;
        airSpeed=jumpSpeed;

    }

    private void resetInAir() {
        inAir = false;
        airSpeed = 0;
    }

    private void updateXPos(float xSpeed){
        if(CanMoveHere(hitbox.x+xSpeed,hitbox.y,hitbox.width,hitbox.height,lvlData)){
            hitbox.x += xSpeed;
        }
        else{//ha akarunk mozdulni, de nincs eleg hely, viszont van egy kis rés a player es a block kozott
            hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
        }
    }

    public void changeHealth(int value){
        currentHealth += value;

        if(currentHealth <= 0) {
            currentHealth = 0;
            //Game Over!
            //gameOver();
        }else if(currentHealth >= maxHealth){
            currentHealth = maxHealth;
        }
    }

    private void loadAnimations() {

        //mivel buffered image-gel dolgozunk, s az image stickerekbol van osszerakva
        //dolgozhatunk az image section-jeivel


            //assert is != null;//hogy ne legyen is = null
            BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);

            animations = new BufferedImage[7][8];
            for(int i=0;i<animations.length;i++){
                for(int j = 0; j< animations[i].length; j++) {
                    animations[i][j]=img.getSubimage(j*64,i*40,64,40);
                }
              }
            statusBarImg =LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);

    }

    public void loadLvlData(int[][] lvlData){
        this.lvlData=lvlData;
        if(!isEntityOnFloor(hitbox, lvlData)){
            inAir = true;
        }
    }

    public void resetDirBooleans(){
         left=false;
         right=false;
         up=false;
         down=false;
    }

    public void setAttacking(boolean attacking){
        this.attacking=attacking;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public void setJump(boolean jump){
        this.jump=jump;
    }

    public void kill(){
        currentHealth = 0;
    }

    public void resetAll(){
        resetDirBooleans();
        currentHealth = maxHealth;
        attacking = false;
        playerAction = IDLE;
        moving = false;
        score = 0;

        hitbox.x = x;
        hitbox.y = y;

        inAir=false;
        if(!isEntityOnFloor(hitbox,lvlData)){
            inAir=true;
        }
    }

    public int getScore(){
        return score;
    }
}
