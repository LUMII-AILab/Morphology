package lv.semti.morphology.Testi;

import lv.semti.morphology.analyzer.Analyzer;
import lv.semti.morphology.analyzer.Word;
import lv.semti.morphology.analyzer.Wordform;
import lv.semti.morphology.attributes.AttributeNames;
import lv.semti.morphology.attributes.AttributeValues;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.util.*;

import static org.junit.Assert.*;


/*
 * For tests involving specific forms using the actual correct form as variable
 * improves readability.
 */
@SuppressWarnings("NonAsciiCharacters")
public class LatgalianTest extends LanguageSpecificTests {

	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			analyzer = new Analyzer("Latgalian.xml", false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Before
	public void defaultsettings() {
		analyzer.defaultSettings();
		analyzer.setCacheSize(0);
		analyzer.clearCache();
	}

	@Ignore("Tēzaurs.lv JSONL export gives repeated lexeme IDs for cases when a single Tēzaurs lexeme is matched to multiple lexicon entries")
	@Test
	public void idItegrityFull()
	{
		lexiconIdIntegrity(true);
	}

	@Test
	public void idItegrityShort()
	{
		lexiconIdIntegrity(false);
	}

	@Test
	public void viejs() {
		List<Wordform> viejs = analyzer.generateInflections("viejs");
//        describe(viejs);
		AttributeValues testset = new AttributeValues();
		testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
		testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Vocative);
		testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		assertInflection(viejs, testset, "viej");
	}

	@Test
	public void ceļš() {
		List<Wordform> ceļš = analyzer.generateInflections("ceļš");
		//describe(ceļš);
		AttributeValues testset = new AttributeValues();
		testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
		testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Dative);
		testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
		assertInflection(ceļš, testset, "celim");
	}

	@Test
	public void bruoļs() {
		List<Wordform> bruoļs = analyzer.generateInflections("bruoļs");
		AttributeValues testset = new AttributeValues();
		testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
		testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Dative);
		testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
		assertInflection(bruoļs, testset, "bruolim");
	}

	@Test
	public void pasauļs() {
		analyzer.enableGuessing = true;
		List<Wordform> pasauļs = analyzer.generateInflectionsFromParadigm("pasauļs", 4);
		//describe(pasauļs);
		assertTrue(pasauļs != null && !pasauļs.isEmpty());
		AttributeValues testset = new AttributeValues();
		testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
		testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Accusative);
		testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		assertInflection(pasauļs, testset, "pasauli");
	}

	@Test
	public void kakis() {
		List<Wordform> kakis = analyzer.generateInflectionsFromParadigm("kakis", 5);
		AttributeValues testset = new AttributeValues();
		testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
		testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Locative);
		testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
		assertInflection(kakis, testset, "kaķūs");
	}

	@Test
	public void akmiņs() {
		List<Wordform> akmiņs = analyzer.generateInflections("akmiņs");
		AttributeValues testset = new AttributeValues();
		testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
		testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Locative);
		testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		assertInflection(akmiņs, testset, "akminī");
	}

	@Test
	public void Jezus() {
		List<Wordform> Jezus = analyzer.generateInflections("Jezus");
		AttributeValues testset = new AttributeValues();
		testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
		testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
		testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		assertInflectionMultipleStrong(Jezus, testset, new HashSet<>(){{ add("Jezus"); add("Jeza");}});
	}

	@Test
	public void dekl1()
	{
		AttributeValues vsk_gen = new AttributeValues();
		vsk_gen.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
		vsk_gen.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		vsk_gen.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);

		List<Wordform> muosa = analyzer.generateInflectionsFromParadigm("tāvs", 1);
		assertInflection(muosa, vsk_gen, "tāva");

		List<Wordform> muote = analyzer.generateInflectionsFromParadigm("ols", 15);
		assertInflectionMultipleStrong(muote, vsk_gen, new HashSet<>(){{ add("ols"); add("ola");}});
	}


	@Test
	public void dekl4() {
		AttributeValues vsk_gen = new AttributeValues();
		vsk_gen.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
		vsk_gen.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		vsk_gen.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);

		List<Wordform> muosa = analyzer.generateInflectionsFromParadigm("muosa", 7);
		assertInflection(muosa, vsk_gen, "muosys");

		List<Wordform> kuoja = analyzer.generateInflectionsFromParadigm("kuoja", 8);
		assertInflection(kuoja, vsk_gen, "kuojis");

		List<Wordform> puika = analyzer.generateInflectionsFromParadigm("puika", 16);
		assertInflectionMultipleStrong(puika, vsk_gen, new HashSet<>(){{ add("puikys"); add("puikas");}});

		List<Wordform> bļuzņa = analyzer.generateInflectionsFromParadigm("bļuzņa", 30);
		assertInflectionMultipleStrong(bļuzņa, vsk_gen, new HashSet<>(){{ add("bļuznis"); add("bļuzņas");}});
	}

	@Test
	public void dekl5() {
		AttributeValues vsk_loc = new AttributeValues();
		vsk_loc.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
		vsk_loc.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		vsk_loc.addAttribute(AttributeNames.i_Case, AttributeNames.v_Locative);
		AttributeValues dsk_gen = new AttributeValues();
		dsk_gen.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
		dsk_gen.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
		dsk_gen.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);

		List<Wordform> muote = analyzer.generateInflectionsFromParadigm("muote", 9);
		assertInflectionMultipleStrong(muote, vsk_loc, new HashSet<>(){{ add("muotē"); add("muotie"); add("muotī");}});
		assertInflection(muote, dsk_gen, "muošu");

		List<Wordform> šaļte = analyzer.generateInflectionsFromParadigm("šaļte", 17);
		assertInflection(šaļte, dsk_gen, "šaļtu");
	}

	@Test
	public void dekl6()
	{
		AttributeValues dsk_gen = new AttributeValues();
		dsk_gen.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
		dsk_gen.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
		dsk_gen.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);

		List<Wordform> sirds = analyzer.generateInflectionsFromParadigm("sirds", 11);
		assertInflection(sirds, dsk_gen, "siržu");

		List<Wordform> zūss = analyzer.generateInflectionsFromParadigm("zūss", 12);
		assertInflection(zūss, dsk_gen, "zūsu");
	}

	@Test
	public void adj()
	{
		AttributeValues sg_nom_masc_comp = new AttributeValues();
		sg_nom_masc_comp.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Adjective);
		sg_nom_masc_comp.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		sg_nom_masc_comp.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
		sg_nom_masc_comp.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
		sg_nom_masc_comp.addAttribute(AttributeNames.i_Degree, AttributeNames.v_Comparative);

		AttributeValues sg_gen_fem_pos_indef = new AttributeValues();
		sg_gen_fem_pos_indef.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Adjective);
		sg_gen_fem_pos_indef.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		sg_gen_fem_pos_indef.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
		sg_gen_fem_pos_indef.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Feminine);
		sg_gen_fem_pos_indef.addAttribute(AttributeNames.i_Degree, AttributeNames.v_Positive);
		sg_gen_fem_pos_indef.addAttribute(AttributeNames.i_Definiteness, AttributeNames.v_Indefinite);

		AttributeValues sg_gen_fem_comp_indef = new AttributeValues();
		sg_gen_fem_comp_indef.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Adjective);
		sg_gen_fem_comp_indef.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		sg_gen_fem_comp_indef.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
		sg_gen_fem_comp_indef.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Feminine);
		sg_gen_fem_comp_indef.addAttribute(AttributeNames.i_Degree, AttributeNames.v_Comparative);
		sg_gen_fem_comp_indef.addAttribute(AttributeNames.i_Definiteness, AttributeNames.v_Indefinite);

		List<Wordform> lobs = analyzer.generateInflectionsFromParadigm("lobs", 20);
		assertInflection(lobs, sg_nom_masc_comp, "lobuoks");
		assertInflectionMultipleStrong(lobs, sg_gen_fem_pos_indef, new HashSet<>(){{ add("lobys"); add("lobas");}});
		assertInflectionMultipleStrong(lobs, sg_gen_fem_comp_indef, new HashSet<>(){{ add("lobuokys"); add("lobuokas");}});

		List<Wordform> agrys = analyzer.generateInflectionsFromParadigm("agrys", 21);
		assertInflection(agrys, sg_nom_masc_comp, "agruokys");
		assertInflectionMultipleStrong(agrys, sg_gen_fem_pos_indef, new HashSet<>(){{ add("agrys"); add("agras");}});
		assertInflectionMultipleStrong(agrys, sg_gen_fem_comp_indef, new HashSet<>(){{ add("agruokys"); add("agruokas");}});

		List<Wordform> slapnis = analyzer.generateInflectionsFromParadigm("slapnis", 22);
		assertInflection(slapnis, sg_nom_masc_comp, "slapņuoks");
		assertInflectionMultipleStrong(slapnis, sg_gen_fem_pos_indef, new HashSet<>(){{ add("slapnis"); add("slapņas");}});
		assertInflectionMultipleStrong(slapnis, sg_gen_fem_comp_indef, new HashSet<>(){{ add("slapņuokys"); add("slapņuokas");}});

		List<Wordform> zaļš = analyzer.generateInflectionsFromParadigm("zaļš", 43);
		assertInflection(zaļš, sg_nom_masc_comp, "zaļuoks");
		assertInflectionMultipleStrong(zaļš, sg_gen_fem_pos_indef, new HashSet<>(){{ add("zalis"); add("zaļas");}});
		assertInflectionMultipleStrong(zaļš, sg_gen_fem_comp_indef, new HashSet<>(){{ add("zaļuokis"); add("zaļuokas");}});

		List<Wordform> malejs = analyzer.generateInflectionsFromParadigm("malejs", 45);
		assertInflection(malejs, sg_nom_masc_comp, "malejuoks");
		assertInflectionMultipleStrong(malejs, sg_gen_fem_pos_indef, new HashSet<>(){{ add("malejis"); add("malejas");}});
		assertInflectionMultipleStrong(malejs, sg_gen_fem_comp_indef, new HashSet<>(){{ add("malejuokys"); add("malejuokas");}});
		for (Wordform wf : malejs) {
			assertNotEquals("malejys", wf.getToken());
		}
	}

	@Test
	public void numerals()
	{
		AttributeValues sg_masc_gen = new AttributeValues();
		sg_masc_gen.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Numeral);
		sg_masc_gen.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		sg_masc_gen.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
		sg_masc_gen.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);

		AttributeValues pl_masc_gen = new AttributeValues();
		pl_masc_gen.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Numeral);
		pl_masc_gen.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
		pl_masc_gen.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
		pl_masc_gen.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);

		List<Wordform> pīci = analyzer.generateInflectionsFromParadigm("pīci", 25);
		assertInflection(pīci, pl_masc_gen, "pīcu");

		List<Wordform> vairuoki = analyzer.generateInflectionsFromParadigm("vairuoki", 25);
		assertInflection(vairuoki, pl_masc_gen, "vairuoku");

		List<Wordform> deveni = analyzer.generateInflectionsFromParadigm("deveni", 42);
		assertInflection(deveni, pl_masc_gen, "deveņu");

		List<Wordform> trešs = analyzer.generateInflectionsFromParadigm("trešs", 26);
		assertInflection(trešs, sg_masc_gen, "treša");
		assertInflection(trešs, pl_masc_gen, "trešu");

		List<Wordform> pyrmais = analyzer.generateInflectionsFromParadigm("pyrmais", 27);
		assertInflection(pyrmais, sg_masc_gen, "pyrmuo");
		assertInflectionMultipleStrong(pyrmais, pl_masc_gen, new HashSet<>(){{ add("pyrmū"); add("pyrmūs");}});

	}

	@Test
	public void adv()
	{
		AttributeValues comp = new AttributeValues();
		comp.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Adverb);
		comp.addAttribute(AttributeNames.i_Degree, AttributeNames.v_Comparative);

		List<Wordform> cīši = analyzer.generateInflectionsFromParadigm("cīši", 32);
		assertInflection(cīši, comp, "cīšuok");

		List<Wordform> slapni = analyzer.generateInflectionsFromParadigm("slapni", 33);
		assertInflection(slapni, comp, "slapņuok");

	}

	@Test
	public void prep()
	{
		AttributeValues prep = new AttributeValues();
		prep.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Preposition);

		List<Wordform> da = analyzer.generateInflectionsFromParadigm("da", 34);
		assertInflection(da, prep, "da");
	}

	@Test
	public void iuzys()
	{
		AttributeValues dsk_nom = new AttributeValues();
		dsk_nom.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
		dsk_nom.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
		dsk_nom.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);

		// Testing recognising plurare tantum
		List<Wordform> iuza_bad = analyzer.generateInflectionsFromParadigm("iuza", 7);
		assertInflection(iuza_bad, dsk_nom, "iuzys");

		AttributeValues plTan = new AttributeValues();
		plTan.addAttribute(AttributeNames.i_NumberSpecial, AttributeNames.v_PlurareTantum);
		plTan.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Feminine);

		List<Wordform> forms = analyzer.generateInflectionsFromParadigm("iuzys", 7, plTan);
		for (Wordform form : forms) {
			assertFalse(form.isMatchingStrong(AttributeNames.i_Number, AttributeNames.v_Singular));
			assertTrue(form.isMatchingStrong(AttributeNames.i_Gender, AttributeNames.v_Feminine));
		}
		assertFalse(forms.isEmpty());

	}

	@Test
	public void valodasNormēšana()
	{
		// Tests, ka ir formas, kam ir norādīts `Valodas_normēšana="Ieteicams"`
		// Tests jāpamaina, ja mainās, kurām formām šo vajag.
		AttributeValues sg_fem_gen_rec = new AttributeValues();
		sg_fem_gen_rec.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
		sg_fem_gen_rec.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		sg_fem_gen_rec.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
		sg_fem_gen_rec.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Feminine);
		sg_fem_gen_rec.addAttribute(AttributeNames.i_Normative, AttributeNames.v_Recommended);

		List<Wordform> muosa = analyzer.generateInflectionsFromParadigm("muosa", 7);
		assertInflection(muosa, sg_fem_gen_rec, "muosys");

		// Tests, ka ir formas, kam ir norādīts `Valodas_normēšana="Nevēlams"`
		// Tests jāpamaina, ja mainās, kurām formām šo vajag.
		AttributeValues sg_fem_loc_und = new AttributeValues();
		sg_fem_loc_und.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
		sg_fem_loc_und.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		sg_fem_loc_und.addAttribute(AttributeNames.i_Case, AttributeNames.v_Locative);
		sg_fem_loc_und.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Feminine);
		sg_fem_loc_und.addAttribute(AttributeNames.i_Normative, AttributeNames.v_Undesirable);

		List<Wordform> muote = analyzer.generateInflectionsFromParadigm("muote", 9);
		assertInflectionMultipleStrong(muote, sg_fem_loc_und, new HashSet<>(){{ add("muotie"); add("muotī");}});

	}

	@Test
	public void mijas() {
		AttributeValues vsk_gen = new AttributeValues();
		vsk_gen.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
		vsk_gen.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		vsk_gen.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
		AttributeValues vsk_acc = new AttributeValues();
		vsk_acc.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
		vsk_acc.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		vsk_acc.addAttribute(AttributeNames.i_Case, AttributeNames.v_Accusative);
		AttributeValues dsk_dat = new AttributeValues();
		dsk_dat.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
		dsk_dat.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
		dsk_dat.addAttribute(AttributeNames.i_Case, AttributeNames.v_Dative);

		List<Wordform> kačs = analyzer.generateInflectionsFromParadigm("kačs", 4);
		assertInflection(kačs, vsk_gen, "kača");
		assertInflection(kačs, vsk_acc, "kači");
		assertInflection(kačs, dsk_dat, "kačim");
		List<Wordform> bruoļs = analyzer.generateInflectionsFromParadigm("bruoļs", 4);
		assertInflection(bruoļs, vsk_gen, "bruoļa");
		assertInflection(bruoļs, vsk_acc, "bruoli");
		assertInflection(bruoļs, dsk_dat, "bruolim");
		List<Wordform> vecs = analyzer.generateInflectionsFromParadigm("vecs", 4);
		assertInflection(vecs, vsk_gen, "veča");
		assertInflection(vecs, vsk_acc, "veci");
		assertInflection(vecs, dsk_dat, "večim");
		List<Wordform> bruoleits = analyzer.generateInflectionsFromParadigm("bruoleits", 4);
		assertInflection(bruoleits, vsk_gen, "bruoleiša");
		assertInflection(bruoleits, vsk_acc, "bruoleiti");
		assertInflection(bruoleits, dsk_dat, "bruoleišim");
		List<Wordform> eļksnis = analyzer.generateInflectionsFromParadigm("eļksnis", 5);
		assertInflection(eļksnis, vsk_gen, "eļkšņa");
		assertInflection(eļksnis, vsk_acc, "eļksni");
		assertInflection(eļksnis, dsk_dat, "eļkšnim");
	}

	@Test // Bugreport, ka varbanis nepaņem pareizo celmu un izloka
	public void vargani() {
		AttributeValues plTan = new AttributeValues();
		plTan.addAttribute(AttributeNames.i_NumberSpecial, AttributeNames.v_PlurareTantum);

		ArrayList<Wordform> vargani = analyzer.generateInflectionsFromParadigm("vargani", 1, "vargan", "", "", plTan);
		//describe(vargani);
		AttributeValues dsk_gen = new AttributeValues();
		dsk_gen.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
		dsk_gen.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
		dsk_gen.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
		assertInflection(vargani, dsk_gen, "varganu");
	}

	@Test
	public void pronounSpecforms() { //ticket_138, 146
		ArrayList<Wordform> esFormas = analyzer.generateInflectionsFromParadigm("es", 29);
		//describe(esFormas);
		assertTrue("Jābūt vairākām formām 'es' tabulai no hardcoded", esFormas.size()>4);
		ArrayList<Wordform> nazkasFormas = analyzer.generateInflectionsFromParadigm("nazkas", 29);
		//describe(nazkasFormas);
		assertFalse("Jābūt vairākām formām 'nazkas' tabulai no hardcoded", nazkasFormas.isEmpty());
	}

	@Test
	public void konj2() {
		List<Wordform> dūmuot = analyzer.generateInflectionsFromParadigm("dūmuot", 44);
		List<Wordform> teireit = analyzer.generateInflectionsFromParadigm("teireit", 44);
		List<Wordform> auklēt = analyzer.generateInflectionsFromParadigm("auklēt", 44);
		List<Wordform> mērcēt = analyzer.generateInflectionsFromParadigm("mērcēt", 46);

		// Tagadne: 110. mija
		AttributeValues ind_pres_1_sg = new AttributeValues();
		ind_pres_1_sg.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		ind_pres_1_sg.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Indicative);
		ind_pres_1_sg.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
		ind_pres_1_sg.addAttribute(AttributeNames.i_Person, "1");
		ind_pres_1_sg.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		assertInflection(dūmuot, ind_pres_1_sg, "dūmoju");
		assertInflection(teireit, ind_pres_1_sg, "teireju");
		assertInflection(auklēt, ind_pres_1_sg, "aukleju");
		assertInflection(mērcēt, ind_pres_1_sg, "mērceju");

		// Pagātne: 111. un 112. mija

		AttributeValues ind_past_1_sg = new AttributeValues();
		ind_past_1_sg.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		ind_past_1_sg.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Indicative);
		ind_past_1_sg.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Past);
		ind_past_1_sg.addAttribute(AttributeNames.i_Person, "1");
		ind_past_1_sg.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		assertInflectionMultipleWeak(dūmuot, ind_past_1_sg, new HashSet<>(){{ add("dūmuoju"); add("dūmovu");}});
		assertInflection(teireit, ind_past_1_sg, "teireju");
		assertInflection(auklēt, ind_past_1_sg, "auklieju");
		assertInflection(mērcēt, ind_past_1_sg, "mērcieju");

		AttributeValues ind_past_3 = new AttributeValues();
		ind_past_3.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		ind_past_3.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Indicative);
		ind_past_3.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Past);
		ind_past_3.addAttribute(AttributeNames.i_Person, "3");
		assertInflectionMultipleWeak(dūmuot, ind_past_3, new HashSet<>(){{ add("dūmuoja"); add("dūmova");}});
		assertInflection(teireit, ind_past_3, "teireja");
		assertInflection(auklēt, ind_past_3, "auklēja");
		assertInflection(mērcēt, ind_past_3, "mērcēja");

		// Nākotne: 113. un 0. mija

		AttributeValues inf_fut_1_sg = new AttributeValues();
		inf_fut_1_sg.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		inf_fut_1_sg.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Indicative);
		inf_fut_1_sg.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Future);
		inf_fut_1_sg.addAttribute(AttributeNames.i_Person, "1");
		inf_fut_1_sg.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		assertInflection(dūmuot, inf_fut_1_sg, "dūmuošu");
		assertInflection(teireit, inf_fut_1_sg, "teireišu");
		assertInflection(auklēt, inf_fut_1_sg, "aukliešu");
		assertInflection(mērcēt, inf_fut_1_sg, "mērciešu");

		AttributeValues ind_fut_3 = new AttributeValues();
		ind_fut_3.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		ind_fut_3.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Indicative);
		ind_fut_3.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Future);
		ind_fut_3.addAttribute(AttributeNames.i_Person, "3");
		assertInflection(dūmuot, ind_fut_3, "dūmuos");
		assertInflection(teireit, ind_fut_3, "teireis");
		assertInflection(auklēt, ind_fut_3, "auklēs");
		assertInflection(mērcēt, ind_fut_3, "mērcēs");

		// Citas izteiksmes

		AttributeValues imp = new AttributeValues();
		imp.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		imp.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Imperative);
		assertInflectionMultipleWeak(dūmuot, imp, new HashSet<>(){{ add("dūmoj"); add("dūmojit");}});
		assertInflectionMultipleWeak(teireit, imp, new HashSet<>(){{ add("teirej"); add("teirejit");}});
		assertInflectionMultipleWeak(auklēt, imp, new HashSet<>(){{ add("auklej"); add("auklejit");}});
		assertInflectionMultipleWeak(auklēt, imp, new HashSet<>(){{ add("auklej"); add("auklejit");}});
		assertInflectionMultipleWeak(mērcēt, imp, new HashSet<>(){{ add("mērcej"); add("mērcejit");}});

		AttributeValues deb = new AttributeValues();
		deb.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		deb.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Debitive);
		assertInflection(dūmuot, deb, "juodūmoj");
		assertInflection(teireit, deb, "juoteirej");
		assertInflection(auklēt, deb, "juoauklej");
		assertInflection(mērcēt, deb, "juomērcej");

		AttributeValues cond = new AttributeValues();
		cond.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		cond.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Conditional);
		cond.addAttribute(AttributeNames.i_Person, "Nepiemīt");
		assertInflection(dūmuot, cond, "dūmuotu");
		assertInflection(teireit, cond, "teireitu");
		assertInflection(auklēt, cond, "auklātu");
		assertInflection(mērcēt, cond, "mārcātu");

		// Nikole 2025-01-30 saka, ka šie atšķiras no pārējās vēlējuma izteiksmes
		AttributeValues cond_2pers_sg = new AttributeValues();
		cond_2pers_sg.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		cond_2pers_sg.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Conditional);
		cond_2pers_sg.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		cond_2pers_sg.addAttribute(AttributeNames.i_Person, "2");
		assertInflection(dūmuot, cond_2pers_sg, "dūmuotim");
		assertInflection(teireit, cond_2pers_sg, "teireitim");
		assertInflection(auklēt, cond_2pers_sg, "auklētim");
		assertInflection(mērcēt, cond_2pers_sg, "mērcētim");

		AttributeValues quot_pres_nogen_nonum = new AttributeValues();
		quot_pres_nogen_nonum.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		quot_pres_nogen_nonum.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Quotative);
		quot_pres_nogen_nonum.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
		quot_pres_nogen_nonum.addAttribute(AttributeNames.i_Gender, AttributeNames.v_NA);
		quot_pres_nogen_nonum.addAttribute(AttributeNames.i_Number, AttributeNames.v_NA);
		assertInflection(dūmuot, quot_pres_nogen_nonum, "dūmojūt");
		assertInflection(teireit, quot_pres_nogen_nonum, "teirejūt");
		assertInflection(auklēt, quot_pres_nogen_nonum, "auklejūt");
		assertInflection(mērcēt, quot_pres_nogen_nonum, "mērcejūt");

		AttributeValues quot_fut_fem_pl = new AttributeValues();
		quot_fut_fem_pl.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		quot_fut_fem_pl.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Quotative);
		quot_fut_fem_pl.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Future);
		quot_fut_fem_pl.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Feminine);
		quot_fut_fem_pl.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
		assertInflection(dūmuot, quot_fut_fem_pl, "dūmuoškūšys");
		assertInflection(teireit, quot_fut_fem_pl, "teireiškūšys");
		assertInflection(auklēt, quot_fut_fem_pl, "auklieškūšys");
		assertInflection(mērcēt, quot_fut_fem_pl, "mērcieškūšys");

	}

	@Test
	public void konj3eit() {
		List<Wordform> dareit = analyzer.generateInflectionsFromParadigm("dareit", 48);
		List<Wordform> taiseit = analyzer.generateInflectionsFromParadigm("taiseit", 48);
		List<Wordform> saceit = analyzer.generateInflectionsFromParadigm("saceit", 49);

		// Īstenības izteiksme
		// Tagadne: 162., 164. mija
		AttributeValues ind_pres_1_sg = new AttributeValues();
		ind_pres_1_sg.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		ind_pres_1_sg.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Indicative);
		ind_pres_1_sg.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
		ind_pres_1_sg.addAttribute(AttributeNames.i_Person, "1");
		ind_pres_1_sg.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		assertInflection(dareit, ind_pres_1_sg, "doru");
		assertInflection(taiseit, ind_pres_1_sg, "taisu");
		assertInflection(saceit, ind_pres_1_sg, "soku");

		// Pagātne: 119. mija
		AttributeValues ind_past_1_sg = new AttributeValues();
		ind_past_1_sg.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		ind_past_1_sg.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Indicative);
		ind_past_1_sg.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Past);
		ind_past_1_sg.addAttribute(AttributeNames.i_Person, "1");
		ind_past_1_sg.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		assertInflection(dareit, ind_past_1_sg, "dareju");
		assertInflection(taiseit, ind_past_1_sg, "taiseju");
		assertInflection(saceit, ind_past_1_sg, "saceju");

		// Nākotne:  0. mija
		AttributeValues inf_fut_1_sg = new AttributeValues();
		inf_fut_1_sg.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		inf_fut_1_sg.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Indicative);
		inf_fut_1_sg.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Future);
		inf_fut_1_sg.addAttribute(AttributeNames.i_Person, "1");
		inf_fut_1_sg.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		assertInflection(dareit, inf_fut_1_sg, "dareišu");
		assertInflection(taiseit, inf_fut_1_sg, "taiseišu");
		assertInflection(saceit, inf_fut_1_sg, "saceišu");

		// Citas izteiksmes
		// Pavēles: 162., 164. mija
		AttributeValues imp = new AttributeValues();
		imp.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		imp.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Imperative);
		assertInflectionMultipleWeak(dareit, imp, new HashSet<>(){{ add("dori"); add("dorit");}});
		assertInflectionMultipleWeak(taiseit, imp, new HashSet<>(){{ add("taisi"); add("taisit");}});
		assertInflectionMultipleWeak(saceit, imp, new HashSet<>(){{ add("soki"); add("sokit");}});

		// Vajadzības: 151., 152. mija
		AttributeValues deb = new AttributeValues();
		deb.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		deb.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Debitive);
		assertInflection(dareit, deb, "juodora");
		assertInflection(taiseit, deb, "juotaisa");
		assertInflection(saceit, deb, "juosoka");

		// Vēlējuma: 0. mija
		AttributeValues cond = new AttributeValues();
		cond.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		cond.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Conditional);
		cond.addAttribute(AttributeNames.i_Person, "Nepiemīt");
		assertInflection(dareit, cond, "dareitu");
		assertInflection(taiseit, cond, "taiseitu");
		assertInflection(saceit, cond, "saceitu");

		AttributeValues cond_2pers_sg = new AttributeValues();
		cond_2pers_sg.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		cond_2pers_sg.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Conditional);
		cond_2pers_sg.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		cond_2pers_sg.addAttribute(AttributeNames.i_Person, "2");
		assertInflection(dareit, cond_2pers_sg, "dareitim");
		assertInflection(taiseit, cond_2pers_sg, "taiseitim");
		assertInflection(saceit, cond_2pers_sg, "saceitim");

		// Atstāstījuma: 162., 164., 0. mija
		AttributeValues quot_pres_nogen_nonum = new AttributeValues();
		quot_pres_nogen_nonum.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		quot_pres_nogen_nonum.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Quotative);
		quot_pres_nogen_nonum.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
		quot_pres_nogen_nonum.addAttribute(AttributeNames.i_Gender, AttributeNames.v_NA);
		quot_pres_nogen_nonum.addAttribute(AttributeNames.i_Number, AttributeNames.v_NA);
		assertInflection(dareit, quot_pres_nogen_nonum, "dorūt");
		assertInflection(taiseit, quot_pres_nogen_nonum, "taisūt");
		assertInflection(saceit, quot_pres_nogen_nonum, "sokūt");

		AttributeValues quot_fut_fem_pl = new AttributeValues();
		quot_fut_fem_pl.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		quot_fut_fem_pl.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Quotative);
		quot_fut_fem_pl.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Future);
		quot_fut_fem_pl.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Feminine);
		quot_fut_fem_pl.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
		assertInflection(dareit, quot_fut_fem_pl, "dareiškūšys");
		assertInflection(taiseit, quot_fut_fem_pl, "taiseiškūšys");
		assertInflection(saceit, quot_fut_fem_pl, "saceiškūšys");

		// Divdabji.
		// Tagadnes darāmās kārtas: 162., 164. mija
		AttributeValues part_act_pres = new AttributeValues();
		part_act_pres.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		part_act_pres.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Participle);
		part_act_pres.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
		part_act_pres.addAttribute(AttributeNames.i_Voice, AttributeNames.v_Active);
		part_act_pres.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
		part_act_pres.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		part_act_pres.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
		part_act_pres.addAttribute(AttributeNames.i_Degree, AttributeNames.v_Positive);
		part_act_pres.addAttribute(AttributeNames.i_Definiteness, AttributeNames.v_Indefinite);
		assertInflection(dareit, part_act_pres, "dorūšs");
		assertInflection(taiseit, part_act_pres, "taisūšs");
		assertInflection(saceit, part_act_pres, "sokūšs");

		// 163., 165. mija
		AttributeValues part_act_pres_comp_def = new AttributeValues();
		part_act_pres_comp_def.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		part_act_pres_comp_def.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Participle);
		part_act_pres_comp_def.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
		part_act_pres_comp_def.addAttribute(AttributeNames.i_Voice, AttributeNames.v_Active);
		part_act_pres_comp_def.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
		part_act_pres_comp_def.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		part_act_pres_comp_def.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
		part_act_pres_comp_def.addAttribute(AttributeNames.i_Degree, AttributeNames.v_Comparative);
		part_act_pres_comp_def.addAttribute(AttributeNames.i_Definiteness, AttributeNames.v_Definite);
		assertInflection(dareit, part_act_pres_comp_def, "dorūšuokais");
		assertInflection(taiseit, part_act_pres_comp_def, "taisūšuokais");
		assertInflection(saceit, part_act_pres_comp_def, "sokūšuokais");

		// Tagadnes ciešamās kārtas: 162., 164. mija
		AttributeValues part_pass_pres = new AttributeValues();
		part_pass_pres.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		part_pass_pres.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Participle);
		part_pass_pres.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
		part_pass_pres.addAttribute(AttributeNames.i_Voice, AttributeNames.v_Passive);
		part_pass_pres.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
		part_pass_pres.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		part_pass_pres.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
		part_pass_pres.addAttribute(AttributeNames.i_Degree, AttributeNames.v_Positive);
		part_pass_pres.addAttribute(AttributeNames.i_Definiteness, AttributeNames.v_Indefinite);
		assertInflection(dareit, part_pass_pres, "doroms");
		assertInflection(taiseit, part_pass_pres, "taisoms");
		assertInflection(saceit, part_pass_pres, "sokoms");

		// 163., 165. mija
		AttributeValues part_pass_pres_comp_def = new AttributeValues();
		part_pass_pres_comp_def.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		part_pass_pres_comp_def.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Participle);
		part_pass_pres_comp_def.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
		part_pass_pres_comp_def.addAttribute(AttributeNames.i_Voice, AttributeNames.v_Passive);
		part_pass_pres_comp_def.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
		part_pass_pres_comp_def.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		part_pass_pres_comp_def.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
		part_pass_pres_comp_def.addAttribute(AttributeNames.i_Degree, AttributeNames.v_Comparative);
		part_pass_pres_comp_def.addAttribute(AttributeNames.i_Definiteness, AttributeNames.v_Definite);
		assertInflection(dareit, part_pass_pres_comp_def, "doromuokais");
		assertInflection(taiseit, part_pass_pres_comp_def, "taisomuokais");
		assertInflection(saceit, part_pass_pres_comp_def, "sokomuokais");

		// Pagātnes ciešamās kārtas: 0. mija
		AttributeValues part_pass_past = new AttributeValues();
		part_pass_past.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		part_pass_past.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Participle);
		part_pass_past.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Past);
		part_pass_past.addAttribute(AttributeNames.i_Voice, AttributeNames.v_Passive);
		part_pass_past.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
		part_pass_past.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		part_pass_past.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
		part_pass_past.addAttribute(AttributeNames.i_Degree, AttributeNames.v_Positive);
		part_pass_past.addAttribute(AttributeNames.i_Definiteness, AttributeNames.v_Indefinite);
		assertInflection(dareit, part_pass_past, "dareits");
		assertInflection(taiseit, part_pass_past, "taiseits");
		assertInflection(saceit, part_pass_past, "saceits");

		// 121. mija
		AttributeValues part_pass_past_comp_def = new AttributeValues();
		part_pass_past_comp_def.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		part_pass_past_comp_def.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Participle);
		part_pass_past_comp_def.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Past);
		part_pass_past_comp_def.addAttribute(AttributeNames.i_Voice, AttributeNames.v_Passive);
		part_pass_past_comp_def.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
		part_pass_past_comp_def.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		part_pass_past_comp_def.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
		part_pass_past_comp_def.addAttribute(AttributeNames.i_Degree, AttributeNames.v_Comparative);
		part_pass_past_comp_def.addAttribute(AttributeNames.i_Definiteness, AttributeNames.v_Definite);
		assertInflection(dareit, part_pass_past_comp_def, "dareituokais");
		assertInflection(taiseit, part_pass_past_comp_def, "taiseituokais");
		assertInflection(saceit, part_pass_past_comp_def, "saceituokais");

		// Pagātnes darāmās kārtas: 119. mija
		AttributeValues part_act_past = new AttributeValues();
		part_act_past.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		part_act_past.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Participle);
		part_act_past.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Past);
		part_act_past.addAttribute(AttributeNames.i_Voice, AttributeNames.v_Active);
		part_act_past.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
		part_act_past.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		part_act_past.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
		part_act_past.addAttribute(AttributeNames.i_Degree, AttributeNames.v_Positive);
		part_act_past.addAttribute(AttributeNames.i_Definiteness, AttributeNames.v_Indefinite);
		assertInflection(dareit, part_act_past, "darejs");
		assertInflection(taiseit, part_act_past, "taisejs");
		assertInflection(saceit, part_act_past, "sacejs");

		// 120. mija
		AttributeValues part_act_past_comp_def = new AttributeValues();
		part_act_past_comp_def.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		part_act_past_comp_def.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Participle);
		part_act_past_comp_def.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Past);
		part_act_past_comp_def.addAttribute(AttributeNames.i_Voice, AttributeNames.v_Active);
		part_act_past_comp_def.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
		part_act_past_comp_def.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		part_act_past_comp_def.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
		part_act_past_comp_def.addAttribute(AttributeNames.i_Degree, AttributeNames.v_Comparative);
		part_act_past_comp_def.addAttribute(AttributeNames.i_Definiteness, AttributeNames.v_Definite);
		assertInflection(dareit, part_act_past_comp_def, "darejušuokais");
		assertInflection(taiseit, part_act_past_comp_def, "taisejušuokais");
		assertInflection(saceit, part_act_past_comp_def, "sacejušuokais");

		// Lietvārds: 0. mija
		AttributeValues noun = new AttributeValues();
		noun.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
		noun.addAttribute(AttributeNames.i_NounType, AttributeNames.v_CommonNoun);
		noun.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Feminine);
		noun.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		noun.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
		assertInflection(dareit, noun, "dareišona");
		assertInflection(taiseit, noun, "taiseišona");
		assertInflection(saceit, noun, "saceišona");
	}

	@Test
	public void konj3ēt()
	{
		List<Wordform> gulēt = analyzer.generateInflectionsFromParadigm("gulēt", 50);
		List<Wordform> ticēt = analyzer.generateInflectionsFromParadigm("ticēt", 51);
		List<Wordform> svinēt = analyzer.generateInflectionsFromParadigm("svinēt", 51);
		List<Wordform> dzierdēt = analyzer.generateInflectionsFromParadigm("dzierdēt", 52);
		//List<Wordform> muocēt = analyzer.generateInflectionsFromParadigm("muocēt", 52);

		// Īstenības izteiksme
		// Tagadne: 124., 125., 128., 166. mija
		AttributeValues ind_pres_1_sg = new AttributeValues();
		ind_pres_1_sg.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		ind_pres_1_sg.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Indicative);
		ind_pres_1_sg.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
		ind_pres_1_sg.addAttribute(AttributeNames.i_Person, "1");
		ind_pres_1_sg.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		assertInflection(gulēt, ind_pres_1_sg, "guļu");
		assertInflection(ticēt, ind_pres_1_sg, "tycu");
		assertInflection(svinēt, ind_pres_1_sg, "svynu");
		assertInflection(dzierdēt, ind_pres_1_sg, "dzieržu");
		//assertInflection(muocēt, ind_pres_1_sg, "muoku");

		AttributeValues ind_pres_2_sg = new AttributeValues();
		ind_pres_2_sg.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		ind_pres_2_sg.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Indicative);
		ind_pres_2_sg.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
		ind_pres_2_sg.addAttribute(AttributeNames.i_Person, "2");
		ind_pres_2_sg.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		assertInflection(gulēt, ind_pres_2_sg, "guli");
		assertInflection(ticēt, ind_pres_2_sg, "tici");
		assertInflection(svinēt, ind_pres_2_sg, "svini");
		assertInflection(dzierdēt, ind_pres_2_sg, "dzierdi");
		//assertInflection(muocēt, ind_pres_2_sg, "muoki");

		AttributeValues ind_pres_3 = new AttributeValues();
		ind_pres_3.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		ind_pres_3.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Indicative);
		ind_pres_3.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
		ind_pres_3.addAttribute(AttributeNames.i_Person, "3");
		assertInflection(gulēt, ind_pres_3, "guļ");
		assertInflection(ticēt, ind_pres_3, "tic");
		assertInflection(svinēt, ind_pres_3, "sviņ");
		assertInflection(dzierdēt, ind_pres_3, "dzierd");
		//assertInflection(muocēt, ind_pres_3, "muok");

		// Citas izteiksmes
		// Pavēles: 124., 128. mija
		AttributeValues imp = new AttributeValues();
		imp.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		imp.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Imperative);
		assertInflectionMultipleWeak(gulēt, imp, new HashSet<>(){{ add("guli"); add("gulit");}});
		assertInflectionMultipleWeak(ticēt, imp, new HashSet<>(){{ add("tici"); add("ticit");}});
		assertInflectionMultipleWeak(svinēt, imp, new HashSet<>(){{ add("svini"); add("svinit");}});
		assertInflectionMultipleWeak(dzierdēt, imp, new HashSet<>(){{ add("dzierdi"); add("dzierdit");}});
		//assertInflectionMultipleWeak(muocēt, imp, new HashSet<>(){{ add("muoki"); add("muokit");}});

		// Vajadzības: 153., 154. mija
		AttributeValues deb = new AttributeValues();
		deb.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		deb.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Debitive);
		assertInflection(gulēt, deb, "juoguļ");
		assertInflection(ticēt, deb, "juotic");
		assertInflection(svinēt, deb, "juosviņ");
		assertInflection(dzierdēt, deb, "juodzierd");
		//assertInflection(muocēt, deb, "juomuok");

		// Vēlējuma: 124., 166. mija
		AttributeValues cond = new AttributeValues();
		cond.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		cond.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Conditional);
		cond.addAttribute(AttributeNames.i_Person, "Nepiemīt");
		assertInflection(gulēt, cond, "gulātu");
		assertInflection(ticēt, cond, "tycātu");
		assertInflection(svinēt, cond, "svynātu");
		assertInflection(dzierdēt, cond, "dzierdātu");
		//assertInflection(muocēt, cond, "muocātu");
		// 0., 124. mija
		AttributeValues cond_2pers_sg = new AttributeValues();
		cond_2pers_sg.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		cond_2pers_sg.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Conditional);
		cond_2pers_sg.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		cond_2pers_sg.addAttribute(AttributeNames.i_Person, "2");
		assertInflection(gulēt, cond_2pers_sg, "gulētim");
		assertInflection(ticēt, cond_2pers_sg, "ticētim");
		assertInflection(svinēt, cond_2pers_sg, "svinētim");
		assertInflection(dzierdēt, cond_2pers_sg, "dzierdētim"); //todo
		//assertInflectionMultipleWeak(muocēt, cond_2pers_sg, new HashSet<>(){{ add("muocātim"); add("muocētim");}});

		// Atstāstījuma: 125., 128., 166. mija
		AttributeValues quot_pres_nogen_nonum = new AttributeValues();
		quot_pres_nogen_nonum.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		quot_pres_nogen_nonum.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Quotative);
		quot_pres_nogen_nonum.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
		quot_pres_nogen_nonum.addAttribute(AttributeNames.i_Gender, AttributeNames.v_NA);
		quot_pres_nogen_nonum.addAttribute(AttributeNames.i_Number, AttributeNames.v_NA);
		assertInflection(gulēt, quot_pres_nogen_nonum, "guļūt");
		assertInflection(ticēt, quot_pres_nogen_nonum, "tycūt");
		assertInflection(svinēt, quot_pres_nogen_nonum, "svynūt");
		assertInflection(dzierdēt, quot_pres_nogen_nonum, "dzieržūt");
		// 124. mija
		AttributeValues quot_fut_fem_pl = new AttributeValues();
		quot_fut_fem_pl.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		quot_fut_fem_pl.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Quotative);
		quot_fut_fem_pl.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Future);
		quot_fut_fem_pl.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Feminine);
		quot_fut_fem_pl.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
		assertInflection(gulēt, quot_fut_fem_pl, "gulieškūšys");
		assertInflection(ticēt, quot_fut_fem_pl, "ticieškūšys");
		assertInflection(svinēt, quot_fut_fem_pl, "svinieškūšys");
		assertInflection(dzierdēt, quot_fut_fem_pl, "dzierdieškūšys");
		//assertInflection(muocēt, quot_fut_fem_pl, "muocieškūšys");

		// Divdabji.
		// Tagadnes darāmās kārtas: 125., 128., 166. mija
		AttributeValues part_act_pres = new AttributeValues();
		part_act_pres.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		part_act_pres.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Participle);
		part_act_pres.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
		part_act_pres.addAttribute(AttributeNames.i_Voice, AttributeNames.v_Active);
		part_act_pres.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
		part_act_pres.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		part_act_pres.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
		part_act_pres.addAttribute(AttributeNames.i_Degree, AttributeNames.v_Positive);
		part_act_pres.addAttribute(AttributeNames.i_Definiteness, AttributeNames.v_Indefinite);
		assertInflection(gulēt, part_act_pres, "guļūšs");
		assertInflection(ticēt, part_act_pres, "tycūšs");
		assertInflection(svinēt, part_act_pres, "svynūšs");
		assertInflection(dzierdēt, part_act_pres, "dzieržūšs");
		//assertInflection(muocēt, part_act_pres, "muokūšs");

		// 127., 129., 167. mija
		AttributeValues part_act_pres_comp_def = new AttributeValues();
		part_act_pres_comp_def.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		part_act_pres_comp_def.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Participle);
		part_act_pres_comp_def.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
		part_act_pres_comp_def.addAttribute(AttributeNames.i_Voice, AttributeNames.v_Active);
		part_act_pres_comp_def.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
		part_act_pres_comp_def.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		part_act_pres_comp_def.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
		part_act_pres_comp_def.addAttribute(AttributeNames.i_Degree, AttributeNames.v_Comparative);
		part_act_pres_comp_def.addAttribute(AttributeNames.i_Definiteness, AttributeNames.v_Definite);
		assertInflection(gulēt, part_act_pres_comp_def, "guļūšuokais");
		assertInflection(ticēt, part_act_pres_comp_def, "tycūšuokais");
		assertInflection(svinēt, part_act_pres_comp_def, "svynūšuokais");
		assertInflection(dzierdēt, part_act_pres_comp_def, "dzieržūšuokais");
		//assertInflection(muocēt, part_act_pres_comp_def, "muokūšuokais");

		// Tagadnes ciešamās kārtas: 125., 128., 166. mija
		AttributeValues part_pass_pres = new AttributeValues();
		part_pass_pres.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		part_pass_pres.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Participle);
		part_pass_pres.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
		part_pass_pres.addAttribute(AttributeNames.i_Voice, AttributeNames.v_Passive);
		part_pass_pres.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
		part_pass_pres.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		part_pass_pres.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
		part_pass_pres.addAttribute(AttributeNames.i_Degree, AttributeNames.v_Positive);
		part_pass_pres.addAttribute(AttributeNames.i_Definiteness, AttributeNames.v_Indefinite);
		assertInflection(gulēt, part_pass_pres, "guļams");
		assertInflection(ticēt, part_pass_pres, "tycams");
		assertInflection(svinēt, part_pass_pres, "svynams");
		assertInflection(dzierdēt, part_pass_pres, "dzieržams");
		//assertInflection(muocēt, part_pass_pres, "muokams");

		// 127., 129., 167. mija
		AttributeValues part_pass_pres_comp_def = new AttributeValues();
		part_pass_pres_comp_def.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		part_pass_pres_comp_def.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Participle);
		part_pass_pres_comp_def.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
		part_pass_pres_comp_def.addAttribute(AttributeNames.i_Voice, AttributeNames.v_Passive);
		part_pass_pres_comp_def.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
		part_pass_pres_comp_def.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		part_pass_pres_comp_def.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
		part_pass_pres_comp_def.addAttribute(AttributeNames.i_Degree, AttributeNames.v_Comparative);
		part_pass_pres_comp_def.addAttribute(AttributeNames.i_Definiteness, AttributeNames.v_Definite);
		assertInflection(gulēt, part_pass_pres_comp_def, "guļamuokais");
		assertInflection(gulēt, part_pass_pres_comp_def, "guļamuokais");
		assertInflection(svinēt, part_pass_pres_comp_def, "svynamuokais");
		assertInflection(dzierdēt, part_pass_pres_comp_def, "dzieržamuokais");
		//assertInflection(muocēt, part_pass_pres_comp_def, "muokamuokais");

		// Pagātnes ciešamās kārtas: 124., 166. mija
		AttributeValues part_pass_past = new AttributeValues();
		part_pass_past.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		part_pass_past.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Participle);
		part_pass_past.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Past);
		part_pass_past.addAttribute(AttributeNames.i_Voice, AttributeNames.v_Passive);
		part_pass_past.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
		part_pass_past.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		part_pass_past.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
		part_pass_past.addAttribute(AttributeNames.i_Degree, AttributeNames.v_Positive);
		part_pass_past.addAttribute(AttributeNames.i_Definiteness, AttributeNames.v_Indefinite);
		assertInflection(gulēt, part_pass_past, "gulāts");
		assertInflection(ticēt, part_pass_past, "tycāts");
		assertInflection(svinēt, part_pass_past, "svynāts");
		assertInflection(dzierdēt, part_pass_past, "dzierdāts");
		//assertInflection(muocēt, part_pass_past, "muocāts");

		// 126., 167. mija
		AttributeValues part_pass_past_comp_def = new AttributeValues();
		part_pass_past_comp_def.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		part_pass_past_comp_def.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Participle);
		part_pass_past_comp_def.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Past);
		part_pass_past_comp_def.addAttribute(AttributeNames.i_Voice, AttributeNames.v_Passive);
		part_pass_past_comp_def.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
		part_pass_past_comp_def.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		part_pass_past_comp_def.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
		part_pass_past_comp_def.addAttribute(AttributeNames.i_Degree, AttributeNames.v_Comparative);
		part_pass_past_comp_def.addAttribute(AttributeNames.i_Definiteness, AttributeNames.v_Definite);
		assertInflection(gulēt, part_pass_past_comp_def, "gulātuokais");
		assertInflection(ticēt, part_pass_past_comp_def, "tycātuokais");
		assertInflection(svinēt, part_pass_past_comp_def, "svynātuokais");
		assertInflection(dzierdēt, part_pass_past_comp_def, "dzierdātuokais");
		//assertInflection(muocēt, part_pass_past_comp_def, "muocātuokais");

		// Pagātnes darāmās kārtas: 124. mija
		AttributeValues part_act_past = new AttributeValues();
		part_act_past.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		part_act_past.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Participle);
		part_act_past.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Past);
		part_act_past.addAttribute(AttributeNames.i_Voice, AttributeNames.v_Active);
		part_act_past.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
		part_act_past.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		part_act_past.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
		part_act_past.addAttribute(AttributeNames.i_Degree, AttributeNames.v_Positive);
		part_act_past.addAttribute(AttributeNames.i_Definiteness, AttributeNames.v_Indefinite);
		assertInflection(gulēt, part_act_past, "guliejs");
		assertInflection(ticēt, part_act_past, "ticiejs");
		assertInflection(svinēt, part_act_past, "sviniejs");
		assertInflection(dzierdēt, part_act_past, "dzierdiejs");
		//assertInflection(muocēt, part_act_past, "muociejs");

		// 126. mija
		AttributeValues part_act_past_comp_def = new AttributeValues();
		part_act_past_comp_def.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		part_act_past_comp_def.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Participle);
		part_act_past_comp_def.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Past);
		part_act_past_comp_def.addAttribute(AttributeNames.i_Voice, AttributeNames.v_Active);
		part_act_past_comp_def.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
		part_act_past_comp_def.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		part_act_past_comp_def.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
		part_act_past_comp_def.addAttribute(AttributeNames.i_Degree, AttributeNames.v_Comparative);
		part_act_past_comp_def.addAttribute(AttributeNames.i_Definiteness, AttributeNames.v_Definite);
		assertInflection(gulēt, part_act_past_comp_def, "guliejušuokais");
		assertInflection(ticēt, part_act_past_comp_def, "ticiejušuokais");
		assertInflection(svinēt, part_act_past_comp_def, "sviniejušuokais");
		assertInflection(dzierdēt, part_act_past_comp_def, "dzierdiejušuokais");
		//assertInflection(muocēt, part_act_past_comp_def, "muociejušuokais");

		// Daļēji lokāmais divdabis: 124., 166. mija
		AttributeValues part_partdecl = new AttributeValues();
		part_partdecl.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		part_partdecl.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Participle);
		part_partdecl.addAttribute(AttributeNames.i_Lokaamiiba, AttributeNames.v_DaljeejiLokaams);
		part_partdecl.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
		part_partdecl.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		part_partdecl.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
		assertInflection(gulēt, part_partdecl, "gulādams");
		assertInflection(ticēt, part_partdecl, "tycādams");
		assertInflection(svinēt, part_partdecl, "svynādams");
		assertInflection(dzierdēt, part_partdecl, "dzierdādams");
		//assertInflection(muocēt, part_partdecl, "muocādams");

		// Lietvārds: 124. mija
		AttributeValues noun = new AttributeValues();
		noun.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
		noun.addAttribute(AttributeNames.i_NounType, AttributeNames.v_CommonNoun);
		noun.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Feminine);
		noun.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		noun.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
		assertInflection(gulēt, noun, "guliešona");
		assertInflection(ticēt, noun, "ticiešona");
		assertInflection(svinēt, noun, "sviniešona");
		assertInflection(dzierdēt, noun, "dzierdiešona");
		//assertInflection(muocēt, noun, "muociešona");

	}

	@Test
	public void verbNeg() {
		List<Wordform> dūmuot = analyzer.generateInflectionsFromParadigm("dūmuot", 44);
		List<Wordform> gulēt = analyzer.generateInflectionsFromParadigm("gulēt", 50);

		AttributeValues testParams = new AttributeValues();
		testParams.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
		testParams.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Indicative);
		testParams.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Past);
		testParams.addAttribute(AttributeNames.i_Person, "1");
		testParams.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
		testParams.addAttribute(AttributeNames.i_Noliegums, AttributeNames.v_Yes);

		for (Wordform wf : dūmuot) {
			assertNotEquals("nedūmuot", wf.getToken());
		}
		assertInflectionMultipleWeak(dūmuot, testParams, new HashSet<>(){{ add("nadūmuoju"); add("nadūmovu");}});
		for (Wordform wf : gulēt) {
			assertNotEquals("vysnaugulamuokuos", wf.getToken());
			assertNotEquals("vysnauguļamuokuos", wf.getToken());
		}

		Word nadūmuoju = analyzer.analyze("nadūmuoju");
		assertTrue(nadūmuoju.isRecognized());
	}

	@Test
	public void verbGuess() {
		analyzer.enableGuessing = true;
		// pokemonizēt
		Word debitive = analyzer.analyze("juopokemonizej");
//		debitive.describe(System.out);
		assertTrue(debitive.isRecognized());
		Word negative = analyzer.analyze("napokemonizēt");
		assertTrue(negative.isRecognized());

		Word izPred = analyzer.analyze("izpokemonizēt");
		assertTrue(izPred.isRecognized());
		Word izPredRefl = analyzer.analyze("izapokemonizēt");
		assertTrue(izPredRefl.isRecognized());

		Word puorPred = analyzer.analyze("puorpokemonizēt");
		assertTrue(puorPred.isRecognized());
		Word puorPredRefl1 = analyzer.analyze("puorsapokemonizēt");
		assertTrue(puorPredRefl1.isRecognized());
		Word puorPredRefl2 = analyzer.analyze("puorzapokemonizēt");
		assertTrue(puorPredRefl2.isRecognized());

		Word negativeWithPred = analyzer.analyze("naapsapokemonizēt");
		assertTrue(negativeWithPred.isRecognized());
	}

	@Test
	public void šys() {
		ArrayList<Wordform> formas = analyzer.generateInflectionsFromParadigm("šys", 29);
		//describe(formas);
		assertTrue("Jābūt vairākām formām 'šys' tabulai no hardcoded", formas.size()>4);
	}
}

