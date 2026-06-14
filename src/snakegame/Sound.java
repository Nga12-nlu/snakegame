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

    // THÊM MỚI: trạng thái tắt/mở âm thanh (toàn cục)
    private static boolean muted = false;

    public static void play(String fileName) {
        if (muted) return;   // THÊM MỚI: không phát nếu đang tắt âm

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

    // THÊM MỚI: bật/tắt âm thanh
    public static void setMuted(boolean m) {
        muted = m;
    }

    public static boolean isMuted() {
        return muted;
    }

    // THÊM MỚI: đảo trạng thái, trả về trạng thái mới
    public static boolean toggleMuted() {
        muted = !muted;
        return muted;
    }
}