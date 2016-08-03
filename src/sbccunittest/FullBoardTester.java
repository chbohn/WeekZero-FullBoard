package sbccunittest;

// 12/23/14

import static junit.framework.Assert.*;
import static org.apache.commons.io.FileUtils.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.io.*;
import java.util.*;

import org.junit.*;

import fullboard.*;

public class FullBoardTester {

	public static int totalScore = 0;

	public static int extraCredit = 0;

	public static InputStream defaultSystemIn;

	public static PrintStream defaultSystemOut;

	public static PrintStream defaultSystemErr;

	public static String newLine = System.getProperty("line.separator");


	@BeforeClass
	public static void beforeTesting() throws Exception {
		totalScore = 0;
		extraCredit = 0;
		String fbSrc = readFileToString(new File("src/fullboard/Main.java"));
		if (fbSrc.contains("fullboardri"))
			throw new Exception("Reference to fullboardri found");
	}


	@AfterClass
	public static void afterTesting() {
		System.out.println("Estimated score (w/o late penalties, etc.) = " + totalScore);
	}


	@Before
	public void setUp() throws Exception {
		defaultSystemIn = System.in;
		defaultSystemOut = System.out;
		defaultSystemErr = System.err;
	}


	@After
	public void tearDown() throws Exception {
		System.setIn(defaultSystemIn);
		System.setOut(defaultSystemOut);
		System.setErr(defaultSystemErr);
	}


	@Test
	public void testFileNotFound() throws Exception {

		sendToStdinOfTestee("blah.txt\n");
		final ByteArrayOutputStream myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));

		Main.main(null);
		String output = myOut.toString();
		System.setOut(defaultSystemOut);

		StringBuilder sb = new StringBuilder("File not found.");

		sb.append(newLine + "Complete" + newLine);
		String expectedOutput = sb.toString();

		// Convert to common end-of-line system.
		output = output.replace("\r\n", "\n");
		expectedOutput = expectedOutput.replace("\r\n", "\n");

		assertEquals(expectedOutput, output);
		totalScore += 5;
	}


	@Test
	public void testSmallMapsFile() throws IOException {
		String filename = "smallmaps.txt";
		generateSmallMapsFile(filename);

		sendToStdinOfTestee(filename);
		final ByteArrayOutputStream myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));

		Main.main(null);
		String output = myOut.toString();
		System.setOut(defaultSystemOut);

		// Expected output
		sendToStdinOfTestee(filename);
		final ByteArrayOutputStream expectedOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(expectedOut));

		fullboardri.Main.main(null);
		String expectedOutput = expectedOut.toString();
		System.setOut(defaultSystemOut);

		// out.println(expectedOutput);

		// Convert to common end-of-line system.
		output = output.replace("\r\n", "\n");
		writeStringToFile(new File("smallmaps_out.txt"), output);
		expectedOutput = expectedOutput.replace("\r\n", "\n");
		writeStringToFile(new File("smallmaps_expected_out.txt"), expectedOutput);


		// Go through outputs, map by map
		String[] oMaps = substringsBetween(output, "map\n", "endmap\n");
		String[] eMaps = substringsBetween(expectedOutput, "map\n", "endmap\n");

		assertEquals("The number of maps doesn't match.", eMaps.length, oMaps.length);

		totalScore += 5;

		// Verify that the # of moves is correct for all maps
		for (int ndx = 0; ndx < eMaps.length; ndx++) {
			String[] olines = oMaps[ndx].split("\n");
			String[] elines = eMaps[ndx].split("\n");
			assertTrue("Each map must have a least one line defined in it.", olines.length > 0);
			assertEquals("The minimum number of moves doesn't match", elines[0].trim(), olines[0].trim());
		}
		totalScore += 10;

		// Verify all maps match.
		assertEquals(expectedOutput, output);
		totalScore += 10;
	}


	@Test(timeout = 120000)
	public void testLargeMapsFile() throws IOException {

		String filename = "largemaps.txt";
		generateLargeMapsFile(filename);

		sendToStdinOfTestee(filename);
		final ByteArrayOutputStream myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));

		Main.main(null);
		String output = myOut.toString();
		System.setOut(defaultSystemOut);
		writeStringToFile(new File("largemaps_out.txt"), output);

		// Expected output
		sendToStdinOfTestee(filename);
		final ByteArrayOutputStream expectedOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(expectedOut));

		fullboardri.Main.main(null);
		String expectedOutput = expectedOut.toString();
		System.setOut(defaultSystemOut);

		// Convert to common end-of-line system.
		output = output.replace("\r\n", "\n");
		expectedOutput = expectedOutput.replace("\r\n", "\n");

		// Verify that the number of maps is correct
		String[] oMaps = substringsBetween(output, "map\n", "endmap\n");
		String[] eMaps = substringsBetween(expectedOutput, "map\n", "endmap\n");
		assertEquals("The number of maps doesn't match.", eMaps.length, oMaps.length);
		totalScore += 5;

		if (oMaps.length == eMaps.length) {
			boolean numMovesCorrect = true;
			String message = null;
			// Verify that the number of moves is correct
			for (int ndx = 0; ndx < eMaps.length; ndx++) {
				String[] expectedLines = eMaps[ndx].split("\n");
				String[] outputLines = oMaps[ndx].split("\n");
				if (!outputLines[1].trim().equals(expectedLines[1].trim())) {
					numMovesCorrect = false;
					message = "For large map " + (ndx + 1) + " the number of moves do not match.";
				}
			}
			assertTrue(message, numMovesCorrect);
			totalScore += 5;
		}

		// Verify that all output is as expected.
		assertEquals(expectedOutput, output);
		totalScore += 10;
	}


	private void generateLargeMapsFile(String filename) throws IOException {
		int vfi = (int) (Math.random() * 2);
		StringBuilder sb = new StringBuilder();

		for (int ndx = 0; ndx < 2; ndx++) {
			int n = (int) (30 * Math.random() + 20);
			String map;
			if (ndx != vfi)
				map = generateMap(n, n, 5);
			else
				map = generateLargeValidMap(n);

			sb.append("map").append(newLine);
			sb.append(map);
			sb.append("endmap").append(newLine);
		}

		writeStringToFile(new File(filename), sb.toString());
	}


	private String generateLargeValidMap(int n) {
		StringBuilder sb = new StringBuilder(generateMap(n, n, 0));
		int pos = (int) (4 * Math.random());
		switch (pos) {
		case 0:
			sb.setCharAt((n / 2) * (n + newLine.length()) + n / 2, '▓');
			sb.setCharAt((n / 2) * (n + newLine.length()) + n / 2 + 1, '▓');
			sb.setCharAt((n / 2) * (n + newLine.length()) + n / 2 + 2, '▓');

			break;

		case 1:
			sb.setCharAt((n / 2 + 2) * (n + newLine.length()) + n / 2, '▓');
			sb.setCharAt((n / 2 + 2) * (n + newLine.length()) + n / 2 + 1, '▓');
			sb.setCharAt((n / 2 + 2) * (n + newLine.length()) + n / 2 + 2, '▓');
			break;

		case 2:
			sb.setCharAt((n / 2) * (n + newLine.length()) + n / 2, '▓');
			sb.setCharAt((n / 2 + 1) * (n + newLine.length()) + n / 2, '▓');
			sb.setCharAt((n / 2 + 2) * (n + newLine.length()) + n / 2, '▓');
			break;

		case 3:
			sb.setCharAt((n / 2) * (n + newLine.length()) + n / 2 + 2, '▓');
			sb.setCharAt((n / 2 + 1) * (n + newLine.length()) + n / 2 + 2, '▓');
			sb.setCharAt((n / 2 + 2) * (n + newLine.length()) + n / 2 + 2, '▓');
			break;
		}
		return sb.toString();
	}


	/**
	 * Works for numRows, numCols up to about 10, then too slow
	 * 
	 * @param numRows
	 * @param numCols
	 * @param numBlocks
	 * @param numMaps
	 * @param numNoSolution
	 * @param filename
	 * @throws IOException
	 */
	public void generateSmallMapsFile(String filename) throws IOException {
		boolean done = false;

		int numMaps = (int) (Math.random() * 4) + 10;
		int numNoSolution = (int) (Math.random() * 2) + 1;
		StringBuilder sb = new StringBuilder();
		int validCount = 0;
		List<Integer> noSolutionIndices = new ArrayList<>(numMaps);
		for (int i = 0; i < numMaps; i++)
			noSolutionIndices.add(i);
		Collections.shuffle(noSolutionIndices);
		noSolutionIndices = noSolutionIndices.subList(0, numNoSolution);
		int noSolutionCount = 0;
		boolean saveSolution = false;
		boolean isANoSolution = false;
		while (!done) {
			int numRows = (int) (Math.random() * 4) + 7;
			int numCols = numRows;
			int numBlocks = 5;
			String map = generateMap(numRows, numCols, numBlocks);
			fullboardri.Main checker = new fullboardri.Main(true);
			String result = checker.processMap(map);
			isANoSolution = result.contains("No solution");
			boolean needNoSolution = false;
			saveSolution = false;
			if (numNoSolution > 0) {
				if (noSolutionCount < numNoSolution) {
					if ((noSolutionIndices.contains(validCount))) {
						needNoSolution = true;
					}
				}
			}

			if (isANoSolution) {
				if (needNoSolution)
					saveSolution = true;
			} else
				saveSolution = true;

			if (saveSolution) {

				sb.append("map").append(newLine);
				sb.append(map);
				sb.append("endmap").append(newLine);

				if (needNoSolution)
					noSolutionCount++;

				validCount++;
				if (validCount >= numMaps)
					done = true;
			}
		}

		writeStringToFile(new File(filename), sb.toString());
	}


	public String generateMap(int numRows, int numCols, int numBlocks) {
		char[][] map = new char[numRows][numCols];


		for (int c = 0; c < numCols; c++)
			map[0][c] = '▓';
		for (int r = 1; r < numRows - 1; r++) {
			map[r][0] = '▓';
			for (int c = 1; c < numCols - 1; c++)
				map[r][c] = ' ';
			map[r][numCols - 1] = '▓';
		}
		for (int c = 0; c < numCols; c++)
			map[numRows - 1][c] = '▓';

		// Place blocks. Note: this is n^2 if numBlocks is of the order of numRows*numCols.
		int blocksPlaced = 0;
		while (blocksPlaced < numBlocks) {
			int r = (int) (Math.random() * (numRows - 2)) + 1;
			int c = (int) (Math.random() * (numCols - 2)) + 1;
			if (map[r][c] != '▓') {
				map[r][c] = '▓';
				blocksPlaced++;
			}
		}

		// Convert to string
		StringBuilder sb = new StringBuilder(numRows * (numCols + newLine.length()));
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++)
				sb.append(map[r][c]);
			sb.append(newLine);
		}

		return sb.toString();
	}


	public void sendToStdinOfTestee(String message) {
		System.setIn(new ByteArrayInputStream(message.getBytes()));
	}
}
