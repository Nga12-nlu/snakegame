package snakegame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SnakeBot {

    private Snake snake;
    private List<int[]> walls;
    private List<Snake> otherSnakes;
    private Random random = new Random();

    private static final char[] DIRS = {'U', 'D', 'L', 'R'};

    public SnakeBot(Snake snake, List<int[]> walls, List<Snake> otherSnakes) {
        this.snake = snake;
        this.walls = walls;
        this.otherSnakes = otherSnakes;
    }

    public void setOtherSnakes(List<Snake> others) {
        this.otherSnakes = others;
    }

    public void update() {
        char current = snake.getDirection();
        char opposite = opposite(current);

        int hx = snake.getX().get(0);
        int hy = snake.getY().get(0);

        List<Food> allFoods = new ArrayList<>();

        char bestDir = current;
        int bestScore = -9999;

        List<Character> shuffled = new ArrayList<>();
        for (char d : DIRS) if (d != opposite) shuffled.add(d);
        Collections.shuffle(shuffled, random);

        for (char dir : shuffled) {
            int nx = hx, ny = hy;
            switch (dir) {
                case 'U': ny -= GameConfig.TILE_SIZE; break;
                case 'D': ny += GameConfig.TILE_SIZE; break;
                case 'L': nx -= GameConfig.TILE_SIZE; break;
                case 'R': nx += GameConfig.TILE_SIZE; break;
            }

            if (isSafe(nx, ny)) {
                int sc = scoreDirection(nx, ny);
                if (sc > bestScore) {
                    bestScore = sc;
                    bestDir = dir;
                }
            }
        }

        if (!isSafe(nextX(hx, bestDir), nextY(hy, bestDir))) {
            for (char dir : DIRS) {
                if (dir == opposite) continue;
                int nx = nextX(hx, dir), ny = nextY(hy, dir);
                if (isSafe(nx, ny)) { bestDir = dir; break; }
            }
        }

        snake.setDirection(bestDir);
    }

    private int scoreDirection(int nx, int ny) {
        int score = random.nextInt(10);
        int margin = GameConfig.TILE_SIZE * 3;
        if (nx < margin || nx > GameConfig.WORLD_WIDTH - margin) score -= 20;
        if (ny < margin || ny > GameConfig.WORLD_HEIGHT - margin) score -= 20;

        for (int[] w : walls) {
            int dx = Math.abs(w[0] - nx);
            int dy = Math.abs(w[1] - ny);
            if (dx < GameConfig.TILE_SIZE * 2 && dy < GameConfig.TILE_SIZE * 2) score -= 15;
        }

        for (Snake s : otherSnakes) {
            if (s == null) continue;
            for (int i = 0; i < s.getBodyParts(); i++) {
                int dx = Math.abs(s.getX().get(i) - nx);
                int dy = Math.abs(s.getY().get(i) - ny);
                if (dx < GameConfig.TILE_SIZE * 2 && dy < GameConfig.TILE_SIZE * 2) score -= 25;
            }
        }

        return score;
    }

    private boolean isSafe(int nx, int ny) {
        if (nx < 0 || nx >= GameConfig.WORLD_WIDTH) return false;
        if (ny < 0 || ny >= GameConfig.WORLD_HEIGHT) return false;
        for (int[] w : walls) if (w[0] == nx && w[1] == ny) return false;
        for (int i = 1; i < snake.getBodyParts(); i++)
            if (snake.getX().get(i) == nx && snake.getY().get(i) == ny) return false;
        for (Snake s : otherSnakes) {
            if (s == null) continue;
            for (int i = 0; i < s.getBodyParts(); i++)
                if (s.getX().get(i) == nx && s.getY().get(i) == ny) return false;
        }
        return true;
    }

    private char opposite(char dir) {
        switch (dir) {
            case 'U': return 'D';
            case 'D': return 'U';
            case 'L': return 'R';
            case 'R': return 'L';
        }
        return 'R';
    }

    private int nextX(int hx, char dir) {
        if (dir == 'L') return hx - GameConfig.TILE_SIZE;
        if (dir == 'R') return hx + GameConfig.TILE_SIZE;
        return hx;
    }

    private int nextY(int hy, char dir) {
        if (dir == 'U') return hy - GameConfig.TILE_SIZE;
        if (dir == 'D') return hy + GameConfig.TILE_SIZE;
        return hy;
    }
}