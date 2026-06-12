package snakegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final int SCREEN_WIDTH  = 600;
    private final int SCREEN_HEIGHT = 600;
    private final int TILE_SIZE     = 25;
    private final int BASE_DELAY    = 120;

    static int highScore = 0;

    private Snake snake;
    private Food normalFood;
    private Food specialFood;   // bonus hoặc poison, xuất hiện ngẫu nhiên

    private Timer timer;
    private boolean running  = false;
    private boolean paused   = false;
    private int score        = 0;
    private int lives        = 3;
    private int level        = 1;

    private Random random = new Random();

    // Flash effect khi mất mạng
    private boolean flashing = false;
    private int flashCount   = 0;

    public GamePanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        snake     = new Snake();
        normalFood = new Food(FoodType.NORMAL);
        specialFood = null;
        running   = true;
        paused    = false;
        score     = 0;
        lives     = 3;
        level     = 1;
        flashing  = false;

        if (timer != null) timer.stop();
        timer = new Timer(BASE_DELAY, this);
        timer.start();
    }

    private void loseLife() {
        lives--;
        Sound.play(Sound.GAME_OVER);
        if (lives <= 0) {
            running = false;
            timer.stop();
            if (score > highScore) highScore = score;
        } else {
            // Reset rắn nhưng giữ điểm
            snake.reset();
            flashing  = true;
            flashCount = 0;
        }
    }

    private void spawnSpecialFood() {
        // 30% spawn bonus, 20% spawn poison
        int r = random.nextInt(100);
        if (r < 30) {
            specialFood = new Food(FoodType.BONUS);
        } else if (r < 50) {
            specialFood = new Food(FoodType.POISON);
        }
    }

    private void updateLevel() {
        int newLevel = score / 50 + 1;
        if (newLevel != level) {
            level = newLevel;
            timer.setDelay(Math.max(50, BASE_DELAY - (level - 1) * 10));
        }
    }

    // ===== VẼ =====

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (running) {
            drawBackground(g2);
            drawFood(g2);
            drawSnake(g2);
            drawHUD(g2);
            if (paused) drawPause(g2);
        } else {
            drawBackground(g2);
            drawGameOver(g2);
        }
    }

    private void drawBackground(Graphics2D g) {
        // Lưới nhẹ
        g.setColor(new Color(20, 20, 20));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(new Color(35, 35, 35));
        for (int i = 0; i < SCREEN_WIDTH; i += TILE_SIZE)
            g.drawLine(i, 0, i, SCREEN_HEIGHT);
        for (int j = 0; j < SCREEN_HEIGHT; j += TILE_SIZE)
            g.drawLine(0, j, SCREEN_WIDTH, j);
    }

    private void drawFood(Graphics2D g) {
        // Normal food — hình tròn đỏ có viền sáng
        drawFoodItem(g, normalFood);
        if (specialFood != null) drawFoodItem(g, specialFood);
    }

    private void drawFoodItem(Graphics2D g, Food food) {
        int fx = food.getX(), fy = food.getY();
        Color c = food.getType().color;
        // Glow
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 60));
        g.fillOval(fx - 4, fy - 4, 33, 33);
        // Body
        g.setColor(c);
        g.fillOval(fx + 2, fy + 2, 21, 21);
        // Shine
        g.setColor(new Color(255, 255, 255, 120));
        g.fillOval(fx + 5, fy + 4, 7, 7);
    }

    private void drawSnake(Graphics2D g) {
        // Flash effect khi mất mạng
        if (flashing && (flashCount / 2) % 2 == 1) return;

        for (int i = 0; i < snake.getBodyParts(); i++) {
            int sx = snake.getX().get(i);
            int sy = snake.getY().get(i);

            if (i == 0) {
                // Đầu rắn
                g.setColor(new Color(0, 220, 80));
                g.fillRoundRect(sx + 1, sy + 1, 23, 23, 8, 8);
                // Mắt
                g.setColor(Color.BLACK);
                char dir = snake.getDirection();
                if (dir == 'R') { g.fillOval(sx+16, sy+5, 5, 5); g.fillOval(sx+16, sy+15, 5, 5); }
                else if (dir == 'L') { g.fillOval(sx+4, sy+5, 5, 5); g.fillOval(sx+4, sy+15, 5, 5); }
                else if (dir == 'U') { g.fillOval(sx+5, sy+4, 5, 5); g.fillOval(sx+15, sy+4, 5, 5); }
                else { g.fillOval(sx+5, sy+16, 5, 5); g.fillOval(sx+15, sy+16, 5, 5); }
            } else {
                // Thân rắn gradient xanh
                float t = (float) i / snake.getBodyParts();
                int green = (int)(180 - t * 60);
                g.setColor(new Color(0, green, 30));
                g.fillRoundRect(sx + 2, sy + 2, 21, 21, 6, 6);
            }
        }
    }

    private void drawHUD(Graphics2D g) {
        // Thanh HUD nền mờ phía trên
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, SCREEN_WIDTH, 45);

        g.setFont(new Font("Consolas", Font.BOLD, 16));
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 28);

        g.setColor(new Color(255, 215, 0));
        g.drawString("Best: " + highScore, 150, 28);

        g.setColor(new Color(100, 200, 255));
        g.drawString("Lv." + level, 300, 28);

        // Trái tim
        for (int i = 0; i < 3; i++) {
            g.setColor(i < lives ? new Color(255, 60, 60) : new Color(80, 80, 80));
            g.drawString("♥", 410 + i * 30, 28);
        }

        // Ghi chú special food
        if (specialFood != null) {
            String hint = specialFood.getType() == FoodType.BONUS ? "★ BONUS +30!" : "☠ POISON!";
            g.setColor(specialFood.getType().color);
            g.setFont(new Font("Consolas", Font.BOLD, 13));
            g.drawString(hint, 10, SCREEN_HEIGHT - 10);
        }
    }

    private void drawPause(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.BOLD, 40));
        g.drawString("PAUSED", 210, 280);
        g.setFont(new Font("Consolas", Font.PLAIN, 18));
        g.drawString("Nhấn P để tiếp tục", 205, 330);
    }

    private void drawGameOver(Graphics2D g) {
        // Overlay
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Panel
        g.setColor(new Color(30, 30, 30, 230));
        g.fillRoundRect(100, 150, 400, 300, 30, 30);
        g.setColor(new Color(200, 50, 50));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(100, 150, 400, 300, 30, 30);

        g.setColor(new Color(255, 80, 80));
        g.setFont(new Font("Consolas", Font.BOLD, 48));
        g.drawString("GAME OVER", 128, 230);

        g.setFont(new Font("Consolas", Font.BOLD, 22));
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 230, 290);

        g.setColor(new Color(255, 215, 0));
        g.drawString("Best:  " + highScore, 230, 330);

        g.setColor(new Color(150, 255, 150));
        g.setFont(new Font("Consolas", Font.PLAIN, 17));
        g.drawString("Nhấn ENTER để chơi lại", 178, 400);
    }

    // ===== LOGIC =====

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            // Flash animation
            if (flashing) {
                flashCount++;
                if (flashCount >= 10) flashing = false;
            }

            snake.move();
            checkFood();
            checkCollision();
            updateLevel();

            // Special food hết hạn
            if (specialFood != null && specialFood.isExpired()) specialFood = null;

            // Random spawn special food mỗi lần ăn hoặc tự nhiên
            if (specialFood == null && random.nextInt(200) == 0) spawnSpecialFood();
        }
        repaint();
    }

    public void checkFood() {
        int hx = snake.getX().get(0), hy = snake.getY().get(0);

        // Normal food
        if (hx == normalFood.getX() && hy == normalFood.getY()) {
            snake.grow();
            normalFood.randomize();
            score += FoodType.NORMAL.points;
            Sound.play(Sound.EAT);
            spawnSpecialFood();
        }

        // Special food
        if (specialFood != null && hx == specialFood.getX() && hy == specialFood.getY()) {
            FoodType ft = specialFood.getType();
            if (ft == FoodType.BONUS) {
                snake.grow();
                score += FoodType.BONUS.points;
                Sound.play(Sound.BONUS);
            } else if (ft == FoodType.POISON) {
                Sound.play(Sound.POISON);
                loseLife();
            }
            specialFood = null;
        }
    }

    public void checkCollision() {
        if (!flashing && snake.checkCollision()) {
            loseLife();
        }
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (snake.getDirection() != 'R') snake.setDirection('L'); break;
                case KeyEvent.VK_RIGHT:
                    if (snake.getDirection() != 'L') snake.setDirection('R'); break;
                case KeyEvent.VK_UP:
                    if (snake.getDirection() != 'D') snake.setDirection('U'); break;
                case KeyEvent.VK_DOWN:
                    if (snake.getDirection() != 'U') snake.setDirection('D'); break;
                case KeyEvent.VK_P:
                    if (running) paused = !paused; break;
                case KeyEvent.VK_ENTER:
                    if (!running) startGame(); break;
            }
        }
    }
}