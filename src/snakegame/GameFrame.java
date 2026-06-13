package snakegame;

import javax.swing.JFrame;

public class GameFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    StartScreen startScreen;
    GamePanel   gamePanel;
    GamePanel2P gamePanel2P;

    public GameFrame() {
        startScreen = new StartScreen(this);
        this.add(startScreen);

        this.setTitle("Snake Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    // Chuyển sang chế độ 1 người
    public void startGame() {
        this.getContentPane().removeAll();
        gamePanel = new GamePanel();
        this.add(gamePanel);
        this.revalidate();
        this.repaint();
        gamePanel.requestFocus();
    }

    // THÊM MỚI: Chuyển sang chế độ 2 người
    public void startGame2P() {
        this.getContentPane().removeAll();
        gamePanel2P = new GamePanel2P();
        this.add(gamePanel2P);
        this.revalidate();
        this.repaint();
        gamePanel2P.requestFocus();
    }

    // THÊM MỚI: Quay về Start Screen (từ Game Over 2P)
    public void showStartScreen() {
        this.getContentPane().removeAll();
        startScreen = new StartScreen(this);
        this.add(startScreen);
        this.revalidate();
        this.repaint();
        this.pack();
        this.setLocationRelativeTo(null);
    }
}