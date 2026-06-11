package snakegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final int SCREEN_WIDTH = 600;
    private final int SCREEN_HEIGHT = 600;
    private final int DELAY = 100;

    static int highScore = 0;

    private Snake snake;
    private Food food;
    private Timer timer;
    private boolean running = false;
    private int score = 0;

    public GamePanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        startGame();
    }

    public void startGame() {
        snake = new Snake();
        food = new Food();
        running = true;
        score = 0;

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (running) {
            draw(g);
        } else {
            gameOver(g);
        }
    }

    public void draw(Graphics g) {

        // vẽ food
        g.setColor(Color.red);
        g.fillOval(food.getX(), food.getY(), 25, 25);

        // vẽ snake
        for (int i = 0; i < snake.getBodyParts(); i++) {
            if (i == 0) {
                g.setColor(Color.green);
            } else {
                g.setColor(new Color(45, 180, 0));
            }
            g.fillRect(snake.getX().get(i), snake.getY().get(i), 25, 25);
        }

        // vẽ score
        g.setColor(Color.white);
        g.setFont(new Font("Ink Free", Font.BOLD, 25));
        g.drawString("Score: " + score, 10, 30);
        g.drawString("High Score: " + highScore, 10, 60);
    }

    public void move() {
        snake.move();
    }

    public void checkFood() {
        if (snake.getX().get(0) == food.getX() &&
            snake.getY().get(0) == food.getY()) {

            snake.grow();
            food.randomize();

            score += 10; // chỉ cộng 1 lần

            Sound.play("eat.wav.wav");
        }
    }

    public void checkCollision() {
        if (snake.checkCollision()) {
            running = false;
            timer.stop();

            // cập nhật high score
            if (score > highScore) {
                highScore = score;
            }

            Sound.play("gameover.wav.wav");
        }
    }

    public void gameOver(Graphics g) {

        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        g.drawString("Game Over", 180, 250);

        g.setFont(new Font("Ink Free", Font.BOLD, 25));
        g.drawString("Score: " + score, 240, 320);
        g.drawString("High Score: " + highScore, 210, 360);

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Press ENTER to Restart", 170, 420);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkCollision();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (snake.getDirection() != 'R')
                        snake.setDirection('L');
                    break;

                case KeyEvent.VK_RIGHT:
                    if (snake.getDirection() != 'L')
                        snake.setDirection('R');
                    break;

                case KeyEvent.VK_UP:
                    if (snake.getDirection() != 'D')
                        snake.setDirection('U');
                    break;

                case KeyEvent.VK_DOWN:
                    if (snake.getDirection() != 'U')
                        snake.setDirection('D');
                    break;

                case KeyEvent.VK_ENTER:
                    if (!running) {
                        startGame();
                    }
                    break;
            }
        }
    }
}