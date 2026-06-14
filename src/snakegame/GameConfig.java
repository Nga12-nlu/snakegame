package snakegame;

/**
 * GameConfig – Các hằng số chung cho kích thước world và viewport.
 * World lớn hơn màn hình hiển thị (viewport), camera sẽ theo dõi đầu rắn.
 */
public class GameConfig {

    public static final int TILE_SIZE = 25;

    // Kích thước viewport (màn hình hiển thị) – không đổi
    public static final int VIEW_WIDTH  = 600;
    public static final int VIEW_HEIGHT = 600;

    // Kích thước world (bản đồ thực tế) – gấp đôi viewport
    public static final int WORLD_WIDTH  = 1200;
    public static final int WORLD_HEIGHT = 1200;

    // Số ô theo world
    public static final int WORLD_COLS = WORLD_WIDTH  / TILE_SIZE; // 48
    public static final int WORLD_ROWS = WORLD_HEIGHT / TILE_SIZE; // 48

    // Số food xuất hiện cùng lúc trên world
    public static final int FOOD_COUNT = 12;
}