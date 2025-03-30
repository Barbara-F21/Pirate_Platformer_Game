package levels;

import utilz.HelpMethods;
import utilz.LoadSave;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Level {

    private int[][] lvlData;
    private BufferedImage img;

    public Level(int[][] lvlData){
        this.lvlData = lvlData;
    }

    public int getSpriteIndex(int x,int y){
        return lvlData[y][x];
    }

    public int[][] getLevelData(){
        return lvlData;
    }
}
