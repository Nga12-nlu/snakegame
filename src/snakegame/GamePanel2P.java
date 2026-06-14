package snakegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Random;

/**
 * GamePanel2P – Chế độ 2 người chơi kiểu slither.io.
 *
 * Luật chơi:
 *  - Đâm vào THÂN đối thủ → người đâm mất 1 mạng, đối thủ vẫn sống.
 *  - Đâm ĐẦU vào ĐẦU nhau → cả 2 mất 1 mạng.
 *  - Ai hết 3 mạng trước → thua.
 *  - Food chung, ai ăn trước được điểm & dài hơn.
 *
 * Điều khiển:
 *  - Player 1 (đỏ): ↑ ↓ ← →
 *  - Player 2 (xanh): W A S D
 */
public class GamePanel2P extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final int SCREEN_WIDTH  = 600;
    private final int SCREEN_HEIGHT = 600;
    private final int TILE_SIZE     = 25;
    private final int BASE_DELAY    = 120;

    // ── Rắn 2 người ───────────────────────────────────────────────────────
    private Snake snake1;   // Player 1 – mũi tên
    private Snake snake2;   // Player 2 – WASD

    private SnakeSkin skin1;
    private SnakeSkin skin2;

    private int score1 = 0, score2 = 0;
    private int lives1 = 3, lives2 = 3;

    // ── Food ──────────────────────────────────────────────────────────────
    private Food normalFood;
    private Food specialFood;

    // ── Map ───────────────────────────────────────────────────────────────
    private MapType     currentMap;
    private List<int[]> walls;

    // ── Trạng thái game ───────────────────────────────────────────────────
    private Timer   timer;
    private boolean running = false;
    private boolean paused  = false;
    private int     level   = 1;
    private Random  random  = new Random();

    // Flash effect riêng cho từng rắn
    private boolean flashing1 = false, flashing2 = false;
    private int     flashCount1 = 0,   flashCount2 = 0;

    // Kết quả trận
    private String winner = "";   // "Player 1", "Player 2", "Draw"

    // ── Constructor ───────────────────────────────────────────────────────
    public GamePanel2P() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new KeyHandler());
        startGame();
    }

    // ── Khởi động / Restart ───────────────────────────────────────────────
    public void startGame() {
        skin1      = StartScreen.chosenSkin;
        skin2      = StartScreen.chosenSkin2;
        currentMap = StartScreen.chosenMap;
        walls      = currentMap.buildWalls();

        // Rắn 1 xuất phát bên trái, rắn 2 bên phải
        snake1 = new Snake();                   // mặc định xuất phát (100,100) đi R
        snake2 = new Snake2();                  // xuất phát (500,500) đi L

        normalFood  = spawnFoodSafe(FoodType.NORMAL);
        specialFood = null;

        score1 = score2 = 0;
        lives1 = lives2 = 3;
        level  = 1;
        running = true;
        paused  = false;
        flashing1 = flashing2 = false;
        winner  = "";

        if (timer != null) timer.stop();
        timer = new Timer(BASE_DELAY, this);
        timer.start();
    }

    // ── Spawn food tránh tường & thân 2 rắn ──────────────────────────────
    private Food spawnFoodSafe(FoodType type) {
        Food f = new Food(type);
        int tries = 0;
        while (tries++ < 200) {
            if (!isOnWall(f.getX(), f.getY())
                && !isOnSnake(f.getX(), f.getY(), snake1)
                && !isOnSnake(f.getX(), f.getY(), snake2)) break;
            f.randomize();
        }
        return f;
    }

    private boolean isOnWall(int px, int py) {
        for (int[] w : walls) if (w[0] == px && w[1] == py) return true;
        return false;
    }

    private boolean isOnSnake(int px, int py, Snake s) {
        if (s == null) return false;
        for (int i = 0; i < s.getBodyParts(); i++)
            if (s.getX().get(i) == px && s.getY().get(i) == py) return true;
        return false;
    }

    // ── Mất mạng ──────────────────────────────────────────────────────────
    private void loseLife1() {
        lives1--;
        Sound.play(Sound.GAME_OVER);
        if (lives1 <= 0) {
            running = false;
            timer.stop();
            winner = lives2 > 0 ? "Player 2" : "Draw";
        } else {
            snake1.reset();
            flashing1   = true;
            flashCount1 = 0;
        }
    }

    private void loseLife2() {
        lives2--;
        Sound.play(Sound.GAME_OVER);
        if (lives2 <= 0) {
            running = false;
            timer.stop();
            winner = lives1 > 0 ? "Player 1" : "Draw";
        } else {
            snake2.reset();
            flashing2   = true;
            flashCount2 = 0;
        }
    }

    // ── Level ─────────────────────────────────────────────────────────────
    private void updateLevel() {
        int topScore = Math.max(score1, score2);
        int newLevel = topScore / 50 + 1;
        if (newLevel != level) {
            level = newLevel;
            timer.setDelay(Math.max(50, BASE_DELAY - (level - 1) * 10));
        }
    }

    // ── ActionPerformed (game loop) ───────────────────────────────────────
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            // Flash countdown
            if (flashing1) { flashCount1++; if (flashCount1 >= 10) flashing1 = false; }
            if (flashing2) { flashCount2++; if (flashCount2 >= 10) flashing2 = false; }

            snake1.move();
            snake2.move();

            checkFood();
            checkCollisions();
            updateLevel();

            if (specialFood != null && specialFood.isExpired()) specialFood = null;
            if (specialFood == null && random.nextInt(200) == 0)
                specialFood = spawnFoodSafe(random.nextInt(100) < 30 ? FoodType.BONUS : FoodType.POISON);
        }
        repaint();
    }

    // ── Kiểm tra ăn food ──────────────────────────────────────────────────
    private void checkFood() {
        int h1x = snake1.getX().get(0), h1y = snake1.getY().get(0);
        int h2x = snake2.getX().get(0), h2y = snake2.getY().get(0);

        // Normal food
        if (h1x == normalFood.getX() && h1y == normalFood.getY()) {
            snake1.grow(); score1 += FoodType.NORMAL.points;
            Sound.play(Sound.EAT);
            normalFood = spawnFoodSafe(FoodType.NORMAL);
        } else if (h2x == normalFood.getX() && h2y == normalFood.getY()) {
            snake2.grow(); score2 += FoodType.NORMAL.points;
            Sound.play(Sound.EAT);
            normalFood = spawnFoodSafe(FoodType.NORMAL);
        }

        // Special food
        if (specialFood != null) {
            boolean p1ate = h1x == specialFood.getX() && h1y == specialFood.getY();
            boolean p2ate = h2x == specialFood.getX() && h2y == specialFood.getY();

            if (p1ate || p2ate) {
                FoodType ft = specialFood.getType();
                if (ft == FoodType.BONUS) {
                    if (p1ate) { snake1.grow(); score1 += FoodType.BONUS.points; }
                    if (p2ate) { snake2.grow(); score2 += FoodType.BONUS.points; }
                    Sound.play(Sound.BONUS);
                } else if (ft == FoodType.POISON) {
                    Sound.play(Sound.POISON);
                    if (p1ate) loseLife1();
                    if (p2ate) loseLife2();
                }
                specialFood = null;
            }
        }
    }

    // ── Kiểm tra va chạm ─────────────────────────────────────────────────
    private void checkCollisions() {
        int h1x = snake1.getX().get(0), h1y = snake1.getY().get(0);
        int h2x = snake2.getX().get(0), h2y = snake2.getY().get(0);

        boolean die1 = false, die2 = false;

        // 1. Va chạm tường map & biên màn hình
        if (!flashing1 && (snake1.checkCollision() || isOnWall(h1x, h1y))) die1 = true;
        if (!flashing2 && (snake2.checkCollision() || isOnWall(h2x, h2y))) die2 = true;

        // 2. Đầu vs Đầu → cả 2 chết
        if (!flashing1 && !flashing2 && h1x == h2x && h1y == h2y) {
            die1 = die2 = true;
        }

        // 3. Đầu P1 đâm vào THÂN P2
        if (!flashing1) {
            for (int i = 1; i < snake2.getBodyParts(); i++)
                if (h1x == snake2.getX().get(i) && h1y == snake2.getY().get(i)) { die1 = true; break; }
        }

        // 4. Đầu P2 đâm vào THÂN P1
        if (!flashing2) {
            for (int i = 1; i < snake1.getBodyParts(); i++)
                if (h2x == snake1.getX().get(i) && h2y == snake1.getY().get(i)) { die2 = true; break; }
        }

        // Áp dụng (xử lý đồng thời tránh chain)
        if (die1) loseLife1();
        if (die2) loseLife2();
    }

    // ===== VẼ =====

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g2);
        drawWalls(g2);
        drawFood(g2);
        drawSnake(g2, snake2, skin2, flashing2, flashCount2, 2);
        drawSnake(g2, snake1, skin1, flashing1, flashCount1, 1);
        drawHUD(g2);

        if (!running) drawResult(g2);
        else if (paused) drawPause(g2);
    }

    private void drawBackground(Graphics2D g) {
        g.setColor(new Color(20, 20, 20));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(new Color(35, 35, 35));
        for (int i = 0; i < SCREEN_WIDTH;  i += TILE_SIZE) g.drawLine(i, 0, i, SCREEN_HEIGHT);
        for (int j = 0; j < SCREEN_HEIGHT; j += TILE_SIZE) g.drawLine(0, j, SCREEN_WIDTH, j);
    }

    private void drawWalls(Graphics2D g) {
        if (walls == null || walls.isEmpty()) return;
        Color wc = currentMap.accentColor;
        for (int[] wall : walls) {
            int wx = wall[0], wy = wall[1];
            g.setColor(new Color(0,0,0,100));
            g.fillRect(wx+3, wy+3, TILE_SIZE, TILE_SIZE);
            g.setColor(wc.darker().darker());
            g.fillRect(wx, wy, TILE_SIZE, TILE_SIZE);
            g.setColor(wc);
            g.fillRect(wx+1, wy+1, TILE_SIZE-4, 4);
            g.fillRect(wx+1, wy+1, 4, TILE_SIZE-4);
            g.setColor(wc.brighter());
            g.fillRect(wx+1, wy+1, 3, 3);
        }
    }

    private void drawFood(Graphics2D g) {
        drawFoodItem(g, normalFood);
        if (specialFood != null) drawFoodItem(g, specialFood);
    }

    private void drawFoodItem(Graphics2D g, Food food) {
        int fx = food.getX(), fy = food.getY();
        Color c = food.getType().color;
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 60));
        g.fillOval(fx-4, fy-4, 33, 33);
        g.setColor(c);
        g.fillOval(fx+2, fy+2, 21, 21);
        g.setColor(new Color(255,255,255,120));
        g.fillOval(fx+5, fy+4, 7, 7);
    }

    private void drawSnake(Graphics2D g, Snake s, SnakeSkin sk,
                           boolean flashing, int flashCount, int playerNum) {
        if (flashing && (flashCount / 2) % 2 == 1) return;

        for (int i = 0; i < s.getBodyParts(); i++) {
            int sx = s.getX().get(i), sy = s.getY().get(i);
            if (i == 0) {
                g.setColor(sk.headColor);
                g.fillRoundRect(sx+1, sy+1, 23, 23, 8, 8);

                // Badge số người chơi
                g.setColor(new Color(0,0,0,180));
                g.fillOval(sx+14, sy-2, 12, 12);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Consolas", Font.BOLD, 9));
                g.drawString("P"+playerNum, sx+15, sy+7);

                // Mắt
                g.setColor(Color.BLACK);
                char dir = s.getDirection();
                if      (dir=='R') { g.fillOval(sx+16,sy+5,5,5); g.fillOval(sx+16,sy+15,5,5); }
                else if (dir=='L') { g.fillOval(sx+4, sy+5,5,5); g.fillOval(sx+4, sy+15,5,5); }
                else if (dir=='U') { g.fillOval(sx+5, sy+4,5,5); g.fillOval(sx+15,sy+4, 5,5); }
                else               { g.fillOval(sx+5, sy+16,5,5);g.fillOval(sx+15,sy+16,5,5);}
            } else {
                g.setColor(i % 2 == 0 ? sk.bodyColorLight : sk.bodyColorDark);
                g.fillRoundRect(sx+2, sy+2, 21, 21, 6, 6);
            }
        }
    }

    private void drawHUD(Graphics2D g) {
        // Nền HUD
        g.setColor(new Color(0,0,0,180));
        g.fillRect(0, 0, SCREEN_WIDTH, 45);

        g.setFont(new Font("Consolas", Font.BOLD, 14));

        // Player 1 (trái)
        g.setColor(skin1.headColor);
        g.drawString("P1", 8, 16);
        g.setColor(Color.WHITE);
        g.drawString(score1 + "pts", 30, 16);
        for (int i = 0; i < 3; i++) {
            g.setColor(i < lives1 ? new Color(255,60,60) : new Color(80,80,80));
            g.drawString("♥", 8 + i*18, 34);
        }

        // Map name (giữa)
        g.setColor(currentMap.accentColor);
        g.setFont(new Font("Consolas", Font.PLAIN, 11));
        String mapLbl = "Lv." + level + "  " + currentMap.displayName;
        int mw = g.getFontMetrics().stringWidth(mapLbl);
        g.drawString(mapLbl, (SCREEN_WIDTH - mw) / 2, 28);

        // Player 2 (phải)
        g.setFont(new Font("Consolas", Font.BOLD, 14));
        g.setColor(skin2.headColor);
        String p2lbl = "P2";
        String p2pts = score2 + "pts";
        int p2ptW = g.getFontMetrics().stringWidth(p2pts);
        g.drawString(p2lbl, SCREEN_WIDTH - 30, 16);
        g.setColor(Color.WHITE);
        g.drawString(p2pts, SCREEN_WIDTH - 8 - p2ptW, 16);
        for (int i = 0; i < 3; i++) {
            g.setColor(i < lives2 ? new Color(255,60,60) : new Color(80,80,80));
            g.drawString("♥", SCREEN_WIDTH - 60 + i*18, 34);
        }

        // Gợi ý special food
        if (specialFood != null) {
            String hint = specialFood.getType() == FoodType.BONUS ? "★ BONUS +30!" : "☠ POISON!";
            g.setColor(specialFood.getType().color);
            g.setFont(new Font("Consolas", Font.BOLD, 12));
            g.drawString(hint, 10, SCREEN_HEIGHT - 8);
        }

        // Hướng dẫn nhỏ góc phải dưới
        g.setColor(new Color(100,100,100));
        g.setFont(new Font("Consolas", Font.PLAIN, 10));
        g.drawString("P1:Arrows  P2:WASD  P:Pause  M:Sound", SCREEN_WIDTH - 240, SCREEN_HEIGHT - 8);

        // THÊM MỚI: icon âm thanh góc phải trên
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        g.setColor(new Color(180,180,180));
        String soundIcon = Sound.isMuted() ? "🔇" : "🔊";
        g.drawString(soundIcon, SCREEN_WIDTH - 20, 40);
    }

    private void drawPause(Graphics2D g) {
        g.setColor(new Color(0,0,0,150));
        g.fillRect(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.BOLD, 40));
        g.drawString("PAUSED", 205, 280);
        g.setFont(new Font("Consolas", Font.PLAIN, 18));
        g.drawString("Nhấn P để tiếp tục", 200, 330);
    }

    private void drawResult(Graphics2D g) {
        g.setColor(new Color(0,0,0,200));
        g.fillRect(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);

        g.setColor(new Color(30,30,30,230));
        g.fillRoundRect(80, 140, 440, 320, 30, 30);

        // Màu viền theo người thắng
        Color borderColor = winner.equals("Draw") ? new Color(255,215,0)
                          : winner.equals("Player 1") ? skin1.headColor : skin2.headColor;
        g.setColor(borderColor);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(80, 140, 440, 320, 30, 30);

        // Tiêu đề
        if (winner.equals("Draw")) {
            g.setColor(new Color(255,215,0));
            g.setFont(new Font("Consolas", Font.BOLD, 44));
            g.drawString("DRAW!", 218, 220);
        } else {
            g.setColor(borderColor);
            g.setFont(new Font("Consolas", Font.BOLD, 38));
            g.drawString(winner + " WINS!", winner.equals("Player 1") ? 148 : 148, 220);
        }

        // Điểm
        g.setFont(new Font("Consolas", Font.BOLD, 20));
        g.setColor(skin1.headColor);
        g.drawString("P1 Score: " + score1, 160, 275);
        g.setColor(skin2.headColor);
        g.drawString("P2 Score: " + score2, 160, 305);

        // Mạng còn lại
        g.setFont(new Font("Consolas", Font.PLAIN, 16));
        g.setColor(new Color(180,180,180));
        g.drawString("P1 lives left: " + Math.max(0, lives1), 160, 340);
        g.drawString("P2 lives left: " + Math.max(0, lives2), 160, 365);

        g.setColor(new Color(150,255,150));
        g.setFont(new Font("Consolas", Font.PLAIN, 16));
        g.drawString("Nhấn ENTER để chơi lại", 172, 420);
        g.setColor(new Color(150,150,255));
        g.drawString("Nhấn ESC về màn hình chính", 155, 445);
    }

    // ── Input ─────────────────────────────────────────────────────────────
    public class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int k = e.getKeyCode();
            // Player 1 – mũi tên
            if (k == KeyEvent.VK_LEFT  && snake1.getDirection() != 'R') snake1.setDirection('L');
            if (k == KeyEvent.VK_RIGHT && snake1.getDirection() != 'L') snake1.setDirection('R');
            if (k == KeyEvent.VK_UP    && snake1.getDirection() != 'D') snake1.setDirection('U');
            if (k == KeyEvent.VK_DOWN  && snake1.getDirection() != 'U') snake1.setDirection('D');
            // Player 2 – WASD
            if (k == KeyEvent.VK_A && snake2.getDirection() != 'R') snake2.setDirection('L');
            if (k == KeyEvent.VK_D && snake2.getDirection() != 'L') snake2.setDirection('R');
            if (k == KeyEvent.VK_W && snake2.getDirection() != 'D') snake2.setDirection('U');
            if (k == KeyEvent.VK_S && snake2.getDirection() != 'U') snake2.setDirection('D');
            // Pause / Restart / ESC về menu
            if (k == KeyEvent.VK_P     && running) paused = !paused;
            if (k == KeyEvent.VK_ENTER && !running) startGame();
            if (k == KeyEvent.VK_M) Sound.toggleMuted(); // THÊM MỚI: tắt/mở âm
            if (k == KeyEvent.VK_ESCAPE && !running) {
                // Quay về Start Screen
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(GamePanel2P.this);
                if (frame instanceof GameFrame) ((GameFrame) frame).showStartScreen();
            }
        }
    }
}