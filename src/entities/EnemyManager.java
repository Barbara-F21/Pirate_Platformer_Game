package entities;

import gamestates.Playing;
import utilz.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utilz.Constants.EnemyConstants.*;

public class EnemyManager {

    private Playing playing;
    private BufferedImage[][] crabbyArr;
    private ArrayList<Crabby> crabbies = new ArrayList<>();

    public EnemyManager(Playing playing){
        this.playing=playing;
        loadEnemyImgs();
        addEnemies();
    }

    private void addEnemies() {
        crabbies=LoadSave.GetCrabs();
    }

    public void update(int[][] lvlData, Player player){
        for(Crabby c : crabbies){
            if(c.isActive()){
                c.update(lvlData, player);
            }
        }
    }

    public void draw(Graphics g, int xLvlOffset){
         drawCrabs(g, xLvlOffset);
    }

    private void drawCrabs(Graphics g, int xLvlOffset) {
        for(Crabby c: crabbies){
            if(c.isActive()){
                g.drawImage(crabbyArr[c.getEnemyState()][c.getAniIndex()], (int)(c.getHitbox().x - xLvlOffset - CRABBY_DRAWOFFSET_X) + c.flipX(), (int)(c.getHitbox().y - CRABBY_DRAWOFFSET_Y),CRABBY_WIDTH * c.flipW(),CRABBY_HEIGHT,null);
                //c.drawAttackBox(g,xLvlOffset);
            }
        }
    }

    private void loadEnemyImgs() {
        crabbyArr=new BufferedImage[5][9];
        BufferedImage tmp= LoadSave.GetSpriteAtlas(LoadSave.CRABBY_SPRITE);

        for(int i=0;i<crabbyArr.length;i++){
            for(int j=0;j<crabbyArr[i].length;j++){
                crabbyArr[i][j] = tmp.getSubimage(j*CRABBY_WIDTH_DEFAULT,i*CRABBY_HEIGHT_DEFAULT, CRABBY_WIDTH_DEFAULT,CRABBY_HEIGHT_DEFAULT);
            }
        }
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox){
         for( Crabby c : crabbies){
             if(c.isActive()){
                 if(attackBox.intersects(c.getHitbox())){
                     System.out.println("Ouch!");
                     c.hurt(10);
                     return;
                 }
             }
         }
    }

    public void resetAllEnemies(){
        for(Crabby c: crabbies){
            c.resetEnemy();
        }
    }
}
