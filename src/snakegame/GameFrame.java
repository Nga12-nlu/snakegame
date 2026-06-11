package snakegame;

import javax.swing.JFrame;

public class GameFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    StartScreen startScreen;
    GamePanel gamePanel;

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

    // Hàm chuyển sang game
    public void startGame() {
        this.getContentPane().removeAll();

        gamePanel = new GamePanel();
        this.add(gamePanel);

        this.revalidate();
        this.repaint();
        gamePanel.requestFocus();
    }
}