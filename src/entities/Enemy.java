package entities;

import main.Game;

import java.awt.geom.Rectangle2D;

import static utilz.Constants.EnemyConstants.*;
import static utilz.HelpMethods.*;
import static utilz.Constants.Directions.*;

public abstract class Enemy extends Entity{
    //azert absztrakt, mert kell egy rendes ellenseg, amely extendeli ezt, ugy, mint a player

    protected int aniIndex, enemyState, enemyType;
    protected int aniTick, aniSpeed = 25;
    protected boolean firstUpdate = true;
    protected boolean inAir ;//= false;
    protected float fallSpeed;
    protected float gravity = 0.04f * Game.SCALE;
    protected float walkSpeed = 0.4f * Game.SCALE;
    protected float walkDir = LEFT;
    protected int tileY;
    protected float attackDistance = Game.TILES_SIZE;
    protected int maxHealth;
    protected int currentHealth;
    protected boolean active = true;
    protected boolean attackChecked;

    public Enemy(float x, float y, int width, int height, int enemyType) {
        super(x, y, width, height);
        this.enemyType=enemyType;
        initHitbox(x,y,width,height);
        maxHealth = GetMaxHealth(enemyType);
        currentHealth = maxHealth;
    }

    protected void updateAnimationTick(){
        aniTick++;
        if(aniTick>=aniSpeed){
            aniTick=0;
            aniIndex++;
            if(aniIndex>=GetSpriteAmount(enemyType,enemyState)){
                aniIndex = 0;
                if(enemyState == ATTACK){//miutan vegigmentem az attack-hez tartozo animacion, vege lesz a tamadasnak
                    enemyState = IDLE;
                }
                else if(enemyState == HIT){
                    enemyState = IDLE;
                }
                else if(enemyState == DEAD){
                    active = false;
                }
            }
        }
    }

    protected void firstUpdateCheck(int [][] lvlData){
        if(!isEntityOnFloor(hitbox,lvlData)){
            inAir=true;
        }
        firstUpdate = false;
    }

    protected void updateInAir(int [][] lvlData){
            if (CanMoveHere(hitbox.x, hitbox.y + fallSpeed, hitbox.width, hitbox.height, lvlData)) {
                hitbox.y+=fallSpeed;
                fallSpeed+= gravity;
            }
            else{
                inAir = false;
                hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, fallSpeed);
                tileY = (int)(hitbox.y/Game.TILES_SIZE);
            }
    }

    protected void move(int [][] lvlData){
        float xSpeed = 0;

        if(walkDir == LEFT) {
            xSpeed = -walkSpeed;
        }
        else{
            xSpeed = walkSpeed;
        }

        if(CanMoveHere(hitbox.x + xSpeed,hitbox.y,hitbox.width,hitbox.height,lvlData)){
            if(IsFloor(hitbox, xSpeed,walkDir, lvlData)){//ha a szakadek stb. szelen van
                hitbox.x +=xSpeed;
                return;
            }
        }

        changeWalkDir();
    }

    protected void turnTowardsPlayer(Player player){
        if(player.hitbox.x > hitbox.x){
            walkDir = RIGHT;
        }
        else {
            walkDir = LEFT;
        }
    }

    protected boolean canSeePlayer(int[][] lvlData, Player player){
        //ugyan azon a szinten kell legyenek, y azonos
        //enemy mindig eredeti y-on marad es nem valtozik
        int playerTileY=(int)(player.getHitbox().y/Game.TILES_SIZE);
        if(playerTileY == tileY){//egy szinten vannak;
            if(isPlayerInRange(player)){
                if(IsSightClear(lvlData, hitbox, player.hitbox, tileY)){//ha mondjuk arok van kozottuk
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isPlayerInRange(Player player) {
        //2 range, egy attack es egy latohatar

        int absDist = (int)(Math.abs(player.hitbox.x - hitbox.x));
        return absDist <= attackDistance * 4;
    }

    protected void newState(int enemyState){
        this.enemyState=enemyState;
        aniTick = 0;
        aniIndex = 0;
    }

    protected boolean isPlayerCloseForAttack(Player player){
        int absDist = (int)(Math.abs(player.hitbox.x - hitbox.x));
        return absDist <= attackDistance;
    }

    protected void changeWalkDir() {
        if(walkDir==LEFT){
            walkDir=RIGHT;
        }
        else{
            walkDir=LEFT;
        }
    }

    public int getAniIndex(){
        return aniIndex;
    }

    public int getEnemyState(){
        return enemyState;
    }

    public void hurt(int amount){
        System.out.println("Got hurt!");
        currentHealth -=amount;
        if(currentHealth <= 0){
            newState(DEAD);
        }
        else{
            newState(HIT);
        }
    }

    public boolean isActive(){
        return active;
    }

    protected void checkEnemyHit(Rectangle2D.Float attackBox,Player player) {
       if(attackBox.intersects(player.hitbox)){
           player.changeHealth(-GetEnemyDamage(enemyType));//azert mert a playerben
       }
       attackChecked = true;
    }

    public void resetEnemy(){
        hitbox.x=x;
        hitbox.y=y;
        currentHealth = maxHealth;
        active=true;
        firstUpdate = true;
        newState(IDLE);
        fallSpeed=0;

    }

}
