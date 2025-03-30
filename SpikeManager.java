package entities;

import gamestates.Playing;
import utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utilz.Constants.Spike_Constants.*;

public class SpikesManager {
    private Playing playing;
    private ArrayList<Spike> spikes;
    private BufferedImage spikeImg;

    public SpikesManager(Playing playing){
        this.playing = playing;
        loadImgs();
    }

    private void loadImgs(){
        spikeImg = LoadSave.GetSpriteAtlas(LoadSave.TRAP_SPIKES);
        //spikes = GetSpikes(spikeImg);
        //spikes = GetSpikes(img);
    }

//    public void update(){
//       spikes.update();
//    }

    public void draw(Graphics g, int xLvlOffset){
        drawTraps(g,xLvlOffset);
    }

    private void drawTraps(Graphics g, int xLvlOffset) {
        for(Spike s: spikes){
            g.drawImage(spikeImg, (int)(s.getHitbox().x - xLvlOffset),(int)(s.getHitbox().y - s.getyOffset()),SPIKE_WIDTH,SPIKE_HEIGHT,null );
        }
    }

    //public void loadObjects(Level newLevel){
    //spikes = newLevel.getSpikes();
}

public void checkSpikesTouched(Player player){
    for(Spike s:spikes){
        if(s.getHitbox().intersects(player.getHitbox())){
            player.kill();
        }
    }
}

}
