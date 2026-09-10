package lv.semti.morphology.analyzer;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Bounded least-recently-used cache for analysis results.
 * <p>
 * All operations are synchronized, so one Analyzer instance can be shared between threads.
 * The underlying map is access-ordered, meaning that even {@code get()} rewrites its linked
 * list; unsynchronized concurrent access silently corrupts it (eviction stops working and
 * entries become unreachable), which is why this class no longer exposes the map directly.
 */
public class Cache<K, V> {
	private static final int DEFAULT_MAX_SIZE = 10000;

	private int maxSize;
	private final LinkedHashMap<K, V> map = new LinkedHashMap<K, V>(16, 0.75f, true) {
		@Override
		protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
			return size() > maxSize;
		}
	};

	public Cache() {
		this(DEFAULT_MAX_SIZE);
	}

	public Cache(int maxSize) {
		this.maxSize = maxSize;
	}

	public synchronized V get(K key) {
		return map.get(key);
	}

	public synchronized void put(K key, V value) {
		if (maxSize > 0) map.put(key, value);
	}

	public synchronized void clear() {
		map.clear();
	}

	public synchronized int size() {
		return map.size();
	}

	/**
	 * Sets the maximum number of entries, evicting the least recently used ones if the cache
	 * currently holds more; 0 turns caching off.
	 */
	public synchronized void setSize(int maxSize) {
		this.maxSize = maxSize;
		Iterator<K> eldestFirst = map.keySet().iterator();
		while (map.size() > Math.max(maxSize, 0) && eldestFirst.hasNext()) {
			eldestFirst.next();
			eldestFirst.remove();
		}
	}
}
