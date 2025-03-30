package utilz;

import entities.Crabby;
import main.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.stream.IntStream;

import static utilz.Constants.EnemyConstants.CRABBY;

public class LoadSave {

    public static final String PLAYER_ATLAS = "player_sprites.png";
    public static final String LEVEL_ATLAS = "outside_sprites.png";
    //public static final String LEVEL_ONE_DATA = "level_one_data.png";
    public static final String LEVEL_ONE_DATA = "1.png";//level_one_data_long
    public static final String MENU_BUTTONS = "button_atlas.png";
    public static final String MENU_BACKGROUND = "menu_background.png";
    public static final String PAUSE_BACKGROUND = "pause_menu.png";
    public static final String SOUND_BUTTONS = "sound_button.png";
    public static final String URM_BUTTONS = "urm_buttons.png";
    public static final String VOLUME_BUTTONS = "volume_buttons.png";
    public static final String MENU_BACKGROUND_IMG = "welcome_background.png";
    public static final String PLAYING_BACKGROUND_IMG = "water.png";//moving background kep
    public static final String BIG_CLOUDS = "big_clouds.png";
    public static final String SMALL_CLOUDS = "small_clouds.png";
    public static final String CRABBY_SPRITE = "crabby_sprite.png";
    public static final String STATUS_BAR = "health_power_bar.png";
    public static final String TRAP_SPIKES = "mypixelart_spikes.png";
    public static final String COIN_PIXELART = "mypixelart_coin.png";
    public static final String FLAG_PIXELART = "mypixelart_flag.png";

    public static BufferedImage GetSpriteAtlas(String fileName){
        BufferedImage img = null;

        InputStream is = LoadSave.class.getResourceAsStream("/" + fileName);
        try {
            //assert is != null;//hogy ne legyen is = null
            img = ImageIO.read(is);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                is.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return img;
    }

    public static ArrayList<Crabby> GetCrabs(){
        BufferedImage img = GetSpriteAtlas(LEVEL_ONE_DATA);
        ArrayList<Crabby> list = new ArrayList<>();//a crab-oknak a list-je

        //minden "item-nek megfeleltetunk majd egy szinkodot/ranget valamelyik alapszinbol s a pixelartos map-en azzal a costumized szinnel fogjuk jelolni oket.
        IntStream.range(0, img.getHeight())
                .forEach(i -> IntStream.range(0, img.getWidth())
                        .forEach(j -> {
                            Color color = new Color(img.getRGB(j, i));
                            int value = color.getGreen(); // A zöld színkódot kapjuk itt meg
                            if (value == CRABBY) { // Ha a színkód megegyezik a CRABBY értékkel
                                list.add(new Crabby(j * Game.TILES_SIZE, i * Game.TILES_SIZE));
                            }
                        }));

        return list;

    }


    public static int[][] GetLevelData(){
        BufferedImage img =GetSpriteAtlas(LEVEL_ONE_DATA);
        int[][] lvlData=new int[img.getHeight()][img.getWidth()];

        IntStream.range(0, img.getHeight())
                .forEach(i -> IntStream.range(0, img.getWidth())
                        .forEach(j -> {
                            Color color = new Color(img.getRGB(j, i));
                            int value = color.getRed(); // A piros színkódért
                            if (value >= 48) { // Ha a színkód nem érvényes
                                value = 0;
                            }
                            lvlData[i][j] = value;
                        }));
        return lvlData;
    }
}
