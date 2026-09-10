package lv.semti.morphology.Testi;

import lv.semti.morphology.analyzer.Cache;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CacheTest {

	@Test
	public void boundedAndLeastRecentlyUsedEviction() {
		Cache<Integer, Integer> cache = new Cache<>(3);
		cache.put(1, 1);
		cache.put(2, 2);
		cache.put(3, 3);
		assertEquals(Integer.valueOf(1), cache.get(1)); // touch 1 so that 2 becomes the eldest
		cache.put(4, 4);
		assertEquals(3, cache.size());
		assertNull(cache.get(2));
		assertEquals(Integer.valueOf(1), cache.get(1));
		assertEquals(Integer.valueOf(4), cache.get(4));
	}

	@Test
	public void sizeZeroDisablesCaching() {
		Cache<Integer, Integer> cache = new Cache<>(0);
		cache.put(1, 1);
		assertNull(cache.get(1));
		assertEquals(0, cache.size());
	}

	@Test
	public void shrinkingEvictsExcessEntries() {
		Cache<Integer, Integer> cache = new Cache<>(100);
		for (int i = 0; i < 100; i++) cache.put(i, i);
		cache.setSize(10);
		assertTrue(cache.size() <= 10);
		cache.setSize(0);
		assertEquals(0, cache.size());
	}

	/**
	 * Unsynchronized access-ordered LinkedHashMap silently breaks under concurrent use: its
	 * linked list gets corrupted, eviction stops working and the size grows past the limit.
	 */
	@Test(timeout = 120_000)
	public void concurrentUseKeepsCacheConsistent() throws Exception {
		final int threads = 16, keys = 400, maxSize = 200, iterations = 500_000;
		final Cache<Integer, Integer> cache = new Cache<>(maxSize);
		ExecutorService pool = Executors.newFixedThreadPool(threads);
		List<Future<Integer>> futures = new ArrayList<>();
		for (int t = 0; t < threads; t++) {
			final int seed = t;
			futures.add(pool.submit(() -> {
				Random random = new Random(seed);
				int wrongValues = 0;
				for (int i = 0; i < iterations; i++) {
					int key = random.nextInt(keys);
					Integer value = cache.get(key);
					if (value == null) cache.put(key, key);
					else if (value != key) wrongValues++;
				}
				return wrongValues;
			}));
		}
		pool.shutdown();
		assertTrue("cache threads did not finish", pool.awaitTermination(90, TimeUnit.SECONDS));
		int wrongValues = 0;
		for (Future<Integer> f : futures) wrongValues += f.get();
		assertEquals(0, wrongValues);
		assertTrue("cache grew past its limit: " + cache.size(), cache.size() <= maxSize);
	}
}
