package entities;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class Entity {

    protected float x;
    protected float y;//csak az extended-ek fogjak tudni meg hasznalni
    protected int width, height;
    protected Rectangle2D.Float hitbox; //hitbox, hogy ne menjen neki semminek

    public Entity(float x, float y, int width, int height){
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
    }

//    protected void drawHitbox(Graphics g,int xLvlOffset){
//        // for debugging the hitbox
//        g.setColor(Color.PINK);
//        g.drawRect((int)hitbox.x - xLvlOffset,(int)hitbox.y,(int)hitbox.width, (int)hitbox.height);
//    }

    protected void initHitbox(float x, float y, int width, int height) {// a scaling miatt kell int
        hitbox=new Rectangle2D.Float(x,y,width,height);
    }

    public Rectangle2D.Float getHitbox(){
        return hitbox;
    }
}

