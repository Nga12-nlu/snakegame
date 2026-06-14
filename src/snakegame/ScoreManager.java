package snakegame;

import java.io.*;

/**
 * ScoreManager – Lưu/đọc điểm cao nhất ra file để không mất khi tắt game.
 * File lưu tại: <thư mục chạy game>/snake_highscore.txt
 */
public class ScoreManager {

    private static final String FILE_NAME = "snake_highscore.txt";

  
    public static int loadHighScore() {
        File f = new File(FILE_NAME);
        if (!f.exists()) return 0;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = br.readLine();
            if (line != null) {
                return Integer.parseInt(line.trim());
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Không thể đọc highscore: " + e.getMessage());
        }
        return 0;
    }


    public static void saveHighScore(int score) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            bw.write(String.valueOf(score));
        } catch (IOException e) {
            System.out.println("Không thể lưu highscore: " + e.getMessage());
        }
    }
}