package snakegame;

public class Main {
    public static void main(String[] args) {
     
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
            ScoreManager.saveHighScore(GamePanel.highScore)
        ));

        new GameFrame();
    }	
}
