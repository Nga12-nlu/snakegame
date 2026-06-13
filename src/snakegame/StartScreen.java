package snakegame;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class StartScreen extends JPanel {

    private static final long serialVersionUID = 1L;

    private SnakeSkin selectedSkin = SnakeSkin.CLASSIC;
    private JLabel    selectedLabel = null;

    // GamePanel đọc biến này để lấy skin
    public static SnakeSkin chosenSkin = SnakeSkin.CLASSIC;

    // THÊM MỚI: biến lưu map đã chọn, GamePanel đọc biến này
    public static MapType chosenMap = MapType.OPEN;

    private MapType   selectedMap  = MapType.OPEN;
    private JLabel    selectedMapLabel = null;

    public StartScreen(GameFrame frame) {
        // THAY ĐỔI: tăng chiều cao panel để chứa thêm khu chọn map
        this.setPreferredSize(new Dimension(600, 820));
        this.setLayout(null);
        this.setBackground(new Color(10, 10, 18));

        // ── TITLE ──────────────────────────────────────────────
        JLabel title = new JLabel("🐍 SNAKE GAME");
        title.setBounds(0, 22, 600, 65);
        title.setFont(new Font("Consolas", Font.BOLD, 40));
        title.setForeground(new Color(0, 220, 80));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(title);

        JLabel sub = new JLabel("Nâng cấp Edition");
        sub.setBounds(0, 88, 600, 24);
        sub.setFont(new Font("Consolas", Font.ITALIC, 15));
        sub.setForeground(new Color(120, 120, 140));
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(sub);

        // ── HƯỚNG DẪN ──────────────────────────────────────────
        String[][] tips = {
            {"🔴", "Food thường  +10 điểm"},
            {"⭐", "Food vàng    +30 điểm  (có thời hạn)"},
            {"💜", "Food độc     mất 1 ❤   (có thời hạn)"},
            {"⬆⬇◀▶", "Di chuyển"},
            {"P", "Tạm dừng / Tiếp tục"}
        };

        int yy = 122;
        for (String[] tip : tips) {
            JLabel icon = new JLabel(tip[0]);
            icon.setBounds(90, yy, 55, 22);
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            icon.setForeground(Color.WHITE);
            this.add(icon);

            JLabel txt = new JLabel(tip[1]);
            txt.setBounds(148, yy, 360, 22);
            txt.setFont(new Font("Consolas", Font.PLAIN, 13));
            txt.setForeground(new Color(190, 190, 200));
            this.add(txt);
            yy += 26;
        }

        // ── SKIN SELECTOR ──────────────────────────────────────
        JLabel skinTitle = new JLabel("CHỌN SKIN RẮN");
        skinTitle.setBounds(0, 262, 600, 24);
        skinTitle.setFont(new Font("Consolas", Font.BOLD, 14));
        skinTitle.setForeground(new Color(255, 215, 0));
        skinTitle.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(skinTitle);

        SnakeSkin[] skins = SnakeSkin.values();
        int cardW = 120, cardH = 72, gapX = 12, gapY = 8;
        int startX = (600 - (cardW * 4 + gapX * 3)) / 2;
        int startY = 292;

        for (int i = 0; i < skins.length; i++) {
            SnakeSkin skin = skins[i];
            int col = i % 4, row = i / 4;
            int cx = startX + col * (cardW + gapX);
            int cy = startY + row * (cardH + gapY);

            JLabel card = buildSkinCard(skin, cardW, cardH);
            card.setBounds(cx, cy, cardW, cardH);

            if (skin == SnakeSkin.CLASSIC) {
                card.setBorder(new LineBorder(new Color(0, 230, 90), 2));
                selectedLabel = card;
            }

            card.addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    if (selectedLabel != null)
                        selectedLabel.setBorder(new LineBorder(new Color(50, 50, 60), 1));
                    selectedSkin  = skin;
                    chosenSkin    = skin;
                    selectedLabel = card;
                    card.setBorder(new LineBorder(skin.headColor, 2));
                }
                @Override public void mouseEntered(MouseEvent e) {
                    if (card != selectedLabel)
                        card.setBorder(new LineBorder(skin.headColor.darker(), 1));
                    card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                @Override public void mouseExited(MouseEvent e) {
                    if (card != selectedLabel)
                        card.setBorder(new LineBorder(new Color(50, 50, 60), 1));
                }
            });
            this.add(card);
        }

        // ── MAP SELECTOR (THÊM MỚI) ────────────────────────────
        JLabel mapTitle = new JLabel("CHỌN MAP");
        mapTitle.setBounds(0, 458, 600, 24);
        mapTitle.setFont(new Font("Consolas", Font.BOLD, 14));
        mapTitle.setForeground(new Color(100, 200, 255));
        mapTitle.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(mapTitle);

        MapType[] maps = MapType.values();
        int mCardW = 170, mCardH = 60, mGapX = 10, mGapY = 8;
        // 3 map mỗi hàng
        int mStartX = (600 - (mCardW * 3 + mGapX * 2)) / 2;
        int mStartY = 488;

        for (int i = 0; i < maps.length; i++) {
            MapType map = maps[i];
            int col = i % 3, row = i / 3;
            int cx = mStartX + col * (mCardW + mGapX);
            int cy = mStartY + row * (mCardH + mGapY);

            JLabel mCard = buildMapCard(map, mCardW, mCardH);
            mCard.setBounds(cx, cy, mCardW, mCardH);

            if (map == MapType.OPEN) {
                mCard.setBorder(new LineBorder(new Color(100, 200, 255), 2));
                selectedMapLabel = mCard;
            }

            mCard.addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    if (selectedMapLabel != null)
                        selectedMapLabel.setBorder(new LineBorder(new Color(50, 50, 60), 1));
                    selectedMap      = map;
                    chosenMap        = map;
                    selectedMapLabel = mCard;
                    mCard.setBorder(new LineBorder(map.accentColor, 2));
                }
                @Override public void mouseEntered(MouseEvent e) {
                    if (mCard != selectedMapLabel)
                        mCard.setBorder(new LineBorder(map.accentColor.darker(), 1));
                    mCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                @Override public void mouseExited(MouseEvent e) {
                    if (mCard != selectedMapLabel)
                        mCard.setBorder(new LineBorder(new Color(50, 50, 60), 1));
                }
            });
            this.add(mCard);
        }

        // ── NÚT CHƠI ──────────────────────────────────────────
        JButton btn = new JButton("▶  CHƠI NGAY");
        btn.setBounds(175, 690, 250, 52);
        btn.setFont(new Font("Consolas", Font.BOLD, 19));
        btn.setBackground(new Color(0, 190, 65));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(0, 220, 80)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(new Color(0, 190, 65)); }
        });
        btn.addActionListener(e -> {
            chosenSkin = selectedSkin;
            chosenMap  = selectedMap;   // THÊM MỚI: lưu map trước khi start
            frame.startGame();
        });
        this.add(btn);

        // ── VERSION ────────────────────────────────────────────
        JLabel ver = new JLabel("v2.1 Map Edition");
        ver.setBounds(0, 760, 600, 20);
        ver.setFont(new Font("Consolas", Font.PLAIN, 11));
        ver.setForeground(new Color(70, 70, 80));
        ver.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(ver);
    }

    // ── BUILD SKIN CARD (giữ nguyên) ───────────────────────────────────────
    private JLabel buildSkinCard(SnakeSkin skin, int w, int h) {
        JLabel card = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // Nền card
                g2.setColor(new Color(22, 22, 32));
                g2.fillRoundRect(0, 0, w - 1, h - 1, 10, 10);
                // Preview rắn mini (3 đốt)
                int bx = 12, by = h / 2 - 7;
                g2.setColor(skin.bodyColorDark);
                g2.fillRoundRect(bx + 28, by + 2, 12, 12, 5, 5);
                g2.setColor(skin.bodyColorLight);
                g2.fillRoundRect(bx + 14, by + 1, 13, 13, 5, 5);
                g2.setColor(skin.headColor);
                g2.fillRoundRect(bx, by - 1, 16, 16, 6, 6);
                g2.setColor(Color.BLACK);
                g2.fillOval(bx + 10, by + 1, 3, 3);
                g2.fillOval(bx + 10, by + 8, 3, 3);
                // Tên
                g2.setFont(new Font("Consolas", Font.BOLD, 12));
                g2.setColor(skin.headColor);
                g2.drawString(skin.displayName, 56, by + 8);
                // Mô tả
                g2.setFont(new Font("Consolas", Font.PLAIN, 9));
                g2.setColor(new Color(160, 160, 170));
                g2.drawString(skin.description, 8, h - 9);
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new LineBorder(new Color(50, 50, 60), 1));
        return card;
    }

    // ── BUILD MAP CARD (THÊM MỚI) ──────────────────────────────────────────
    private JLabel buildMapCard(MapType map, int w, int h) {
        JLabel card = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);

                // Nền card
                g2.setColor(new Color(18, 18, 28));
                g2.fillRoundRect(0, 0, w - 1, h - 1, 10, 10);

                // Mini preview: vẽ tường thu nhỏ (scale = 1/12 so với 600px)
                double scale = (h - 20.0) / 600.0;
                int previewSize = (int)((h - 20) * 1.0);
                int px = 6, py = 10;

                // Nền mini map
                g2.setColor(new Color(30, 30, 45));
                g2.fillRect(px, py, previewSize, previewSize);

                // Tường mini
                g2.setColor(map.accentColor);
                for (int[] wall : map.buildWalls()) {
                    int wx = (int)(wall[0] * scale) + px;
                    int wy = (int)(wall[1] * scale) + py;
                    int ws = Math.max(2, (int)(25 * scale));
                    g2.fillRect(wx, wy, ws, ws);
                }

                // Tên map
                g2.setFont(new Font("Consolas", Font.BOLD, 12));
                g2.setColor(map.accentColor);
                g2.drawString(map.displayName, previewSize + px + 6, py + 16);

                // Mô tả map
                g2.setFont(new Font("Consolas", Font.PLAIN, 9));
                g2.setColor(new Color(160, 160, 170));
                // Xuống dòng nếu dài
                String desc = map.description;
                int maxW = w - previewSize - px - 10;
                FontMetrics fm = g2.getFontMetrics();
                if (fm.stringWidth(desc) > maxW) {
                    // Chia 2 dòng thô
                    int mid = desc.length() / 2;
                    int sp  = desc.lastIndexOf(' ', mid);
                    if (sp < 0) sp = mid;
                    g2.drawString(desc.substring(0, sp), previewSize + px + 6, py + 30);
                    g2.drawString(desc.substring(sp + 1), previewSize + px + 6, py + 42);
                } else {
                    g2.drawString(desc, previewSize + px + 6, py + 30);
                }

                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new LineBorder(new Color(50, 50, 60), 1));
        return card;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0, 80, 30, 50));
        for (int i = 0; i < 18; i++) {
            int x = (i * 97) % 590;
            int y = (i * 73 + 40) % 670;
            g2.fillOval(x, y, 4, 4);
        }
    }
}