package lv.semti.gf;

import lv.semti.morphology.analyzer.Analyzer;
import lv.semti.morphology.attributes.*;
import lv.semti.morphology.lexicon.Ending;
import lv.semti.morphology.lexicon.Paradigm;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class ParadigmExport
{
	static String RESULT_FILE = "PortedMorphoParadigmsLav.gf";
	static String INDENT = "  ";
	final static String GF_BIG_SEPERATOR = "_";
	final static String GF_OPER_LEMMA_POSTFIX = GF_BIG_SEPERATOR + "fromLemma";
	final static String GF_OPER_STEMS_POSTFIX = GF_BIG_SEPERATOR + "fromStems";
	final static String GF_STEMCHANGE_SIMPLE_OPER = "stemchangeSimple";
	final static String[] SUPPORTED_PARADIGMS = new String []{
			"noun-1a", "noun-1b", "noun-2a", "noun-2b", "noun-2c", "noun-2d",
			"noun-3f", "noun-3m", "noun-4f", "noun-4m", "noun-5fa", "noun-5fb",
			"noun-5ma", "noun-5mb", "noun-6a", "noun-6b",
	};

	final static String[] PARADIGMS_WITH_PURAL_NOM_LEMMAS = new String []{
			"noun-1a", "noun-1b", "noun-2a", "noun-2b", "noun-2c", "noun-2d",
			"noun-3f", "noun-3m", "noun-4f", "noun-4m", "noun-5fa", "noun-5fb",
			"noun-5ma", "noun-5mb", "noun-6a", "noun-6b",
	};

	/* Unsupported paradigms:
			//LV
			"abbr", "adj-1", "adj-2", "adj-infl", "adjdef-f1", "adjdef-f2",
			"adjdef-m", "adverb", "adverb-2", "card-1", "card-2", "card-infl",
			"card-irreg", "conj", "excl", "foreign", "hardcoded", "noun-0",
			***, "noun-g", "noun-r1",
			"noun-r2", "noun-r3", "number", "ord", "part-1", "part-2", "part-3",
			"part-4", "particle", "prep", "pron", "punct", "residual", "verb-1",
			"verb-1i", "verb-1r", "verb-2", "verb-2r", "verb-3a", "verb-3ar",
			"verb-3b", "verb-3br",
			// LTG
			"abbr-ltg", "adj-1a-ltg", "adj-1b-ltg", "adj-2a-ltg", "adj-2b-ltg",
			"adj-3-ltg", "adj-4-ltg", "adverb-1-ltg", "adverb-2a-ltg",
			"adverb-2b-ltg", "card-i-ltg", "card-infl-ltg", "card-pl1-ltg",
			"card-pl2-ltg", "conj-ltg", "excl-ltg", "foreign-ltg", "hardcoded",
			"noun-0-ltg", "noun-1a-ltg", "noun-1b-ltg", "noun-1c-ltg",
			"noun-1d-ltg", "noun-1e-ltg", "noun-2a-ltg", "noun-2b-ltg",
			"noun-3-ltg", "noun-4fa-ltg", "noun-4fb-ltg", "noun-4ma-ltg",
			"noun-4mb-ltg", "noun-5fa-ltg", "noun-5fc-ltg", "noun-5ma-ltg",
			"noun-5mc-ltg", "noun-6a-ltg", "noun-6b-ltg", "noun-r1-ltg",
			"number-ltg", "ord-def-ltg", "ord-indef-ltg", "particle-ltg",
			"prep-ltg", "pron-ltg", "punct-ltg", "verb-2a-ltg", "verb-2b-ltg",
			"verb-3a-ltg", "verb-3b-ltg", "verb-3c-ltg", "verb-3d-ltg"
	 */

	Analyzer analyzer;
	BufferedWriter gfOut;
	int indentDepth = 0;
	private final Set<String> supportedParadigms;
	private final Set<String> paradigmsWithPlNomLemmas;

	public ParadigmExport(boolean latgalian) throws IOException
	{
		try {
			if (latgalian)
				analyzer = new Analyzer("Latgalian.xml", false);
			else
				analyzer = new Analyzer(false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		gfOut = new BufferedWriter(new OutputStreamWriter(
				Files.newOutputStream(Paths.get(RESULT_FILE)), StandardCharsets.UTF_8));
		supportedParadigms = new HashSet<>(Arrays.asList(SUPPORTED_PARADIGMS));
		paradigmsWithPlNomLemmas = new HashSet<>(Arrays.asList(PARADIGMS_WITH_PURAL_NOM_LEMMAS));
	}

	// TODO:
	// - Ielādēt paradigmu failu
	// - Ielādēt propertiju failu
	// - Izdrukāt vajadzīgās paradigmas
	public static void main(String[] args) throws Exception
	{
		boolean doLatgalian = false;
		if (args != null && args.length > 0)
			doLatgalian = Boolean.parseBoolean(args[0]);
		ParadigmExport exporter = new ParadigmExport(doLatgalian);

		exporter.printGfHeader();

		for (Paradigm paradigm : exporter.analyzer.paradigms)
		{
			if (!exporter.supportedParadigms.contains(paradigm.getName()))
				System.out.println (paradigm.getName() + " is not supported yet.");
			else
			{
				String resultTypeGf = null;
				if (paradigm.getName().startsWith("noun-")) // TODO parameter for paradigm?
				{
					resultTypeGf = "Noun";
					exporter.writeGf2DTableParadigm(
							paradigm, AttributeNames.i_Number, AttributeNames.i_Case, resultTypeGf,
							new String[]{AttributeNames.i_Gender});
					exporter.writeParadigmFromNormalLemmaWrapper(paradigm, resultTypeGf);
				}
				if (exporter.paradigmsWithPlNomLemmas.contains(paradigm.getName()))
				{
					AttributeValues pluraleTantum = new AttributeValues();
					pluraleTantum.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
					pluraleTantum.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
					exporter.writeParadigmFromArbitraryLemmaWrapper(paradigm, resultTypeGf, pluraleTantum);
				}
				// TODO other paradigms

			}
		}

		exporter.printGfTail();
		exporter.closeGfFile();
	}

	void writeParadigmFromNormalLemmaWrapper(Paradigm paradigm, String resultType)
			throws IOException
	{
		String gfParadigmName = getGFParadigmName(paradigm.name);
		String lemmaEnding = paradigm.getLemmaEnding().getEnding();
		gfOut.newLine();
		writeGfFullLine(gfParadigmName + GF_OPER_LEMMA_POSTFIX
				+ " : Str -> " + resultType + " = \\lemma ->");
		indentDepth++;
		writeGfFullLine("case lemma of {");
		indentDepth++;
		writeGfFullLine("stem + \"" +  lemmaEnding
				+ "\" => " + gfParadigmName + GF_OPER_STEMS_POSTFIX + " stem ;");
		writeGfFullLine("_ => Predef.error (\"" + gfParadigmName + GF_OPER_LEMMA_POSTFIX
				+ " is only applicable for words that end in -" + lemmaEnding
				+ ", tried to apply to\" ++ lemma)");
		indentDepth--;
		writeGfFullLine("} ;");
		indentDepth--;
	}

	void writeParadigmFromArbitraryLemmaWrapper (
			Paradigm paradigm, String resultType, AttributeValues lemmaParams)
			throws IOException
	{
		String gfParadigmName = getGFParadigmName(paradigm.name);
		String pos = paradigm.getValue(AttributeNames.i_PartOfSpeech);
		ArrayList<Ending> potentialLemmaEndings = paradigm.getEndingsByAttributes(lemmaParams);
		if (potentialLemmaEndings.size() != 1)
			throw new IllegalArgumentException (
					"writeParadigmFromArbitraryLemmaWrapper() must have lemmaParams argument that specifies a unique lemma ");
		String lemmaEnding = potentialLemmaEndings.get(0).getEnding();
		String methodPostfix = GF_BIG_SEPERATOR + "from" + lemmaParams.keySet()
				.stream().sorted()
				.map(attr -> getGFValue(attr, lemmaParams.getValue(attr), pos))
				.reduce((s1, s2) -> s1 + s2).orElse("Unk");

		gfOut.newLine();
		writeGfFullLine(gfParadigmName + methodPostfix + " : Str -> " + resultType + " = \\lemma ->");
		indentDepth++;
		writeGfFullLine("case lemma of {");
		indentDepth++;
		writeGfFullLine("stem + \"" +  lemmaEnding + "\" => " + gfParadigmName + GF_OPER_STEMS_POSTFIX + " stem ;");
		writeGfFullLine("_ => Predef.error (\"" + gfParadigmName + methodPostfix
				+ " is only applicable for words that end in -" + lemmaEnding
				+ ", tried to apply to\" ++ lemma)");
		indentDepth--;
		writeGfFullLine("} ;");
		indentDepth--;
	}

	void writeGf2DTableParadigm(Paradigm paradigm, String firstLevelAttribute,
								String secondLevelAttribute, String resultType,
								String[] lexicalAttributes) throws IOException
	{
		// TODO: handle "Nepiemīt" with more care
		gfOut.newLine();
		writeGfFullLine(
				getGFParadigmName(paradigm.name) + GF_OPER_STEMS_POSTFIX
						+ " : Str -> " + resultType + " = \\stem ->");
		writeGfFullLine("{");
		indentDepth++;
		writeGfFullLine("s = table {");
		indentDepth++;

		String pos = paradigm.getValue(AttributeNames.i_PartOfSpeech); // TODO vai šai konstantei jābūt te hardcoded?
		List<Ending> verifCopy = (List<Ending>) paradigm.endings.clone();
		List<String> firstLevelValues = TagSet.getTagSet().getAllowedValues(firstLevelAttribute, "LV")
				.stream().sorted().collect(Collectors.toList());
		boolean addFirstLevelSemicolon = false;
		for (String firstLevelValue : firstLevelValues)
		{
			AttributeValues filter = new AttributeValues();
			filter.addAttribute(firstLevelAttribute, firstLevelValue);
			List<Ending> filteredEndings1 = paradigm.getEndingsByAttributes(filter);
			if (filteredEndings1 == null || filteredEndings1.isEmpty())
				continue; // TODO: vai reizēm var vajadzēt drukāt tukšu?
			String firstLevelGFName = getGFValue(firstLevelAttribute, firstLevelValue, pos);

			if (addFirstLevelSemicolon) writeGfLineEnd(" ;");
			//else gfOut.newLine();
			writeGfFullLine(firstLevelGFName + " => table {");
			indentDepth++;

			// Te jāizdrukā visas attiecīgā skaitļa formas
			List<String> secondLevelValues = TagSet.getTagSet().getAllowedValues(secondLevelAttribute, "LV")
					.stream().sorted().collect(Collectors.toList());
			boolean addSecondLevelSemicolon = false;
			for  (String secondLevelValue : secondLevelValues)
			{
				AttributeValues filterSecondLev = new AttributeValues(filter);
				filterSecondLev.addAttribute(secondLevelAttribute, secondLevelValue);
				List<Ending> filteredEndings2 = paradigm.getEndingsByAttributes(filterSecondLev);
				if (filteredEndings2 == null || filteredEndings2.isEmpty())
					continue; // TODO: vai reizēm var vajadzēt drukāt tukšu?
				else
				{
					if (addSecondLevelSemicolon) writeGfLineEnd(" ;");
					String secondLevelGFName = getGFValue(secondLevelAttribute, secondLevelValue, pos);
					writeGfLineStart(secondLevelGFName + " => ");

					ArrayList<String> forms = new ArrayList<>();
					for (Ending e : filteredEndings2)
					{
						int stemchangeID = e.getMija();
						if (stemchangeID == 0)
							forms.add("stem + \"" + e.getEnding() + "\"");
						else
							forms.add(GF_STEMCHANGE_SIMPLE_OPER + " " + stemchangeID + " stem + \"" + e.getEnding() + "\"");
						verifCopy.remove(e);
					}
					if (forms.size() > 1)
						gfOut.write("variants { "
								+ forms.stream().reduce((f1, f2) -> f1 + " ; " + f2).orElse("")
								+ " }");
					else gfOut.write(forms.get(0));

					addSecondLevelSemicolon = true;
				}
			}

			gfOut.newLine();
			indentDepth--;
			writeGfLineStart("}");
			addFirstLevelSemicolon = true;
		}
		gfOut.newLine();
		indentDepth--;
		writeGfLineStart("}");

		for (String lexAttribute : lexicalAttributes)
		{
			String lexAttrValue = paradigm.getValue(lexAttribute);
			if (lexAttrValue != null && !lexAttrValue.isEmpty())
			{
				String attributeGf = getGFAttributeName(lexAttribute, pos);
				String valueGF = getGFValue(lexAttribute, lexAttrValue, pos);
				if (attributeGf != null && !attributeGf.isEmpty()
					&& valueGF != null && !valueGF.isEmpty())
				{
					writeGfLineEnd(" ;");
					writeGfLineStart(attributeGf + " = " + valueGF);
				}
			}
		}

		gfOut.newLine();
		indentDepth--;
		writeGfFullLine("} ;");

		// Pabrīdinām, ja kāda galotne neizdrukājās
		if (!verifCopy.isEmpty())
			System.err.println (paradigm.getName() + " has ignored endings: "
					+ verifCopy.stream().map(Objects::toString).reduce("", (s1, s2) -> s1 + "; " + s2));

	}

	/**
	 * Start line with indent and end with newline.
	 */
	void writeGfFullLine(String line) throws IOException
	{
		gfOut.write(String.join("", Collections.nCopies(indentDepth, INDENT)));
		gfOut.write(line);
		gfOut.newLine();
	}

	/**
	 * Start line with indent, but don't put newline at the end.
	 */
	void writeGfLineStart(String line) throws IOException
	{
		gfOut.write(String.join("", Collections.nCopies(indentDepth, INDENT)));
		gfOut.write(line);
	}

	/**
	 * Write line without indent, but add newline at the end.
	 */
	void writeGfLineEnd(String line) throws IOException
	{
		gfOut.write(line);
		gfOut.newLine();
	}

	void printGfHeader() throws IOException
	{
		gfOut.write("--# -path=.:abstract:common:prelude");
		gfOut.newLine();
		gfOut.newLine();

		gfOut.write("-- Contents of this file are automatically ported paradigms from");
		gfOut.newLine();
		gfOut.write("-- https://github.com/LUMII-AILab/Morphology/blob/master/src/main/resources/Lexicon_v2.xml");
		gfOut.newLine();
		gfOut.write("-- NB: Do NOT edit this without consulting lauma@ailab.lv or normundsg@ailab.lv");
		gfOut.newLine();
		gfOut.write("--     Otherwise your changes might get accidentally revoked!");
		gfOut.newLine();
		gfOut.newLine();

		gfOut.write("resource PortedMorphoParadigmsLav = open PortedMorphoStemchangesLav, ResLav in {");
		gfOut.newLine();
		gfOut.newLine();

		gfOut.write("flags coding = utf8 ;");
		gfOut.newLine();
		gfOut.newLine();

		gfOut.write("oper");
		gfOut.newLine();

		indentDepth = 1;
	}

	void printGfTail() throws IOException
	{
		gfOut.write("}");
		gfOut.newLine();
		gfOut.newLine();
	}

	void closeGfFile() throws IOException
	{
		gfOut.flush();
		gfOut.close();
	}

	private static String getGFValue (String attrName, String attrValue, String pos)
	{
		LinkedList<Attribute> numberAttrs = TagSet.getTagSet().getAttribute(attrName, pos, "LV");
		if (numberAttrs == null || numberAttrs.isEmpty())
			throw new IllegalArgumentException("Warning: Attribute \"" + attrName
					+ "\" for pos \"" + pos + "\" was not found! ");
		else if (numberAttrs.size() > 1)
			System.err.println("Warning: Attribute \"" + attrName + "\" for pos \""
					+ pos + "\" has multiple interpetations! ");
		String result = ((FixedAttribute)numberAttrs.getFirst()).toGF(attrValue);
		if (result == null || result.isEmpty())
			System.err.println("Warning: Value \""+ attrValue + "\" of attribute \""
					+ attrName + "\" for pos \"" + pos + "\" has no GF name! ");
		return result;
	}

	private static String getGFAttributeName (String attrName, String pos)
	{
		LinkedList<Attribute> numberAttrs = TagSet.getTagSet().getAttribute(attrName, pos, "LV");
		if (numberAttrs == null || numberAttrs.isEmpty())
			throw new IllegalArgumentException("Warning: Attribute \"" + attrName
					+ "\" for pos \"" + pos + "\" was not found! ");
		else if (numberAttrs.size() > 1)
			System.err.println("Warning: Attribute \"" + attrName + "\" for pos \""
					+ pos + "\" has multiple interpetations! ");
		String result = numberAttrs.getFirst().attributeGF;
		if (result == null || result.isEmpty())
			System.err.println("Warning: Attribute \"" + attrName + "\" for pos \""
					+ pos + "\" has no GF name! ");
		return result;
	}

	private static String getGFParadigmName (String paradigm)
	{
		if (paradigm == null || paradigm.isEmpty()) return paradigm;
		return paradigm.replace("-", GF_BIG_SEPERATOR);
	}
}