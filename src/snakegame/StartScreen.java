package snakegame;

import javax.swing.*;
import java.awt.*;

public class StartScreen extends JPanel {

    private static final long serialVersionUID = 1L;

    public StartScreen(GameFrame frame) {

        this.setPreferredSize(new Dimension(600, 600));
        this.setLayout(null);
        this.setBackground(Color.BLACK);

        JLabel title = new JLabel("SNAKE GAME");
        title.setBounds(150, 100, 300, 50);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.GREEN);
        this.add(title);

        JButton newGameBtn = new JButton("New Game");
        newGameBtn.setBounds(200, 300, 200, 50);
        newGameBtn.setFont(new Font("Arial", Font.BOLD, 20));

        newGameBtn.addActionListener(e -> {
            frame.startGame();
        });

        this.add(newGameBtn);
    }
}