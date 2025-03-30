package utilz;

import main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utilz.Constants.Directions.LEFT;
import static utilz.Constants.Spike_Constants.SPIKE;
//import static entities.Spike.*;

public class HelpMethods {

    public static boolean CanMoveHere(float x,float y, float width, float height, int[][] lvlData){

        //System.out.println(x + " " + y );
        if(!IsSolid(x,y,lvlData)){
            if(!IsSolid(x+width,y+height,lvlData)){
                if(!IsSolid(x+width,y,lvlData)){
                    if(!IsSolid(x,y+height,lvlData)){//tehat mozoghatunk
                        return true;
                    }
                }
            }
        }
        System.out.println("Nem Mehet!");
        return false;
    }

    private static boolean IsSolid(float x, float y, int[][] lvlData) {//azt is, hogy a gamewindowban van-e

        int maxWidth=lvlData[0].length * Game.TILES_SIZE;
        if(x<0 || x>= maxWidth){
            return true;
        }
        if(y<0 || y>= Game.GAME_HEIGHT){
            return true;
        }

        float xIndex = x/Game.TILES_SIZE;
        float yIndex = y/Game.TILES_SIZE;

        return IsTileSolid((int)xIndex,(int)yIndex, lvlData);
    }

    public static boolean IsTileSolid(int xTile, int yTile, int[][] lvlData){
        int value = lvlData[yTile][xTile];

        if(value >= 48 || value <0 || value != 11){// a sheeten van egy ures block s az miatt van az a value==11
            return true;
        }
        return false;
    }

    public static float GetEntityXPosNextToWall(Rectangle2D.Float hitbox, float xSpeed) {

         //hitbox kisebb mint egy tile/block
        //xSpeed nem lesz 0, mert akkor nem lenne collision

        int currentTile= (int)(hitbox.x/Game.TILES_SIZE);
        if(xSpeed>0){
            //Right
            int tileXPos = currentTile * Game.TILES_SIZE;
            int xOffset = (int)(Game.TILES_SIZE - hitbox.width);
            return tileXPos + xOffset - 1;//hogy a legszele ne csusszon bele
        }
        else {
            //Left
            return currentTile * Game.TILES_SIZE;
        }
    }
    public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox,float airSpeed){
        int currentTile= (int)(hitbox.y/Game.TILES_SIZE);
        if(airSpeed > 0){
            //falling - touching floor
            int tileYPos=currentTile * Game.TILES_SIZE;
            int yOffset = (int)(Game.TILES_SIZE - hitbox.height);
            return tileYPos + yOffset - 1;
        }
        else {
            //jumping
            return currentTile * Game.TILES_SIZE;
        }
    }
    public static boolean isEntityOnFloor(Rectangle2D.Float hitbox,int[][] lvlData){
        //check the pixel below bottomleft and bottomright
        if(!IsSolid(hitbox.x, hitbox.y+hitbox.height + 1, lvlData)){//+1 a bottom left sarokert
          if(!IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData)){//+1 a bottom right cornerért
              return false;
          }
        }
        return true;
    }

    public static boolean IsFloor(Rectangle2D.Float hitbox,float xSpeed,float walkDir, int[][] lvlData){
        if(walkDir == LEFT){
            return IsSolid(hitbox.x + xSpeed, hitbox.y + hitbox.height + 1, lvlData);//a hitbox alatti pixelert van + 1
        }
        else{
            return IsSolid(hitbox.x + hitbox.width + xSpeed, hitbox.y + + hitbox.height + 1, lvlData);//a hitbox alatti pixelert van + 1
        }

    }

    public static boolean IsAllTilesWalkable(int xStart, int xEnd, int y, int[][] lvlData){
        for(int i=0;i<(xEnd - xStart);i++) {
            if (IsTileSolid(xStart + i, y, lvlData)) {
                return false;
            }
            if(!IsTileSolid(xStart + i, y + 1, lvlData)){
                return false;
            }
        }
        return true;
    }

    public static boolean IsSightClear(int[][] lvlData, Rectangle2D.Float hitbox1, Rectangle2D.Float hitbox2, int tileY) {//azert itt es nem az Enemy classben, mert "in general" hasznalhato ez, 2 pont kozott tavolsag

        int XTile1 = (int)(hitbox1.x/Game.TILES_SIZE);
        int XTile2 = (int)(hitbox2.x/Game.TILES_SIZE);

        //csak a block-ok szelesseget, tiles- okat nezzuk es nem pl. 5 pixelenkent, így gyorsabb lesz

        if(XTile1 > XTile2){
            return IsAllTilesWalkable(XTile2,XTile1,tileY,lvlData);
        }
        else {
            return IsAllTilesWalkable(XTile1,XTile2,tileY,lvlData);
        }
    }
}
