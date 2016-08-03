package fullboard;

import java.util.*;

import org.apache.commons.lang3.*;

public class Board implements Comparable<Board> {

	private char[][] spaces = null;
	private int moves = 0, x = 0, y = 0, size = 0;

	public Board(String map) {
		String[] rows = StringUtils.split(map, "\r\n");
		size = rows.length;
		spaces = new char[rows.length][rows[0].length()];
		for (int i = 0; i < rows.length; i++)
			for (int j = 0; j < rows[i].length(); j++)
				spaces[i][j] = rows[i].charAt(j);
	}

	public Board(Board other) {
		char[][] copying = new char[other.spaces.length][other.spaces.length];
		for (int i = 0; i < other.spaces.length; i++)
			copying[i] = Arrays.copyOf(other.spaces[i], other.spaces.length);
		spaces = copying;
		moves = other.moves;
		x = other.x;
		y = other.y;
		size = other.size;
	}

	public Board move(Direction d) {
		switch (d) {
		case UP:
			for (int i = (x - 1); i >= 0; i--) {
				if (spaces[i][y] != Space.EMPTY)
					return null;
				spaces[i][y] = Space.ARROW_UP;
				if (spaces[i - 1][y] != Space.EMPTY) {
					x = i;
					break;
				}
			}
			break;
		case DOWN:
			for (int i = (x + 1); i <= size; i++) {
				if (spaces[i][y] != Space.EMPTY)
					return null;
				spaces[i][y] = Space.ARROW_DOWN;
				if (spaces[i + 1][y] != Space.EMPTY) {
					x = i;
					break;
				}
			}
			break;
		case LEFT:
			for (int i = (y - 1); i >= 0; i--) {
				if (spaces[x][i] != Space.EMPTY)
					return null;
				spaces[x][i] = Space.ARROW_LEFT;
				if (spaces[x][i - 1] != Space.EMPTY) {
					y = i;
					break;
				}
			}
			break;
		case RIGHT:
			for (int i = (y + 1); i <= size; i++) {
				if (spaces[x][i] != Space.EMPTY)
					return null;
				spaces[x][i] = Space.ARROW_RIGHT;
				if (spaces[x][i + 1] != Space.EMPTY) {
					y = i;
					break;
				}
			}
			break;
		}
		moves++;
		return this;
	}

	public Board setStart(int xPos, int yPos) {
		spaces[xPos][yPos] = Space.START;
		x = xPos;
		y = yPos;
		return this;
	}

	public Board setFinish(int xPos, int yPos) {
		spaces[xPos][yPos] = Space.FINISH;
		x = xPos;
		y = yPos;
		return this;
	}

	@Override
	public int compareTo(Board other) {
		return this.toString().compareTo(other.toString());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < spaces.length; i++) {
			for (int j = 0; j < spaces[i].length; j++)
				sb.append(spaces[i][j]);
			if (i != (spaces.length - 1))
				sb.append("\n");
		}
		return sb.toString();
	}

	public boolean isSolvable() {
		if (spaces[x - 1][y] != Space.EMPTY && spaces[x + 1][y] != Space.EMPTY && spaces[x][y - 1] != Space.EMPTY
				&& spaces[x][y + 1] != Space.EMPTY)
			return false;
		return true;
	}

	public boolean isSolved() {
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				if (spaces[i][j] == Space.EMPTY)
					return false;
		return true;
	}

	public char getSpaceAt(int xPos, int yPos) {
		return spaces[xPos][yPos];
	}

	public int getMoves() {
		return moves;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getSize() {
		return size;
	}

}
