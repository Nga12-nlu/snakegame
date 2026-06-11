package snakegame;

import javax.sound.sampled.*;
import java.net.URL;

public class Sound {

    public static void play(String fileName) {
        try {
            URL url = Sound.class.getResource("/sound/" + fileName);

            if (url == null) {
                System.out.println("Không tìm thấy file âm thanh!");
                return;
            }

            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}