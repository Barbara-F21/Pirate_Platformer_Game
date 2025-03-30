package main;

import Inputs.KeyboardInputs;
import Inputs.MouseInputs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static main.Game.GAME_HEIGHT;
import static main.Game.GAME_WIDTH;
import static utilz.Constants.PlayerConstants.*;
import static utilz.Constants.*;
import static utilz.Constants.Directions.*;

public class GamePanel extends JPanel{

    private MouseInputs mouseInputs;
    private Game game;

    public GamePanel(Game game){

        mouseInputs = new MouseInputs(this);
        this.game=game;

        setPanelSize();
        addKeyListener(new KeyboardInputs(this));//atadja az aktualis gamepanelt
        addMouseListener(mouseInputs);//klick, lenyomva, elengedve
        addMouseMotionListener(mouseInputs);//mozgatos
    }

    private void setPanelSize() {//azert itt lesz az ablak beallitva itt és nem a Gamewindowban, mert beleszamolja a top bart is s nem lesz akkor a pont a direkt jatekablak, mint amekkorara allitottuk.
        Dimension size = new Dimension(GAME_WIDTH,GAME_HEIGHT);
        setPreferredSize(size);
        System.out.println("size: "+GAME_WIDTH + " : "+GAME_HEIGHT);
    }

    public void updateGame(){

    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);//e nelkul fura viselkedes, visszamaradt previous frame
        game.render(g);
    }

    public Game getGame(){
        return game;
    }
}
