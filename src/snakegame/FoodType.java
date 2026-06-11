package snakegame;

import java.awt.Color;

public enum FoodType {
    NORMAL(Color.RED, 10, -1),       // food thường, +10đ, không hết hạn
    BONUS(new Color(255, 215, 0), 30, 5000),  // food vàng, +30đ, tồn tại 5 giây
    POISON(new Color(160, 32, 240), -1, 8000); // food độc, -1 mạng, tồn tại 8 giây

    public final Color color;
    public final int points;
    public final int durationMs; // -1 = không hết hạn

    FoodType(Color color, int points, int durationMs) {
        this.color = color;
        this.points = points;
        this.durationMs = durationMs;
    }
}