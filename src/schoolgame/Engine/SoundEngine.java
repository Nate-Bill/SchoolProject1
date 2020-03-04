/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgame.Engine;

import javax.sound.sampled.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author 14NBill
 */
public class SoundEngine {

    public void Play(String URI) {
        new Thread(() -> {
            try {
                Thread.currentThread().setName("AudioPlayer");
                Clip clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(this.getClass().getResource(URI)));
                clip.start();
                clip.addLineListener(myLineEvent -> {
                    if (myLineEvent.getType() == LineEvent.Type.STOP)
                        new Thread(() -> {
                            try {
                                Thread.sleep(1000);
                                clip.close();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }).start();
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}
