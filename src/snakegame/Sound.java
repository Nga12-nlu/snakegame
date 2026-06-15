package snakegame;

import javax.sound.sampled.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Sound {
    // File names
    public static final String EAT       = "eat.wav";
    public static final String GAME_OVER = "gameover.wav";
    public static final String BONUS     = "eat.wav";
    public static final String POISON    = "gameover.wav";
    public static final String BACKGROUND = "background.wav"; // thêm nhạc nền

    private static boolean muted = false;
    private static Clip backgroundClip = null; // để quản lý nhạc nền
    private static final List<Clip> activeClips = new ArrayList<>(); // quản lý các clip đang phát

    // Phát âm thanh hiệu ứng (không lặp lại)
    public static void play(String fileName) {
        if (muted) return;

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
                
                // Thêm vào danh sách quản lý
                synchronized (activeClips) {
                    activeClips.add(clip);
                }
                
                // Tự động xóa khỏi danh sách khi phát xong
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                        synchronized (activeClips) {
                            activeClips.remove(clip);
                        }
                    }
                });
                
                clip.start();
            } catch (Exception e) {
                System.err.println("Lỗi phát âm thanh " + fileName + ": " + e.getMessage());
            }
        }).start();
    }
    
    // Phát nhạc nền (lặp lại)
    public static void playBackground(String fileName) {
        if (muted) {
            stopBackground();
            return;
        }
        
        stopBackground(); // dừng nhạc nền cũ nếu có
        
        new Thread(() -> {
            try {
                URL url = Sound.class.getResource("/sound/" + fileName);
                if (url == null) {
                    System.out.println("Không tìm thấy nhạc nền: " + fileName);
                    return;
                }
                
                AudioInputStream audio = AudioSystem.getAudioInputStream(url);
                backgroundClip = AudioSystem.getClip();
                backgroundClip.open(audio);
                backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
                backgroundClip.start();
            } catch (Exception e) {
                System.err.println("Lỗi phát nhạc nền: " + e.getMessage());
            }
        }).start();
    }
    
    // Dừng nhạc nền
    public static void stopBackground() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
            backgroundClip.close();
            backgroundClip = null;
        }
    }
    
    // Dừng tất cả âm thanh đang phát
    public static void stopAll() {
        stopBackground();
        
        synchronized (activeClips) {
            for (Clip clip : activeClips) {
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.close();
            }
            activeClips.clear();
        }
    }

    // Bật/tắt âm thanh
    public static void setMuted(boolean m) {
        if (muted == m) return;
        
        muted = m;
        
        if (muted) {
            stopAll(); // tắt âm thanh đang phát
        }
    }

    public static boolean isMuted() {
        return muted;
    }

    public static boolean toggleMuted() {
        setMuted(!muted);
        return muted;
    }
}
