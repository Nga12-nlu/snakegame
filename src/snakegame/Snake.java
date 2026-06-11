package snakegame;
import java.util.ArrayList;
public class Snake {
	

	
	    private ArrayList<Integer> x = new ArrayList<>();
	    private ArrayList<Integer> y = new ArrayList<>();

	    private int bodyParts = 6;
	    private char direction = 'R';

	    public Snake() {
	        for (int i = 0; i < bodyParts; i++) {
	            x.add(100 - i * 25);
	            y.add(100);
	        }
	    }

	    public void move() {
	        for (int i = bodyParts - 1; i > 0; i--) {
	            x.set(i, x.get(i - 1));
	            y.set(i, y.get(i - 1));
	        }

	        switch (direction) {
	            case 'U': y.set(0, y.get(0) - 25); break;
	            case 'D': y.set(0, y.get(0) + 25); break;
	            case 'L': x.set(0, x.get(0) - 25); break;
	            case 'R': x.set(0, x.get(0) + 25); break;
	        }
	    }

	    public void grow() {
	        bodyParts++;
	        x.add(x.get(x.size() - 1));
	        y.add(y.get(y.size() - 1));
	    }

	    public boolean checkCollision() {
	        // đâm vào thân
	        for (int i = bodyParts - 1; i > 0; i--) {
	            if (x.get(0).equals(x.get(i)) && y.get(0).equals(y.get(i))) {
	                return true;
	            }
	        }

	        // đâm tường
	        if (x.get(0) < 0 || x.get(0) >= 600 || y.get(0) < 0 || y.get(0) >= 600) {
	            return true;
	        }

	        return false;
	    }

	    public ArrayList<Integer> getX() { return x; }
	    public ArrayList<Integer> getY() { return y; }
	    public int getBodyParts() { return bodyParts; }

	    public void setDirection(char dir) {
	        this.direction = dir;
	    }

	    public char getDirection() {
	        return direction;
	    }
	}

