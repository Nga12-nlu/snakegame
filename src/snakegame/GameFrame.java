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

    public void startGame() {
        this.getContentPane().removeAll();
        gamePanel = new GamePanel(false);
        this.add(gamePanel);
        this.revalidate();
        this.pack();
        this.setLocationRelativeTo(null);
        this.repaint();
        gamePanel.requestFocus();
    }

    public void startGameVsBot() {
        this.getContentPane().removeAll();
        gamePanel = new GamePanel(true);
        this.add(gamePanel);
        this.revalidate();
        this.pack();
        this.setLocationRelativeTo(null);
        this.repaint();
        gamePanel.requestFocus();
    }

    public void startGame2P() {
        this.getContentPane().removeAll();
        gamePanel2P = new GamePanel2P(false);
        this.add(gamePanel2P);
        this.revalidate();
        this.pack();
        this.setLocationRelativeTo(null);
        this.repaint();
        gamePanel2P.requestFocus();
    }

    public void startGame2PVsBot() {
        this.getContentPane().removeAll();
        gamePanel2P = new GamePanel2P(true);
        this.add(gamePanel2P);
        this.revalidate();
        this.pack();
        this.setLocationRelativeTo(null);
        this.repaint();
        gamePanel2P.requestFocus();
    }

    public void showStartScreen() {
        this.getContentPane().removeAll();
        startScreen = new StartScreen(this);
        this.add(startScreen);
        this.revalidate();
        this.pack();
        this.setLocationRelativeTo(null);
        this.repaint();
    }
}