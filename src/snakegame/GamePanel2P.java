package snakegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel2P extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final int SCREEN_WIDTH  = GameConfig.VIEW_WIDTH;
    private final int SCREEN_HEIGHT = GameConfig.VIEW_HEIGHT;
    private final int TILE_SIZE     = GameConfig.TILE_SIZE;
    private final int WORLD_WIDTH   = GameConfig.WORLD_WIDTH;
    private final int WORLD_HEIGHT  = GameConfig.WORLD_HEIGHT;
    private final int BASE_DELAY    = 120;

    private Snake snake1;
    private Snake snake2;

    private SnakeSkin skin1;
    private SnakeSkin skin2;

    private int score1 = 0, score2 = 0;
    private int lives1 = 3, lives2 = 3;

    private List<Food> normalFoods;
    private Food       specialFood;

    private MapType     currentMap;
    private List<int[]> walls;

    private Timer   timer;
    private boolean running = false;
    private boolean paused  = false;
    private int     level   = 1;
    private Random  random  = new Random();

    private boolean flashing1 = false, flashing2 = false;
    private int     flashCount1 = 0,   flashCount2 = 0;

    private String winner = "";

    private int camX = 0, camY = 0;
    private double zoom = 1.0;
    private static final double MIN_ZOOM = 0.35;
    private static final int    MARGIN   = 120;

    private boolean botMode = false;
    private SnakeBot bot2;

    public GamePanel2P() {
        this(false);
    }

    public GamePanel2P(boolean botMode) {
        this.botMode = botMode;
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new KeyHandler());
        startGame();
    }

    public void startGame() {
        skin1      = StartScreen.chosenSkin;
        skin2      = botMode ? SnakeSkin.LAVA : StartScreen.chosenSkin2;
        currentMap = StartScreen.chosenMap;
        walls      = currentMap.buildWalls();

        snake1 = new Snake();
        snake2 = new Snake2();
        ensureSafeSpawn(snake1, 250, 250);
        ensureSafeSpawn(snake2, GameConfig.WORLD_WIDTH - 300, GameConfig.WORLD_HEIGHT - 300);

        normalFoods = new ArrayList<>();
        for (int i = 0; i < GameConfig.FOOD_COUNT; i++) {
            normalFoods.add(spawnFoodSafe(FoodType.NORMAL));
        }
        specialFood = null;

        score1 = score2 = 0;
        lives1 = lives2 = 3;
        level  = 1;
        running = true;
        paused  = false;
        flashing1 = flashing2 = false;
        winner  = "";

        if (botMode) {
            List<Snake> others = new ArrayList<>();
            others.add(snake1);
            bot2 = new SnakeBot(snake2, walls, others);
        }

        updateCamera();

        if (timer != null) timer.stop();
        timer = new Timer(BASE_DELAY, this);
        timer.start();
    }

    private void updateCamera() {
        int x1 = snake1.getX().get(0), y1 = snake1.getY().get(0);
        int x2 = snake2.getX().get(0), y2 = snake2.getY().get(0);

        int cx = (x1 + x2) / 2;
        int cy = (y1 + y2) / 2;

        int spanX = Math.abs(x1 - x2) + MARGIN * 2;
        int spanY = Math.abs(y1 - y2) + MARGIN * 2;

        double zoomX = Math.min(1.0, (double) SCREEN_WIDTH  / Math.max(spanX, 1));
        double zoomY = Math.min(1.0, (double) SCREEN_HEIGHT / Math.max(spanY, 1));

        zoom = Math.max(MIN_ZOOM, Math.min(zoomX, zoomY));

        double viewW = SCREEN_WIDTH  / zoom;
        double viewH = SCREEN_HEIGHT / zoom;

        camX = (int) (cx - viewW / 2);
        camY = (int) (cy - viewH / 2);

        camX = (int) Math.max(0, Math.min(camX, WORLD_WIDTH  - viewW));
        camY = (int) Math.max(0, Math.min(camY, WORLD_HEIGHT - viewH));

        if (viewW > WORLD_WIDTH)  camX = (int) ((WORLD_WIDTH  - viewW) / 2);
        if (viewH > WORLD_HEIGHT) camY = (int) ((WORLD_HEIGHT - viewH) / 2);
    }

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

    private void ensureSafeSpawn(Snake s, int fallbackX, int fallbackY) {
        for (int i = 0; i < s.getBodyParts(); i++) {
            if (isOnWall(s.getX().get(i), s.getY().get(i))) {
                int offsetX = fallbackX - s.getX().get(0);
                int offsetY = fallbackY - s.getY().get(0);
                for (int j = 0; j < s.getBodyParts(); j++) {
                    s.getX().set(j, s.getX().get(j) + offsetX);
                    s.getY().set(j, s.getY().get(j) + offsetY);
                }
                break;
            }
        }
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

    private void loseLife1() {
        lives1--;
        Sound.play(Sound.GAME_OVER);
        if (lives1 <= 0) {
            running = false;
            timer.stop();
            winner = lives2 > 0 ? (botMode ? "BOT" : "Player 2") : "Draw";
        } else {
            snake1.reset();
            ensureSafeSpawn(snake1, 250, 250);
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
            ensureSafeSpawn(snake2, GameConfig.WORLD_WIDTH - 300, GameConfig.WORLD_HEIGHT - 300);
            flashing2   = true;
            flashCount2 = 0;
        }
    }

    private void updateLevel() {
        int topScore = Math.max(score1, score2);
        int newLevel = topScore / 50 + 1;
        if (newLevel != level) {
            level = newLevel;
            timer.setDelay(Math.max(50, BASE_DELAY - (level - 1) * 10));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            if (flashing1) { flashCount1++; if (flashCount1 >= 10) flashing1 = false; }
            if (flashing2) { flashCount2++; if (flashCount2 >= 10) flashing2 = false; }

            if (botMode && bot2 != null) bot2.update();

            snake1.move();
            snake2.move();

            checkFood();
            checkCollisions();
            updateLevel();
            updateCamera();

            if (specialFood != null && specialFood.isExpired()) specialFood = null;
            if (specialFood == null && random.nextInt(200) == 0)
                specialFood = spawnFoodSafe(random.nextInt(100) < 30 ? FoodType.BONUS : FoodType.POISON);
        }
        repaint();
    }

    private void checkFood() {
        int h1x = snake1.getX().get(0), h1y = snake1.getY().get(0);
        int h2x = snake2.getX().get(0), h2y = snake2.getY().get(0);

        for (Food f : normalFoods) {
            boolean p1ate = h1x == f.getX() && h1y == f.getY();
            boolean p2ate = h2x == f.getX() && h2y == f.getY();
            if (p1ate || p2ate) {
                if (p1ate) { snake1.grow(); score1 += FoodType.NORMAL.points; }
                if (p2ate) { snake2.grow(); score2 += FoodType.NORMAL.points; }
                Sound.play(Sound.EAT);
                f.randomize();
                while (isOnWall(f.getX(), f.getY())
                       || isOnSnake(f.getX(), f.getY(), snake1)
                       || isOnSnake(f.getX(), f.getY(), snake2)) f.randomize();
                break;
            }
        }

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

    private void checkCollisions() {
        int h1x = snake1.getX().get(0), h1y = snake1.getY().get(0);
        int h2x = snake2.getX().get(0), h2y = snake2.getY().get(0);

        boolean die1 = false, die2 = false;

        if (!flashing1 && (snake1.checkCollision() || isOnWall(h1x, h1y))) die1 = true;
        if (!flashing2 && (snake2.checkCollision() || isOnWall(h2x, h2y))) die2 = true;

        if (!flashing1 && !flashing2 && h1x == h2x && h1y == h2y) {
            die1 = die2 = true;
        }

        if (!flashing1) {
            for (int i = 1; i < snake2.getBodyParts(); i++)
                if (h1x == snake2.getX().get(i) && h1y == snake2.getY().get(i)) { die1 = true; break; }
        }

        if (!flashing2) {
            for (int i = 1; i < snake1.getBodyParts(); i++)
                if (h2x == snake1.getX().get(i) && h2y == snake1.getY().get(i)) { die2 = true; break; }
        }

        if (die1) loseLife1();
        if (die2) loseLife2();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(10, 10, 10));
        g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        Graphics2D world = (Graphics2D) g2.create();
        world.scale(zoom, zoom);
        world.translate(-camX, -camY);

        drawBackground(world);
        drawWalls(world);
        drawFood(world);
        drawSnakeEntity(world, snake2, skin2, flashing2, flashCount2, botMode ? "BOT" : "P2");
        drawSnakeEntity(world, snake1, skin1, flashing1, flashCount1, "P1");
        world.dispose();

        drawHUD(g2);
        drawMinimap(g2);

        if (!running) drawResult(g2);
        else if (paused) drawPause(g2);
    }

    private void drawBackground(Graphics2D g) {
        g.setColor(new Color(20, 20, 20));
        g.fillRect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        g.setColor(new Color(35, 35, 35));
        for (int i = 0; i <= WORLD_WIDTH;  i += TILE_SIZE) g.drawLine(i, 0, i, WORLD_HEIGHT);
        for (int j = 0; j <= WORLD_HEIGHT; j += TILE_SIZE) g.drawLine(0, j, WORLD_WIDTH, j);
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
        for (Food f : normalFoods) drawFoodItem(g, f);
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

    private void drawSnakeEntity(Graphics2D g, Snake s, SnakeSkin sk,
                                  boolean flashing, int flashCount, String label) {
        if (flashing && (flashCount / 2) % 2 == 1) return;

        for (int i = 0; i < s.getBodyParts(); i++) {
            int sx = s.getX().get(i);
            int sy = s.getY().get(i);

            if (i == 0) {
                g.setColor(sk.headColor);
                g.fillRoundRect(sx+1, sy+1, 23, 23, 8, 8);

                g.setColor(new Color(0,0,0,180));
                g.fillOval(sx + 14, sy - 2, 12, 12);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Consolas", Font.BOLD, 9));
                g.drawString(label, sx + 15, sy + 7);

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
        g.setColor(new Color(0,0,0,180));
        g.fillRect(0, 0, SCREEN_WIDTH, 45);

        g.setFont(new Font("Consolas", Font.BOLD, 14));

        g.setColor(skin1.headColor);
        g.drawString("P1", 8, 16);
        g.setColor(Color.WHITE);
        g.drawString(score1 + "pts", 30, 16);
        for (int i = 0; i < 3; i++) {
            g.setColor(i < lives1 ? new Color(255,60,60) : new Color(80,80,80));
            g.drawString("♥", 8 + i*18, 34);
        }

        g.setColor(currentMap.accentColor);
        g.setFont(new Font("Consolas", Font.PLAIN, 11));
        String mapLbl = "Lv." + level + "  " + currentMap.displayName
                      + "  (" + Math.round(zoom * 100) + "%)";
        int mw = g.getFontMetrics().stringWidth(mapLbl);
        g.drawString(mapLbl, (SCREEN_WIDTH - mw) / 2, 28);

        g.setFont(new Font("Consolas", Font.BOLD, 14));
        g.setColor(skin2.headColor);
        String p2lbl = botMode ? "BOT" : "P2";
        String p2pts = score2 + "pts";
        int p2ptW = g.getFontMetrics().stringWidth(p2pts);
        g.drawString(p2lbl, SCREEN_WIDTH - 38, 16);
        g.setColor(Color.WHITE);
        g.drawString(p2pts, SCREEN_WIDTH - 8 - p2ptW, 16);
        for (int i = 0; i < 3; i++) {
            g.setColor(i < lives2 ? new Color(255,60,60) : new Color(80,80,80));
            g.drawString("♥", SCREEN_WIDTH - 60 + i*18, 34);
        }

        if (specialFood != null) {
            String hint = specialFood.getType() == FoodType.BONUS ? "★ BONUS +30!" : "☠ POISON!";
            g.setColor(specialFood.getType().color);
            g.setFont(new Font("Consolas", Font.BOLD, 12));
            g.drawString(hint, 10, SCREEN_HEIGHT - 8);
        }

        g.setColor(new Color(100,100,100));
        g.setFont(new Font("Consolas", Font.PLAIN, 10));
        String ctrl = botMode ? "P1:Arrows  P:Pause  M:Sound" : "P1:Arrows  P2:WASD  P:Pause  M:Sound";
        g.drawString(ctrl, SCREEN_WIDTH - g.getFontMetrics().stringWidth(ctrl) - 4, SCREEN_HEIGHT - 8);

        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        g.setColor(new Color(180,180,180));
        g.drawString(Sound.isMuted() ? "🔇" : "🔊", SCREEN_WIDTH - 20, 40);
    }

    private void drawMinimap(Graphics2D g) {
        int mmSize = 90;
        int mmX = SCREEN_WIDTH - mmSize - 10;
        int mmY = SCREEN_HEIGHT - mmSize - 60;
        double scale = (double) mmSize / WORLD_WIDTH;

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(mmX - 4, mmY - 4, mmSize + 8, mmSize + 8, 8, 8);
        g.setColor(new Color(50, 50, 60));
        g.drawRoundRect(mmX - 4, mmY - 4, mmSize + 8, mmSize + 8, 8, 8);

        g.setColor(currentMap.accentColor);
        for (int[] wall : walls) {
            int wx = mmX + (int)(wall[0] * scale);
            int wy = mmY + (int)(wall[1] * scale);
            g.fillRect(wx, wy, 2, 2);
        }

        g.setColor(new Color(255, 255, 255, 180));
        int vx = mmX + (int)(camX * scale);
        int vy = mmY + (int)(camY * scale);
        int vw = (int)((SCREEN_WIDTH  / zoom) * scale);
        int vh = (int)((SCREEN_HEIGHT / zoom) * scale);
        g.drawRect(vx, vy, vw, vh);

        g.setColor(skin1.headColor);
        int h1x = mmX + (int)(snake1.getX().get(0) * scale);
        int h1y = mmY + (int)(snake1.getY().get(0) * scale);
        g.fillOval(h1x - 2, h1y - 2, 5, 5);

        g.setColor(skin2.headColor);
        int h2x = mmX + (int)(snake2.getX().get(0) * scale);
        int h2y = mmY + (int)(snake2.getY().get(0) * scale);
        g.fillOval(h2x - 2, h2y - 2, 5, 5);
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

        Color borderColor = winner.equals("Draw") ? new Color(255,215,0)
                          : winner.equals("Player 1") ? skin1.headColor : skin2.headColor;
        g.setColor(borderColor);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(80, 140, 440, 320, 30, 30);

        if (winner.equals("Draw")) {
            g.setColor(new Color(255,215,0));
            g.setFont(new Font("Consolas", Font.BOLD, 44));
            g.drawString("DRAW!", 218, 220);
        } else {
            g.setColor(borderColor);
            g.setFont(new Font("Consolas", Font.BOLD, 38));
            g.drawString(winner + " WINS!", 130, 220);
        }

        g.setFont(new Font("Consolas", Font.BOLD, 20));
        g.setColor(skin1.headColor);
        g.drawString("P1 Score: " + score1, 160, 275);
        g.setColor(skin2.headColor);
        g.drawString((botMode ? "BOT" : "P2") + " Score: " + score2, 160, 305);

        g.setFont(new Font("Consolas", Font.PLAIN, 16));
        g.setColor(new Color(180,180,180));
        g.drawString("P1 lives left: " + Math.max(0, lives1), 160, 340);
        g.drawString((botMode ? "BOT" : "P2") + " lives left: " + Math.max(0, lives2), 160, 365);

        g.setColor(new Color(150,255,150));
        g.setFont(new Font("Consolas", Font.PLAIN, 16));
        g.drawString("Nhấn ENTER để chơi lại", 172, 420);
        g.setColor(new Color(150,150,255));
        g.drawString("Nhấn ESC về màn hình chính", 155, 445);
    }

    public class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int k = e.getKeyCode();
            if (k == KeyEvent.VK_LEFT  && snake1.getDirection() != 'R') snake1.setDirection('L');
            if (k == KeyEvent.VK_RIGHT && snake1.getDirection() != 'L') snake1.setDirection('R');
            if (k == KeyEvent.VK_UP    && snake1.getDirection() != 'D') snake1.setDirection('U');
            if (k == KeyEvent.VK_DOWN  && snake1.getDirection() != 'U') snake1.setDirection('D');
            if (!botMode) {
                if (k == KeyEvent.VK_A && snake2.getDirection() != 'R') snake2.setDirection('L');
                if (k == KeyEvent.VK_D && snake2.getDirection() != 'L') snake2.setDirection('R');
                if (k == KeyEvent.VK_W && snake2.getDirection() != 'D') snake2.setDirection('U');
                if (k == KeyEvent.VK_S && snake2.getDirection() != 'U') snake2.setDirection('D');
            }
            if (k == KeyEvent.VK_P     && running) paused = !paused;
            if (k == KeyEvent.VK_ENTER && !running) startGame();
            if (k == KeyEvent.VK_M) Sound.toggleMuted();
            if (k == KeyEvent.VK_ESCAPE && !running) {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(GamePanel2P.this);
                if (frame instanceof GameFrame) ((GameFrame) frame).showStartScreen();
            }
        }
    }
}