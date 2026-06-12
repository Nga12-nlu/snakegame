package snakegame;

import javax.sound.sampled.*;
import java.net.URL;

public class Sound {

    // ===== ĐỔI TÊN FILE ÂM THANH TẠI ĐÂY =====
    public static final String EAT      = "eat.wav";
    public static final String GAME_OVER = "gameover.wav";
    public static final String BONUS    = "eat.wav";   // thay bằng file khác nếu có
    public static final String POISON   = "gameover.wav"; // thay bằng file khác nếu có
    // ============================================

    public static void play(String fileName) {
        // Chạy trong thread riêng để không lag game
        new Thread(() -> {
            try {
                URL url = Sound.class.getResource("/sound/" + fileName);
                if (url == null) {
                    System.out.println("Không tìm thấy âm thanh: " + fileName);
                    return;
                }
                AudioInputStream audio = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audio);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}