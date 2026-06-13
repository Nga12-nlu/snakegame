package snakegame;

/**
 * Snake2 – Rắn của Player 2.
 * Xuất phát từ góc phải dưới (500, 500), hướng đi sang trái (L).
 */
public class Snake2 extends Snake {

    public Snake2() {
        // Snake() gọi initDefault() (không gọi reset()),
        // nên Snake2() an toàn để override reset() sau đó.
        reset(); // đặt lại vị trí P2 ngay sau khi super() xong
    }

    @Override
    public void reset() {
        x.clear();
        y.clear();
        if (trail != null) trail.clear();
        bodyParts = 6;
        direction = 'L';
        // Đầu ở (500,500), đuôi kéo sang phải
        for (int i = 0; i < bodyParts; i++) {
            x.add(500 + i * 25);
            y.add(500);
        }
    }
}