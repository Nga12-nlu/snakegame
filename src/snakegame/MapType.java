package snakegame;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MapType – định nghĩa các bản đồ cho Snake Game.
 * Mỗi map có: tên hiển thị, mô tả, màu tường và danh sách tọa độ tường (ô 25x25).
 */
public enum MapType {

    // ── 1. MAP TRỐNG (Classic) ────────────────────────────────────────────────
    OPEN("Open Field", "Không có vật cản, dễ chơi", new Color(60, 180, 60)) {
        @Override
        public List<int[]> buildWalls() {
            return new ArrayList<>();   // không có tường
        }
    },

    // ── 2. MAP VIỀN TỨ PHÍA (Border) ─────────────────────────────────────────
    BORDER("Border Maze", "Viền tường bên trong, tránh góc chết", new Color(100, 160, 255)) {
        @Override
        public List<int[]> buildWalls() {
            List<int[]> w = new ArrayList<>();
            int S = 25, COLS = 24, ROWS = 24;
            // Hàng 4 (y=100) và hàng 19 (y=475), từ cột 4 đến 19
            for (int c = 4; c <= 19; c++) {
                w.add(new int[]{c * S, 4 * S});
                w.add(new int[]{c * S, 19 * S});
            }
            // Cột 4 (x=100) và cột 19 (x=475), từ hàng 5 đến 18
            for (int r = 5; r <= 18; r++) {
                w.add(new int[]{4 * S, r * S});
                w.add(new int[]{19 * S, r * S});
            }
            return w;
        }
    },

    // ── 3. MAP CHỮ THẬP (Cross) ──────────────────────────────────────────────
    CROSS("Cross Roads", "Tường hình chữ thập giữa màn hình", new Color(255, 160, 50)) {
        @Override
        public List<int[]> buildWalls() {
            List<int[]> w = new ArrayList<>();
            int S = 25;
            // Thanh ngang giữa (y=275–300, từ x=100 đến x=500)
            for (int c = 4; c <= 19; c++) {
                w.add(new int[]{c * S, 11 * S});
                w.add(new int[]{c * S, 12 * S});
            }
            // Thanh dọc giữa (x=275–300, từ y=100 đến y=500)
            for (int r = 4; r <= 19; r++) {
                w.add(new int[]{11 * S, r * S});
                w.add(new int[]{12 * S, r * S});
            }
            return w;
        }
    },

    // ── 4. MAP MÔNG LUNG (Labyrinth) ─────────────────────────────────────────
    LABYRINTH("Labyrinth", "Mê cung thử thách phản xạ", new Color(200, 80, 255)) {
        @Override
        public List<int[]> buildWalls() {
            List<int[]> w = new ArrayList<>();
            int S = 25;
            // Tường trên: hàng 3, cột 2-10
            for (int c = 2; c <= 10; c++) w.add(new int[]{c * S, 3 * S});
            // Tường trên phải: hàng 3, cột 13-21
            for (int c = 13; c <= 21; c++) w.add(new int[]{c * S, 3 * S});
            // Tường trái dưới: cột 2, hàng 4-12
            for (int r = 4; r <= 12; r++) w.add(new int[]{2 * S, r * S});
            // Tường phải dưới: cột 21, hàng 4-12
            for (int r = 4; r <= 12; r++) w.add(new int[]{21 * S, r * S});
            // Tường giữa ngang: hàng 8, cột 5-10
            for (int c = 5; c <= 10; c++) w.add(new int[]{c * S, 8 * S});
            // Tường giữa ngang: hàng 8, cột 13-18
            for (int c = 13; c <= 18; c++) w.add(new int[]{c * S, 8 * S});
            // Tường giữa dọc: cột 7, hàng 9-14
            for (int r = 9; r <= 14; r++) w.add(new int[]{7 * S, r * S});
            // Tường giữa dọc: cột 16, hàng 9-14
            for (int r = 9; r <= 14; r++) w.add(new int[]{16 * S, r * S});
            // Tường dưới: hàng 17, cột 4-11
            for (int c = 4; c <= 11; c++) w.add(new int[]{c * S, 17 * S});
            // Tường dưới phải: hàng 17, cột 13-19
            for (int c = 13; c <= 19; c++) w.add(new int[]{c * S, 17 * S});
            // Tường đáy: hàng 21, cột 2-21
            for (int c = 2; c <= 21; c++) w.add(new int[]{c * S, 21 * S});
            return w;
        }
    },

    // ── 5. MAP PHÒNG (Rooms) ─────────────────────────────────────────────────
    ROOMS("Four Rooms", "4 phòng với cửa nhỏ qua lại", new Color(255, 80, 120)) {
        @Override
        public List<int[]> buildWalls() {
            List<int[]> w = new ArrayList<>();
            int S = 25;
            // Tường ngang giữa (hàng 11), có cửa ở cột 5 và cột 18
            for (int c = 1; c <= 22; c++) {
                if (c != 5 && c != 18) w.add(new int[]{c * S, 11 * S});
            }
            // Tường dọc giữa (cột 11), có cửa ở hàng 5 và hàng 18
            for (int r = 1; r <= 22; r++) {
                if (r != 5 && r != 18) w.add(new int[]{11 * S, r * S});
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

    /** Trả về danh sách các ô tường [{x, y}, ...] theo đơn vị pixel (bội số của 25). */
    public abstract List<int[]> buildWalls();
}