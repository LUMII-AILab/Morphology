package lv.semti.morphology.Testi;

import lv.semti.morphology.analyzer.Analyzer;
import lv.semti.morphology.analyzer.Word;
import lv.semti.morphology.analyzer.Wordform;
import lv.semti.morphology.attributes.AttributeNames;
import lv.semti.morphology.attributes.AttributeValues;
import lv.semti.morphology.lexicon.Ending;
import lv.semti.morphology.lexicon.Lexeme;
import lv.semti.morphology.lexicon.Paradigm;
import lv.semti.morphology.lexicon.StemType;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class LanguageSpecificTests
{
	static Analyzer analyzer;

	// TODO (no Pētera) - šie varbūt ir par assertThat matcheriem jāpārtaisa
	void assertInflection(List<Wordform> forms, AttributeValues testAttributes, String validForm) {
		boolean found = false;
		for (Wordform wf : forms) {
			// Originaly there was `if (wf.isMatchingWeak(testAttributes))`, but
			// that is deceiving: it gives an illusion that form generation
			// results for abbreviations like FMS and non-inflectable names like
			// Kirill matches queries for having genitive or dative case despite
			// the fact that they don't have cases at all.
			if (wf.isMatchingStrongOneSide(testAttributes)) {
				if (!validForm.equalsIgnoreCase(wf.getToken())) {
					System.err.print("Found a different form");
					wf.describe(new PrintWriter(System.err));
				}
				assertEquals(validForm, wf.getToken());
				found = true;
				break;
			}
		}
		if (!found) {
			System.err.printf("assertInflection failed: looking for '%s'\n", validForm);
			testAttributes.describe(new PrintWriter(System.err));
			System.err.println("In:");
			for (Wordform wf : forms) {
				wf.describe(new PrintWriter(System.err));
				System.err.println("\t---");
			}
		}
		assertTrue(found);
	}

	void assertInflectionMultipleStrong(List<Wordform> forms, AttributeValues testAttributes, Set<String> validForms) {
		HashSet<String> foundCorrect = new HashSet<>();
		HashSet<String> foundOther = new HashSet<>();
		for (Wordform wf : forms) {
			if (wf.isMatchingStrongOneSide(testAttributes)) {
				if (validForms.contains(wf.getToken())) foundCorrect.add(wf.getToken());
				else foundOther.add(wf.getToken());
			}
		}

		if (!foundOther.isEmpty())
		{
			System.err.print("assertInflectionMultiple failed with spare forms:\n");
			System.err.println (foundOther);
		}
		if (validForms.size() != foundCorrect.size())
		{
			System.err.print("assertInflectionMultiple failed with not enough correct:\n");
			System.err.println (foundCorrect);
		}
		assertTrue(foundOther.isEmpty());
		assertEquals(validForms.size(), foundCorrect.size());
	}

	void assertInflectionMultipleWeak(List<Wordform> forms, AttributeValues testAttributes, Set<String> validForms) {
		HashSet<String> foundCorrect = new HashSet<>();
		for (Wordform wf : forms) {
			if (wf.isMatchingStrongOneSide(testAttributes)) {
				if (validForms.contains(wf.getToken())) foundCorrect.add(wf.getToken());
			}
		}
		if (validForms.size() != foundCorrect.size())
		{
			System.err.print("assertInflectionMultiple failed with not enough correct:\n");
			System.err.println (foundCorrect);
		}
		assertEquals(validForms.size(), foundCorrect.size());
	}

	void assertNounInflection(List<Wordform> forms, String number, String nounCase, String gender, String validForm) {
		AttributeValues testAttributes = new AttributeValues();
		testAttributes.addAttribute(AttributeNames.i_Case, nounCase);
		testAttributes.addAttribute(AttributeNames.i_Number, number);
		if (!gender.isEmpty()) testAttributes.addAttribute(AttributeNames.i_Gender, gender);
		assertInflection(forms, testAttributes, validForm);
	}

	void assertNounInflectionMultipleStrong(List<Wordform> forms, String number, String nounCase, String gender, Set<String> validForms) {
		AttributeValues testAttributes = new AttributeValues();
		testAttributes.addAttribute(AttributeNames.i_Case, nounCase);
		testAttributes.addAttribute(AttributeNames.i_Number, number);
		if (!gender.isEmpty()) testAttributes.addAttribute(AttributeNames.i_Gender, gender);
		assertInflectionMultipleStrong(forms, testAttributes, validForms);
	}

	void assertNoInflection(List<Wordform> forms, AttributeValues testset) {
		for (Wordform wf : forms) {
			assertFalse(wf.isMatchingWeak(testset));
		}
	}

	void assertNoForm(List<Wordform> forms, String invalidForm) {
		for (Wordform wf : forms) {
			assertFalse(invalidForm.equalsIgnoreCase(wf.getToken()));
		}
	}

	void assertLemma(String word, String expectedLemma) {
		Word analysis = analyzer.analyze(word);
		if (!analysis.isRecognized())
			System.out.printf("'%s' should be recognizable", word);
		assertTrue(analysis.isRecognized());
		Wordform forma = analysis.getBestWordform();
		assertEquals(expectedLemma, forma.getValue(AttributeNames.i_Lemma));
	}

	void lexiconIdIntegrity(boolean checkLexemes) {
		HashMap<Integer, Paradigm> paradigmIds = new HashMap<>();
		HashMap<Integer, Lexeme> lexemeIds = new HashMap<>();
		HashMap<Integer, Ending> endingIds = new HashMap<>();

		for (Paradigm paradigm : analyzer.paradigms) {
			if (paradigmIds.get(paradigm.getID()) != null)
				fail("Repeated paradigm ID " + paradigm.getID());
			paradigmIds.put(paradigm.getID(), paradigm);

			if (checkLexemes)
				for (Lexeme lexeme : paradigm.lexemes) {
					if (lexemeIds.get(lexeme.getID()) != null) {
						lexeme.describe(new PrintWriter(System.err));
						lexemeIds.get(lexeme.getID()).describe(new PrintWriter(System.err));
						fail(String.format("Repeated lexeme ID %d : '%s' un '%s'",
								lexeme.getID(), lexeme.getStem(StemType.STEM1),
								lexemeIds.get(lexeme.getID()).getStem(StemType.STEM1)));
					}
					lexemeIds.put(lexeme.getID(), lexeme);
				}

			for (Ending ending : paradigm.endings) {
				if (endingIds.get(ending.getID()) != null)
					fail("Repeated ending ID " + ending.getID());
				endingIds.put(ending.getID(), ending);
			}
		}
	}

	void describe(List<Wordform> forms) {
		PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
		for (Wordform wf : forms) {
			wf.describe(out);
			out.println();
		}
		out.flush();
	}

}
