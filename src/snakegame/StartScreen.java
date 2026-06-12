package snakegame;

import javax.swing.*;
import java.awt.*;

public class StartScreen extends JPanel {

    private static final long serialVersionUID = 1L;

    public StartScreen(GameFrame frame) {
        this.setPreferredSize(new Dimension(600, 600));
        this.setLayout(null);
        this.setBackground(Color.BLACK);

        // Title
        JLabel title = new JLabel("🐍 SNAKE GAME");
        title.setBounds(100, 80, 400, 70);
        title.setFont(new Font("Consolas", Font.BOLD, 38));
        title.setForeground(new Color(0, 220, 80));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(title);

        // Subtitle
        JLabel sub = new JLabel("Nâng cấp Edition");
        sub.setBounds(100, 155, 400, 30);
        sub.setFont(new Font("Consolas", Font.ITALIC, 16));
        sub.setForeground(new Color(150, 150, 150));
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(sub);

        // Hướng dẫn
        String[] tips = {
            "🔴 Food thường  +10 điểm",
            "⭐ Food vàng    +30 điểm  (có thời hạn)",
            "💜 Food độc     mất 1 ❤   (có thời hạn)",
            "⬆ ⬇ ◀ ▶  Di chuyển",
            "P  —  Tạm dừng"
        };

        int yy = 220;
        for (String tip : tips) {
            JLabel lbl = new JLabel(tip);
            lbl.setBounds(130, yy, 340, 28);
            lbl.setFont(new Font("Consolas", Font.PLAIN, 14));
            lbl.setForeground(new Color(200, 200, 200));
            this.add(lbl);
            yy += 32;
        }

        // Nút chơi
        JButton btn = new JButton("▶  Chơi ngay");
        btn.setBounds(175, 430, 250, 55);
        btn.setFont(new Font("Consolas", Font.BOLD, 20));
        btn.setBackground(new Color(0, 180, 60));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> frame.startGame());
        this.add(btn);
    }
}