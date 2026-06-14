package snakegame;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MapType – định nghĩa các bản đồ cho Snake Game.
 * World có kích thước 1200x1200 (48x48 ô 25px).
 * Mỗi map có: tên hiển thị, mô tả, màu tường và danh sách tọa độ tường (ô 25x25).
 */
public enum MapType {

    // ── 1. MAP TRỐNG (Classic) ────────────────────────────────────────────────
    OPEN("Open Field", "Không có vật cản, dễ chơi", new Color(60, 180, 60)) {
        @Override
        public List<int[]> buildWalls() {
            return new ArrayList<>();
        }
    },

    // ── 2. MAP VIỀN TỨ PHÍA (Border) ─────────────────────────────────────────
    BORDER("Border Maze", "Viền tường bên trong, tránh góc chết", new Color(100, 160, 255)) {
        @Override
        public List<int[]> buildWalls() {
            List<int[]> w = new ArrayList<>();
            int S = 25;
            // Viền cách mép world 4 ô, từ cột/hàng 4 đến 43
            for (int c = 4; c <= 43; c++) {
                w.add(new int[]{c * S, 4 * S});
                w.add(new int[]{c * S, 43 * S});
            }
            for (int r = 5; r <= 42; r++) {
                w.add(new int[]{4 * S, r * S});
                w.add(new int[]{43 * S, r * S});
            }
            return w;
        }
    },

    // ── 3. MAP CHỮ THẬP (Cross) ──────────────────────────────────────────────
    CROSS("Cross Roads", "Tường hình chữ thập giữa map, chia 4 khu vực", new Color(255, 160, 50)) {
        @Override
        public List<int[]> buildWalls() {
            List<int[]> w = new ArrayList<>();
            int S = 25;
            // Thanh ngang giữa world (hàng 23-24, từ cột 4 đến 43)
            for (int c = 4; c <= 43; c++) {
                w.add(new int[]{c * S, 23 * S});
                w.add(new int[]{c * S, 24 * S});
            }
            // Thanh dọc giữa world (cột 23-24, từ hàng 4 đến 43)
            for (int r = 4; r <= 43; r++) {
                w.add(new int[]{23 * S, r * S});
                w.add(new int[]{24 * S, r * S});
            }
            return w;
        }
    },

    // ── 4. MAP MÔNG LUNG (Labyrinth) ─────────────────────────────────────────
    LABYRINTH("Labyrinth", "Mê cung lớn thử thách phản xạ", new Color(200, 80, 255)) {
        @Override
        public List<int[]> buildWalls() {
            List<int[]> w = new ArrayList<>();
            int S = 25;
            // Lặp pattern mê cung trên 4 góc của world 48x48 (offset 0 và 24)
            int[] offsets = {0, 24};
            for (int ox : offsets) {
                for (int oy : offsets) {
                    addLabyrinthBlock(w, S, ox, oy);
                }
            }
            return w;
        }

        private void addLabyrinthBlock(List<int[]> w, int S, int ox, int oy) {
            // Tường trên: hàng 3, cột 2-10
            for (int c = 2; c <= 10; c++) w.add(new int[]{(ox+c) * S, (oy+3) * S});
            // Tường trên phải: hàng 3, cột 13-21
            for (int c = 13; c <= 21; c++) w.add(new int[]{(ox+c) * S, (oy+3) * S});
            // Tường trái dưới: cột 2, hàng 4-12
            for (int r = 4; r <= 12; r++) w.add(new int[]{(ox+2) * S, (oy+r) * S});
            // Tường phải dưới: cột 21, hàng 4-12
            for (int r = 4; r <= 12; r++) w.add(new int[]{(ox+21) * S, (oy+r) * S});
            // Tường giữa ngang: hàng 8, cột 5-10
            for (int c = 5; c <= 10; c++) w.add(new int[]{(ox+c) * S, (oy+8) * S});
            // Tường giữa ngang: hàng 8, cột 13-18
            for (int c = 13; c <= 18; c++) w.add(new int[]{(ox+c) * S, (oy+8) * S});
            // Tường giữa dọc: cột 7, hàng 9-14
            for (int r = 9; r <= 14; r++) w.add(new int[]{(ox+7) * S, (oy+r) * S});
            // Tường giữa dọc: cột 16, hàng 9-14
            for (int r = 9; r <= 14; r++) w.add(new int[]{(ox+16) * S, (oy+r) * S});
            // Tường dưới: hàng 17, cột 4-11
            for (int c = 4; c <= 11; c++) w.add(new int[]{(ox+c) * S, (oy+17) * S});
            // Tường dưới phải: hàng 17, cột 13-19
            for (int c = 13; c <= 19; c++) w.add(new int[]{(ox+c) * S, (oy+17) * S});
        }
    },

    // ── 5. MAP PHÒNG (Rooms) ─────────────────────────────────────────────────
    ROOMS("Four Rooms", "Nhiều phòng lớn với cửa nhỏ qua lại", new Color(255, 80, 120)) {
        @Override
        public List<int[]> buildWalls() {
            List<int[]> w = new ArrayList<>();
            int S = 25;
            // Tường ngang giữa world (hàng 23), có 4 cửa
            for (int c = 1; c <= 46; c++) {
                if (c != 10 && c != 22 && c != 26 && c != 38)
                    w.add(new int[]{c * S, 23 * S});
            }
            // Tường dọc giữa world (cột 23), có 4 cửa
            for (int r = 1; r <= 46; r++) {
                if (r != 10 && r != 22 && r != 26 && r != 38)
                    w.add(new int[]{23 * S, r * S});
            }
            return w;
        }
    };

    // ── THUỘC TÍNH ────────────────────────────────────────────────────────────
    public final String displayName;
    public final String description;
    public final Color  accentColor;

    MapType(String displayName, String description, Color accentColor) {
        this.displayName = displayName;
        this.description = description;
        this.accentColor = accentColor;
    }

    /** Trả về danh sách các ô tường [{x, y}, ...] theo đơn vị pixel (bội số của 25), trong world 1200x1200. */
    public abstract List<int[]> buildWalls();
}