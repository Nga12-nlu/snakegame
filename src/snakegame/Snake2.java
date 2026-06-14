package snakegame;

public class Snake2 extends Snake {

    public Snake2() {
        reset();
    }

    @Override
    public void reset() {
        x.clear();
        y.clear();
        if (trail != null) trail.clear();
        bodyParts = 6;
        direction = 'L';
        // Đầu xuất phát ở góc phải dưới của world, đuôi kéo sang phải
        int startX = GameConfig.WORLD_WIDTH  - 300;
        int startY = GameConfig.WORLD_HEIGHT - 300;
        for (int i = 0; i < bodyParts; i++) {
            x.add(startX + i * 25);
            y.add(startY);
        }
    }
}