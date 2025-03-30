package ui;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundManager {

    private static Clip backgroundClip;
    private static Clip sfxClip;
    private FloatControl bgVolumeControl;

    public SoundManager() throws UnsupportedAudioFileException, IOException, LineUnavailableException {

        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("sounds/pirates_of_the_caribean.wav"))) {
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioInputStream);
        }


        bgVolumeControl = (FloatControl) backgroundClip.getControl(FloatControl.Type.MASTER_GAIN);

        AudioInputStream audioInputStreamSFX = AudioSystem.getAudioInputStream(new File("sounds/punch_enemy.wav"));
        sfxClip = AudioSystem.getClip();
        sfxClip.open(audioInputStreamSFX);
    }

    public void startBackGroundMusic() {
        backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stopBackGroundMusic(){
        backgroundClip.stop();
    }

    public void startSFX(){
        sfxClip.stop();
        sfxClip.start();
        sfxClip.setFramePosition(0);//kezdje mindig a legeslegelejerol;
    }

    public void setVolume(float volume) {
        // Volume: 0.0 (minimum) - 1.0 (maximum)
        //Ahhoz, hogy jobban mukodjon fel kell huzni a hangerot 100-ra a gepen
        float backGroundClip = bgVolumeControl.getMinimum();
        float max = bgVolumeControl.getMaximum();
        float scaledVolume = backGroundClip + (max - backGroundClip) * volume;
        System.out.println("MIN:" + bgVolumeControl.getMinimum());
        System.out.println("MAX:" + bgVolumeControl.getMaximum());
        System.out.println("SCALED:" + scaledVolume);

        bgVolumeControl.setValue(scaledVolume);
        if (backgroundClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl volumeControl = (FloatControl) backgroundClip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(scaledVolume);
        } else {
            System.out.println("MASTER_GAIN control is not supported.");
        }
    }

}
