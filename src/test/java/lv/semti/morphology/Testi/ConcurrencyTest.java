package lv.semti.morphology.Testi;

import lv.semti.morphology.analyzer.Analyzer;
import lv.semti.morphology.analyzer.Word;
import lv.semti.morphology.analyzer.Wordform;
import lv.semti.morphology.attributes.AttributeNames;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Verifies that a single Analyzer instance can be shared between threads for
 * analysis, including when the analysis result cache is enabled.
 */
public class ConcurrencyTest {
	private static Analyzer analyzer;

	private static final List<String> WORDS = Arrays.asList(
			"cirvis", "zaļš", "Rīga", "iet", "mājas", "skaisti", "ābols", "runāt", "bērni", "un",
			"123", "vīrs", "sieva", "dzīvot", "skolotāja", "labdien", "nē", "Jānis", "LATVIJA", "grāmatu",
			"lasīja", "nelasīja", "visskaistākais", "mazulītis", "gribētu", "būt", "es", "viņš", "kas", "ar",
			"Latvijas", "Universitāte", "Matemātikas", "informātikas", "institūts", "izstrādāja", "morfoloģisko", "analizatoru", "kurš", "spēj",
			"noteikt", "vārda", "pamatformu", "gramatiskās", "kategorijas", "šodien", "rīt", "vakar", "nekad", "vienmēr",
			"ātri", "lēni", "labi", "slikti", "daudz", "maz", "pirmais", "otrais", "trešais", "desmit",
			"galds", "krēsls", "logs", "durvis", "siena", "grīda", "griesti", "jumts", "pagrabs", "bēniņi",
			"suns", "kaķis", "zirgs", "govs", "cūka", "vista", "gailis", "pīle", "zoss", "aita",
			"upe", "ezers", "jūra", "kalns", "ieleja", "mežs", "lauks", "pļava", "purvs", "sala",
			"sarkans", "dzeltens", "zils", "balts", "melns", "pelēks", "brūns", "oranžs", "violets", "rozā",
			"ēst", "dzert", "gulēt", "strādāt", "mācīties", "spēlēt", "dziedāt", "dejot", "peldēt", "skriet",
			"lielākais", "mazākais", "skaistākā", "neskaistākā", "nedzirdēts", "pārlasīt", "aizskriet", "atnākt", "iznākums", "kaimiņiene");

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		analyzer = new Analyzer(false);
		analyzer.defaultSettings();
		analyzer.enableGuessing = true;
		analyzer.enableVocative = true;
		analyzer.enableAnalysisCache = true;
		analyzer.setCacheSize(WORDS.size() / 2); // small enough that eviction happens constantly
		analyzer.clearCache();
	}

	private static List<String> describe(Word w) {
		List<String> result = new ArrayList<>();
		for (Wordform wf : w.wordforms)
			result.add(wf.getTag() + " " + wf.getValue(AttributeNames.i_Lemma));
		Collections.sort(result);
		return result;
	}

	@Test(timeout = 120_000)
	public void concurrentAnalyzeWithCache() throws Exception {
		Map<String, List<String>> expected = new HashMap<>();
		for (String word : WORDS)
			expected.put(word, describe(analyzer.analyze(word)));

		int threads = 8;
		int iterations = 100_000;
		ExecutorService pool = Executors.newFixedThreadPool(threads);
		List<Future<Integer>> futures = new ArrayList<>();
		for (int t = 0; t < threads; t++) {
			final int seed = t;
			futures.add(pool.submit(new Callable<Integer>() {
				@Override
				public Integer call() {
					Random random = new Random(seed);
					int mismatches = 0;
					for (int i = 0; i < iterations; i++) {
						String word = WORDS.get(random.nextInt(WORDS.size()));
						if (!expected.get(word).equals(describe(analyzer.analyze(word))))
							mismatches++;
					}
					return mismatches;
				}
			}));
		}
		pool.shutdown();
		assertTrue("Analysis threads did not finish; the analysis cache is probably corrupted",
				pool.awaitTermination(90, TimeUnit.SECONDS));
		int mismatches = 0;
		for (Future<Integer> f : futures)
			mismatches += f.get(); // rethrows any exception from the worker
		assertEquals("Results differed from single-threaded analysis", 0, mismatches);
	}
}
