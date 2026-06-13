package snakegame;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class StartScreen extends JPanel {

    private static final long serialVersionUID = 1L;

    // ── Skin P1 ────────────────────────────────────────────────────────────
    private SnakeSkin selectedSkin  = SnakeSkin.CLASSIC;
    private JLabel    selectedLabel = null;
    public static SnakeSkin chosenSkin  = SnakeSkin.CLASSIC;

    // ── Skin P2 (THÊM MỚI) ────────────────────────────────────────────────
    private SnakeSkin selectedSkin2  = SnakeSkin.OCEAN;
    private JLabel    selectedLabel2 = null;
    public static SnakeSkin chosenSkin2 = SnakeSkin.OCEAN;

    // ── Map (THÊM MỚI từ lần trước) ───────────────────────────────────────
    public static MapType chosenMap = MapType.OPEN;
    private MapType   selectedMap     = MapType.OPEN;
    private JLabel    selectedMapLabel = null;

    public StartScreen(GameFrame frame) {
        this.setPreferredSize(new Dimension(600, 980));
        this.setLayout(null);
        this.setBackground(new Color(10, 10, 18));

        // ── TITLE ──────────────────────────────────────────────────────────
        JLabel title = new JLabel("🐍 SNAKE GAME");
        title.setBounds(0, 14, 600, 55);
        title.setFont(new Font("Consolas", Font.BOLD, 38));
        title.setForeground(new Color(0, 220, 80));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(title);

        JLabel sub = new JLabel("Nâng cấp Edition");
        sub.setBounds(0, 70, 600, 22);
        sub.setFont(new Font("Consolas", Font.ITALIC, 14));
        sub.setForeground(new Color(120, 120, 140));
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(sub);

        // ── HƯỚNG DẪN ──────────────────────────────────────────────────────
        String[][] tips = {
            {"🔴","Food thường  +10 điểm"},
            {"⭐","Food vàng    +30 điểm  (có thời hạn)"},
            {"💜","Food độc     mất 1 ❤   (có thời hạn)"},
            {"⬆⬇◀▶","P1 di chuyển  |  P2: W A S D"},
            {"P","Tạm dừng / Tiếp tục"}
        };
        int yy = 98;
        for (String[] tip : tips) {
            JLabel icon = new JLabel(tip[0]);
            icon.setBounds(80, yy, 60, 20);
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
            icon.setForeground(Color.WHITE);
            this.add(icon);
            JLabel txt = new JLabel(tip[1]);
            txt.setBounds(144, yy, 380, 20);
            txt.setFont(new Font("Consolas", Font.PLAIN, 12));
            txt.setForeground(new Color(190, 190, 200));
            this.add(txt);
            yy += 24;
        }

        // ── SKIN P1 ────────────────────────────────────────────────────────
        int skinY = 226;
        addSkinSection("SKIN - PLAYER 1  (↑↓←→)", new Color(255, 215, 0),
                       skinY, true, frame);

        // ── SKIN P2 ────────────────────────────────────────────────────────
        addSkinSection("SKIN - PLAYER 2  (W A S D)", new Color(100, 200, 255),
                       skinY + 180, false, frame);

        // ── MAP SELECTOR ───────────────────────────────────────────────────
        int mapY = skinY + 380;
        JLabel mapTitle = new JLabel("CHỌN MAP");
        mapTitle.setBounds(0, mapY, 600, 22);
        mapTitle.setFont(new Font("Consolas", Font.BOLD, 13));
        mapTitle.setForeground(new Color(100, 200, 255));
        mapTitle.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(mapTitle);

        MapType[] maps = MapType.values();
        int mW = 170, mH = 58, mGX = 10, mGY = 8;
        int mStartX = (600 - (mW * 3 + mGX * 2)) / 2;
        int mStartY = mapY + 28;
        for (int i = 0; i < maps.length; i++) {
            MapType map = maps[i];
            int col = i % 3, row = i / 3;
            int cx = mStartX + col * (mW + mGX);
            int cy = mStartY + row * (mH + mGY);
            JLabel mCard = buildMapCard(map, mW, mH);
            mCard.setBounds(cx, cy, mW, mH);
            if (map == MapType.OPEN) {
                mCard.setBorder(new LineBorder(new Color(100,200,255), 2));
                selectedMapLabel = mCard;
            }
            mCard.addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    if (selectedMapLabel != null)
                        selectedMapLabel.setBorder(new LineBorder(new Color(50,50,60),1));
                    selectedMap = map; chosenMap = map;
                    selectedMapLabel = mCard;
                    mCard.setBorder(new LineBorder(map.accentColor, 2));
                }
                @Override public void mouseEntered(MouseEvent e) {
                    if (mCard != selectedMapLabel)
                        mCard.setBorder(new LineBorder(map.accentColor.darker(),1));
                    mCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                @Override public void mouseExited(MouseEvent e) {
                    if (mCard != selectedMapLabel)
                        mCard.setBorder(new LineBorder(new Color(50,50,60),1));
                }
            });
            this.add(mCard);
        }

        // ── NÚT CHƠI ──────────────────────────────────────────────────────
        int btnY = mapY + 30 + (int)Math.ceil(maps.length / 3.0) * (mH + mGY) + 20;

        // Nút 1 người
        JButton btn1P = new JButton("▶  1 NGƯỜI CHƠI");
        btn1P.setBounds(50, btnY, 230, 48);
        btn1P.setFont(new Font("Consolas", Font.BOLD, 16));
        btn1P.setBackground(new Color(0, 190, 65));
        btn1P.setForeground(Color.WHITE);
        btn1P.setFocusPainted(false);
        btn1P.setBorderPainted(false);
        btn1P.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn1P.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn1P.setBackground(new Color(0,220,80)); }
            public void mouseExited (MouseEvent e) { btn1P.setBackground(new Color(0,190,65)); }
        });
        btn1P.addActionListener(e -> {
            chosenSkin = selectedSkin;
            chosenMap  = selectedMap;
            frame.startGame();
        });
        this.add(btn1P);

        // Nút 2 người (THÊM MỚI)
        JButton btn2P = new JButton("👥  2 NGƯỜI CHƠI");
        btn2P.setBounds(320, btnY, 230, 48);
        btn2P.setFont(new Font("Consolas", Font.BOLD, 16));
        btn2P.setBackground(new Color(30, 100, 220));
        btn2P.setForeground(Color.WHITE);
        btn2P.setFocusPainted(false);
        btn2P.setBorderPainted(false);
        btn2P.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn2P.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn2P.setBackground(new Color(50,130,255)); }
            public void mouseExited (MouseEvent e) { btn2P.setBackground(new Color(30,100,220)); }
        });
        btn2P.addActionListener(e -> {
            chosenSkin  = selectedSkin;
            chosenSkin2 = selectedSkin2;
            chosenMap   = selectedMap;
            frame.startGame2P();
        });
        this.add(btn2P);

        // ── VERSION ────────────────────────────────────────────────────────
        JLabel ver = new JLabel("v2.2 Multiplayer Edition");
        ver.setBounds(0, btnY + 60, 600, 18);
        ver.setFont(new Font("Consolas", Font.PLAIN, 11));
        ver.setForeground(new Color(70,70,80));
        ver.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(ver);
    }

    // ── Helper: thêm khu chọn skin (dùng cho cả P1 và P2) ─────────────────
    private void addSkinSection(String titleText, Color titleColor,
                                int startY, boolean isP1, GameFrame frame) {
        JLabel lbl = new JLabel(titleText);
        lbl.setBounds(0, startY, 600, 22);
        lbl.setFont(new Font("Consolas", Font.BOLD, 13));
        lbl.setForeground(titleColor);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(lbl);

        SnakeSkin[] skins = SnakeSkin.values();
        int cW = 120, cH = 65, gX = 10, gY = 6;
        int sX = (600 - (cW * 4 + gX * 3)) / 2;
        int sY = startY + 26;

        for (int i = 0; i < skins.length; i++) {
            SnakeSkin skin = skins[i];
            int col = i % 4, row = i / 4;
            int cx = sX + col * (cW + gX);
            int cy = sY + row * (cH + gY);
            JLabel card = buildSkinCard(skin, cW, cH);
            card.setBounds(cx, cy, cW, cH);

            SnakeSkin defaultSkin = isP1 ? SnakeSkin.CLASSIC : SnakeSkin.OCEAN;
            if (skin == defaultSkin) {
                card.setBorder(new LineBorder(titleColor, 2));
                if (isP1) selectedLabel  = card;
                else      selectedLabel2 = card;
            }

            card.addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    if (isP1) {
                        if (selectedLabel != null)
                            selectedLabel.setBorder(new LineBorder(new Color(50,50,60),1));
                        selectedSkin  = skin; chosenSkin  = skin;
                        selectedLabel = card;
                        card.setBorder(new LineBorder(skin.headColor, 2));
                    } else {
                        if (selectedLabel2 != null)
                            selectedLabel2.setBorder(new LineBorder(new Color(50,50,60),1));
                        selectedSkin2  = skin; chosenSkin2 = skin;
                        selectedLabel2 = card;
                        card.setBorder(new LineBorder(skin.headColor, 2));
                    }
                }
                @Override public void mouseEntered(MouseEvent e) {
                    JLabel sel = isP1 ? selectedLabel : selectedLabel2;
                    if (card != sel)
                        card.setBorder(new LineBorder(skin.headColor.darker(),1));
                    card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                @Override public void mouseExited(MouseEvent e) {
                    JLabel sel = isP1 ? selectedLabel : selectedLabel2;
                    if (card != sel)
                        card.setBorder(new LineBorder(new Color(50,50,60),1));
                }
            });
            this.add(card);
        }
    }

    // ── Build Skin Card (giữ nguyên) ───────────────────────────────────────
    private JLabel buildSkinCard(SnakeSkin skin, int w, int h) {
        JLabel card = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(22,22,32));
                g2.fillRoundRect(0,0,w-1,h-1,10,10);
                int bx=12, by=h/2-7;
                g2.setColor(skin.bodyColorDark);  g2.fillRoundRect(bx+28,by+2,12,12,5,5);
                g2.setColor(skin.bodyColorLight); g2.fillRoundRect(bx+14,by+1,13,13,5,5);
                g2.setColor(skin.headColor);      g2.fillRoundRect(bx,by-1,16,16,6,6);
                g2.setColor(Color.BLACK);
                g2.fillOval(bx+10,by+1,3,3); g2.fillOval(bx+10,by+8,3,3);
                g2.setFont(new Font("Consolas",Font.BOLD,11));
                g2.setColor(skin.headColor);
                g2.drawString(skin.displayName, 56, by+8);
                g2.setFont(new Font("Consolas",Font.PLAIN,9));
                g2.setColor(new Color(160,160,170));
                g2.drawString(skin.description, 8, h-7);
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new LineBorder(new Color(50,50,60),1));
        return card;
    }

    // ── Build Map Card (giữ nguyên) ────────────────────────────────────────
    private JLabel buildMapCard(MapType map, int w, int h) {
        JLabel card = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(18,18,28));
                g2.fillRoundRect(0,0,w-1,h-1,10,10);
                double scale = (h-20.0)/600.0;
                int ps = (int)(h-20), px=6, py=10;
                g2.setColor(new Color(30,30,45));
                g2.fillRect(px,py,ps,ps);
                g2.setColor(map.accentColor);
                for (int[] wall : map.buildWalls()) {
                    int wx=(int)(wall[0]*scale)+px, wy=(int)(wall[1]*scale)+py;
                    int ws=Math.max(2,(int)(25*scale));
                    g2.fillRect(wx,wy,ws,ws);
                }
                g2.setFont(new Font("Consolas",Font.BOLD,11));
                g2.setColor(map.accentColor);
                g2.drawString(map.displayName, ps+px+6, py+14);
                g2.setFont(new Font("Consolas",Font.PLAIN,9));
                g2.setColor(new Color(160,160,170));
                String desc = map.description;
                FontMetrics fm = g2.getFontMetrics();
                int maxW = w-ps-px-10;
                if (fm.stringWidth(desc) > maxW) {
                    int sp = desc.lastIndexOf(' ', desc.length()/2);
                    if (sp<0) sp=desc.length()/2;
                    g2.drawString(desc.substring(0,sp), ps+px+6, py+27);
                    g2.drawString(desc.substring(sp+1), ps+px+6, py+39);
                } else {
                    g2.drawString(desc, ps+px+6, py+27);
                }
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new LineBorder(new Color(50,50,60),1));
        return card;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0,80,30,50));
        for (int i=0;i<18;i++) {
            int x=(i*97)%590, y=(i*73+40)%670;
            g2.fillOval(x,y,4,4);
        }
    }
}