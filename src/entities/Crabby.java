package entities;

import main.Game;

import java.awt.geom.Rectangle2D;

import static utilz.Constants.Directions.RIGHT;
import static utilz.Constants.EnemyConstants.*;

public class Crabby extends Enemy{

    private Rectangle2D.Float attackBox;
    private int attackBoxXOffset;

    public Crabby(float x, float y) {
        super(x, y, CRABBY_WIDTH, CRABBY_HEIGHT, CRABBY);//csak az x es a y fog valtozni
        initHitbox(x,y,(int)(22* Game.SCALE),(int)(19*Game.SCALE));//a crab merete alapjan
        initAttackBox();
    }

    private void initAttackBox() {
          attackBox = new Rectangle2D.Float(x,y,(int)(82*Game.SCALE),(int)(19*Game.SCALE));//a crab-nek a hitboxja hosszu, mert kifele ut a csapjaival, magassag marad
          attackBoxXOffset = (int)(Game.SCALE*30);//egy csapjat 30 pixelre nyujtja a testtol(22 pixel)
    }

    public void update(int[][] lvlData, Player player){
        updateBehaviour(lvlData,player);
        updateAnimationTick();
        updateAttackBox();
    }

    private void updateAttackBox(){
        attackBox.x = hitbox.x - attackBoxXOffset;
        attackBox.y = hitbox.y;
    }

    private void updateBehaviour(int[][] lvlData, Player player){
        //System.out.println("state " + enemyState);

        if(firstUpdate){
            firstUpdateCheck(lvlData);
        }
        if(inAir) {
            updateInAir(lvlData);
        }
        else{

            switch (enemyState){
                case IDLE:
                    newState(RUNNING);
                    break;
                case RUNNING:

                    if(canSeePlayer(lvlData, player)){
                        turnTowardsPlayer(player);
                        if(isPlayerCloseForAttack(player)){
                            newState(ATTACK);
                        }
                    }

                    move(lvlData);
                    break;
                case ATTACK:
                    if(aniIndex == 0){
                        attackChecked = false;
                    }
                    if(aniIndex == 3 && !attackChecked){//az animacio kozepen megnezzuk, hogy ervenyes-e az attack
                          checkEnemyHit(attackBox,player);
                    }
                    break;
                case HIT:
                    break;
            }
        }
    }

    public int flipX(){
        if(walkDir == RIGHT){//alapjaraton a crab jobbra nez spreadsheet szerint
            return width;
        }
        else {
            return 0;
        }
    }
    public int flipW(){
        if(walkDir == RIGHT){//itt is azert
            return -1;
        }
        else {
            return 1;
        }
    }

//    public void drawAttackBox(Graphics g, int LvlXOffset){
//        g.setColor(Color.RED);
//        g.drawRect((int)(attackBox.x - LvlXOffset),(int)attackBox.y,(int)attackBox.width,(int)attackBox.height);
//    }
}
