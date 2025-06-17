package helpers;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class AudioPlayer {
    private Clip clip;

    public AudioPlayer(String filePath) {
        try {
            URL url = this.getClass().getResource(filePath);
            if (url == null) {
                throw new RuntimeException("Error: Audio file not found at " + filePath);
            }
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioInput);
        } catch (Exception e) {
            System.err.println("Error loading audio file: " + filePath);
            e.printStackTrace();
        }
    }

    public void loop() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}