package fullboard;

import java.io.*;
import java.util.*;

import org.apache.commons.io.*;
import org.apache.commons.lang3.*;

public class Main {

	public static void main(String[] args) throws IOException {
		// Check if file exists
		File file = new File(args != null ? args[0] : "");
		if (!file.exists()) {
			System.out.println("File not found.\nComplete");
			return;
		}
		// START - PROCESS FILE'S MAPS
		String[] maps = StringUtils.substringsBetween(FileUtils.readFileToString(file), "map", "endmap");
		ArrayList<Board> emptyBoards = new ArrayList<Board>(maps.length);
		for (int i = 0; i < maps.length; i++)
			emptyBoards.add(new Board(maps[i].trim()));
		// END
		// START - FILL QUEUES WITH STARTING POSITIONS
		for (Board b : emptyBoards) {
			// START - BUILD SETUP
			Queue<Board> remainingUnsolved = new LinkedList<Board>();
			for (int i = 0; i < b.getSize(); i++)
				for (int j = 0; j < b.getSize(); j++)
					if (b.getSpaceAt(i, j) == Space.EMPTY)
						remainingUnsolved.add(new Board(b).setStart(i, j));
			// END
			// START - SOLVE BOARD
			ArrayList<Board> winningBoards = new ArrayList<Board>();
			while (!remainingUnsolved.isEmpty()) {
				Board popped = remainingUnsolved.remove();
				if (popped.isSolved()) {
					popped.setFinish(popped.getX(), popped.getY());
					winningBoards.add(new Board(popped));
				} else if (popped.isSolvable()) {
					for (Direction d : Direction.values()) {
						Board nextMove = new Board(popped).move(d);
						if (nextMove != null)
							remainingUnsolved.add(nextMove);
					}
				}
			}
			// END
			// START - PRINT & SORT BOARD
			Collections.sort(winningBoards);
			System.out.println("map");
			if (winningBoards.isEmpty()) {
				System.out.println("No solution");
				System.out.println(b);
			} else {
				int leastMoves = Integer.MAX_VALUE;
				for (Board board : winningBoards)
					if (board.getMoves() < leastMoves)
						leastMoves = board.getMoves();
				System.out.println(leastMoves + " moves");
				for (Board board : winningBoards) {
					System.out.println("solution");
					System.out.println(board);
					System.out.println("endsolution");
				}
			}
			System.out.println("endmap");
			// END
		}
		// END
		// START - DEBUG
		// for (Direction d : Direction.values()) {
		// Board b = new Board(emptyBoards.get(0)).setStart(3,
		// 5).move(d).move(d);
		// System.out.println(b);
		// }
		// END - DEBUG
		System.out.print("Complete");
	}

}
