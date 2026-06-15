package snakegame;

import java.util.ArrayList;

public class Snake {

    protected ArrayList<Integer> x = new ArrayList<>();
    protected ArrayList<Integer> y = new ArrayList<>();

    protected int bodyParts = 6;
    protected char direction = 'R';

    // Trail glow: lưu các vị trí cũ để vẽ hiệu ứng đuôi mờ dần
    protected ArrayList<int[]> trail = new ArrayList<>();
    private static final int TRAIL_LEN = 8; // Tăng lên 8 cho mượt hơn

    public Snake() {
        initDefault();
    }

    protected void initDefault() {
        x.clear(); 
        y.clear(); 
        trail.clear();
        bodyParts = 6;
        direction = 'R';
        
        // Spawn ở vị trí an toàn (cách border ít nhất 2 ô)
        // Giả sử BORDER = 100 (4 ô * 25), WORLD_WIDTH = 700 (28 ô)
        int startCol = 8;  // ô thứ 8
        int startRow = 8;  // ô thứ 8
        int startX = startCol * 25;
        int startY = startRow * 25;
        
        for (int i = 0; i < bodyParts; i++) {
            x.add(startX - i * 25);
            y.add(startY);
        }
    }

    public void reset() {
        initDefault();
    }

    public void move() {
        // Lưu vị trí đuôi trước khi dịch chuyển (cho trail)
        if (bodyParts > 0 && !x.isEmpty()) {
            int lastIndex = bodyParts - 1;
            trail.add(new int[]{ x.get(lastIndex), y.get(lastIndex) });
            while (trail.size() > TRAIL_LEN) {
                trail.remove(0);
            }
        }

        // Dịch chuyển thân
        for (int i = bodyParts - 1; i > 0; i--) {
            x.set(i, x.get(i - 1));
            y.set(i, y.get(i - 1));
        }
        
        // Dịch chuyển đầu
        switch (direction) {
            case 'U': y.set(0, y.get(0) - GameConfig.TILE_SIZE); break;
            case 'D': y.set(0, y.get(0) + GameConfig.TILE_SIZE); break;
            case 'L': x.set(0, x.get(0) - GameConfig.TILE_SIZE); break;
            case 'R': x.set(0, x.get(0) + GameConfig.TILE_SIZE); break;
        }
    }

    public void grow() {
        if (x.isEmpty() || y.isEmpty()) return;
        
        bodyParts++;
        int lastIndex = x.size() - 1;
        // Thêm vào cuối với vị trí trùng phần tử cuối (sẽ được cập nhật ở lần move tiếp theo)
        x.add(x.get(lastIndex));
        y.add(y.get(lastIndex));
    }

    public boolean checkCollision() {
        // Kiểm tra tự cắn (bỏ qua kiểm tra nếu đầu rắn quá gần? không, kiểm tra bình thường)
        for (int i = bodyParts - 1; i > 0; i--) {
            if (x.get(0).equals(x.get(i)) && y.get(0).equals(y.get(i))) {
                return true;
            }
        }
        
        // Kiểm tra va chạm tường dựa trên GameConfig
        if (x.get(0) < 0 || x.get(0) >= GameConfig.WORLD_WIDTH
         || y.get(0) < 0 || y.get(0) >= GameConfig.WORLD_HEIGHT) {
            return true;
        }
        
        return false;
    }

    // Thêm method tiện ích
    public int getHeadX() { return x.isEmpty() ? 0 : x.get(0); }
    public int getHeadY() { return y.isEmpty() ? 0 : y.get(0); }
    
    public boolean contains(int px, int py) {
        for (int i = 0; i < bodyParts; i++) {
            if (x.get(i) == px && y.get(i) == py) return true;
        }
        return false;
    }

    // Getters/Setters
    public ArrayList<Integer> getX() { return x; }
    public ArrayList<Integer> getY() { return y; }
    public int getBodyParts() { return bodyParts; }
    public void setDirection(char dir) { 
        // Chống đảo chiều 180 độ
        if ((direction == 'U' && dir == 'D') ||
            (direction == 'D' && dir == 'U') ||
            (direction == 'L' && dir == 'R') ||
            (direction == 'R' && dir == 'L')) {
            return;
        }
        this.direction = dir; 
    }
    public char getDirection() { return direction; }
    public ArrayList<int[]> getTrail() { return trail; }
}
