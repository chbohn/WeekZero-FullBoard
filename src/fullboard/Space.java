package fullboard;

public class Space {

	public static final char EMPTY = 32, FINISH = 70, START = 83, ARROW_LEFT = 0x2190, ARROW_UP = 0x2191,
			ARROW_RIGHT = 0x2192, ARROW_DOWN = 0x2193, WALL = 0x2593;

	private int x = 0, y = 0;

	public Space(int xPos, int yPos) {
		x = xPos;
		y = yPos;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}