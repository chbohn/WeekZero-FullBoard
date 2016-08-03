package fullboard;

import java.io.*;
import java.util.*;

import org.apache.commons.io.*;
import org.apache.commons.lang3.*;

public class Main {

	public static void main(String[] args) throws IOException {
		File file = null;
		if (args == null) {
			Scanner s = new Scanner(System.in);
			String filename = s.next();
			s.close();
			file = new File(filename != null ? filename : "");
			if (!file.exists()) {
				System.out.println("File not found.\nComplete");
				return;
			}
		} else {
			file = new File(args[0]);
			if (!file.exists()) {
				System.out.println("File not found.\nComplete");
				return;
			}
		}
		String[] maps = StringUtils.substringsBetween(FileUtils.readFileToString(file), "map", "endmap");
		ArrayList<Board> emptyBoards = new ArrayList<Board>(maps.length);
		for (int i = 0; i < maps.length; i++)
			emptyBoards.add(new Board(maps[i].trim()));
		for (Board b : emptyBoards) {
			Queue<Board> remainingUnsolved = new LinkedList<Board>();
			for (int i = 0; i < b.getSize(); i++)
				for (int j = 0; j < b.getSize(); j++)
					if (b.getSpaceAt(i, j) == Space.EMPTY)
						remainingUnsolved.add(new Board(b).setStart(i, j));
			ArrayList<Board> winningBoards = new ArrayList<Board>();
			while (!remainingUnsolved.isEmpty()) {
				Board popped = remainingUnsolved.remove();
				if (popped.isSolved()) {
					popped.setFinish(popped.getX(), popped.getY());
					winningBoards.add(popped);
					// winningBoards.add(new Board(popped));
				} else if (popped.isSolvable()) {
					for (Direction d : Direction.values()) {
						Board nextMove = new Board(popped).move(d);
						if (nextMove != null)
							remainingUnsolved.add(nextMove);
					}
				}
			}
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
				for (int i = 0; i < winningBoards.size(); i++) {
					if (winningBoards.get(i).getMoves() > leastMoves) {
						winningBoards.remove(i);
						i--;
					}
				}
				for (Board board : winningBoards) {
					System.out.println("solution");
					System.out.println(board);
					System.out.println("endsolution");
				}
			}
			System.out.println("endmap");
		}
		System.out.println("Complete");
	}

}
