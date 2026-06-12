package snakegame;

import java.awt.Color;

public enum SnakeSkin {

    CLASSIC("Classic",
            new Color(0, 220, 80),
            new Color(0, 160, 40),
            new Color(0, 200, 60),
            "Rắn xanh lá cổ điển"),

    OCEAN("Ocean",
            new Color(0, 180, 255),
            new Color(0, 100, 200),
            new Color(0, 150, 230),
            "Rắn xanh biển"),

    LAVA("Lava",
            new Color(255, 80, 0),
            new Color(180, 30, 0),
            new Color(220, 60, 0),
            "Rắn dung nham đỏ"),

    GALAXY("Galaxy",
            new Color(180, 80, 255),
            new Color(100, 20, 180),
            new Color(140, 50, 220),
            "Rắn thiên hà tím"),

    GOLD("Gold",
            new Color(255, 215, 0),
            new Color(180, 140, 0),
            new Color(220, 180, 0),
            "Rắn vàng hoàng gia"),

    NEON("Neon",
            new Color(0, 255, 200),
            new Color(0, 180, 130),
            new Color(0, 220, 170),
            "Rắn neon phát sáng"),

    CANDY("Candy",
            new Color(255, 100, 180),
            new Color(200, 50, 130),
            new Color(230, 80, 160),
            "Rắn kẹo ngọt"),

    SHADOW("Shadow",
            new Color(80, 80, 80),
            new Color(30, 30, 30),
            new Color(55, 55, 55),
            "Rắn bóng tối");

    public final String displayName;
    public final Color headColor;
    public final Color bodyColorDark;
    public final Color bodyColorLight;
    public final String description;

    SnakeSkin(String displayName, Color headColor, Color bodyColorDark,
              Color bodyColorLight, String description) {
        this.displayName    = displayName;
        this.headColor      = headColor;
        this.bodyColorDark  = bodyColorDark;
        this.bodyColorLight = bodyColorLight;
        this.description    = description;
    }
}