package timings;

import org.junit.*;

import fullboard.*;

public class Timings {

	private static final int TEST_COUNT = 10;

	@Test
	public void smallMapsTiming() throws Exception {
		long start = System.nanoTime();
		for (int i = 0; i < TEST_COUNT; i++)
			Main.main(new String[] { "smallmaps.txt" });
		long stop = System.nanoTime();
		System.out.println((double) ((stop - start) / 1e9));
	}

	// @Test
	public void largeMapsTiming() throws Exception {
		long start = System.nanoTime();
		for (int i = 0; i < TEST_COUNT; i++)
			Main.main(new String[] { "largemaps.txt" });
		long stop = System.nanoTime();
		System.out.println((double) ((stop - start) / 1e9));
	}

}
