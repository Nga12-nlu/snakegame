package snakegame;

import java.util.Random;

public class Food {

    private int x;
    private int y;
    private final int TILE_SIZE = 25;
    private final int SCREEN_WIDTH = 600;
    private final int SCREEN_HEIGHT = 600;

    private FoodType type;
    private long spawnTime;
    private Random random;

    public Food(FoodType type) {
        this.random = new Random();
        this.type = type;
        randomize();
    }

    public void randomize() {
        x = random.nextInt(SCREEN_WIDTH / TILE_SIZE) * TILE_SIZE;
        y = random.nextInt(SCREEN_HEIGHT / TILE_SIZE) * TILE_SIZE;
        spawnTime = System.currentTimeMillis();
    }

    public boolean isExpired() {
        if (type.durationMs == -1) return false;
        return (System.currentTimeMillis() - spawnTime) > type.durationMs;
    }

    public FoodType getType() { return type; }
    public int getX() { return x; }
    public int getY() { return y; }
}