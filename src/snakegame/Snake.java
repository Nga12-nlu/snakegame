package snakegame;

import java.util.ArrayList;

public class Snake {

    protected ArrayList<Integer> x = new ArrayList<>();
    protected ArrayList<Integer> y = new ArrayList<>();

    protected int  bodyParts = 6;
    protected char direction = 'R';

    // Trail glow: lưu các vị trí cũ để vẽ hiệu ứng đuôi mờ dần
    protected ArrayList<int[]> trail = new ArrayList<>();
    private static final int TRAIL_LEN = 5;

    public Snake() {
        // Gọi trực tiếp logic init thay vì reset() để tránh
        // subclass override reset() bị gọi trước khi fields của subclass sẵn sàng
        initDefault();
    }

    /** Khởi tạo vị trí mặc định P1: đầu ở góc trên-trái khu trung tâm, hướng R */
    protected void initDefault() {
        x.clear(); y.clear(); trail.clear();
        bodyParts = 6;
        direction = 'R';
        // Spawn cách mép world 1 chút và tránh vùng tường biên (BORDER cách mép 4 ô = 100px)
        int startX = 150;
        int startY = 150;
        for (int i = 0; i < bodyParts; i++) {
            x.add(startX - i * 25);
            y.add(startY);
        }
    }

    /** Reset P1 về vị trí ban đầu (subclass override để đặt vị trí riêng) */
    public void reset() {
        initDefault();
    }

    public void move() {
        // Lưu vị trí đuôi trước khi dịch chuyển (cho trail)
        if (bodyParts > 0) {
            trail.add(new int[]{ x.get(bodyParts - 1), y.get(bodyParts - 1) });
            if (trail.size() > TRAIL_LEN) trail.remove(0);
        }

        for (int i = bodyParts - 1; i > 0; i--) {
            x.set(i, x.get(i - 1));
            y.set(i, y.get(i - 1));
        }
        switch (direction) {
            case 'U': y.set(0, y.get(0) - 25); break;
            case 'D': y.set(0, y.get(0) + 25); break;
            case 'L': x.set(0, x.get(0) - 25); break;
            case 'R': x.set(0, x.get(0) + 25); break;
        }
    }

    public void grow() {
        bodyParts++;
        x.add(x.get(x.size() - 1));
        y.add(y.get(y.size() - 1));
    }

    public boolean checkCollision() {
        for (int i = bodyParts - 1; i > 0; i--)
            if (x.get(0).equals(x.get(i)) && y.get(0).equals(y.get(i))) return true;
        if (x.get(0) < 0 || x.get(0) >= GameConfig.WORLD_WIDTH
         || y.get(0) < 0 || y.get(0) >= GameConfig.WORLD_HEIGHT) return true;
        return false;
    }

    public ArrayList<Integer>  getX()         { return x; }
    public ArrayList<Integer>  getY()         { return y; }
    public int                 getBodyParts() { return bodyParts; }
    public void                setDirection(char dir) { this.direction = dir; }
    public char                getDirection() { return direction; }
    public ArrayList<int[]>    getTrail()     { return trail; }
}