/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgame.Engine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 *
 * @author 14NBill
 */
public class SoundEngine {

    public void Play(String URI) {
        new Thread(() -> {
            try {
                InputStream in = new FileInputStream(this.getClass().getResource(URI).getFile());
                AudioStream audioStream = new AudioStream(in);
                AudioPlayer.player.start(audioStream);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}
