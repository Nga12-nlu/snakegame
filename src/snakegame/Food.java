package snakegame;

import java.util.Random;

public class Food {

    private int x;
    private int y;

    private FoodType type;
    private long spawnTime;
    private Random random;

    public Food(FoodType type) {
        this.random = new Random();
        this.type = type;
        randomize();
    }

    public void randomize() {
        x = random.nextInt(GameConfig.WORLD_COLS) * GameConfig.TILE_SIZE;
        y = random.nextInt(GameConfig.WORLD_ROWS) * GameConfig.TILE_SIZE;
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