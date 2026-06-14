package snakegame;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class StartScreen extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int W = 1200;
    private static final int H = 660;

    private SnakeSkin selectedSkin   = SnakeSkin.CLASSIC;
    private JLabel    selectedLabel  = null;
    public static SnakeSkin chosenSkin  = SnakeSkin.CLASSIC;

    private SnakeSkin selectedSkin2  = SnakeSkin.OCEAN;
    private JLabel    selectedLabel2 = null;
    public static SnakeSkin chosenSkin2 = SnakeSkin.OCEAN;

    public static MapType chosenMap    = MapType.OPEN;
    private MapType   selectedMap      = MapType.OPEN;
    private JLabel    selectedMapLabel = null;

    public StartScreen(GameFrame frame) {
        this.setPreferredSize(new Dimension(W, H));
        this.setLayout(null);
        this.setBackground(new Color(10, 10, 18));

        int COL_L = 10;
        int COL_R = 610;
        int COL_W = 580;

        JLabel title = new JLabel("🐍 SNAKE GAME");
        title.setBounds(0, 10, W, 46);
        title.setFont(new Font("Consolas", Font.BOLD, 36));
        title.setForeground(new Color(0, 220, 80));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(title);

        JLabel sub = new JLabel("Nâng cấp Edition  —  Best Score: " + ScoreManager.loadHighScore());
        sub.setBounds(0, 56, W, 20);
        sub.setFont(new Font("Consolas", Font.PLAIN, 13));
        sub.setForeground(new Color(180, 180, 100));
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(sub);

        JButton soundBtn = new JButton(Sound.isMuted() ? "🔇" : "🔊");
        soundBtn.setBounds(W - 46, 8, 38, 38);
        soundBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        soundBtn.setBackground(new Color(30, 30, 42));
        soundBtn.setForeground(Color.WHITE);
        soundBtn.setFocusPainted(false);
        soundBtn.setBorder(new LineBorder(new Color(60, 60, 75), 1));
        soundBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        soundBtn.addActionListener(e -> {
            boolean m = Sound.toggleMuted();
            soundBtn.setText(m ? "🔇" : "🔊");
        });
        this.add(soundBtn);

        JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
        sep.setBounds(20, 80, W - 40, 2);
        sep.setForeground(new Color(40, 40, 55));
        this.add(sep);

        JSeparator divider = new JSeparator(JSeparator.VERTICAL);
        divider.setBounds(COL_R - 6, 84, 2, H - 100);
        divider.setForeground(new Color(40, 40, 55));
        this.add(divider);

        int y = 88;

        addLabel("SKIN — PLAYER 1  (↑ ↓ ← →)", new Color(255, 215, 0), COL_L, y, COL_W);
        y += 24;
        y = addSkinRow(COL_L, y, COL_W, true);

        y += 10;
        addLabel("SKIN — PLAYER 2  (W A S D)", new Color(100, 200, 255), COL_L, y, COL_W);
        y += 24;
        addSkinRow(COL_L, y, COL_W, false);

        int tipY = H - 108;
        String[][] tips = {
            {"🔴 +10", "Food thường"},
            {"⭐ +30", "Food vàng (thời hạn)"},
            {"💜 -❤", "Food độc (thời hạn)"},
            {"[P]", "Tạm dừng / Tiếp tục"},
            {"[M]", "Tắt / Mở âm thanh"},
        };
        addLabel("HƯỚNG DẪN", new Color(160, 160, 180), COL_L, tipY - 22, COL_W);
        int tx = COL_L;
        for (String[] t : tips) {
            JLabel ic = makeLabel(t[0], new Color(220, 180, 60), 11, Font.BOLD);
            ic.setBounds(tx, tipY, 60, 18);
            this.add(ic);
            JLabel txt = makeLabel(t[1], new Color(170, 170, 185), 10, Font.PLAIN);
            txt.setBounds(tx + 60, tipY, 90, 18);
            this.add(txt);
            tx += 155;
            if (tx + 150 > COL_R - 10) { tx = COL_L; tipY += 20; }
        }

        int ry = 88;
        addLabel("CHỌN MAP", new Color(100, 200, 255), COL_R, ry, COL_W);
        ry += 24;

        MapType[] maps = MapType.values();
        int mW = 180, mH = 62, mGX = 8, mGY = 6;
        int cols3 = 3;
        int mStartX = COL_R + (COL_W - (mW * cols3 + mGX * (cols3 - 1))) / 2;
        for (int i = 0; i < maps.length; i++) {
            MapType map = maps[i];
            int col = i % cols3, row = i / cols3;
            int mx = mStartX + col * (mW + mGX);
            int my = ry + row * (mH + mGY);
            JLabel mCard = buildMapCard(map, mW, mH);
            mCard.setBounds(mx, my, mW, mH);
            if (map == MapType.OPEN) {
                mCard.setBorder(new LineBorder(new Color(100, 200, 255), 2));
                selectedMapLabel = mCard;
            }
            mCard.addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    if (selectedMapLabel != null)
                        selectedMapLabel.setBorder(new LineBorder(new Color(50, 50, 60), 1));
                    selectedMap = map; chosenMap = map;
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

        int rows = (int) Math.ceil(maps.length / (double) cols3);
        int btnY = ry + rows * (mH + mGY) + 14;

        int bW = 134, bH = 44, bGap = 10;
        int totalBtnW = bW * 4 + bGap * 3;
        int bStartX = COL_R + (COL_W - totalBtnW) / 2;

        JButton btn1P = makeButton("▶ 1 NGƯỜI", new Color(0, 190, 65), new Color(0, 220, 80));
        btn1P.setBounds(bStartX, btnY, bW, bH);
        btn1P.addActionListener(e -> { chosenSkin = selectedSkin; chosenMap = selectedMap; frame.startGame(); });
        this.add(btn1P);

        JButton btnBot1P = makeButton("🤖 VS BOT 1P", new Color(180, 80, 0), new Color(220, 110, 0));
        btnBot1P.setBounds(bStartX + bW + bGap, btnY, bW, bH);
        btnBot1P.addActionListener(e -> { chosenSkin = selectedSkin; chosenMap = selectedMap; frame.startGameVsBot(); });
        this.add(btnBot1P);

        JButton btn2P = makeButton("👥 2 NGƯỜI", new Color(30, 100, 220), new Color(50, 130, 255));
        btn2P.setBounds(bStartX + (bW + bGap) * 2, btnY, bW, bH);
        btn2P.addActionListener(e -> { chosenSkin = selectedSkin; chosenSkin2 = selectedSkin2; chosenMap = selectedMap; frame.startGame2P(); });
        this.add(btn2P);

        JButton btnBot2P = makeButton("🤖 2P VS BOT", new Color(100, 30, 200), new Color(130, 60, 240));
        btnBot2P.setBounds(bStartX + (bW + bGap) * 3, btnY, bW, bH);
        btnBot2P.addActionListener(e -> { chosenSkin = selectedSkin; chosenMap = selectedMap; frame.startGame2PVsBot(); });
        this.add(btnBot2P);

        JLabel note = new JLabel("Bot = rắn Lava đỏ cam, di chuyển ngẫu nhiên tự tránh tường  |  v2.3 Bot Edition");
        note.setBounds(COL_R, btnY + bH + 8, COL_W, 16);
        note.setFont(new Font("Consolas", Font.PLAIN, 10));
        note.setForeground(new Color(90, 90, 100));
        note.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(note);
    }

    private void addLabel(String text, Color color, int x, int y, int w) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, w, 20);
        lbl.setFont(new Font("Consolas", Font.BOLD, 12));
        lbl.setForeground(color);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(lbl);
    }

    private JLabel makeLabel(String text, Color color, int size, int style) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Consolas", style, size));
        l.setForeground(color);
        return l;
    }

    private int addSkinRow(int startX, int startY, int colW, boolean isP1) {
        SnakeSkin[] skins = SnakeSkin.values();
        int cW = 130, cH = 58, gX = 8, gY = 5;
        int perRow = 4;
        int gridW = perRow * cW + (perRow - 1) * gX;
        int ox = startX + (colW - gridW) / 2;

        int maxRow = 0;
        for (int i = 0; i < skins.length; i++) {
            SnakeSkin skin = skins[i];
            int col = i % perRow, row = i / perRow;
            if (row > maxRow) maxRow = row;
            int cx = ox + col * (cW + gX);
            int cy = startY + row * (cH + gY);
            JLabel card = buildSkinCard(skin, cW, cH);
            card.setBounds(cx, cy, cW, cH);

            SnakeSkin def = isP1 ? SnakeSkin.CLASSIC : SnakeSkin.OCEAN;
            if (skin == def) {
                Color tc = isP1 ? new Color(255, 215, 0) : new Color(100, 200, 255);
                card.setBorder(new LineBorder(tc, 2));
                if (isP1) selectedLabel  = card;
                else      selectedLabel2 = card;
            }

            card.addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    if (isP1) {
                        if (selectedLabel != null) selectedLabel.setBorder(new LineBorder(new Color(50,50,60),1));
                        selectedSkin = skin; chosenSkin = skin;
                        selectedLabel = card; card.setBorder(new LineBorder(skin.headColor, 2));
                    } else {
                        if (selectedLabel2 != null) selectedLabel2.setBorder(new LineBorder(new Color(50,50,60),1));
                        selectedSkin2 = skin; chosenSkin2 = skin;
                        selectedLabel2 = card; card.setBorder(new LineBorder(skin.headColor, 2));
                    }
                }
                @Override public void mouseEntered(MouseEvent e) {
                    JLabel sel = isP1 ? selectedLabel : selectedLabel2;
                    if (card != sel) card.setBorder(new LineBorder(skin.headColor.darker(), 1));
                    card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                @Override public void mouseExited(MouseEvent e) {
                    JLabel sel = isP1 ? selectedLabel : selectedLabel2;
                    if (card != sel) card.setBorder(new LineBorder(new Color(50,50,60),1));
                }
            });
            this.add(card);
        }
        return startY + (maxRow + 1) * (cH + gY);
    }

    private JButton makeButton(String text, Color normal, Color hover) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Consolas", Font.BOLD, 13));
        btn.setBackground(normal);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited (MouseEvent e) { btn.setBackground(normal); }
        });
        return btn;
    }

    private JLabel buildSkinCard(SnakeSkin skin, int w, int h) {
        JLabel card = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(22, 22, 32));
                g2.fillRoundRect(0, 0, w-1, h-1, 10, 10);
                int bx = 10, by = h/2 - 7;
                g2.setColor(skin.bodyColorDark);  g2.fillRoundRect(bx+28, by+2, 12, 12, 5, 5);
                g2.setColor(skin.bodyColorLight); g2.fillRoundRect(bx+14, by+1, 13, 13, 5, 5);
                g2.setColor(skin.headColor);      g2.fillRoundRect(bx,    by-1, 16, 16, 6, 6);
                g2.setColor(Color.BLACK);
                g2.fillOval(bx+10, by+1, 3, 3); g2.fillOval(bx+10, by+8, 3, 3);
                g2.setFont(new Font("Consolas", Font.BOLD, 11));
                g2.setColor(skin.headColor);
                g2.drawString(skin.displayName, 55, by + 8);
                g2.setFont(new Font("Consolas", Font.PLAIN, 9));
                g2.setColor(new Color(160, 160, 170));
                g2.drawString(skin.description, 8, h - 7);
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new LineBorder(new Color(50, 50, 60), 1));
        return card;
    }

    private JLabel buildMapCard(MapType map, int w, int h) {
        JLabel card = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(18, 18, 28));
                g2.fillRoundRect(0, 0, w-1, h-1, 10, 10);
                double scale = (h - 16.0) / GameConfig.WORLD_WIDTH;
                int ps = h - 16, px = 5, py = 8;
                g2.setColor(new Color(30, 30, 45));
                g2.fillRect(px, py, ps, ps);
                g2.setColor(map.accentColor);
                for (int[] wall : map.buildWalls()) {
                    int wx = (int)(wall[0] * scale) + px;
                    int wy = (int)(wall[1] * scale) + py;
                    int ws = Math.max(2, (int)(25 * scale));
                    g2.fillRect(wx, wy, ws, ws);
                }
                g2.setFont(new Font("Consolas", Font.BOLD, 11));
                g2.setColor(map.accentColor);
                g2.drawString(map.displayName, ps + px + 6, py + 14);
                g2.setFont(new Font("Consolas", Font.PLAIN, 9));
                g2.setColor(new Color(160, 160, 170));
                String desc = map.description;
                FontMetrics fm = g2.getFontMetrics();
                int maxW = w - ps - px - 8;
                if (fm.stringWidth(desc) > maxW) {
                    int sp = desc.lastIndexOf(' ', desc.length() / 2);
                    if (sp < 0) sp = desc.length() / 2;
                    g2.drawString(desc.substring(0, sp), ps + px + 6, py + 27);
                    g2.drawString(desc.substring(sp + 1), ps + px + 6, py + 39);
                } else {
                    g2.drawString(desc, ps + px + 6, py + 27);
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
        g2.setColor(new Color(0, 80, 30, 40));
        for (int i = 0; i < 24; i++) {
            int x = (i * 127) % (W - 10);
            int y = (i * 97 + 40) % (H - 10);
            g2.fillOval(x, y, 4, 4);
        }
    }
}