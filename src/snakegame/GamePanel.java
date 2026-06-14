package snakegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final int SCREEN_WIDTH  = GameConfig.VIEW_WIDTH;
    private final int SCREEN_HEIGHT = GameConfig.VIEW_HEIGHT;
    private final int TILE_SIZE     = GameConfig.TILE_SIZE;
    private final int WORLD_WIDTH   = GameConfig.WORLD_WIDTH;
    private final int WORLD_HEIGHT  = GameConfig.WORLD_HEIGHT;
    private final int BASE_DELAY    = 120;

    static int highScore = ScoreManager.loadHighScore();

    private Snake  snake;

    private Snake      botSnake;
    private SnakeBot   bot;
    private SnakeSkin  botSkin;
    private boolean    botEnabled = false;
    private int        botScore   = 0;
    private int        botLives   = 3;
    private boolean    botFlashing   = false;
    private int        botFlashCount = 0;

    private List<Food> normalFoods;
    private Food       specialFood;

    private Timer   timer;
    private boolean running = false;
    private boolean paused  = false;
    private int     score   = 0;
    private int     lives   = 3;
    private int     level   = 1;

    private Random  random = new Random();

    private boolean flashing  = false;
    private int     flashCount = 0;

    private SnakeSkin skin;

    private MapType      currentMap;
    private List<int[]>  walls;

    private int camX = 0, camY = 0;

    public GamePanel() {
        this(false);
    }

    public GamePanel(boolean enableBot) {
        this.botEnabled = enableBot;
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        skin        = StartScreen.chosenSkin;
        currentMap  = StartScreen.chosenMap;
        walls       = currentMap.buildWalls();

        snake       = new Snake();
        ensureSafeSpawn(snake);
        normalFoods = new ArrayList<>();
        for (int i = 0; i < GameConfig.FOOD_COUNT; i++) {
            normalFoods.add(spawnFoodSafe(FoodType.NORMAL));
        }
        specialFood = null;
        running     = true;
        paused      = false;
        score       = 0;
        lives       = 3;
        level       = 1;
        flashing    = false;

        if (botEnabled) {
            botSkin   = SnakeSkin.LAVA;
            botSnake  = new Snake2();
            ensureSafeSpawn(botSnake);
            List<Snake> others = new ArrayList<>();
            others.add(snake);
            bot       = new SnakeBot(botSnake, walls, others);
            botScore  = 0;
            botLives  = 3;
            botFlashing   = false;
            botFlashCount = 0;
        }

        updateCamera();

        if (timer != null) timer.stop();
        timer = new Timer(BASE_DELAY, this);
        timer.start();
    }

    private void updateCamera() {
        int headX = snake.getX().get(0);
        int headY = snake.getY().get(0);

        camX = headX - SCREEN_WIDTH / 2;
        camY = headY - SCREEN_HEIGHT / 2;

        camX = Math.max(0, Math.min(camX, WORLD_WIDTH  - SCREEN_WIDTH));
        camY = Math.max(0, Math.min(camY, WORLD_HEIGHT - SCREEN_HEIGHT));

        camX = (camX / TILE_SIZE) * TILE_SIZE;
        camY = (camY / TILE_SIZE) * TILE_SIZE;
    }

    private Food spawnFoodSafe(FoodType type) {
        Food f = new Food(type);
        int tries = 0;
        while (tries++ < 200) {
            if (!isOnWall(f.getX(), f.getY()) && !isOnSnake(f.getX(), f.getY())) break;
            f.randomize();
        }
        return f;
    }

    private boolean isOnSnake(int px, int py) {
        if (snake == null) return false;
        for (int i = 0; i < snake.getBodyParts(); i++)
            if (snake.getX().get(i) == px && snake.getY().get(i) == py) return true;
        return false;
    }

    private void loseLife() {
        lives--;
        Sound.play(Sound.GAME_OVER);
        if (lives <= 0) {
            running = false;
            timer.stop();
            ScoreManager.saveHighScore(highScore);
        } else {
            snake.reset();
            ensureSafeSpawn(snake);
            flashing   = true;
            flashCount = 0;
            updateCamera();
        }
    }

    private void botLoseLife() {
        botLives--;
        if (botLives <= 0) {
            botLives = 0;
        } else {
            botSnake.reset();
            ensureSafeSpawn(botSnake);
            botFlashing   = true;
            botFlashCount = 0;
        }
    }

    private void spawnSpecialFood() {
        int r = random.nextInt(100);
        if      (r < 30) specialFood = spawnFoodSafe(FoodType.BONUS);
        else if (r < 50) specialFood = spawnFoodSafe(FoodType.POISON);
    }

    private void updateLevel() {
        int newLevel = score / 50 + 1;
        if (newLevel != level) {
            level = newLevel;
            timer.setDelay(Math.max(50, BASE_DELAY - (level - 1) * 10));
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        if (running) {
            drawBackground(g2);
            drawWalls(g2);
            drawFood(g2);
            if (botEnabled && botLives > 0) drawBotSnake(g2);
            drawSnake(g2);
            drawHUD(g2);
            drawMinimap(g2);
            if (paused) drawPause(g2);
        } else {
            drawBackground(g2);
            drawWalls(g2);
            drawGameOver(g2);
        }
    }

    private void drawBackground(Graphics2D g) {
        g.setColor(new Color(20, 20, 20));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(new Color(35, 35, 35));
        int startX = -(camX % TILE_SIZE);
        int startY = -(camY % TILE_SIZE);
        for (int i = startX; i < SCREEN_WIDTH;  i += TILE_SIZE) g.drawLine(i, 0, i, SCREEN_HEIGHT);
        for (int j = startY; j < SCREEN_HEIGHT; j += TILE_SIZE) g.drawLine(0, j, SCREEN_WIDTH, j);
    }

    private void drawWalls(Graphics2D g) {
        if (walls == null || walls.isEmpty()) return;
        Color wallColor = currentMap.accentColor;
        Color wallDark  = wallColor.darker().darker();
        for (int[] wall : walls) {
            int wx = wall[0] - camX, wy = wall[1] - camY;
            if (wx < -TILE_SIZE || wx > SCREEN_WIDTH || wy < -TILE_SIZE || wy > SCREEN_HEIGHT) continue;
            g.setColor(new Color(0, 0, 0, 100));
            g.fillRect(wx + 3, wy + 3, TILE_SIZE, TILE_SIZE);
            g.setColor(wallDark);
            g.fillRect(wx, wy, TILE_SIZE, TILE_SIZE);
            g.setColor(wallColor);
            g.fillRect(wx + 1, wy + 1, TILE_SIZE - 4, 4);
            g.fillRect(wx + 1, wy + 1, 4, TILE_SIZE - 4);
            g.setColor(wallColor.brighter());
            g.fillRect(wx + 1, wy + 1, 3, 3);
        }
    }

    private void drawFood(Graphics2D g) {
        for (Food f : normalFoods) drawFoodItem(g, f);
        if (specialFood != null) drawFoodItem(g, specialFood);
    }

    private void drawFoodItem(Graphics2D g, Food food) {
        int fx = food.getX() - camX, fy = food.getY() - camY;
        if (fx < -TILE_SIZE || fx > SCREEN_WIDTH || fy < -TILE_SIZE || fy > SCREEN_HEIGHT) return;
        Color c = food.getType().color;
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 60));
        g.fillOval(fx - 4, fy - 4, 33, 33);
        g.setColor(c);
        g.fillOval(fx + 2, fy + 2, 21, 21);
        g.setColor(new Color(255, 255, 255, 120));
        g.fillOval(fx + 5, fy + 4, 7, 7);
    }

    private void drawSnake(Graphics2D g) {
        if (flashing && (flashCount / 2) % 2 == 1) return;

        for (int i = 0; i < snake.getBodyParts(); i++) {
            int sx = snake.getX().get(i) - camX;
            int sy = snake.getY().get(i) - camY;

            if (i == 0) {
                g.setColor(skin.headColor);
                g.fillRoundRect(sx + 1, sy + 1, 23, 23, 8, 8);

                g.setColor(Color.BLACK);
                char dir = snake.getDirection();
                if      (dir == 'R') { g.fillOval(sx+16, sy+5,  5, 5); g.fillOval(sx+16, sy+15, 5, 5); }
                else if (dir == 'L') { g.fillOval(sx+4,  sy+5,  5, 5); g.fillOval(sx+4,  sy+15, 5, 5); }
                else if (dir == 'U') { g.fillOval(sx+5,  sy+4,  5, 5); g.fillOval(sx+15, sy+4,  5, 5); }
                else                 { g.fillOval(sx+5,  sy+16, 5, 5); g.fillOval(sx+15, sy+16, 5, 5); }

            } else {
                g.setColor(i % 2 == 0 ? skin.bodyColorLight : skin.bodyColorDark);
                g.fillRoundRect(sx + 2, sy + 2, 21, 21, 6, 6);
            }
        }
    }

    private void drawBotSnake(Graphics2D g) {
        if (botFlashing && (botFlashCount / 2) % 2 == 1) return;

        for (int i = 0; i < botSnake.getBodyParts(); i++) {
            int sx = botSnake.getX().get(i) - camX;
            int sy = botSnake.getY().get(i) - camY;
            if (sx < -TILE_SIZE || sx > SCREEN_WIDTH + TILE_SIZE) continue;
            if (sy < -TILE_SIZE || sy > SCREEN_HEIGHT + TILE_SIZE) continue;

            if (i == 0) {
                g.setColor(botSkin.headColor);
                g.fillRoundRect(sx + 1, sy + 1, 23, 23, 8, 8);

                g.setColor(new Color(0, 0, 0, 180));
                g.fillOval(sx + 14, sy - 2, 12, 12);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Consolas", Font.BOLD, 9));
                g.drawString("BOT", sx + 14, sy + 7);

                g.setColor(Color.BLACK);
                char dir = botSnake.getDirection();
                if      (dir == 'R') { g.fillOval(sx+16, sy+5,  5, 5); g.fillOval(sx+16, sy+15, 5, 5); }
                else if (dir == 'L') { g.fillOval(sx+4,  sy+5,  5, 5); g.fillOval(sx+4,  sy+15, 5, 5); }
                else if (dir == 'U') { g.fillOval(sx+5,  sy+4,  5, 5); g.fillOval(sx+15, sy+4,  5, 5); }
                else                 { g.fillOval(sx+5,  sy+16, 5, 5); g.fillOval(sx+15, sy+16, 5, 5); }
            } else {
                g.setColor(i % 2 == 0 ? botSkin.bodyColorLight : botSkin.bodyColorDark);
                g.fillRoundRect(sx + 2, sy + 2, 21, 21, 6, 6);
            }
        }
    }

    private void drawHUD(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, SCREEN_WIDTH, 45);

        g.setFont(new Font("Consolas", Font.BOLD, 16));

        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 28);

        g.setColor(new Color(255, 215, 0));
        g.drawString("Best: " + highScore, 150, 28);

        g.setColor(new Color(100, 200, 255));
        g.drawString("Lv." + level, 300, 28);

        for (int i = 0; i < 3; i++) {
            g.setColor(i < lives ? new Color(255, 60, 60) : new Color(80, 80, 80));
            g.drawString("♥", 410 + i * 30, 28);
        }

        if (botEnabled) {
            g.setColor(botSkin.headColor);
            g.setFont(new Font("Consolas", Font.BOLD, 12));
            g.drawString("BOT: " + botScore, 10, 42);
            for (int i = 0; i < 3; i++) {
                g.setColor(i < botLives ? new Color(255, 120, 0) : new Color(80, 80, 80));
                g.setFont(new Font("Consolas", Font.PLAIN, 11));
                g.drawString("♥", 85 + i * 16, 42);
            }
        }

        g.setFont(new Font("Consolas", Font.PLAIN, 11));
        g.setColor(currentMap.accentColor);
        String mapLabel = "Map: " + currentMap.displayName;
        int labelW = g.getFontMetrics().stringWidth(mapLabel);
        g.drawString(mapLabel, SCREEN_WIDTH - labelW - 8, 15);

        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        g.setColor(new Color(180, 180, 180));
        String soundIcon = (Sound.isMuted() ? "🔇" : "🔊") + " [M]";
        int soundW = g.getFontMetrics().stringWidth(soundIcon);
        g.drawString(soundIcon, SCREEN_WIDTH - soundW - 8, 32);

        if (specialFood != null) {
            String hint = specialFood.getType() == FoodType.BONUS ? "★ BONUS +30!" : "☠ POISON!";
            g.setColor(specialFood.getType().color);
            g.setFont(new Font("Consolas", Font.BOLD, 13));
            g.drawString(hint, 10, SCREEN_HEIGHT - 10);
        }
    }

    private void drawMinimap(Graphics2D g) {
        int mmSize = 90;
        int mmX = SCREEN_WIDTH - mmSize - 10;
        int mmY = SCREEN_HEIGHT - mmSize - 10;
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
        int vw = (int)(SCREEN_WIDTH * scale);
        int vh = (int)(SCREEN_HEIGHT * scale);
        g.drawRect(vx, vy, vw, vh);

        g.setColor(skin.headColor);
        int hx = mmX + (int)(snake.getX().get(0) * scale);
        int hy = mmY + (int)(snake.getY().get(0) * scale);
        g.fillOval(hx - 2, hy - 2, 5, 5);

        if (botEnabled && botLives > 0) {
            g.setColor(botSkin.headColor);
            int bx = mmX + (int)(botSnake.getX().get(0) * scale);
            int by = mmY + (int)(botSnake.getY().get(0) * scale);
            g.fillOval(bx - 2, by - 2, 5, 5);
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
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        g.setColor(new Color(30, 30, 30, 230));
        g.fillRoundRect(100, 130, 400, 330, 30, 30);
        g.setColor(new Color(200, 50, 50));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(100, 130, 400, 330, 30, 30);

        g.setColor(new Color(255, 80, 80));
        g.setFont(new Font("Consolas", Font.BOLD, 48));
        g.drawString("GAME OVER", 128, 210);

        g.setFont(new Font("Consolas", Font.BOLD, 22));
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 230, 270);

        g.setColor(new Color(255, 215, 0));
        g.drawString("Best:  " + highScore, 230, 310);

        if (botEnabled) {
            g.setColor(botSkin.headColor);
            g.setFont(new Font("Consolas", Font.BOLD, 16));
            g.drawString("Bot Score: " + botScore, 220, 345);
        }

        g.setColor(new Color(150, 255, 150));
        g.setFont(new Font("Consolas", Font.PLAIN, 17));
        g.drawString("Nhấn ENTER để chơi lại", 178, 390);

        g.setColor(new Color(150, 150, 255));
        g.drawString("Nhấn ESC để về menu", 178, 420);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            if (flashing) {
                flashCount++;
                if (flashCount >= 10) flashing = false;
            }

            if (botEnabled && botLives > 0) {
                if (botFlashing) {
                    botFlashCount++;
                    if (botFlashCount >= 10) botFlashing = false;
                }
                bot.update();
                botSnake.move();
                checkBotFood();
                checkBotCollision();
            }

            snake.move();
            checkFood();
            checkCollision();
            updateLevel();
            updateCamera();

            if (score > highScore) highScore = score;

            if (specialFood != null && specialFood.isExpired()) specialFood = null;
            if (specialFood == null && random.nextInt(200) == 0) spawnSpecialFood();
        }
        repaint();
    }

    public void checkFood() {
        int hx = snake.getX().get(0), hy = snake.getY().get(0);

        for (Food f : normalFoods) {
            if (hx == f.getX() && hy == f.getY()) {
                snake.grow();
                score += FoodType.NORMAL.points;
                Sound.play(Sound.EAT);
                f.randomize();
                while (isOnWall(f.getX(), f.getY()) || isOnSnake(f.getX(), f.getY())) f.randomize();
                spawnSpecialFood();
                break;
            }
        }

        if (specialFood != null &&
            hx == specialFood.getX() && hy == specialFood.getY()) {
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

    private void checkBotFood() {
        int hx = botSnake.getX().get(0), hy = botSnake.getY().get(0);

        for (Food f : normalFoods) {
            if (hx == f.getX() && hy == f.getY()) {
                botSnake.grow();
                botScore += FoodType.NORMAL.points;
                Sound.play(Sound.EAT);
                f.randomize();
                while (isOnWall(f.getX(), f.getY())) f.randomize();
                break;
            }
        }

        if (specialFood != null &&
            hx == specialFood.getX() && hy == specialFood.getY()) {
            FoodType ft = specialFood.getType();
            if (ft == FoodType.BONUS) {
                botSnake.grow();
                botScore += FoodType.BONUS.points;
            } else if (ft == FoodType.POISON) {
                botLoseLife();
            }
            specialFood = null;
        }
    }

    public void checkCollision() {
        if (!flashing && snake.checkCollision()) {
            loseLife();
            return;
        }
        if (!flashing) {
            int hx = snake.getX().get(0), hy = snake.getY().get(0);
            if (isOnWall(hx, hy)) {
                loseLife();
                return;
            }
            if (botEnabled && botLives > 0) {
                for (int i = 0; i < botSnake.getBodyParts(); i++) {
                    if (botSnake.getX().get(i) == hx && botSnake.getY().get(i) == hy) {
                        loseLife();
                        return;
                    }
                }
            }
        }
    }

    private void checkBotCollision() {
        if (botFlashing) return;
        if (botSnake.checkCollision()) { botLoseLife(); return; }
        int hx = botSnake.getX().get(0), hy = botSnake.getY().get(0);
        if (isOnWall(hx, hy)) { botLoseLife(); return; }
        for (int i = 0; i < snake.getBodyParts(); i++) {
            if (snake.getX().get(i) == hx && snake.getY().get(i) == hy) {
                botLoseLife();
                return;
            }
        }
    }

    private void ensureSafeSpawn(Snake s) {
        for (int i = 0; i < s.getBodyParts(); i++) {
            if (isOnWall(s.getX().get(i), s.getY().get(i))) {
                int offsetX = 250 - s.getX().get(0);
                int offsetY = 250 - s.getY().get(0);
                for (int j = 0; j < s.getBodyParts(); j++) {
                    s.getX().set(j, s.getX().get(j) + offsetX);
                    s.getY().set(j, s.getY().get(j) + offsetY);
                }
                break;
            }
        }
    }

    private boolean isOnWall(int px, int py) {
        for (int[] wall : walls) {
            if (wall[0] == px && wall[1] == py) return true;
        }
        return false;
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:  if (snake.getDirection() != 'R') snake.setDirection('L'); break;
                case KeyEvent.VK_RIGHT: if (snake.getDirection() != 'L') snake.setDirection('R'); break;
                case KeyEvent.VK_UP:    if (snake.getDirection() != 'D') snake.setDirection('U'); break;
                case KeyEvent.VK_DOWN:  if (snake.getDirection() != 'U') snake.setDirection('D'); break;
                case KeyEvent.VK_P:     if (running) paused = !paused; break;
                case KeyEvent.VK_ENTER: if (!running) startGame(); break;
                case KeyEvent.VK_M:     Sound.toggleMuted(); break;
                case KeyEvent.VK_ESCAPE:
                    if (!running) {
                        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(GamePanel.this);
                        if (frame instanceof GameFrame) ((GameFrame) frame).showStartScreen();
                    }
                    break;
            }
        }
    }
}