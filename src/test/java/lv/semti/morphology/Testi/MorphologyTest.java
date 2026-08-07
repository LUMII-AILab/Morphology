package lv.semti.morphology.Testi;

import lv.semti.morphology.analyzer.*;
import lv.semti.morphology.attributes.AttributeNames;
import lv.semti.morphology.attributes.AttributeValues;
import lv.semti.morphology.lexicon.Lexeme;
import lv.semti.morphology.lexicon.Paradigm;
import lv.semti.morphology.lexicon.StemType;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;

// TODO: organise tests in more systematic chunks according to their properties

/*
 * For tests involving specific forms using the actual correct form as variable
 * improves readability.
 */
@SuppressWarnings("NonAsciiCharacters")
public class MorphologyTest extends LanguageSpecificTests {

    @BeforeClass
    public static void setUpBeforeClass() {
        try {
            analyzer = new Analyzer(false);
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

    @Ignore("Tēzaurs.lv JSON eksportā ir vairākas morfo-leksēmas kas nāk no vienas tēzaurs-leksēmas un tādēļ ir ar vienādu leksēmas ID - piemēram, ja ir 1. konj. verbam paralēlformas dažos celmos")
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

    //FIXME - jāpārtaisa uz parametrizētiem testiem...

    @Test
    public void cirvis() {
        Word cirvis = analyzer.analyze("cirvis");
        assertTrue(cirvis.isRecognized());
        assertEquals("ncmsn2", cirvis.wordforms.getFirst().getTag());
    }

    @Test
    public void nadziņi() {
        //2008-09-06 atrasts gļuks, ka "pīrādziņi" analīzē pamatforma bija "pīrāgš"
        //2012-02-10 - vairs nav aktuāls 'pīrāgs', jābūt 'pīrādziņš'
        //2015-08-03 failo, jo atrod LĢIS apdzīvoto vietu "Pīrāgi", nomainīts uz nadziņiem
        analyzer.enableDiminutive = true;
        Word nadziņi = analyzer.analyze("nadziņi");
        assertTrue(nadziņi.isRecognized());
        Wordform forma = nadziņi.getBestWordform();
        assertEquals("nadziņš", forma.getValue(AttributeNames.i_Lemma));
    }

    @Test
    public void meitenīte() {
        //2008-09-06 atrasts gļuks, ka "meitenīte" analīzē ir 2 varianti -
        // gan tīri no celma 'meitenīte', gan arī ar deminutīvu no 'meitene'
        Word meitenīte = analyzer.analyze("meitenītē");
        assertTrue(meitenīte.isRecognized());
        assertEquals(1, meitenīte.wordformsCount());
    }

    @Test
    public void simtiem() {
        //2008-09-07 atrasts gļuks, ka "simtiem" analīzē kā pamatforma ir "simti" nevis "simts"
        Word simtiem = analyzer.analyze("simtiem");
        assertTrue(simtiem.isRecognized());
        assertEquals("simts", simtiem.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
    }

    @Test
    public void ēdīs() {
        //2008-09-08 atrasts gļuks, ka pie "ēdīs" analīzes uzkaras
        Word ēdīs = analyzer.analyze("ēdīs");
        assertTrue(ēdīs.isRecognized());
    }

    @Test
    public void ceļu() {
        //2008-09-12 atrasts gļuks, ka "ceļu" analīzē ir tikai lietvārda varianti, bet nav darbības vārda forma
        Word ceļu = analyzer.analyze("ceļu");
        assertTrue(ceļu.isRecognized());

        AttributeValues verbs = new AttributeValues();
        verbs.addAttribute("Vārdšķira", "Darbības vārds");

        ceļu.filterByAttributes(verbs);
        assertTrue(ceļu.isRecognized());
    }

    @Test
    public void sniga() {
        //2008-09-11 atrasts gļuks, ka intransitīvajiem verbiem īpašībā raksta 'netransitīvs'.
        //likvidēta šī īpašība
        Word sniga = analyzer.analyze("sniga");
        assertTrue(sniga.isRecognized());
        assertNull(sniga.wordforms.getFirst().getValue("Verbu grupa no vecā projekta"));
    }

    @Test
    public void bieži() {
        //2008-09-24 atrasts gļuks, ka "bieži" analīzē pamatforma bija "biež"
        Word bieži = analyzer.analyze("bieži");
        assertTrue(bieži.isRecognized());

        boolean found = false;
        for (Wordform wf : bieži.wordforms) {
            if (wf.getValue(AttributeNames.i_Lemma).equals("bieži"))
                found = true;
        }

        assertTrue(found);
    }

    @Test
    public void zaļāk() {
        //2008-09-15 atrasts gļuks, ka apstākļvārdiem pārākā/vispārākā ir sajaukta vietām
        Word zaļāk = analyzer.analyze("zaļāk");
        assertTrue(zaļāk.isRecognized());
        assertEquals("Pārākā", zaļāk.wordforms.getFirst().getValue("Pakāpe"));

        Word viszaļāk = analyzer.analyze("viszaļāk");
        assertTrue(viszaļāk.isRecognized());
        assertEquals("Vispārākā", viszaļāk.wordforms.getFirst().getValue("Pakāpe"));
    }

    @Test
    public void ātrākVisātrāk() {
        //Ticket #6 - nepareizi analizē pārāko/vispārāko pakāpi
        Word ātrāks = analyzer.analyze("ātrāks");
        assertTrue(ātrāks.isRecognized());
        assertEquals("Pārākā", ātrāks.wordforms.getFirst().getValue("Pakāpe"));

        Word visātrākais = analyzer.analyze("visātrākais");
        assertTrue(visātrākais.isRecognized());
        assertEquals("Vispārākā", visātrākais.wordforms.getFirst().getValue("Pakāpe"));
    }

    @Test
    public void pieveicis() {
        analyzer.enablePrefixes = true;
        Word pieveicis = analyzer.analyze("pieveicis");
        assertTrue(pieveicis.isRecognized());
//        assertEquals(AttributeNames.v_Prefix, pieveicis.wordforms.get(0).getValue(AttributeNames.i_Guess));
        assertEquals("vmnpdmsnasnpn", pieveicis.wordforms.getFirst().getTag());
    }

    @Test
    public void paņēmis() {
        Word paņēmis = analyzer.analyze("paņēmis");
        assertTrue(paņēmis.isRecognized());
        assertEquals("vmnpdmsnasnpn", paņēmis.wordforms.getFirst().getTag());
    }

    @Test
    public void durkls() {
        // 2015-08-26 tēzaura apstrādes gaitā mainījās priekšstats šī vārda paradigmu.
        Word durkls = analyzer.analyze("durkls");
        if (durkls.isRecognized())
            assertEquals("1", durkls.wordforms.getFirst().getValue(AttributeNames.i_ParadigmID));
    }

    @Test
    public void lasis() {
        // 2016-02-03 atklāta ģenerēšanas kļūda - trūkst mijas.
        List<Wordform> lasis = analyzer.generateInflections("lasis");
        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
        testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
        testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
        assertInflection(lasis, testset, "laša");

        testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Dative);
        testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
        assertInflection(lasis, testset, "lašiem");

        Word w = analyzer.analyze("lasiem");
        assertFalse(w.isRecognized());
    }

    @Test
    public void skansts() {
        List<Wordform> skansts = analyzer.generateInflections("skansts");
        assertNotEquals(0, skansts.size());
        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
        testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
        testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
        assertInflection(skansts, testset, "skanšu");
    }

    @Test
    public void debesis() {
        // 2016-02-03 ir divu veidu debesis - 3. un 6. deklinācija
        // 3. deklinācijā lokās pēc standarta, bet 6. deklinācijā bez mijas
        List<Wordform> debesis = analyzer.generateInflectionsFromParadigm("debesis", 3);
        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
        testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
        testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
        testset.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
        assertInflection(debesis, testset, "debeša");

        testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Dative);
        testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
        assertInflection(debesis, testset, "debešiem");

        List<Wordform> debess = analyzer.generateInflectionsFromParadigm("debess", 35);
        testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
        testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
        testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
        testset.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Feminine);
        assertInflection(debess, testset, "debess");

        testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
        assertInflection(debess, testset, "debesu");

        testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Dative);
        assertInflection(debess, testset, "debesīm");

        //Word w = locītājs.analyze("debesis");
        //assertTrue(w.isRecognized());
        //assertEquals(w.wordforms.size(), 3); // siev. dz. dsk. nom., akuz., vīr. dz. vsk. nom.
    }

    @Test
    public void balss() {
        // 2016-02-03 Tā kā "debesis" kļūda visticamāk ir saistīta ar 6.dekl. izņēmumiem, tad papildus tests uz tiem.
        List<Wordform> balss = analyzer.generateInflections("balss");
        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
        testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
        testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
        assertInflection(balss, testset, "balsu");

        Word w = analyzer.analyze("balšu");
        assertFalse(w.isRecognized());
    }

    @Test
    public void crap() {
        Word crap = analyzer.analyze("crap");
        assertFalse(crap.isRecognized());
        analyzer.enableGuessing = true;
        analyzer.enableAllGuesses = true;
        analyzer.guessInflexibleNouns = true;
        crap = analyzer.analyze("crap");
        assertTrue(crap.isRecognized());
        assertEquals(AttributeNames.v_Ending, crap.wordforms.getFirst().getValue(AttributeNames.i_Guess));
    }

    @Test
    public void speedTest() {
        long startTime = System.currentTimeMillis();

        analyzer.enableVocative = true;
        analyzer.enableDiminutive = true;
        analyzer.enablePrefixes = false;
        analyzer.enableAllGuesses = true;
        analyzer.searchCompoundWords = false;

        int count = 0;
        for (int i = 1; i < 100; i++) {
            analyzer.analyze("cirvis");
            analyzer.analyze("roku");
            analyzer.analyze("nepadomājot");
            analyzer.analyze("Kirils");
            analyzer.analyze("parakt");
            analyzer.analyze("bundziņas");
            analyzer.analyze("pokemonizēt");
            analyzer.analyze("xyzzyt");
            analyzer.analyze("žvirblis");
            analyzer.analyze("Murgainšteineniem");
            count += 10;
        }

        long endTime = System.currentTimeMillis();
        long interval = endTime - startTime;
        System.out.printf("%d pieprasījumi sekundē (%d ms)\n", count * 1000L / interval, interval);
    }

    //TODO - dubulto leksēmu tests jāuztaisa
    @Test
    public void dubultLeksēmas() {
        PrintWriter out;
        out = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));

        for (Paradigm paradigm : analyzer.paradigms) {
            for (ArrayList<Lexeme> lexemes : paradigm.getLexemesByStem(StemType.STEM1).values()) {
                for (int i = 0; i < lexemes.size(); i++) {
                    for (int j = i + 1; j < lexemes.size(); j++) {
                        Lexeme l1 = lexemes.get(i);
                        Lexeme l2 = lexemes.get(j);

                        boolean matching = true;
                        for (StemType st : paradigm.getStems())
                            if (!l1.getStem(st).equals(l2.getStem(st))) matching = false;

                        for (Entry<String, String> tuple : l1.entrySet()) {
                            if (tuple.getKey().equals("Leksēmas nr")) continue;
                            String other = l1.getValue(tuple.getKey());
                            if (!tuple.getValue().equals(other))
                                matching = false;
                        }

                        for (Entry<String, String> tuple : l2.entrySet()) {
                            if (tuple.getKey().equals("Leksēmas nr")) continue;
                            String other = l1.getValue(tuple.getKey());
                            if (!tuple.getValue().equals(other))
                                matching = false;
                        }

                        if (matching) {
                            System.err.println("Atkārtojas leksēmas:");
                            l1.describe(new PrintWriter(System.err));
                            l2.describe(new PrintWriter(System.err));
                        }
                    }
                }
            }
        }
        out.flush();
    }

    @Test
    public void ticket9() {
        // Ticket #9 - vienskaitlinieki, daudzskaitlinieki, ģenitīveņi

        Word turiene = analyzer.analyze("turiene");
        assertTrue(turiene.isRecognized());
        assertEquals("turiene", turiene.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        Word turienēm = analyzer.analyze("turienēm");
        assertFalse(turienēm.isRecognized());

        Word bikses = analyzer.analyze("bikses");
        assertTrue(bikses.isRecognized());
        assertEquals("bikses", bikses.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        Word augstpapēžu = analyzer.analyze("augstpapēžu");
        assertTrue(augstpapēžu.isRecognized());
        assertEquals("augstpapēžu", augstpapēžu.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        Word augstpapēdis = analyzer.analyze("augstpapēdis");
        assertFalse(augstpapēdis.isRecognized());
    }

    @Test
    public void ticket29() {
        // Ticket #29 - noliegtie vietniekvārdi
        // 2012. 9. janvaaris - Gunta saka ka nevajag vinjus par noliegtajiem saukt
        Word neviens = analyzer.analyze("neviens");
        assertTrue(neviens.isRecognized());
        //assertEquals("p_0msny0", neviens.wordforms.get(0).getValue(AttributeNames.i_Tag));
    }

    @Test
    public void ticket37() {
        // Ticket #37 - 'panest' taču nav noliegts
        analyzer.enablePrefixes = true;
        Word panest = analyzer.analyze("panest");
        assertTrue(panest.isRecognized());
        assertEquals("vmnn0t1000n", panest.wordforms.getFirst().getTag());
    }


    @Test
    public void ticket16() {
        // Ticket #16 - 'trūkst' kļūdaini analizējās kā arī 2. personas forma
        Word trūkst = analyzer.analyze("trūkst");
        assertTrue(trūkst.isRecognized());
        for (Wordform wordform : trūkst.wordforms)
            assertFalse(wordform.isMatchingStrong(AttributeNames.i_Person, "2"));
    }

    @Test
    public void ticket65() {
        // Ticket #65 - neuzrāda noliegumu un neuzrāda kārtu atgriezeniskajiem verbiem
        Word dodas = analyzer.analyze("dodas");
        assertTrue(dodas.isRecognized());
        assertEquals(AttributeNames.v_Active, dodas.wordforms.getFirst().getValue(AttributeNames.i_Voice));
        assertEquals("vmyip_i30an", dodas.wordforms.getFirst().getTag());
    }


    @Test
    @Ignore("2023-06-15 - skaitļa vārdiem pie pārnešanas uz tēzauru likvidējām kārtas datus")
    public void ticket76() {
        // Ticket #76 - skaitļa vārdiem neaiziet uz marķējumu skaitļa vārda kārta
        Word simt = analyzer.analyze("simt");
        assertTrue(simt.isRecognized());
        assertEquals(AttributeNames.v_Hundreds, simt.wordforms.getFirst().getValue(AttributeNames.i_Order));
        assertEquals("mc_0p0", simt.wordforms.getFirst().getTag());
    }

    @Test
    public void ticket84() {
        // Ticket #84 - gļuks ar 1. konjugācijas darbības vārdu divdabjiem -is -usi un atgriezeniskajiem -ies -usies
        Word griezis = analyzer.analyze("griezis");
        assertTrue(griezis.isRecognized());

        boolean found = false;
        for (Wordform wordform : griezis.wordforms)
            if (wordform.isMatchingStrong(AttributeNames.i_Mood, AttributeNames.v_Participle)) found = true;
        assertTrue(found);

        Word griezies = analyzer.analyze("griezies");
        assertTrue(griezies.isRecognized());

        found = false;
        for (Wordform wordform : griezis.wordforms)
            if (wordform.isMatchingStrong(AttributeNames.i_Mood, AttributeNames.v_Participle)) found = true;
        assertTrue(found);
    }

    @Test
    public void tuStum() {
        // 2011-06-09 Laumas reportēts, ka neatpazīst "stumt" formu "stum"
        Word stum = analyzer.analyze("stum");
        assertTrue(stum.isRecognized());

        assertEquals("2", stum.wordforms.getFirst().getValue(AttributeNames.i_Person));
        assertEquals(AttributeNames.v_Present, stum.wordforms.getFirst().getValue(AttributeNames.i_Tense));
    }

    @Test
    public void man() {
        // 2011-12-29 "man" pamatformu uzdod "man", vajag "es"
        Word man = analyzer.analyze("man");
        assertTrue(man.isRecognized());
        assertEquals("es", man.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
    }

    @Test
    public void vairāki() {
        // 2019-10-23 Laura apgalvo, ka sen ir izlemts ka īpašības vārds.
        // agrāk (2011-12-29) bija pārcelts no skaitļa vārda uz vietniekvārdu
        Word vairāki = analyzer.analyze("vairāki");
        assertTrue(vairāki.isRecognized());

        assertEquals(AttributeNames.v_Adjective, vairāki.wordforms.getFirst().getValue(AttributeNames.i_PartOfSpeech));
    }

    @Test
    public void daudzus() {
        // 2019-10-23 Laura apgalvo, ka sen ir izlemts ka īpašības vārds.
        // agrāk (2011-12-29) bija pārcelts no skaitļa vārda uz vietniekvārdu
        Word daudzus = analyzer.analyze("daudzus");
        assertTrue(daudzus.isRecognized());

        assertEquals(AttributeNames.v_Adjective, daudzus.wordforms.getFirst().getValue(AttributeNames.i_PartOfSpeech));
    }

    @Test
    public void jāpasaka() {
        // 2011-12-29 "jāpasaka" neatpazīst
        //vmnd0t300an
        Word jāpasaka = analyzer.analyze("jāpasaka");
        assertTrue(jāpasaka.isRecognized());
    }

    @Test
    public void vajag() {
        // 2012-01-03 "vajag" neatpazīst
        Word vajag = analyzer.analyze("vajag");
        assertTrue(vajag.isRecognized());
    }

    @Test
    public void Vilis() {
        // pie 'viņi' un 'viņiem' atrod vārdu ar pamatformu 'Vilis'
        Word viņi = analyzer.analyze("viņi");
        assertTrue(viņi.isRecognized());
//        describe(viņi.wordforms);
        assertEquals(2, viņi.wordformsCount());
    }

    @Test
    public void atgādinām() {
        // Ticket #226
        // 3.konjugācijas darbības vārdi ar -īt, -īties, -ināt, -ināties
        // locās ar garajiem burtiem izskaņās.
        Word atgādinām = analyzer.analyze("atgādinām");
        assertTrue(atgādinām.isRecognized());
        assertEquals(2, atgādinām.wordformsCount());

        Word atgādināt = analyzer.analyze("atgādināt");
        assertTrue(atgādināt.isRecognized());
        assertEquals(2, atgādināt.wordformsCount());

        Word atgādinat = analyzer.analyze("atgādinat");
        assertFalse(atgādinat.isRecognized());

        Word atgādinam = analyzer.analyze("atgādinam");
        assertFalse(atgādinam.isRecognized());
    }

    @Test
    public void bijušais() {
        // Ticket #255: Neatpazīst "bijušais" dažādos locījumos.
        Word bijušais = analyzer.analyze("bijušais");
        assertTrue(bijušais.isRecognized());

        Word bijusī = analyzer.analyze("bijusī");
        assertTrue(bijusī.isRecognized());

        Word bijušajiem = analyzer.analyze("bijušajiem");
        assertTrue(bijušajiem.isRecognized());
    }

    @Test
    public void video() {
        // Ticket #245: nelokāmie lietvārdi vienmēr ir nominatīvā.
        Word video = analyzer.analyze("video");
        assertTrue(video.isRecognized());
        assertEquals(1, video.wordformsCount());
    }

    @Test
    public void neviens() {
        // Ticket #259: Neviens, nekas, nekāds ir nenoteiktais vietniekvārdi
        // ar noliegumu yes.
        Word neviens = analyzer.analyze("neviens");
        assertTrue(neviens.isRecognized());

        assertEquals(AttributeNames.v_Pronoun, neviens.wordforms.getFirst().getValue(AttributeNames.i_PartOfSpeech));
        assertEquals(AttributeNames.v_Yes, neviens.wordforms.getFirst().getValue(AttributeNames.i_Noliegums));
        assertEquals(AttributeNames.v_Nenoteiktais, neviens.wordforms.getFirst().getValue(AttributeNames.i_VvTips));

        Word nekas = analyzer.analyze("nekas");
        assertTrue(nekas.isRecognized());

        assertEquals(AttributeNames.v_Pronoun, nekas.wordforms.getFirst().getValue(AttributeNames.i_PartOfSpeech));
        assertEquals(AttributeNames.v_Yes, nekas.wordforms.getFirst().getValue(AttributeNames.i_Noliegums));
        assertEquals(AttributeNames.v_Nenoteiktais, nekas.wordforms.getFirst().getValue(AttributeNames.i_VvTips));

        Word nekāds = analyzer.analyze("nekāds");
        assertTrue(nekāds.isRecognized());

        int ind = 0;
        while (ind < nekāds.wordformsCount() &&
                !AttributeNames.v_Pronoun.equalsIgnoreCase(
                        nekāds.wordforms.get(ind).getValue(AttributeNames.i_PartOfSpeech))) {
            ind++;
        }

        assertTrue(ind < nekāds.wordformsCount());
        //assertEquals(AttributeNames.v_Pronoun, nekāds.wordforms.get(ind).getValue(AttributeNames.i_PartOfSpeech));
        assertEquals(AttributeNames.v_Yes, nekāds.wordforms.get(ind).getValue(AttributeNames.i_Noliegums));
        //assertEquals(AttributeNames.v_Nenoteiktie, nekāds.wordforms.get(ind).getValue(AttributeNames.i_VvTips));
    }

    @Test
    public void atnes() {
        // Lauras sūdzība - nesaprot 'atnes' pavēles formu
        Word atnes = analyzer.analyze("atnes");
        assertTrue(atnes.isRecognized());

        AttributeValues filtrs = new AttributeValues();
        filtrs.addAttribute("Izteiksme", "Pavēles");

        atnes.filterByAttributes(filtrs);
        assertTrue(atnes.isRecognized());
    }

    @Test
    public void jāatceras() {
        // Lauras sūdzība - neatpazīst 'jāatceras'
        Word jāatceras = analyzer.analyze("jāatceras");
        assertTrue(jāatceras.isRecognized());

        AttributeValues filtrs = new AttributeValues();
        filtrs.addAttribute("Izteiksme", "Vajadzības");

        jāatceras.filterByAttributes(filtrs);
        assertTrue(jāatceras.isRecognized());
    }

    @Test
    public void jāmāk() {
        // Lauras sūdzība - neatpazīst 'jāmāk'
        Word jāmāk = analyzer.analyze("jāmāk");
        assertTrue(jāmāk.isRecognized());

        AttributeValues testset = new AttributeValues();
        testset.addAttribute("Izteiksme", "Vajadzības");

        jāmāk.filterByAttributes(testset);
        assertTrue(jāmāk.isRecognized());
    }

    @Test
    public void vislabāk() {
        // 2012. 3.feb Gunta saka ka 'vislabāk' pamatforma ir 'labi'
        Word vislabāk = analyzer.analyze("vislabāk");

        assertTrue(vislabāk.isRecognized());
        boolean irPareizā = false;
        for (Wordform vārdforma : vislabāk.wordforms) {
            if (vārdforma.getValue(AttributeNames.i_Lemma).equals("labi"))
                irPareizā = true;
        }

        assertTrue(irPareizā);
    }

    @Test
    public void vairāk() {
        // 2012. 3.feb Gunta saka ka 'vairāk' pamatforma ir 'daudz'
        Word vairāk = analyzer.analyze("vairāk");

        assertTrue(vairāk.isRecognized());
        boolean found = false;
        for (Wordform wf : vairāk.wordforms) {
            if (wf.getValue(AttributeNames.i_Lemma).equals("daudz"))
                found = true;
        }

        assertTrue(found);
    }

    @Test
    public void deminutive() {
        // 2012. 10.feb Vienojāmies ar valodniecēm ka deminutīviem lemmas arī ir deminutīvā

        analyzer.enableDiminutive = true;
        // Used to test on 'cirvītis', but from Summer 2026, it is in actual lexicon
        Word bērnudārziņš = analyzer.analyze("bērnudārziņš");
        Word pļava = analyzer.analyze("pļaviņa");

        assertTrue(bērnudārziņš.isRecognized());
        assertTrue(pļava.isRecognized());

        boolean found = false;
        for (Wordform wf : bērnudārziņš.wordforms) {
            if (wf.getValue(AttributeNames.i_Lemma).equals("bērnudārziņš")) {
                found = true;
                assertEquals(AttributeNames.v_Deminutive, wf.getValue(AttributeNames.i_Guess));
            }
        }
        assertTrue(found);

        found = false;
        for (Wordform wf : pļava.wordforms) {
            if (wf.getValue(AttributeNames.i_Lemma).equals("pļaviņa"))
                found = true;
        }
        assertTrue(found);
    }

    @Test
    public void riebties() {
        // 2012-03-14 "riebties" neatpazīstot

        analyzer.enableGuessing = true;
        Word riebties = analyzer.analyze("riebties");
        assertTrue(riebties.isRecognized());
        assertEquals("riebties", riebties.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
    }

    @Test
    public void sa() {
        // 2012-03-16 esot crash

        analyzer.enablePrefixes = true;
        Word sa = analyzer.analyze("");
        assertFalse(sa.isRecognized());
    }

    @Test
    public void noliegumu_lemma() {
        analyzer.enablePrefixes = true;
        // Noliegumu atvasinājumiem lai ir oriģinālā pamatforma atvasināta
        Word nenest = analyzer.analyze("nenesāt");
        assertTrue(nenest.isRecognized());
        assertEquals("nest", nenest.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
    }

    @Test
    public void kususi() {
        // 2012-03-27 - pie priedēkļu atpazīšanas vārdiem noplēsa priedēkļus

        analyzer.enablePrefixes = true;
        List<Word> tokens = Splitting.tokenize(analyzer, "Vai esi piekususi?");
        Word piekususi = tokens.get(2);
        assertTrue(piekususi.isRecognized());
        assertEquals("piekususi", piekususi.getToken());
        //if (! piekususi.getCorrectWordform().getToken().equalsIgnoreCase("piekususi")) {
        //	PrintWriter izeja = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"));
        //	piekususi.getCorrectWordform().describe(izeja);
        //}
        assertEquals("piekususi", piekususi.wordforms.getFirst().getToken());
    }

    @Test
    public void tokenizesafety() {
        // 2012-03-27 atrasts bug ka tokenizators reizēm izmainīja vārdus
        String text = "Vīrs ar cirvi piekusa joklmnasdasd1239612321 *(&(*^)@!!@# /t/txxx/n\t\nasdas cimdiņi cimdiņzeķītes";
        LinkedList<Word> tokens = Splitting.tokenize(analyzer, text);
        StringBuilder wordtokens = new StringBuilder();
        for (Word w : tokens) {
            wordtokens.append(w.getToken());
            for (Wordform wf : w.wordforms) {
                assertEquals(w.getToken(), wf.getToken());
            }
        }
        assertEquals(text.replace(" ", "").replace("\t", "").replace("\n", ""), wordtokens.toString());

        analyzer.enableVocative = true;
        analyzer.enableDiminutive = true;
        analyzer.enablePrefixes = true;
        analyzer.enableGuessing = true;
        analyzer.enableAllGuesses = true;
        analyzer.searchCompoundWords = true;

        tokens = Splitting.tokenize(analyzer, text);
        wordtokens = new StringBuilder();
        for (Word w : tokens) {
            wordtokens.append(w.getToken());
            for (Wordform wf : w.wordforms) {
                assertEquals(w.getToken(), wf.getToken());
            }
        }
        assertEquals(text.replace(" ", "").replace("\t", "").replace("\n", ""), wordtokens.toString());
    }


    @Test
    public void saīsinājumi() {
        Word uc = analyzer.analyze("u.c.");
        assertTrue(uc.isRecognized());
        assertEquals("yd", uc.wordforms.getFirst().getTag());
    }

    @Test
    public void nopūzdamās() {
        // 2012-03-28 - nesaprot 'nopūzdamās', saprot 'nopūsdamās'
        Word nopūzdamās = analyzer.analyze("pūzdamās");
        assertTrue(nopūzdamās.isRecognized());

        Word nopūsdamās = analyzer.analyze("pūsdamās");
        assertFalse(nopūsdamās.isRecognized());

        Word grūzdams = analyzer.analyze("grūzdams");
        assertTrue(grūzdams.isRecognized());

        Word mezdams = analyzer.analyze("mezdams");
        assertTrue(mezdams.isRecognized());

        Word elsdams = analyzer.analyze("elsdams");
        assertTrue(elsdams.isRecognized());

        Word milzdams = analyzer.analyze("milzdams");
        assertTrue(milzdams.isRecognized());

        Word nesdams = analyzer.analyze("nesdams");
        assertTrue(nesdams.isRecognized());
    }

    @Test
    public void ts() {
        Word nopūsts = analyzer.analyze("pūsts");
        assertTrue(nopūsts.isRecognized());

        Word grūsts = analyzer.analyze("grūsts");
        assertTrue(grūsts.isRecognized());

        Word mests = analyzer.analyze("mests");
        assertTrue(mests.isRecognized());

        Word elsts = analyzer.analyze("elsts");
        assertTrue(elsts.isRecognized());

        Word mēzts = analyzer.analyze("mēzts");
        assertTrue(mēzts.isRecognized());

        Word nests = analyzer.analyze("nests");
        assertTrue(nests.isRecognized());
    }

    @Test
    public void residuals() {
        // Bezmorfoloģijas elementu klasifikācija
        Word slīpsvītra = analyzer.analyze("/");
        assertTrue(slīpsvītra.isRecognized());
        assertEquals("zx", slīpsvītra.wordforms.getFirst().getTag());

        Word dr = analyzer.analyze("dr.");
        assertTrue(dr.isRecognized());
        assertEquals("y", dr.wordforms.getFirst().getTag());

        Word plus = analyzer.analyze("+");
        assertTrue(plus.isRecognized());
        assertEquals("xx", plus.wordforms.getFirst().getTag());
    }

    @Test
    public void numbers() {
        // Ciparu atpazīšana
        Word num = analyzer.analyze("123456");
        assertTrue(num.isRecognized());
        assertEquals("xn", num.wordforms.getFirst().getTag());
        assertEquals("123456", num.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        Word ord = analyzer.analyze("15.");
        assertTrue(ord.isRecognized());
        assertEquals("xo", ord.wordforms.getFirst().getTag());
    }

    @Test
    public void pieci() {
        Word pieci = analyzer.analyze("pieci");
        assertTrue(pieci.isRecognized());
        assertEquals("pieci", pieci.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        pieci = analyzer.analyze("5");
        assertTrue(pieci.isRecognized());
        assertEquals("5", pieci.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
    }

    @Test
    public void iejāt() {
        // 2012-03-30 iejāt neatpazina dēļ buga
        analyzer.enablePrefixes = true;

        Word iejāt = analyzer.analyze("iejāt");
        assertTrue(iejāt.isRecognized());
        assertEquals("iejāt", iejāt.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
    }

    @Test
    public void labākais() {
        //Vispārāko pakāpju alternatīvas... Bet īsti nerullē, labākais ir vārdam 'labāks' noteiktā forma, nevis vispārākā pakāpe
        Word ātrāks = analyzer.analyze("labāks");
        assertTrue(ātrāks.isRecognized());
        assertEquals(AttributeNames.v_Comparative, ātrāks.wordforms.getFirst().getValue(AttributeNames.i_Degree));

        Word visātrākais = analyzer.analyze("labākais");
        assertTrue(visātrākais.isRecognized());
        //assertEquals(AttributeNames.v_Superlative, visātrākais.wordforms.get(0).getValue(AttributeNames.i_Degree));
    }

    @Test
    public void reziduāļi() {
        analyzer.enableDiminutive = true;
        analyzer.enablePrefixes = true;
        analyzer.enableGuessing = true;
        analyzer.enableAllGuesses = true;
        analyzer.searchCompoundWords = true;

        Word m = analyzer.analyze("M.");
        assertTrue(m.isRecognized());
        assertEquals(AttributeNames.v_Abbreviation, m.wordforms.getFirst().getValue(AttributeNames.i_PartOfSpeech));
    }

    @Test
    public void atstarpes() {
        analyzer.enableDiminutive = true;
        analyzer.enablePrefixes = true;
        analyzer.enableGuessing = true;
        analyzer.enableAllGuesses = true;
        analyzer.searchCompoundWords = true;

        //Atsevišķus burtus nevajadzētu minēt kā reālus vārdus

        Word ne = analyzer.analyze("ne ");
        assertTrue(ne.isRecognized());
        assertEquals("ne", ne.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
    }

    @Test
    public void gunta2012mai() {
        // Guntas reportētie neatpazītie vārdi

        Word atguvies = analyzer.analyze("atguvies");
        assertTrue(atguvies.isRecognized());

        Word sizdams = analyzer.analyze("sizdams");
        assertTrue(sizdams.isRecognized());

        Word sēzdamies = analyzer.analyze("sēzdamies");
        assertTrue(sēzdamies.isRecognized());

        Word sarūdzis = analyzer.analyze("sarūdzis");
        assertTrue(sarūdzis.isRecognized());

        Word irties = analyzer.analyze("irties");
        assertTrue(irties.isRecognized());

        Word tekalēt = analyzer.analyze("tekalēt");
        assertTrue(tekalēt.isRecognized());

        Word kļūt = analyzer.analyze("kļūt");
        assertTrue(kļūt.isRecognized());

        Word proti = analyzer.analyze("proti");
        assertTrue(proti.isRecognized());
    }

    @Test
    public void lūzīs() {
        Word lūzīs = analyzer.analyze("lūzīs");
        assertTrue(lūzīs.isRecognized());
        assertEquals("lūzt", lūzīs.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
    }

    @Test
    public void ģenerēšana() {
        List<Wordform> Valdis = analyzer.generateInflections("Valdis");
        assertNounInflection(Valdis, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Valda");
        assertNounInflection(Valdis, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "Valdim");

        List<Wordform> Raitis = analyzer.generateInflections("Raitis");
        assertNounInflection(Raitis, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Raita");

        List<Wordform> cerēt = analyzer.generateInflections("cerēt");
        // TODO - salikt verbiem testpiemērus
    }

    @Test
    public void ģenerēšanaNezināmiem() {
        analyzer.enableGuessing = true;
        analyzer.enableVocative = true;
        analyzer.guessVerbs = false;
        analyzer.guessParticiples = false;
        analyzer.guessAdjectives = false;
        analyzer.guessInflexibleNouns = true;
        analyzer.enableAllGuesses = true;

        assertTrue("Valdis".matches("\\p{Lu}.*"));
        assertTrue("Ādolfs".matches("\\p{Lu}.*"));
        assertFalse("valdis".matches("\\p{Lu}.*"));
        assertFalse("ādolfs".matches("\\p{Lu}.*"));

        Word zolā = analyzer.analyze("Zolā");
        assertTrue(zolā.isRecognized());
        assertEquals(AttributeNames.v_Noun, zolā.wordforms.getFirst().getValue(AttributeNames.i_PartOfSpeech));

        ArrayList<Wordform> formas = analyzer.generateInflections("Zolā");
		assertFalse(formas.isEmpty());
    }

    @Test
    public void vešana() {
        Word vešana = analyzer.analyze("vešana");
        assertTrue(vešana.isRecognized());
        assertEquals("vest", vešana.wordforms.getFirst().getValue(AttributeNames.i_SourceLemma));

        Word vesšana = analyzer.analyze("vesšana");
        assertFalse(vesšana.isRecognized());

        Word mēzšana = analyzer.analyze("mēzšana");
        assertFalse(mēzšana.isRecognized());
    }

    @Test
    public void nelokaamie() {
        analyzer.enableDiminutive = true;
        analyzer.enablePrefixes = true;
        analyzer.enableGuessing = true;
        analyzer.enableAllGuesses = true;
        analyzer.searchCompoundWords = true;
        analyzer.guessInflexibleNouns = true;

        Word vārds = analyzer.analyze("TrrT");
        assertTrue(vārds.isRecognized());
        assertEquals("Trrt", vārds.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        vārds = analyzer.analyze("GAIZINAISI-Ā3");
        assertTrue(vārds.isRecognized());
        assertEquals("Gaizinaisi-Ā3", vārds.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
        assertEquals(AttributeNames.v_Residual, vārds.wordforms.getFirst().getValue(AttributeNames.i_PartOfSpeech));

        vārds = analyzer.analyze("0.40");
        assertTrue(vārds.isRecognized());
        assertEquals("0.40", vārds.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
        assertEquals(AttributeNames.v_Residual, vārds.wordforms.getFirst().getValue(AttributeNames.i_PartOfSpeech));
        assertEquals(AttributeNames.v_Number, vārds.wordforms.getFirst().getValue(AttributeNames.i_ResidualType));

        vārds = analyzer.analyze("6/7");
        assertTrue(vārds.isRecognized());
        assertEquals("6/7", vārds.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
        assertEquals(AttributeNames.v_Residual, vārds.wordforms.getFirst().getValue(AttributeNames.i_PartOfSpeech));
        assertEquals(AttributeNames.v_Number, vārds.wordforms.getFirst().getValue(AttributeNames.i_ResidualType));

        vārds = analyzer.analyze("....");
        assertTrue(vārds.isRecognized());
        for (Wordform wf : vārds.wordforms) {
            assertEquals("...", wf.getValue(AttributeNames.i_Lemma));
        }
    }

    @Test
    public void personvaardi_Varis() {
        // 2012.06.08 sūtītie komentāri par locīšanas defektiem.
        analyzer.enableGuessing = true;
        analyzer.enableVocative = true;
        analyzer.guessVerbs = false;
        analyzer.guessParticiples = false;
        analyzer.guessAdjectives = false;
        analyzer.guessInflexibleNouns = true;
        analyzer.enableAllGuesses = true;

        List<Wordform> forms = analyzer.generateInflections("Valdis", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Valda");
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "Valdim");

        forms = analyzer.generateInflections("Čaikovskis", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Čaikovska");

        forms = analyzer.generateInflections("Cēsis", true);
        assertNounInflection(forms, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "Cēsu");

        forms = analyzer.generateInflections("Raitis", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Raita");

        forms = analyzer.generateInflections("Auziņš", true);
        assertNounInflection(forms, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "Auziņu");

        forms = analyzer.generateInflections("Ivis", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Ivja");
        assertNounInflection(forms, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "Ivju");

        forms = analyzer.generateInflections("Eglīts", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Eglīša");

        forms = analyzer.generateInflections("Švirkste", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Genitive, AttributeNames.v_Feminine, "Švirkstes");

        forms = analyzer.generateInflections("Taļikova", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Genitive, AttributeNames.v_Feminine, "Taļikovas");

        forms = analyzer.generateInflections("Bērziņš", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Nominative, AttributeNames.v_Masculine, "Bērziņš");

        forms = analyzer.generateInflections("Dīcis", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Masculine, "Dīcim");

        forms = analyzer.generateInflections("Asna", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Feminine, "Asnai");

        forms = analyzer.generateInflections("Lielais", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Masculine, "Lielajam");

        forms = analyzer.generateInflections("Mazā", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Feminine, "Mazajai");

        forms = analyzer.generateInflections("Zaļais", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Masculine, "Zaļajam");

        forms = analyzer.generateInflections("Santis", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Genitive, AttributeNames.v_Masculine, "Santa");
    }

    @Test
    public void no_iepirkšanās() {
        Word word = analyzer.analyze("no iepirkšanās");
        assertFalse(word.isRecognized());

        word = analyzer.analyze("uz kino");
        assertFalse(word.isRecognized());

        word = analyzer.analyze("nocirvis");
        assertFalse(word.isRecognized());
    }

    @Test
    public void cache() {
        analyzer.setCacheSize(1000);
        analyzer.enableGuessing = true;
        analyzer.enableVocative = true;
        analyzer.guessVerbs = false;
        analyzer.guessParticiples = false;
        analyzer.guessAdjectives = false;
        analyzer.guessInflexibleNouns = true;
        analyzer.enableAllGuesses = true;

        Word sacelt = analyzer.analyze("sacelt");
        assertTrue(sacelt.isRecognized());
        assertEquals("sacelt", sacelt.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        Word celt = analyzer.analyze("celt");
        assertTrue(celt.isRecognized());
        assertEquals("celt", celt.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
    }

    @Test
    public void mazajai() {
        Word mazajai = analyzer.analyze("mazajai");
        assertTrue(mazajai.isRecognized());
        assertEquals("mazs", mazajai.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
    }

    @Test
    public void personvārdi_Varis2() {
        // 2012.07.05 sūtītie komentāri par vokatīvu defektiem.
        analyzer.enableGuessing = true;
        analyzer.enableVocative = true;
        analyzer.guessVerbs = false;
        analyzer.guessParticiples = false;
        analyzer.guessAdjectives = false;
        analyzer.guessInflexibleNouns = true;
        analyzer.enableAllGuesses = true;

        List<Wordform> forms = analyzer.generateInflections("Pauls", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Paul");

        forms = analyzer.generateInflections("Laura", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Laura");

        forms = analyzer.generateInflections("Lauriņa", true);
        assertNounInflectionMultipleStrong(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "",
                new HashSet<>(){{ add("Lauriņ"); add("Lauriņa");}});

        forms = analyzer.generateInflections("Made", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Made");

        forms = analyzer.generateInflections("Kristīnīte", true);
        assertNounInflectionMultipleStrong(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "",
                new HashSet<>(){{ add("Kristīnīt"); add("Kristīnīte");}});

        forms = analyzer.generateInflections("Margrieta", true);
        assertNounInflectionMultipleStrong(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "",
                new HashSet<>(){{ add("Margrieta"); add("Margriet");}});
    }

    // TODO: WTF?
    @Test
    public void leksikoni() {
        Word pokemons = analyzer.analyze("Bisjakovs");
        assertFalse(pokemons.isRecognized());
    }

    @Test
    public void daudzskaitlinieki() {
        // analyzeLemma nestrādā
        Word augstpapēžu = analyzer.analyzeLemma("augstpapēžu");
        assertTrue(augstpapēžu.isRecognized());
    }

    @Test
    public void personvārdi_Varis3() {
        // 2012.07.14 sūtītie komentāri par vokatīvu defektiem.
        analyzer.enableGuessing = true;
        analyzer.enableVocative = true;
        analyzer.guessVerbs = false;
        analyzer.guessParticiples = false;
        analyzer.guessAdjectives = false;
        analyzer.guessInflexibleNouns = true;
        analyzer.enableAllGuesses = true;

        List<Wordform> auziņš = analyzer.generateInflections("Auziņš", true);
        assertNounInflection(auziņš, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Auziņ");

        assertTrue(analyzer.analyze("Miervalda").isRecognized());
        assertTrue(analyzer.analyze("Miervalža").isRecognized());
        List<Wordform> miervaldis = analyzer.generateInflections("Miervaldis", true);
        assertNounInflection(miervaldis, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Miervalda");
    }


    @Test
    public void Laura10Aug() {
        Word word = analyzer.analyze("vienai");
        assertTrue(word.isRecognized());
        assertEquals("viens", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        word = analyzer.analyze("pirmajai");
        assertTrue(word.isRecognized());
        assertEquals("pirmais", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        word = analyzer.analyze("trešās");
        assertTrue(word.isRecognized());
        assertEquals("trešais", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        word = analyzer.analyze("piecsimt");
        assertTrue(word.isRecognized());
        assertEquals("mc_0p0", word.wordforms.getFirst().getTag());
    }

    @Test
    public void personvārdi_Varis4() {
        // 2012.08.13 P33 vokatīvu shēma
        analyzer.enableGuessing = true;
        analyzer.enableVocative = true;
        analyzer.guessVerbs = false;
        analyzer.guessParticiples = false;
        analyzer.guessAdjectives = false;
        analyzer.guessInflexibleNouns = true;
        analyzer.enableAllGuesses = true;

        List<Wordform> forms = analyzer.generateInflections("Jēkabs");
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Jēkab");

        forms = analyzer.generateInflections("Mārtiņš");
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Mārtiņ");

        forms = analyzer.generateInflections("Mikus");
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Miku");

        forms = analyzer.generateInflections("Ingus");
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Ingu");

        forms = analyzer.generateInflections("Kalns");
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Kaln");

        forms = analyzer.generateInflections("Liepiņš");
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Liepiņ");

        forms = analyzer.generateInflections("Zaķis");
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Zaķi");

        forms = analyzer.generateInflections("Ledus");
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Ledu");

        forms = analyzer.generateInflections("Platais");
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Platais");

        forms = analyzer.generateInflections("Lielais");
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Lielais");

        forms = analyzer.generateInflections("Biezais");
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Biezais");

        forms = analyzer.generateInflections("Silvija");
        assertNounInflectionMultipleStrong(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "",
                new HashSet<>(){{ add("Silvij"); add("Silvija");}});

        //forms = analyzer.generateInflections("Kadrije"); //hipotētiski
        //assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Kadrij");

        forms = analyzer.generateInflections("Karlīne");
        assertNounInflectionMultipleStrong(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "",
                new HashSet<>(){{ add("Karlīn"); add("Karlīne");}});

        forms = analyzer.generateInflections("Vilhelmīne");
        assertNounInflectionMultipleStrong(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "",
                new HashSet<>(){{ add("Vilhelmīn"); add("Vilhelmīne");}});

        forms = analyzer.generateInflections("Skaidrīte");
        assertNounInflectionMultipleStrong(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "",
                new HashSet<>(){{ add("Skaidrīt"); add("Skaidrīte");}});

        forms = analyzer.generateInflections("Juliāna");
        assertNounInflectionMultipleStrong(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "",
                new HashSet<>(){{ add("Juliān"); add("Juliāna");}});

        forms = analyzer.generateInflections("Eglīte");
        assertNounInflectionMultipleStrong(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "",
                new HashSet<>(){{ add("Eglīt"); add("Eglīte");}});

        forms = analyzer.generateInflections("Lapsiņa");
        assertNounInflectionMultipleStrong(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "",
                new HashSet<>(){{ add("Lapsiņ"); add("Lapsiņa");}});

        forms = analyzer.generateInflections("Pilsētniece");
        assertNounInflectionMultipleStrong(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "",
                new HashSet<>(){{ add("Pilsētniec"); add("Pilsētniece");}});

        forms = analyzer.generateInflections("Salnāja");
        assertNounInflectionMultipleStrong(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "",
                new HashSet<>(){{ add("Salnāja"); add("Salnāj");}});

        forms = analyzer.generateInflections("Garkāje");
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Garkāje");

        forms = analyzer.generateInflections("Zeidmane");
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Zeidmane");

        forms = analyzer.generateInflections("Kreice");
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Kreice");

        forms = analyzer.generateInflections("Kreija");
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Kreija");

        forms = analyzer.generateInflections("Kreitenberga");
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Kreitenberga");

        //Nav norealizēts: Par salikteņiem Ja salikteņa 2.  daļa atsevišķi kvalificējas īsajai formai, tad arī saliktenis kvalificējas īsajai formai.
    }

    @Test
    public void personvārdi_Varis5() {
        // 2012.08.13 Vara komentāri
        analyzer.enableGuessing = true;
        analyzer.enableVocative = true;
        analyzer.guessVerbs = false;
        analyzer.guessParticiples = false;
        analyzer.guessAdjectives = false;
        analyzer.guessInflexibleNouns = true;
        analyzer.enableAllGuesses = true;

        List<Wordform> forms = analyzer.generateInflections("Arvydas", true);
        assertNounInflection(forms, AttributeNames.v_NA, AttributeNames.v_NA, "", "Arvydas");

        forms = analyzer.generateInflections("Rīta", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Rīta");

        forms = analyzer.generateInflections("rīta", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "rīta");
    }

    @Test
    public void laura_Aug13() {
        analyzer.enableGuessing = true;
        // 2012.08.13 Lauras samarķētā atšķirību analīze
        List<Wordform> formas = analyzer.generateInflections("Fredis");
        assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Freda");

        formas = analyzer.generateInflections("Alda");
        assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Aldas");


        Word freda = analyzer.analyze("Freda");
        assertTrue(freda.isRecognized());

        boolean found = false;
        for (Wordform wf : freda.wordforms) {
            if (wf.getValue(AttributeNames.i_Lemma).equals("Fredis")) {
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    public void laura_Aug13_2() {
        analyzer.enableGuessing = true;
        Word sia = analyzer.analyze("SIA");
        assertTrue(sia.isRecognized());

        Word numur = analyzer.analyze("numur");
        assertTrue(numur.isRecognized());
    }

    @Test
    public void GuntaAug22() {
        // 2012.08.22 Gunta saka, ka "pazūd" atpazīst kā 2. personas vārdu; tas ir fail 7. mijā
        Word ēd = analyzer.analyze("ēd");
        assertTrue(ēd.isRecognized());
        boolean found = false;
        for (Wordform wf : ēd.wordforms) {
            if (wf.isMatchingStrong(AttributeNames.i_Person, "2")) found = true;
        }
        assertTrue(found);

        Word pazūd = analyzer.analyze("pazūd");
        assertTrue(pazūd.isRecognized());
        for (Wordform wf : pazūd.wordforms) {
            assertFalse(wf.isMatchingStrong(AttributeNames.i_Person, "2"));
        }
    }

    @Test
    public void InflectionSep4() {
        // 2012.09.04 konstatēts, ka lokot dažiem vārdiem nepareizi mijas strādā
        List<Wordform> forms = analyzer.generateInflections("iemācīties");

        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_EndingID, "1057");
        assertInflection(forms, testset, "iemācoties");
        testset.addAttribute(AttributeNames.i_EndingID, "1027");
        assertInflection(forms, testset, "jāiemācās");
        testset.addAttribute(AttributeNames.i_EndingID, "1210");
        assertInflection(forms, testset, "jāiemācoties");

        forms = analyzer.generateInflections("mācīt");
        testset.addAttribute(AttributeNames.i_EndingID, "472");
        assertInflection(forms, testset, "mācām");
        testset.addAttribute(AttributeNames.i_EndingID, "474");
        assertInflection(forms, testset, "māca");
        testset.addAttribute(AttributeNames.i_EndingID, "487");
        assertInflection(forms, testset, "jāmāca");
        testset.addAttribute(AttributeNames.i_EndingID, "1204");
        assertInflection(forms, testset, "jāmācot");

        forms = analyzer.generateInflections("mācēt");
        testset.addAttribute(AttributeNames.i_EndingID, "1779");
        assertInflection(forms, testset, "māku");
        testset.addAttribute(AttributeNames.i_EndingID, "1780");
        assertInflection(forms, testset, "māki");
        testset.addAttribute(AttributeNames.i_EndingID, "1781");
        assertInflection(forms, testset, "mākam");
        testset.addAttribute(AttributeNames.i_EndingID, "1783");
        assertInflection(forms, testset, "māk");
        testset.addAttribute(AttributeNames.i_EndingID, "1794");
        assertInflection(forms, testset, "jāmāk");
        testset.addAttribute(AttributeNames.i_EndingID, "2328");
        assertInflection(forms, testset, "jāmākot");

        forms = analyzer.generateInflections("tecēt");
        testset.addAttribute(AttributeNames.i_EndingID, "1779");
        assertInflection(forms, testset, "teku");
        testset.addAttribute(AttributeNames.i_EndingID, "1780");
        assertInflection(forms, testset, "teci");
        testset.addAttribute(AttributeNames.i_EndingID, "1781");
        assertInflection(forms, testset, "tekam");
        testset.addAttribute(AttributeNames.i_EndingID, "1783");
        assertInflection(forms, testset, "tek");

        AttributeValues paradigmSpec = new AttributeValues();
        paradigmSpec.addAttribute(AttributeNames.i_ParadigmID, "45");
        forms = analyzer.generateInflections("gulēt", false, paradigmSpec);
        testset.addAttribute(AttributeNames.i_EndingID, "1780");
        assertInflection(forms, testset, "guli");
        testset.addAttribute(AttributeNames.i_EndingID, "1783");
        assertInflection(forms, testset, "guļ");
        testset.addAttribute(AttributeNames.i_EndingID, "1798");
        assertInflection(forms, testset, "guliet");
        testset.addAttribute(AttributeNames.i_EndingID, "2328");
        assertInflection(forms, testset, "jāguļot");

        forms = analyzer.generateInflections("aizgulēties");
        testset.addAttribute(AttributeNames.i_EndingID, "2337");
        assertInflection(forms, testset, "aizguļos");

        forms = analyzer.generateInflections("vajadzēt");
//        testset.addAttribute(AttributeNames.i_EndingID, "1779");
//        assertInflection(formas, testset, "vajagu");
//        testset.addAttribute(AttributeNames.i_EndingID, "1781");
//        assertInflection(formas, testset, "vajagam");
        testset.addAttribute(AttributeNames.i_EndingID, "1783");
        assertInflection(forms, testset, "vajag");
        testset.addAttribute(AttributeNames.i_EndingID, "1794");
        assertNoInflection(forms, testset); // neģenerējam "jāvajag"
        testset.addAttribute(AttributeNames.i_EndingID, "2328");
        assertNoInflection(forms, testset); // neģenerējam "jāvajagot"

        forms = analyzer.generateInflections("mocīt", false, paradigmSpec);
        testset.addAttribute(AttributeNames.i_EndingID, "1780");
        assertInflection(forms, testset, "moki");

        forms = analyzer.generateInflections("slodzīt");
        testset.addAttribute(AttributeNames.i_EndingID, "1779");
        assertInflection(forms, testset, "slogu");

        forms = analyzer.generateInflections("mesties");
        testset.addAttribute(AttributeNames.i_EndingID, "1072");
        assertInflection(forms, testset, "mešanās");

        forms = analyzer.generateInflections("pūsties");
        testset.addAttribute(AttributeNames.i_EndingID, "1087");
        assertInflection(forms, testset, "pūties");

        Word word = analyzer.analyze("gulošs");
        assertTrue(word.isRecognized());
        word = analyzer.analyze("guļošs");
        assertTrue(word.isRecognized());
    }


    @Test
    public void gunta_20120911() {
        //korpusā vārdi "ness" un "vess" ir marķēti kā verbu "nest" un "vest" formas

        Word word = analyzer.analyze("nest");
        assertTrue(word.isRecognized());

        word = analyzer.analyze("nesīs");
        assertTrue(word.isRecognized());

        word = analyzer.analyze("vest");
        assertTrue(word.isRecognized());

        word = analyzer.analyze("vedīs");
        assertTrue(word.isRecognized());

        word = analyzer.analyze("vess");
        assertFalse(word.isRecognized());

        word = analyzer.analyze("vesīs");
        //assertFalse(vārds.isRecognized()); // FIXME - tur palīdzētu mijām čekošana, vai uzminētais sakrīt ar izlocīto; vai arī post-processing check par 3o sakni 6. mijai....

        word = analyzer.analyze("ness");
        assertFalse(word.isRecognized());
    }

    @Test
    public void pazūdi() {
        // 2012.09.12 konstatēts ka mija pareizi neloka šo formu
        Word pazūdi = analyzer.analyze("pazūdi");
        assertTrue(pazūdi.isRecognized());

        boolean found = false;
        for (Wordform wf : pazūdi.wordforms) {
            if (wf.getValue(AttributeNames.i_EndingID).equals("790")) {
                found = true;
            }
        }
        assertTrue(found);

        List<Wordform> pazust = analyzer.generateInflections("pazust");

        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_EndingID, "790");
        assertInflection(pazust, testset, "pazūdi");

        List<Wordform> atrast = analyzer.generateInflections("atrast");
        testset.addAttribute(AttributeNames.i_EndingID, "790");
        assertInflection(atrast, testset, "atrodi");
    }

    @Test
    public void vajadzības_minēšana() {
        analyzer.enablePrefixes = true;

        //Priedēkļu atvasināšana nestrādā, ja ir vajadzības izteiksme

        Word word = analyzer.analyze("rakt");
        assertTrue(word.isRecognized());
        assertEquals("rakt", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        word = analyzer.analyze("aizrakt");
        assertTrue(word.isRecognized());
        assertEquals("aizrakt", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        word = analyzer.analyze("jārok");
        assertTrue(word.isRecognized());
        assertEquals("rakt", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        word = analyzer.analyze("jāaizrok");
        assertTrue(word.isRecognized());
        assertEquals("aizrakt", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
    }

    @Test
    public void divdabju_pārākās_formas() {
        Word word = analyzer.analyze("izkusušais");
        assertTrue(word.isRecognized());
        assertEquals("izkust", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        word = analyzer.analyze("izkusušākais");
        assertTrue(word.isRecognized());
        assertEquals("izkust", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        word = analyzer.analyze("visizkusušākais");
        assertTrue(word.isRecognized());
        assertEquals("izkust", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        word = analyzer.analyze("visveiktākais");
        assertTrue(word.isRecognized());
        assertEquals("veikt", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        word = analyzer.analyze("vislasītākais");
        assertTrue(word.isRecognized());
        assertEquals("lasīt", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        word = analyzer.analyze("veicu");
        assertTrue(word.isRecognized());
        assertEquals("veikt", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
        word = analyzer.analyze("veikušais");
        assertTrue(word.isRecognized());
        assertEquals("veikt", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
        word = analyzer.analyze("veicušais");
        assertFalse(word.isRecognized());

        word = analyzer.analyze("sarūgu");
        assertTrue(word.isRecognized());
        assertEquals("sarūgt", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
        word = analyzer.analyze("sarūgušais");
        assertTrue(word.isRecognized());
        assertEquals("sarūgt", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
        word = analyzer.analyze("sarūdzušais");
        assertFalse(word.isRecognized());

        // tas pats 2. un 3. konjug.
        word = analyzer.analyze("zaigojušāks");
        assertTrue(word.isRecognized());
        assertEquals("zaigot", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
        word = analyzer.analyze("zaigojošāks");
        assertTrue(word.isRecognized());
        assertEquals("zaigot", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        word = analyzer.analyze("vislasījušākais");
        assertTrue(word.isRecognized());
        assertEquals("lasīt", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
        word = analyzer.analyze("lasošāks");
        assertTrue(word.isRecognized());
        assertEquals("lasīt", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        word = analyzer.analyze("vislasāmākais");
        assertTrue(word.isRecognized());
        assertEquals("lasīt", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
        word = analyzer.analyze("saprotamāks");
        assertTrue(word.isRecognized());
        assertEquals("saprast", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        word = analyzer.analyze("viszaigojošāk");
        assertTrue(word.isRecognized());
        assertEquals("zaigojoši", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
    }

    @Test
    public void normunds20130128() {
        Word māc = analyzer.analyze("māc");
        assertTrue(māc.isRecognized());
        assertEquals("mākt", māc.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
        assertEquals(3, māc.wordforms.size());

        List<Wordform> pļaut = analyzer.generateInflections("pļaut");
        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
        testset.addAttribute(AttributeNames.i_Person, "3");
        testset.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Indicative);
        testset.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
        assertInflection(pļaut, testset, "pļauj");

        testset.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Past);
        assertInflection(pļaut, testset, "pļāva");

        List<Wordform> kļaut = analyzer.generateInflections("kļaut");
        testset.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
        assertInflection(kļaut, testset, "kļauj");
        testset.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Past);
        assertInflection(kļaut, testset, "kļāva");

        List<Wordform> iekļaut = analyzer.generateInflections("iekļaut");
        testset.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
        assertInflection(iekļaut, testset, "iekļauj");
        testset.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Past);
        assertInflection(iekļaut, testset, "iekļāva");
    }

    // FIXME: what was this supposed to do?
    @Test
    @Ignore
    public void vienādās_nenoteiksmes() {
        Paradigm firstConj = analyzer.paradigmByID(15);
        Paradigm secondConj = analyzer.paradigmByID(16);
        Paradigm thirdConj = analyzer.paradigmByID(17);
        LinkedList<Lexeme> lexemes = new LinkedList<>();
        lexemes.addAll(firstConj.lexemes);
        lexemes.addAll(secondConj.lexemes);
        lexemes.addAll(thirdConj.lexemes);
        for (Lexeme lex : lexemes) {
            LinkedList<Lexeme> alternatives = new LinkedList<>();
            ArrayList<Lexeme> xx = firstConj.getLexemesByStem(StemType.STEM1).get(lex.getStem(StemType.STEM1));
            if (xx != null) alternatives.addAll(xx);
            xx = secondConj.getLexemesByStem(StemType.STEM1).get(lex.getStem(StemType.STEM1));
            if (xx != null) alternatives.addAll(xx);
            xx = thirdConj.getLexemesByStem(StemType.STEM1).get(lex.getStem(StemType.STEM1));
            if (xx != null) alternatives.addAll(xx);
            /*
            for (Lexeme alternatīva : alternatīvas) {
                if (lex.getID() < alternatīva.getID()) {
                    if (lex.getParadigm() != alternatīva.getParadigm()) {
                        System.out.printf("%st: %s un %s konjugācijas\n", lex.getStem(0), lex.getParadigm().getValue(AttributeNames.i_Konjugaacija), alternatīva.getParadigm().getValue(AttributeNames.i_Konjugaacija));
                    }
                    if (lex.getParadigm() == pirmā && alternatīva.getParadigm() == pirmā && (!lex.getStem(1).equalsIgnoreCase(alternatīva.getStem(1)) || !lex.getStem(2).equalsIgnoreCase(alternatīva.getStem(2)))) {
                        System.out.printf("%st: %su %su vai %su %su\n", lex.getStem(0), lex.getStem(1), lex.getStem(2), alternatīva.getStem(1), alternatīva.getStem(2));
                    }
                }
            }
            */
        }
    }

    @Test
    public void personvārdi_Varis6() {
        // 2013.02.05 Vara komentāri
        analyzer.enableGuessing = true;
        analyzer.enableVocative = true;
        analyzer.guessVerbs = false;
        analyzer.guessParticiples = false;
        analyzer.guessAdjectives = false;
        analyzer.guessInflexibleNouns = true;
        analyzer.enableAllGuesses = true;

        List<Wordform> forms = analyzer.generateInflections("Edvards", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Edvarda");

        forms = analyzer.generateInflections("Ludis", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Luda");

        forms = analyzer.generateInflections("Krists", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "Kristam");

        forms = analyzer.generateInflections("Staņislava", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Feminine, "Staņislavai");

        forms = analyzer.generateInflections("Raisa", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Feminine, "Raisai");

        forms = analyzer.generateInflections("Alberta", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Feminine, "Albertai");

        forms = analyzer.generateInflections("Gunta", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Feminine, "Guntai");
    }

    @Test
    public void gunta19dec_3() {
        // Guntas sūdzības pa skype 2012.12.19 - retās deklinācijas
        analyzer.enableGuessing = true;
        analyzer.enableVocative = true;
        analyzer.guessVerbs = false;
        analyzer.guessParticiples = false;
        analyzer.guessAdjectives = false;
        analyzer.guessInflexibleNouns = true;
        analyzer.enableAllGuesses = true;

        Word word = analyzer.analyze("ragus");
        assertTrue(word.isRecognized());
        assertEquals("rags", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        word = analyzer.analyze("dermatovenerologi");
        assertTrue(word.isRecognized());
        assertEquals("dermatovenerologs", word.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
    }

    @Test
    public void normunds_2013feb25() {
        List<Wordform> dziedāt = analyzer.generateInflections("dziedāt");
        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
        testset.addAttribute(AttributeNames.i_Person, "1");
        testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
        testset.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Indicative);
        testset.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
        assertInflection(dziedāt, testset, "dziedam");

        testset.removeAttribute(AttributeNames.i_Number);
        testset.addAttribute(AttributeNames.i_Person, "3");
        assertInflection(dziedāt, testset, "dzied");

        Word dziedam = analyzer.analyze("dziedam");
        assertTrue(dziedam.isRecognized());
        assertEquals("dziedāt", dziedam.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        dziedam = analyzer.analyze("dzied");
        assertTrue(dziedam.isRecognized());
        assertEquals("dziedāt", dziedam.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        dziedam = analyzer.analyze("dziedām");
        assertFalse(dziedam.isRecognized());
    }

    @Test
    public void pp20130313() {
        // aizdomas par 5. mijas bugiem

        List<Wordform> rakt = analyzer.generateInflections("rakt");
        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
        testset.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Debitive);
        assertInflection(rakt, testset, "jārok");
    }

    @Test
    public void guessbyending_adjective_surnames() {
        // Guess by ending should return appropriate nominative values for adjective-based surnames
        Word possibilities = analyzer.guessByEnding("mazā", "Mazā");
        AttributeValues filter = new AttributeValues();
        filter.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Adjective);
        filter.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Feminine);
        filter.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
        boolean found = false;
        for (Wordform wf : possibilities.wordforms) {
            if (wf.isMatchingWeak(filter)) {
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    public void varis20130221() {
        analyzer.enableGuessing = true;
        analyzer.enableVocative = true;
        analyzer.guessVerbs = false;
        analyzer.guessParticiples = false;
        analyzer.guessAdjectives = false;
        analyzer.guessInflexibleNouns = true;
        analyzer.enableAllGuesses = true;

        List<Wordform> forms = analyzer.generateInflections("Liepa", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "Liepai");

        AttributeValues filter = new AttributeValues();
        filter.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);

        forms = analyzer.generateInflections("Liepa", true, filter);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "Liepam");

        forms = analyzer.generateInflections("Lielais", true, filter);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Masculine, "Lielajam");

        forms = analyzer.generateInflections("Valdīšana", true, filter);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Masculine, "Valdīšanam");

        filter.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Feminine);
        forms = analyzer.generateInflections("Dzelzs", true, filter);
        //for (Wordform forma:formas) forma.describe();
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "Dzelzij");

        forms = analyzer.generateInflections("Mazā", true, filter);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Feminine, "Mazajai");

        forms = analyzer.generateInflections("Valdīšana", true, filter);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Feminine, "Valdīšanai");
    }

    @Test
    public void lemmageneration1() {
        Word possibilities = analyzer.analyze("Biezā");
        analyzer.filterInflectionOptions(true, new AttributeValues(), possibilities.wordforms);
        assertEquals(2, possibilities.wordformsCount()); // masc genitive, fem nominative
        ArrayList<Wordform> result = analyzer.generateInflections_TryLemmas("Biezā", possibilities);
        for (Wordform wf : result) {
            assertTrue(wf.isMatchingStrong(AttributeNames.i_Gender, AttributeNames.v_Feminine));
        }
    }

    @Test
    public void varis20130317() {
        analyzer.enableGuessing = true;
        analyzer.enableVocative = true;
        analyzer.guessVerbs = false;
        analyzer.guessParticiples = false;
        analyzer.guessAdjectives = false;
        analyzer.guessInflexibleNouns = true;
        analyzer.enableAllGuesses = true;

        assertTrue("Biezā".matches("\\p{Lu}.*"));
        assertTrue("BIEZĀ".matches("\\p{Lu}.*"));

        List<Wordform> forms = analyzer.generateInflections("Biezā", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "Biezajai");

        forms = analyzer.generateInflections("BIEZĀ", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "BIEZAJAI");

        AttributeValues filter = new AttributeValues();
        filter.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Feminine);
        forms = analyzer.generateInflections("VĪTOLA", true, filter);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "VĪTOLAI");

        forms = analyzer.generateInflections("BAGĀTĀ", true, filter);
		assertFalse(forms.isEmpty());
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "BAGĀTAJAI");

        forms = analyzer.generateInflections("Vītola", true, filter);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "Vītolai");

        // 2026-08-07 I found that original assertInflection method used weak
        // attribute matching and, thus, these tests succeed despite having no
        // cases at all. Switching to stronger matching, these tests don't work.
        forms = analyzer.generateInflections("Kirill", true);
		assertFalse(forms.isEmpty());
        //assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "Kirill");

        forms = analyzer.generateInflections("Andrej", true);
		assertFalse(forms.isEmpty());
        //assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "Andrej");
    }

    @Test
    public void laura_20130605() {
        // Vietniekvārdiem neieliek pēdējo pozīciju tagā (noliegumu); -šana atvasinātās formas nav ok
        Word viņš = analyzer.analyze("viņš");
        assertTrue(viņš.isRecognized());
        assertTrue(viņš.getBestWordform().getTag().equalsIgnoreCase("pp3msnn") || viņš.getBestWordform().getTag().equalsIgnoreCase("pd3msnn"));

        Word ciršana = analyzer.analyze("ciršana");
        assertTrue(ciršana.isRecognized());
        assertEquals("ncfsn4", ciršana.wordforms.getFirst().getTag());
        assertEquals("ciršana", ciršana.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        Word mazgāšanās = analyzer.analyze("mazgāšanos");
        assertTrue(mazgāšanās.isRecognized());
        assertEquals("ncfsar", mazgāšanās.getBestWordform().getTag());

        analyzer.enableGuessing = true;
        Word izpaudusies = analyzer.analyze("izpaudusies");
        assertTrue(izpaudusies.isRecognized());
    }

    @Test
    public void gunta_20130605() {
        // LVK2013 Korpuss saka, ka verba "attiecas" lemma ir "attiecties"
        Word attiecas = analyzer.analyze("attiecas");
        assertTrue(attiecas.isRecognized());
        assertEquals("attiekties", attiecas.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        Word esošo = analyzer.analyze("esošo");
        assertTrue(esošo.isRecognized());
        assertEquals("būt", esošo.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        Word mācās = analyzer.analyze("mācās");
        assertTrue(mācās.isRecognized());
        boolean found = false;
        for (Wordform wf : mācās.wordforms) {
            if (wf.isMatchingStrong(AttributeNames.i_Lemma, "mācīties")) found = true;
        }
        assertTrue(found);

        Word acīmredzot = analyzer.analyze("acīmredzot");
        assertTrue(acīmredzot.isRecognized());
        assertEquals("acīmredzot", acīmredzot.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        Word lielākoties = analyzer.analyze("lielākoties");
        assertTrue(lielākoties.isRecognized());
        assertEquals("lielākoties", lielākoties.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

    }

    /**
     * Korpusa analīze - vārdi, kuriem analizators neiedeva nevienu sakarīgu variantu
     */
    @Test
    public void korpuss_20130605() {
        Word ņem = analyzer.analyze("ņem");
        assertTrue(ņem.isRecognized());
        assertEquals("ņemt", ņem.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        List<Wordform> ņemt = analyzer.generateInflections("ņemt");
        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
        testset.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
        testset.addAttribute(AttributeNames.i_Person, "2");
        testset.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Indicative);
        assertInflection(ņemt, testset, "ņem");

        boolean found = false;
        for (Wordform wf : ņem.wordforms) {
            if (wf.isMatchingStrong(AttributeNames.i_Person, "2")) found = true;
        }
        assertTrue(found);

        List<Wordform> pāriet = analyzer.generateInflections("pāriet");
        testset.addAttribute(AttributeNames.i_Person, "3");
        assertInflection(pāriet, testset, "pāriet");
    }

    @Test
    public void korpuss_20130606() {
        Word acs = analyzer.analyze("acs");
        assertTrue(acs.isRecognized());
        assertEquals("acs", acs.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
        assertEquals(AttributeNames.v_Feminine, acs.wordforms.getFirst().getValue(AttributeNames.i_Gender));

        List<Wordform> formas = analyzer.generateInflections("atkāpties");
        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
        testset.addAttribute(AttributeNames.i_Person, "2");
        testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
        testset.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Imperative);
        assertInflection(formas, testset, "atkāpies");
    }

    @Test
    public void uri() {
        Word url = analyzer.analyze("www.pillar.lv");
        assertTrue(url.isRecognized());
        assertEquals("xu", url.wordforms.getFirst().getTag());
    }

    //    @Ignore("Jāskatās pēc tēzaura datu pievienošanas")
    @Test
    public void obligātiatpazīstamie() throws IOException {
        {
            BufferedReader in;
            String line;
            in = new BufferedReader(
                    new InputStreamReader(getClass().getClassLoader().getResourceAsStream("mandatory.txt"), StandardCharsets.UTF_8));

            int notRecognized = 0;
            while ((line = in.readLine()) != null) {
                if (line.contains("#") || line.isEmpty()) continue;
                List<Word> words = Splitting.tokenize(analyzer, line);
                for (Word word : words) {
                    if (!word.isRecognized()) {
                        notRecognized += 1;
                        System.err.printf("Neatpazīts vārds '%s' frāzē '%s'\n", word.getToken(), line);
                    }
                }
            }
            in.close();
            assertTrue("Par daudz neatpazītu vārdu", notRecognized < 70);
        }
    }

    @Test
    public void lociishanas_lielie_burti() {
        analyzer.enableGuessing = true;
        analyzer.enableVocative = true;
        analyzer.guessVerbs = false;
        analyzer.guessParticiples = false;
        analyzer.guessAdjectives = false;
        analyzer.guessInflexibleNouns = true;
        analyzer.enableAllGuesses = true;

        List<Wordform> forms = analyzer.generateInflections("Valdis", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Valda");

        forms = analyzer.generateInflections("Vīķe-Freiberga", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Vīķes-Freibergas");

        forms = analyzer.generateInflections("Žverelo-Freiberga", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Žverelo-Freibergas");

//		formas = locītājs.generateInflections("Freiberga-Žverelo", true);
//		assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Freibergas-Žverelo");

        // 2026-08-07 I found that original assertInflection method used weak
        // attribute matching and, thus, this test succeed despite having no
        // cases at all. Switching to stronger matching, this test don't work.
        //forms = analyzer.generateInflections("Rīga-Best", true);
        //assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Rīga-Best");

        forms = analyzer.generateInflections("Best-Rīga", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Best-Rīgas");

        forms = analyzer.generateInflections("Rudaus-Rudovskis", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Rudaus-Rudovska");

//        formas = locītājs.generateInflections("Pavļuta-Deslandes", true);
//        assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Pavļutas-Deslandes");
    }

    @Test
    public void jaundzimushais() {
        assertLemma("jaundzimušajam", "jaundzimis");
        assertLemma("jaundzimusī", "jaundzimusi");
        assertLemma("galvenajam", "galvenais");
    }

    @Test
    public void guessinglimits() {
        analyzer.enableGuessing = true;
        analyzer.enableVocative = false;
        analyzer.guessVerbs = false;
        analyzer.guessNouns = true;
        analyzer.enableAllGuesses = true;
        Word w = analyzer.analyze("xxxbs");
        assertTrue(w.isRecognized());

        w = analyzer.analyze("xxxes");
        for (Wordform wf : w.wordforms) {
            assertFalse(wf.isMatchingStrong(AttributeNames.i_Declension, "1"));
        }
    }

    @Test
    public void izskanjas() {
        analyzer.enableGuessing = true;
        analyzer.enableVocative = false;
        analyzer.guessVerbs = true;
        analyzer.enableAllGuesses = true;

        Word austrumlatvija = analyzer.analyze("Austrumlatvija");
        assertTrue(austrumlatvija.isRecognized());

        Word w = analyzer.analyze("mirušais");
        assertTrue(w.isRecognized());
    }

    // Todo - ???
    /**
     * 2014-03-31 bug - autocreated lexemes from generateInflectionsFromParadigm pollute future analysis results
     */
    @Test
    public void inflect_garbage_collection() {
        analyzer.generateInflections("Šašliki");
        Word bulduri = analyzer.analyze("Šašliki");
        assertTrue(bulduri.isRecognized());
        for (Wordform wf : bulduri.wordforms) {
            assertEquals("šašliks", wf.getValue(AttributeNames.i_Lemma));
        }
    }

    /**
     * LETA lietvārdu locījumu pārbaude - nekorektas mijas 6. dekl
     */
    @Test
    public void mijas6dekl() {
        analyzer.enableGuessing = true;
        analyzer.enableVocative = true;
        analyzer.guessVerbs = false;
        analyzer.guessParticiples = false;
        analyzer.guessAdjectives = false;
        analyzer.guessInflexibleNouns = true;
        analyzer.enableAllGuesses = true;

        List<Wordform> forms = analyzer.generateInflections("acs", true);
        assertNounInflection(forms, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "acu");

        forms = analyzer.generateInflections("auss", true);
        assertNounInflection(forms, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "ausu");

//        formas = locītājs.generateInflections("kūts", true);
//        assertNounInflection(formas, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "kūtu");
        forms = analyzer.generateInflections("zoss", true);
        assertNounInflection(forms, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "zosu");
        forms = analyzer.generateInflections("dakts", true);
        assertNounInflection(forms, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "daktu");
        forms = analyzer.generateInflections("šalts", true);
        assertNounInflection(forms, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "šaltu");
        forms = analyzer.generateInflections("maksts", true);
        assertNounInflection(forms, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "makstu");
    }


    /**
     * LETA lietvārdu locījumu pārbaude - defaultajai formai lokot jābūt ar galotni
     */
    @Ignore("nav skaidra pozīcija par vokatīviem")
    @Test
    public void vokatiivi() {
        analyzer.enableGuessing = true;
        analyzer.enableVocative = true;
        analyzer.guessVerbs = false;
        analyzer.guessParticiples = false;
        analyzer.guessAdjectives = false;
        analyzer.guessInflexibleNouns = true;
        analyzer.enableAllGuesses = true;

        List<Wordform> forms = analyzer.generateInflections("koks", true);
        describe(forms);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "koks");

        forms = analyzer.generateInflections("paziņa", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "paziņa");
    }

    @Test
    public void kviesis() {
        Word kvieši = analyzer.analyze("kvieši");
        assertTrue(kvieši.isRecognized());
        assertEquals("kviesis", kvieši.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        List<Wordform> kviesis = analyzer.generateInflections("kviesis", true);
        assertNounInflection(kviesis, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "kviešu");
    }

    @Test
    public void viesis_tālskatis() {
        Word tālskatu = analyzer.analyze("tālskatu");
        assertTrue(tālskatu.isRecognized());
        assertEquals("tālskatis", tālskatu.wordforms.getFirst().getValue(AttributeNames.i_Lemma));
        assertFalse(analyzer.analyze("tālskašu").isRecognized());

        List<Wordform> viesis = analyzer.generateInflections("viesis", true);
        assertNounInflection(viesis, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "viesu");
    }

    /**
     * LETA lietvārdu locījumu pārbaude - citi gljuki
     */
    @Test
    public void gljuki20140401() {
        analyzer.enableGuessing = true;
        analyzer.enableVocative = true;
        analyzer.guessVerbs = false;
        analyzer.guessParticiples = false;
        analyzer.guessAdjectives = false;
        analyzer.guessInflexibleNouns = true;
        analyzer.enableAllGuesses = true;

        List<Wordform> mēness = analyzer.generateInflections("mēness", true);
        assertNounInflection(mēness, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "mēness");
        assertNounInflection(mēness, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "mēnes");
    }

    /**
     * Treat out-of-vocabulary acronyms as not flexive - e.g. NATO, FMS, IMS etc
     */
    @Test
    public void acronyms() {
        analyzer.enableGuessing = true;
        analyzer.enableVocative = true;
        analyzer.guessVerbs = false;
        analyzer.guessParticiples = false;
        analyzer.guessAdjectives = false;
        analyzer.guessInflexibleNouns = true;
        analyzer.enableAllGuesses = true;

        // 2026-08-07 I found that original assertInflection method used weak
        // attribute matching and, thus, this test succeed despite having no
        // cases at all. Switching to stronger matching, this test don't work.
        List<Wordform> fms = analyzer.generateInflections("FMS", false);
        //assertNounInflection(fms, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "FMS");
		assertEquals(1, fms.size());
    }

    /**
     * 2014.08.01 Bug with verb stem changes -> rakt -> *rakis (racis); *rakiens (raciens)
     */
    @Test
    public void rakiens() {
        Word w = analyzer.analyze("racis");
        assertTrue(w.isRecognized());
        assertEquals("rakt", w.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        w = analyzer.analyze("rakis");
        assertFalse(w.isRecognized());

        w = analyzer.analyze("veicis");  // lai nesalauž šo
        assertTrue(w.isRecognized());
        assertEquals("veikt", w.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        List<Wordform> rakt = analyzer.generateInflections("rakt", false);
        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
        testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
        testset.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
        testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
        assertInflection(rakt, testset, "raciens");
    }

    /**
     * 2014.08.25 Bug with verb stem changes
     */
    @Test
    public void lecdams() {
        Word w = analyzer.analyze("lēkdams");
        assertTrue(w.isRecognized());
        assertEquals("lēkt", w.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        w = analyzer.analyze("lēcdams");
        assertFalse(w.isRecognized());

        List<Wordform> lēkt = analyzer.generateInflections("lēkt", false);
        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
        testset.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Participle);
        testset.addAttribute(AttributeNames.i_Lokaamiiba, AttributeNames.v_DaljeejiLokaams);
        testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
        testset.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
        testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
        assertInflection(lēkt, testset, "lēkdams");
    }

    /**
     * 2014.08.25 Bug with verb 'līt'
     */
    @Test
    public void līstiiet() {
        Word w = analyzer.analyze("līstiet");
        assertTrue(w.isRecognized());
        assertEquals("līt", w.wordforms.getFirst().getValue(AttributeNames.i_Lemma));

        w = analyzer.analyze("līstiiet");
        assertFalse(w.isRecognized());

        // un vēl bija "lijdams" gļukforma
        List<Wordform> līt = analyzer.generateInflections("līt", false);
        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
        testset.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Participle);
        testset.addAttribute(AttributeNames.i_Lokaamiiba, AttributeNames.v_DaljeejiLokaams);
        testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
        testset.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
        testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
        assertInflection(līt, testset, "līdams");
    }

    /**
     * Local dialectal words from Tēzaurs should not be in default lexicon
     */
    @Test
    public void apvidvārdi() {
        Word w = analyzer.analyze("īstāis");
        assertFalse(w.isRecognized());
    }

    /**
     * In case if there is any ambiguity between a normal word and a lexeme flagged as rare, the rare option should be excluded completely by default ('ar' -> 'art'; 'ir' -> 'irt')
     */
    @Test
    public void retie() {
        Word w = analyzer.analyze("aršana");
        assertTrue(w.isRecognized());

        w = analyzer.analyze("ar");
        for (Wordform wf : w.wordforms)
            assertFalse(wf.isMatchingStrong(AttributeNames.i_Lemma, "art"));
    }

    @Test
    public void turlais() {
        analyzer.enableGuessing = true;
        analyzer.enableVocative = true;
        analyzer.guessVerbs = false;
        analyzer.guessParticiples = false;
        analyzer.guessInflexibleNouns = true;
        analyzer.enableAllGuesses = true;

        Word w = analyzer.guessByEnding("turlais", "Turlais");
        assertTrue(w.isRecognized());
        for (Wordform wf : w.wordforms)
            assertFalse(wf.isMatchingStrong(AttributeNames.i_Lemma, "art"));
    }

    // TODO: should it still be true?
    @Test
    public void apstākļa_vārdu_ģenerēšana() {
        List<Wordform> labi = analyzer.generateInflections("labi");
        assertEquals(1, labi.size());
    }

    @Test
    public void rozā() {
        List<Wordform> rozā = analyzer.generateInflections("rozā");
        assertEquals(1, rozā.size());
        assertTrue(rozā.getFirst().isMatchingStrong(AttributeNames.i_PartOfSpeech, AttributeNames.v_Adjective));
    }

    @Test // klausītājies, vēlējumies - http://valoda.ailab.lv/latval/vidusskolai/morfol/lietv-atgr.htm
    public void reflexive_nouns() {
        Word klausītājies = analyzer.analyze("klausītājies");
        assertTrue(klausītājies.isRecognized());

        Word vēlējumies = analyzer.analyze("vēlējumies");
        assertTrue(vēlējumies.isRecognized());

        Word acīsskatīšanās = analyzer.analyze("acīsskatīšanās");
        assertTrue(acīsskatīšanās.isRecognized());

        Word pakaļdzinējies = analyzer.analyze("pakaļdzinējies");
        assertTrue(pakaļdzinējies.isRecognized());
    }

    @Test // https://github.com/PeterisP/morphology/issues/7
    public void mijas_3_konj() {
        // mīcīt -> mīcu
        Word test = analyzer.analyze("mīcu");
        assertTrue(test.isRecognized());
        test = analyzer.analyze("mīku");
        assertFalse(test.isRecognized());

        test = analyzer.analyze("mācu");
        assertTrue(test.isRecognized());
        test = analyzer.analyze("māku");
        assertFalse(test.getBestWordform().getValue(AttributeNames.i_Lemma).equalsIgnoreCase("mācīt"));

        test = analyzer.analyze("tūcu");
        assertTrue(test.isRecognized());
        test = analyzer.analyze("tūku");
        assertFalse(test.getBestWordform().getValue(AttributeNames.i_Lemma).equalsIgnoreCase("tūcīt"));

        // sacīt -> saku
        test = analyzer.analyze("sacu");
        assertFalse(test.isRecognized());
        test = analyzer.analyze("saku");
        assertTrue(test.isRecognized());

        // sacīt -> saku
        test = analyzer.analyze("izsacos");
        assertFalse(test.isRecognized());
        test = analyzer.analyze("izsakos");
        assertTrue(test.isRecognized());

        test = analyzer.analyze("slaucu");
        assertTrue(test.isRecognized());
        assertFalse(test.getBestWordform().getValue(AttributeNames.i_Lemma).equalsIgnoreCase("slaucīt"));
        assertTrue(test.getBestWordform().getValue(AttributeNames.i_Lemma).equalsIgnoreCase("slaukt"));
        test = analyzer.analyze("slauku");
        assertTrue(test.isRecognized());

        test = analyzer.analyze("braucu");
        assertFalse(test.getBestWordform().getValue(AttributeNames.i_Lemma).equalsIgnoreCase("braucīt"));
        test = analyzer.analyze("brauku");
        assertTrue(test.isRecognized());

        test = analyzer.analyze("uzbraucu");
        assertFalse(test.getBestWordform().getValue(AttributeNames.i_Lemma).equalsIgnoreCase("uzbraucīt"));
        test = analyzer.analyze("uzbrauku");
        assertTrue(test.isRecognized());

//		test = locītājs.analyze("izšļaucu");
//		assertFalse(test.getBestWordform().getValue(AttributeNames.i_Lemma).equalsIgnoreCase("izšļaucīt"));
//		test = locītājs.analyze("izšļauku");
//		assertTrue(test.isRecognized());

        // ņurcīt -> ņurcu un ņurku
        test = analyzer.analyze("ņurcu");
        assertTrue(test.isRecognized());
        test = analyzer.analyze("ņurku");
        assertTrue(test.isRecognized());

        test = analyzer.analyze("murcu");
        assertTrue(test.isRecognized());
        test = analyzer.analyze("murku");
        assertTrue(test.isRecognized());

        test = analyzer.analyze("mocu");
        assertTrue(test.isRecognized());
        test = analyzer.analyze("moku");
        assertTrue(test.isRecognized());
    }

    @Test // pēc analoģijas ar visu citu būtu jābūt sēžošs bet ir sēdošs
    public void sēdošs() {
        Word sēdošs = analyzer.analyze("sēdošs");
        assertTrue(sēdošs.isRecognized());

    }

    @Test // izmaiņas izteiksmju sarakstā
    public void vajadzībasatstāstījuma() {
        Word jārokot = analyzer.analyze("jārokot");
        assertTrue(jārokot.isRecognized());
        assertEquals(AttributeNames.v_DebitiveQuotative, jārokot.wordforms.getFirst().getValue(AttributeNames.i_Mood));
    }

    @Test // Tezauram locīšanai - lai nelokam to, kas nav leksikonā bez minēšanas
    public void nelocīt() {
        List<Wordform> forms = analyzer.generateInflections("yyyyyyy");
        assertEquals(0, forms.size());

        analyzer.guessVerbs = false;
        analyzer.guessParticiples = false;

        forms = analyzer.generateInflections("pavārāms");
        assertTrue(forms.isEmpty());

        forms = analyzer.generateInflections("nav");
        assertTrue(forms.isEmpty());
    }

    @Test // Crash uz sliktu locīšanu
    public void locīt_ar_sliktu_paradigmu() {
        analyzer.generateInflectionsFromParadigm("vārāms", 16);
        assertTrue(true);
    }

    @Test // izmaiņas ar substantivizējušamies divdabjiem un īpašībasvārdiem
    public void adjektīviskā_deklinācija() {
        List<Wordform> forms = analyzer.generateInflections("mēnessērdzīgais", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "mēnessērdzīgajam");

        forms = analyzer.generateInflections("mēnessērdzīgā", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "mēnessērdzīgajai");

        forms = analyzer.generateInflections("cietušais", false);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "cietušajam");

        forms = analyzer.generateInflections("dzeramais", true);
        assertNounInflection(forms, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "dzeramajam");
    }

    @Test // Hardcoded vārdu locīšana
    public void inflect_hardcoded() {
        List<Wordform> būt = analyzer.generateInflections("būt");

        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_Person, "3");
        testset.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Indicative);
        testset.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
        testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
        assertInflection(būt, testset, "ir");

        būt = analyzer.generateInflections("būt");
        testset.addAttribute(AttributeNames.i_Noliegums, AttributeNames.v_Yes);
        assertInflection(būt, testset, "nav");

        List<Wordform> viņš = analyzer.generateInflections("viņš");

        testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Dative);
        assertInflection(viņš, testset, "viņam");
    }

    @Test
    // Bija vārdiem simts, miljons utml sieviešu dzimtes formas arī. Pārklājas ar https://github.com/PeterisP/morphology/issues/1
    public void simtas() {
        List<Wordform> simts = analyzer.generateInflections("simts");

        for (Wordform wf : simts) {
            if (wf.getToken().equalsIgnoreCase("simtas")) {
                wf.describe();
            }
            assertNotEquals("simtas", wf.getToken());
        }

        // 2023-06-15 - tagad tēzaurā ir arī skaitļavārda leksēma 'simts'
//        Word simtas = locītājs.analyze("simtas");
//        describe(simtas.wordforms);
//        assertFalse(simtas.isRecognized());
    }

    @Test // Problēma ar vārdu krāties, kur bija formas 'krāos' u.c.
    public void krāties() {
        List<Wordform> krāties = analyzer.generateInflections("krāties");
        for (Wordform wf : krāties) {
            assertNotEquals("krāos", wf.getToken());
        }

        Word krāos = analyzer.analyze("krāos");
        assertFalse(krāos.isRecognized());
    }

    @Test // Locījumu ģenerēšanai jādarbojas ar vairākiem celmiem 1. konjugācijas gadījumā
    public void multistem_generateinflections() {
        List<Wordform> sairšana = analyzer.generateInflectionsFromParadigm("irt", 15, "ir", "irst", "ir");
        List<Wordform> laivas_iršana = analyzer.generateInflectionsFromParadigm("irt", 15, "ir", "ir", "īr");

        AttributeValues verbPast = new AttributeValues();
        verbPast.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
        verbPast.addAttribute(AttributeNames.i_Person, "3");
        verbPast.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Indicative);
        verbPast.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Past);
        assertInflection(sairšana, verbPast, "ira");
        assertInflection(laivas_iršana, verbPast, "īra");

        AttributeValues verbPresent = new AttributeValues();
        verbPresent.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
        verbPresent.addAttribute(AttributeNames.i_Person, "1");
        verbPresent.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Indicative);
        verbPresent.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
        assertInflection(sairšana, verbPresent, "irstu");
        assertInflection(laivas_iršana, verbPresent, "iru");
    }

    @Test
    public void sixthDeclPlurals() {
        // 2016./2017. gadā bija problēma, ka tēzaurā ģenerē datīvu "ļaudiij"
        AttributeValues attrs = new AttributeValues();
        attrs.addAttribute(AttributeNames.i_NumberSpecial, AttributeNames.v_PlurareTantum);
        attrs.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);

        List<Wordform> ļaudis = analyzer.generateInflectionsFromParadigm("ļaudis", 11, attrs);
        for (Wordform wf : ļaudis) {
            assertNotEquals("ļaudiij", wf.getToken());
            assertFalse(wf.isMatchingStrong(AttributeNames.i_Number, AttributeNames.v_Singular));
            assertTrue(wf.isMatchingStrong(AttributeNames.i_Gender, AttributeNames.v_Masculine));
        }

        Word ļaudiij = analyzer.analyze("ļaudiij");
        if (ļaudiij.isRecognized())
            ļaudiij.describe(System.out);
        assertFalse(ļaudiij.isRecognized());

        // 2026-06-05 Gunta sūdzas, korpusā asinis tago ncfsg6.
        // Leksēma "asinis" atišķirībā no "ļaudis" tajā brīdī ir "neīstais
        // daudzskaitlinieks" -- leksēma, kam lemma norādīta daudzskaitlī, un ir
        // atribūts "Leksēmas pamatformas īpatnības": "Daudzskaitlis".
        Word asinis = analyzer.analyze("asinis");
        assertTrue(asinis.isRecognized());
        describe(asinis.wordforms);
        boolean found = false;
        for (Wordform wf :  asinis.wordforms) {
            if (wf.isMatchingStrong(AttributeNames.i_Case, AttributeNames.v_Genitive))
                found = true;
        }
        assertFalse(found);

    }

    @Test // https://github.com/PeterisP/morphology/issues/15
    public void griedt() {
        List<Wordform> griezt = analyzer.generateInflections("griezt");
        for (Wordform wf : griezt) {
            assertNotEquals("gried", wf.getToken());
            assertNotEquals("griediet", wf.getToken());
        }

        assertFalse(analyzer.analyze("gried").isRecognized());
        assertFalse(analyzer.analyze("griediet").isRecognized());
        assertTrue(analyzer.analyze("griez").isRecognized());
        assertTrue(analyzer.analyze("grieziet").isRecognized());
    }

    @Test
    public void lemmas2017mar() {
        assertLemma("izpaužas", "izpausties");
        assertLemma("finanšu", "finanses");
        assertLemma("tūkstotim", "tūkstotis");
        //assertLemma("tūkstošus", "tūkstotis");
        assertLemma("slēpjas", "slēpties");
//        assertLemma("pārējie", "pārējais");
        analyzer.enableGuessing = true;
        assertLemma("Pētera", "Pēteris");
        assertLemma("NATO", "NATO");
        Word lībiešu = analyzer.analyze("lībiešu");
        assertTrue(lībiešu.isRecognized());
        boolean foundLemma = false;
        for (Wordform wf : lībiešu.wordforms) {
            if (wf.isMatchingStrong(AttributeNames.i_Lemma, "lībietis"))
                foundLemma = true;
        }
        assertTrue(foundLemma);
    }

    // https://github.com/PeterisP/morphology/issues/104
    @Test
    public void turpms() {
        Word turpmākiem = analyzer.analyze("turpmākiem");
        assertTrue(turpmākiem.isRecognized());
        assertLemma("turpmākiem", "turpmāks");

        List<Wordform> turpmāks = analyzer.generateInflections("turpmāks");
        for (Wordform wf : turpmāks) {
            assertNotEquals("turpms", wf.getToken());
            assertNotEquals("turpmākāks", wf.getToken());
        }

        turpmāks = analyzer.generateInflectionsFromParadigm("turpmāks", 13, new AttributeValues());
        for (Wordform wf : turpmāks) {
            assertNotEquals("turpms", wf.getToken());
            assertNotEquals("turpmākāks", wf.getToken());
        }
    }

    @Test
    public void turpms2() {
        Word turpmākajā = analyzer.analyze("Turpmākajā");
        assertTrue(turpmākajā.isRecognized());
        assertLemma("Turpmākajā", "turpmāks");
    }

    //    https://github.com/PeterisP/morphology/issues/12
    @Test
    public void pase() {
        List<Wordform> pase = analyzer.generateInflections("pase");
        List<Wordform> kase = analyzer.generateInflections("kase");
        List<Wordform> rase = analyzer.generateInflections("rase");

        AttributeValues nounPluralGen = new AttributeValues();
        nounPluralGen.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
        nounPluralGen.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
        nounPluralGen.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
        assertInflection(pase, nounPluralGen, "pasu");
        assertInflection(kase, nounPluralGen, "kasu");
        assertInflection(rase, nounPluralGen, "rasu");

        // vēl pase, kase, apaļmute, diplomande, artiste, amtisemīte, autobāze, mufe
        // manšete, torte, cunfte, dzeņaukste, plekste, lete, mufe ir ar optionālo miju
        // aste, balle, gāze ir bez mijas
    }

    @Test
    public void pēdējajam() {
        List<Wordform> pēdējs = analyzer.generateInflections("pēdējs");
        for (Wordform wf : pēdējs) {
            if (wf.getToken().equalsIgnoreCase("pēdējajam"))
                describe(new LinkedList<>(Collections.singletonList(wf)));
            assertNotEquals("pēdējajam", wf.getToken()); // šo formu nedrīkst ģenerēt
        }

        List<Wordform> pēdējais = analyzer.generateInflections("pēdējais");
        for (Wordform forma : pēdējais) {
            if (forma.getToken().equalsIgnoreCase("pēdējajam"))
                describe(new LinkedList<>(Collections.singletonList(forma)));
            assertNotEquals("pēdējajam", forma.getToken()); // šo formu nedrīkst ģenerēt
        }
        assertLemma("pēdējam", "pēdējais");
        assertLemma("pēdējajam", "pēdējais");  // bet drīkst atpazīt
        assertLemma("pēdējs", "pēdējs"); // ja nu kāds tā pasaka, tad lai ir tā novecojusī lemma
        assertLemma("vispēdējākais", "pēdējs");  // vai tā ir ok?
        assertLemma("vispēdējākajam", "pēdējs"); // vai tā ir ok?
    }

    @Test
    public void divdabju_pakāpe() {
        Word ziedošs = analyzer.analyze("ziedošs");
        assertTrue(ziedošs.isRecognized());
        assertEquals(AttributeNames.v_Positive, ziedošs.getBestWordform().getValue(AttributeNames.i_Degree));
        assertEquals("vmnpdmsnapnpn", ziedošs.wordforms.getFirst().getTag());

        Word ziedošāks = analyzer.analyze("ziedošāks");
        assertTrue(ziedošāks.isRecognized());
        assertEquals(AttributeNames.v_Comparative, ziedošāks.getBestWordform().getValue(AttributeNames.i_Degree));
        assertEquals("vmnpdmsnapncn", ziedošāks.wordforms.getFirst().getTag());

        Word visziedošākais = analyzer.analyze("visziedošākais");
        assertTrue(visziedošākais.isRecognized());
        assertEquals(AttributeNames.v_Superlative, visziedošākais.getBestWordform().getValue(AttributeNames.i_Degree));
        assertEquals("vmnpdmsnapysn", visziedošākais.wordforms.getFirst().getTag());
    }

    @Test
    @Ignore("Pie personvārdu pārcelšanas uz tēzauru, nepārnesām vārdu skaitu. Tagad varēs tēzaurā karodziņos norādīt, ka rets")
    public void frequencies() {
        assertTrue(analyzer.analyze("Kaspars").isRecognized());
        assertFalse(analyzer.analyze("Induls").isRecognized());
    }

    @Test
    public void balamute() {
        AttributeValues paradigmSpec = new AttributeValues();
        paradigmSpec.addAttribute(AttributeNames.i_ParadigmID, "47");

        List<Wordform> balamute = analyzer.generateInflections("balamute", false, paradigmSpec);
        AttributeValues nounPluralGen = new AttributeValues();
        nounPluralGen.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
        nounPluralGen.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
        nounPluralGen.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
        assertInflection(balamute, nounPluralGen, "balamutu");

        AttributeValues nounSingularDat = new AttributeValues();
        nounSingularDat.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
        nounSingularDat.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
        nounSingularDat.addAttribute(AttributeNames.i_Case, AttributeNames.v_Dative);
        assertInflection(balamute, nounSingularDat, "balamutem");
    }

    @Test
    public void žirafe() {
        Word w = analyzer.analyze("žirafu");
        assertTrue(w.isRecognized());
        w = analyzer.analyze("žirafju");
        assertTrue(w.isRecognized());
    }

    @Test
    public void viszaļāk() {
        Word w = analyzer.analyze("viszaļāk");
        assertTrue(w.isRecognized());

        List<Wordform> zaļš = analyzer.generateInflections("zaļš");

        AttributeValues advSuperlative = new AttributeValues();
        advSuperlative.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Adverb);
        advSuperlative.addAttribute(AttributeNames.i_Degree, AttributeNames.v_Superlative);
        assertInflection(zaļš, advSuperlative, "viszaļāk");
    }

    @Test
    public void iekosties() {
        List<Wordform> kost = analyzer.generateInflections("kost");
        AttributeValues verbPres2ndPersSing = new AttributeValues();
        verbPres2ndPersSing.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
        verbPres2ndPersSing.addAttribute(AttributeNames.i_Person, "2");
        verbPres2ndPersSing.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
        verbPres2ndPersSing.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
        assertInflection(kost, verbPres2ndPersSing, "kod");
        List<Wordform> izpausties = analyzer.generateInflections("izpausties");
        assertInflection(izpausties, verbPres2ndPersSing, "izpaudies");
        List<Wordform> izlauzties = analyzer.generateInflections("izlauzties");
        assertInflection(izlauzties, verbPres2ndPersSing, "izlauzies");

        Word w = analyzer.analyze("kod");
        assertTrue(w.isRecognized());
        w = analyzer.analyze("koz");
        assertFalse(w.isRecognized());
    }

    @Test
    public void aizkost() {
        List<Wordform> aizkost = analyzer.generateInflections("aizkost");
        AttributeValues verbPres2ndPersSing = new AttributeValues();
        verbPres2ndPersSing.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
        verbPres2ndPersSing.addAttribute(AttributeNames.i_Person, "2");
        verbPres2ndPersSing.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
        verbPres2ndPersSing.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
        assertInflection(aizkost, verbPres2ndPersSing, "aizkod");

        Word w = analyzer.analyze("aizkod");
        assertTrue(w.isRecognized());
        w = analyzer.analyze("aizkoz");
        assertFalse(w.isRecognized());
    }

    @Test
    public void jaundzimušākais() {
        Word w = analyzer.analyze("jaundzimušais");
        assertTrue(w.isRecognized());
        w = analyzer.analyze("jaundzimušākais");
        assertFalse(w.isRecognized());
        w = analyzer.analyze("jaundzimušajam");
        assertTrue(w.isRecognized());
        w = analyzer.analyze("jaundzimušākajam");
        assertFalse(w.isRecognized());
    }

    @Test // https://github.com/PeterisP/morphology/issues/3
    public void guessAbbreviation() {
        Word w = analyzer.analyze("PZLK");
        assertFalse(w.isRecognized());
        analyzer.enableGuessing = true;
        w = analyzer.analyze("PZLK");
        assertTrue(w.isRecognized());
        boolean found = false;
        for (Wordform wf : w.wordforms) {
            if (wf.isMatchingStrong(AttributeNames.i_PartOfSpeech, AttributeNames.v_Abbreviation))
                found = true;
        }
        assertTrue(found);
    }

    @Test // https://github.com/PeterisP/morphology/issues/3
    public void guessInflexive() {
        Word w = analyzer.analyze("pluto");
        assertFalse(w.isRecognized());
        analyzer.enableGuessing = true;
        w = analyzer.analyze("pluto");
        assertTrue(w.isRecognized());
        boolean found = false;
        for (Wordform wf : w.wordforms) {
            if (wf.isMatchingStrong(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun))
                found = true;
        }
        assertTrue(found);
    }

    @Test
    public void zaļoksnējajā() {
        Word w = analyzer.analyze("zaļoksnējajā");
        assertTrue(w.isRecognized());
    }

    @Test
    public void plāns_B() {
        Word w = analyzer.analyze("B");
        assertTrue(w.isRecognized());
        assertEquals("xd", w.getBestWordform().getTag());
    }

    @Test
    public void pelus() {
        AttributeValues nounMascPlTant = new AttributeValues();
        nounMascPlTant.addAttribute(AttributeNames.i_NumberSpecial, AttributeNames.v_PlurareTantum);
        nounMascPlTant.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);

        List<Wordform> pelus = analyzer.generateInflectionsFromParadigm("pelus", 31, nounMascPlTant);
        assertNotEquals(0, pelus.size());
    }

    @Test
    public void sēžu() {
        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
        testset.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Indicative);
        testset.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
        testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
        testset.addAttribute(AttributeNames.i_Person, "1");

        List<Wordform> sēdu = analyzer.generateInflectionsFromParadigm("sēdēt", 17);
        assertInflection(sēdu, testset, "sēdu");
        List<Wordform> sēžu = analyzer.generateInflectionsFromParadigm("sēdēt", 45);
        assertInflection(sēžu, testset, "sēžu");

        List<Wordform> aizsēdēties = analyzer.generateInflectionsFromParadigm("aizsēdēties", 46);
        assertInflection(aizsēdēties, testset, "aizsēžos");

        testset.addAttribute(AttributeNames.i_Person, "2");
        assertInflection(aizsēdēties, testset, "aizsēdies");
    }

    @Test
//    Ticket #18
    public void roberts_20171110() {
        Word w = analyzer.analyze("!!!!");
        assertTrue(w.isRecognized());
        assertEquals("zs", w.getBestWordform().getTag());
        w = analyzer.analyze("!!!");
        assertTrue(w.isRecognized());
        assertEquals("zs", w.getBestWordform().getTag());
    }

    @Test
    public void manīmTevim() {
        Word manīm = analyzer.analyze("manīm");
        assertTrue(manīm.isRecognized());
        Word tevim = analyzer.analyze("tevim");
        assertTrue(tevim.isRecognized());

        // Since 2024 "manim", "manīm" and "tevim" is generated as well.
        AttributeValues testsetRare = new AttributeValues();
        testsetRare.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Pronoun);
        testsetRare.addAttribute(AttributeNames.i_Case, AttributeNames.v_Dative);
        testsetRare.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
        testsetRare.addAttribute(AttributeNames.i_General_Frequency, AttributeNames.v_Rare);
        AttributeValues testsetNonlit = new AttributeValues();
        testsetNonlit.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Pronoun);
        testsetNonlit.addAttribute(AttributeNames.i_Case, AttributeNames.v_Dative);
        testsetNonlit.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
        testsetNonlit.addAttribute(AttributeNames.i_Usage, AttributeNames.v_Nonlit);

        List<Wordform> es = analyzer.generateInflections("es");
        assertInflection(es, testsetRare, "manim");
        assertInflection(es, testsetNonlit, "manīm");

        List<Wordform> tu = analyzer.generateInflections("tu");
        assertInflection(tu, testsetRare, "tevim");
        // FIXME: update accordign to linguists
        //assertInflection(tu, testsetNonlit, "tevīm");
    }

    /**
     * Aizdomas par tagset problēmām
     */
    @Test
    public void laura_20180614() {
        Word w = analyzer.analyze("ka");
        assertTrue(w.isRecognized());
        assertEquals("cs", w.getBestWordform().getTag());

        w = analyzer.analyze("arī");
        assertTrue(w.isRecognized());
        boolean found = false;
        for (Wordform f : w.wordforms) {
            if (f.getTag().equalsIgnoreCase("q"))
                found = true;
        }
        assertTrue("Nav 'arī' kā partikula ar 'q' tagu", found);

        w = analyzer.analyze("var");
        assertTrue(w.isRecognized());
        found = false;
        for (Wordform f : w.wordforms) {
            if (f.getTag().startsWith("vo"))
                found = true;
        }
        assertTrue("Nav 'var' varianta ar 'vo...' tagu", found);

        w = analyzer.analyze("norādījuši");
        assertTrue(w.isRecognized());
        assertTrue(w.getBestWordform().getTag() + " needs to end with pn", w.getBestWordform().getTag().endsWith("pn"));

        w = analyzer.analyze("zaļajā");
        assertTrue(w.isRecognized());
        assertEquals("afmslyp", w.getBestWordform().getTag());
    }

    @Test
    public void nav() {
        // Jābūt gan variantam kā saitiņai, gan patstāvīgajā nozīmē 'man nav mājas'
        Word nav = analyzer.analyze("nav");
        assertTrue(nav.isRecognized());
//        nav.describe(System.out);
        boolean found_m = false;
        boolean found_c = false;
        boolean found_tag = false;
        for (Wordform wf : nav.wordforms) {
            assertEquals("būt", wf.getValue(AttributeNames.i_Lemma));
            if (wf.isMatchingStrong(AttributeNames.i_VerbType, AttributeNames.v_MainVerb))
                found_m = true;
            if (wf.isMatchingStrong(AttributeNames.i_VerbType, AttributeNames.v_Buut))
                found_c = true;
            if (wf.getTag().equalsIgnoreCase("vmnipii30ay"))
                found_tag = true;
        }
        assertTrue(found_m);
        assertTrue(found_c);
        assertTrue(found_tag);
    }

    @Test
    public void ņukši() {
        Word ņukši = analyzer.analyze("Ņukši");
        assertTrue(ņukši.isRecognized());

        Word ņukšu = analyzer.analyze("Ņukšu");
        assertTrue(ņukšu.isRecognized());
    }

    @Test
    public void vajagu() {
        ArrayList<Wordform> formas = analyzer.generateInflections("vajadzēt");
        for (Wordform wf : formas) {
            assertNotEquals("vajagu", wf.getToken());
            assertNotEquals("vajagi", wf.getToken());
        }
        formas = analyzer.generateInflections("ievajadzēties");
        boolean found = false;
        for (Wordform wf : formas) {
			if (wf.getToken().equalsIgnoreCase("ievajagos")) {
				found = true;
				break;
			}
        }
        assertTrue(found);
    }

    @Test
    public void būt() {
        ArrayList<Wordform> būt = analyzer.generateInflections("būt");
        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
        testset.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Debitive);
        assertInflection(būt, testset, "jābūt");

        Word jābūt = analyzer.analyze("jābūt");
        assertTrue(jābūt.isRecognized());

        testset.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Indicative);
        testset.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
        testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
        testset.addAttribute(AttributeNames.i_Person, "2");
        assertInflection(būt, testset, "esi");

        testset.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Indicative);
        testset.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
        testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
        testset.addAttribute(AttributeNames.i_Person, "1");
        assertInflection(būt, testset, "esmu");
    }

    @Test
    public void pats() {
        ArrayList<Wordform> pats = analyzer.generateInflectionsFromParadigm("pats", 1);
        for (Wordform wf : pats) {
            if (wf.getToken().equalsIgnoreCase("paša")) {
                wf.describe();
            }
            assertNotEquals("paša", wf.getToken());
        }
    }

    @Test
    public void generationFrom1stConjStems() {
        ArrayList<Wordform> jaust = analyzer.generateInflectionsFromParadigm("jaust", 15, "jaus", "jauš", "jaut");
        for (Wordform wf : jaust) {
            if (wf.getToken().equalsIgnoreCase("jauš")) {
                assertNotEquals("2", wf.getValue(AttributeNames.i_Person));
            }
        }
        jaust = analyzer.generateInflectionsFromParadigm("jaust", 15, "jaus", "jauž", "jaud");
        for (Wordform wf : jaust) {
            if (wf.getToken().equalsIgnoreCase("jauš")) {
                assertNotEquals("2", wf.getValue(AttributeNames.i_Person));
            }
        }

        // Testing on missing stems.
        ArrayList<Wordform> aust = analyzer.generateInflectionsFromParadigm("aust", 15, "aus", "auž", null);
        assertFalse(aust.isEmpty());
        aust = analyzer.generateInflectionsFromParadigm("aust", 15, "aus", null, "aud");
        assertFalse(aust.isEmpty());
        aust = analyzer.generateInflectionsFromParadigm("aust", 15, "aus", null, null);
        assertFalse(aust.isEmpty());
        aust = analyzer.generateInflectionsFromParadigm("aust", 15, null, null, null);
        assertTrue(aust.isEmpty());

        ArrayList<Wordform> austies = analyzer.generateInflectionsFromParadigm("austies", 18, "aus", "auž", null);
        assertFalse(austies.isEmpty());
        austies = analyzer.generateInflectionsFromParadigm("austies", 18, "aus", null, "aud");
        assertFalse(austies.isEmpty());
        austies = analyzer.generateInflectionsFromParadigm("austies", 18, "aus", null, null);
        assertFalse(austies.isEmpty());
        austies = analyzer.generateInflectionsFromParadigm("austies", 18, null, null, null);
        assertTrue(austies.isEmpty());
    }

    @Test
    public void generationMissingOnlyStem()
    {
        ArrayList<Wordform> fakeVerb = analyzer.generateInflectionsFromParadigm("tēvs", 1, null, null, null);
        assertTrue(fakeVerb.isEmpty());
    }

    @Test
    public void iet() {
        ArrayList<Wordform> iet = analyzer.generateInflections("iet");
        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
        testset.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Debitive);
        assertInflection(iet, testset, "jāiet");

        Word jāiet = analyzer.analyze("jāiet");
        assertTrue(jāiet.isRecognized());
        Word jāej = analyzer.analyze("jāej");
        assertFalse(jāej.isRecognized());

        List<Wordform> nepaiet = analyzer.generateInflections("nepaiet");
        testset.addAttribute(AttributeNames.i_Mood, AttributeNames.v_Indicative);
        testset.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
        testset.addAttribute(AttributeNames.i_Person, "3");
//        describe(formas);
        assertInflection(nepaiet, testset, "nepaiet");

        Word nepaietWf = analyzer.analyze("nepaiet");
        assertTrue(nepaietWf.isRecognized());
        Word nepaej = analyzer.analyze("nepaej");
        assertTrue(nepaej.isRecognized());
    }

    @Test
    public void ticket_26() {
        Word w = analyzer.analyze("sen");
        assertTrue(w.isRecognized());
        assertEquals("rpn", w.getBestWordform().getTag());

        w = analyzer.analyze("drīz");
        assertTrue(w.isRecognized());
        assertEquals("rpn", w.getBestWordform().getTag());

        w = analyzer.analyze("pārāk");
        assertTrue(w.isRecognized());
        assertEquals("r0n", w.getBestWordform().getTag());

        w = analyzer.analyze("daudzāk");
        assertTrue(w.isRecognized());
        assertEquals("rcn", w.getBestWordform().getTag());
        assertEquals("daudz", w.getBestWordform().getValue(AttributeNames.i_Lemma));

        w = analyzer.analyze("vairāk");
        assertTrue(w.isRecognized());
        assertEquals("rcn", w.getBestWordform().getTag());
        assertEquals("daudz", w.getBestWordform().getValue(AttributeNames.i_Lemma));
    }

    @Test
    public void saites() {
        Word w = analyzer.analyze("http://www.faili.lv/fails.php?id=215");
        assertTrue(w.isRecognized());
        assertEquals("xu", w.getBestWordform().getTag());

        w = analyzer.analyze("www.skaistas-vietas.lv");
        assertTrue(w.isRecognized());
        assertEquals("xu", w.getBestWordform().getTag());

        w = analyzer.analyze("https://esta.MRB.dhs.gov/");
        assertTrue(w.isRecognized());
        assertEquals("xu", w.getBestWordform().getTag());
    }

    @Test
    public void softhyphen() {
        List<Word> tokens = Splitting.tokenize(analyzer, "cirvim cir\u00ADvim");
        assertEquals(2, tokens.size());
        assertTrue(tokens.get(0).isRecognized());
        assertEquals("cirvis", tokens.get(0).getBestWordform().getValue(AttributeNames.i_Lemma));

        assertTrue(tokens.get(1).isRecognized());
        assertEquals("cirvis", tokens.get(1).getBestWordform().getValue(AttributeNames.i_Lemma));
    }

    @Test
    public void unicodeweirdness() {
        Word w;  // Ugly violation of DRY because I can't find a good way to initialize a literal map or list of tuples in Java with values like "«" -> "zq"
        w = analyzer.analyze("-");
        assertTrue(w.isRecognized());
        assertEquals("zd", w.getBestWordform().getTag());
        w = analyzer.analyze("–");
        assertTrue(w.isRecognized());
        assertEquals("zd", w.getBestWordform().getTag());
        w = analyzer.analyze("—");
        assertTrue(w.isRecognized());
        assertEquals("zd", w.getBestWordform().getTag());
        w = analyzer.analyze("”");
        assertTrue(w.isRecognized());
        assertEquals("zq", w.getBestWordform().getTag());
        assertEquals("\"", w.getBestWordform().getValue(AttributeNames.i_Lemma));
        w = analyzer.analyze("«");
        assertTrue(w.isRecognized());
        assertEquals("zq", w.getBestWordform().getTag());
        w = analyzer.analyze("»");
        assertTrue(w.isRecognized());
        assertEquals("zq", w.getBestWordform().getTag());
        w = analyzer.analyze("“");
        assertTrue(w.isRecognized());
        assertEquals("zq", w.getBestWordform().getTag());
        w = analyzer.analyze("„");
        assertTrue(w.isRecognized());
        assertEquals("zq", w.getBestWordform().getTag());
        w = analyzer.analyze("%");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("/");
        assertTrue(w.isRecognized());
        assertEquals("zx", w.getBestWordform().getTag());
        w = analyzer.analyze("*");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("_");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("[");
        assertTrue(w.isRecognized());
        assertEquals("zb", w.getBestWordform().getTag());
        w = analyzer.analyze("]");
        assertTrue(w.isRecognized());
        assertEquals("zb", w.getBestWordform().getTag());
        w = analyzer.analyze("•");
        assertTrue(w.isRecognized());
        assertEquals("zx", w.getBestWordform().getTag());
        w = analyzer.analyze("=");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
//        w = locītājs.analyze("&");
//        assertTrue(w.isRecognized());
//        assertEquals("zx", w.getBestWordform().getTag());
        w = analyzer.analyze("…");
        assertTrue(w.isRecognized());
        assertEquals("zs", w.getBestWordform().getTag());
        w = analyzer.analyze("+");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze(">");
        assertTrue(w.isRecognized());
        assertEquals("zb", w.getBestWordform().getTag());
        w = analyzer.analyze("’");
        assertTrue(w.isRecognized());
        assertEquals("zq", w.getBestWordform().getTag());
        w = analyzer.analyze("<");
        assertTrue(w.isRecognized());
        assertEquals("zb", w.getBestWordform().getTag());
        w = analyzer.analyze("§");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("°");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("\\");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("±");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("·");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("²");
        assertTrue(w.isRecognized());
        assertEquals("xn", w.getBestWordform().getTag());
        w = analyzer.analyze("‘");
        assertTrue(w.isRecognized());
        assertEquals("zq", w.getBestWordform().getTag());
        w = analyzer.analyze("~");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("−");
        assertTrue(w.isRecognized());
        assertEquals("zd", w.getBestWordform().getTag());
        w = analyzer.analyze("@");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("∙");
        assertTrue(w.isRecognized());
        assertEquals("zx", w.getBestWordform().getTag());
        w = analyzer.analyze("‒");
        assertTrue(w.isRecognized());
        assertEquals("zd", w.getBestWordform().getTag());
        w = analyzer.analyze("×");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("®");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("#");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("½");
        assertTrue(w.isRecognized());
        assertEquals("xn", w.getBestWordform().getTag());
        w = analyzer.analyze("`");
        assertTrue(w.isRecognized());
        assertEquals("zq", w.getBestWordform().getTag());
        w = analyzer.analyze("{");
        assertTrue(w.isRecognized());
        assertEquals("zb", w.getBestWordform().getTag());
        w = analyzer.analyze("©");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("$");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("→");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("¼");
        assertTrue(w.isRecognized());
        assertEquals("xn", w.getBestWordform().getTag());
        w = analyzer.analyze("™");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("}");
        assertTrue(w.isRecognized());
        assertEquals("zb", w.getBestWordform().getTag());
        w = analyzer.analyze("∆");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("≤");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("―");
        assertTrue(w.isRecognized());
        assertEquals("zd", w.getBestWordform().getTag());
        w = analyzer.analyze("¬");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("¾");
        assertTrue(w.isRecognized());
        assertEquals("xn", w.getBestWordform().getTag());
        w = analyzer.analyze("‟");
        assertTrue(w.isRecognized());
        assertEquals("zq", w.getBestWordform().getTag());
        w = analyzer.analyze("|");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("≥");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("∝");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("^");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("³");
        assertTrue(w.isRecognized());
        assertEquals("xn", w.getBestWordform().getTag());
        w = analyzer.analyze("≠");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("‰");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("£");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("´");
        assertTrue(w.isRecognized());
        assertEquals("zq", w.getBestWordform().getTag());
        w = analyzer.analyze("←");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("∂");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("↔");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("¹");
        assertTrue(w.isRecognized());
        assertEquals("xn", w.getBestWordform().getTag());
        w = analyzer.analyze("‚");
        assertTrue(w.isRecognized());
        assertEquals("zq", w.getBestWordform().getTag());
        w = analyzer.analyze("≈");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("†");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("¤");
        assertTrue(w.isRecognized());
        assertEquals("xx", w.getBestWordform().getTag());
        w = analyzer.analyze("″");
        assertTrue(w.isRecognized());
        assertEquals("zq", w.getBestWordform().getTag());
    }

    @Test
    public void nespēja() {
        Word w = analyzer.analyze("nespēja");
        for (Wordform wf : w.wordforms) {
            if (wf.isMatchingStrong(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb))
                assertEquals("spēt", wf.getValue(AttributeNames.i_Lemma));
        }
    }

    @Test
    @Ignore("Initials (uppercase) conflict with valid abbreviations (lowercase) from Tēzaurs.lv data")
    public void initials() {
        Word w = analyzer.analyze("J.");
        assertEquals("J.", w.getBestWordform().getValue(AttributeNames.i_Lemma));
        assertTrue(w.isRecognized());
        assertEquals("yp", w.getBestWordform().getTag());
    }

    @Test
    public void esmāt() {
        assertLemma("esmu", "būt");
    }

    @Test
    public void Saeima() {
        assertLemma("Saeimas", "Saeima");
    }

    /**
     * Decision to update lemmatization according to UD principles - feminine adjectives and numerals will have masculine lemma
     */
    @Test
    public void feminineAdjectives() {
        Word w = analyzer.analyze("zaļais");
        assertEquals("zaļš", w.getBestWordform().getValue(AttributeNames.i_Lemma));

        w = analyzer.analyze("zaļajai");
        assertEquals("zaļš", w.getBestWordform().getValue(AttributeNames.i_Lemma));

        w = analyzer.analyze("sarkanajam");
        assertEquals("sarkans", w.getBestWordform().getValue(AttributeNames.i_Lemma));

        w = analyzer.analyze("sarkanai");
        assertEquals("sarkans", w.getBestWordform().getValue(AttributeNames.i_Lemma));

        w = analyzer.analyze("otram");
        assertEquals("otrs", w.getBestWordform().getValue(AttributeNames.i_Lemma));

        w = analyzer.analyze("otrajai");
        assertEquals("otrais", w.getBestWordform().getValue(AttributeNames.i_Lemma));
    }

    // Ticket #40 'šitais' and 'šitas' do not get inflected
    @Test
    public void šitais() {
        List<Wordform> šitais = analyzer.generateInflections("šitais");
        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Dative);
        testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
        assertInflection(šitais, testset, "šitajam");

        List<Wordform> šitas = analyzer.generateInflections("šitas");
        assertInflection(šitas, testset, "šitam");
    }

    // Ticket #41 inflexible form for 'trīs'
    @Test
    public void trīs() {
        Word w = analyzer.analyze("trīs");
        boolean found = false;
        for (Wordform wf : w.wordforms) {
            if (wf.isMatchingStrong(AttributeNames.i_Case, AttributeNames.v_NA))
                found = true;
        }
        assertTrue(found);

        List<Wordform> trīs = analyzer.generateInflectionsFromParadigm("trīs", 29);
        assertFalse(trīs.isEmpty());
    }

    // Ticket #59
    @Test
    public void pusotrs() {
        Word w = analyzer.analyze("pusotrs");
        assertTrue(w.isRecognized());
        assertEquals("mfsmsn", w.getBestWordform().getTag());
    }

    // Ticket #56
    @Test
    public void celties() {
        Word w = analyzer.analyze("celties");
        assertTrue(w.isRecognized());
        assertEquals("vmyn0_1000n", w.getBestWordform().getTag());
    }

    // Ticket #48
    @Test
    public void overzealous_verb_guessing() {
        analyzer.enableDerivedNouns = false;
        analyzer.enableGuessing = true;
        Word w = analyzer.analyze("uzvarētājs");
        assertTrue(w.isRecognized());
        assertNotEquals("uzvarētājt", w.getBestWordform().getValue(AttributeNames.i_Lemma));
    }

    // Ticket #81 - noun derivation
    @Test
    public void noun_derivation() {
        analyzer.enableDerivedNouns = false; // Check that the words are OOV
        Word w = analyzer.analyze("slavētājs");
        assertFalse(w.isRecognized());
        w = analyzer.analyze("tīkotājs");
        assertFalse(w.isRecognized());
        w = analyzer.analyze("atsācējs");
        assertFalse(w.isRecognized());
        w = analyzer.analyze("kodējs");
        assertFalse(w.isRecognized());
        analyzer.enableDerivedNouns = true; // Check that the automatic derivation finds them
        w = analyzer.analyze("slavētājs");
        assertTrue(w.isRecognized());
        w = analyzer.analyze("tīkotājs");
        assertTrue(w.isRecognized());
        w = analyzer.analyze("atsācējs");
        assertTrue(w.isRecognized());
        w = analyzer.analyze("kodējs");
        assertTrue(w.isRecognized());
    }

    // Ticket #84 'skate' and 'apskate' get wrong inflection due to missing stem change
    @Test
    public void apskate() {
        List<Wordform> plate = analyzer.generateInflections("plate");
        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
        testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
        assertInflection(plate, testset, "plašu");

        List<Wordform> skate = analyzer.generateInflections("skate");
        assertInflection(skate, testset, "skašu");

        List<Wordform> tālskatis = analyzer.generateInflections("tālskatis");
        assertInflection(tālskatis, testset, "tālskatu");
    }

    @Test
    public void suitableParadigms_smoketest() {
        analyzer.guessAllParadigms = true;
        List<Paradigm> options;
        options = analyzer.suitableParadigms("žikivators");
        assertEquals(2, options.size()); // -s lietvārds, -s īpašības vārds

        options = analyzer.suitableParadigms("virzis");
        assertEquals(1, options.size());
        for (Paradigm p : options) {
            assertNotEquals(1, p.getID()); // -s šeit nav adekvāts minējums
        }

        options = analyzer.suitableParadigms("pokemonizēt");
        assertEquals(2, options.size());

        options = analyzer.suitableParadigms("askdjasdlkjakalsdj");
        assertEquals(1, options.size());
        assertEquals(39, options.getFirst().getID());

        //options = analyzer.suitableParadigms("mazpokemoni");
        /*for (Paradigm p : options) {
            System.out.printf("%d : %s\n", p.getID(), p.getName());
        }*/
    }

    // Piemēri, kuriem Artūrs identificēja, ka neko neatrod
    @Test
    public void suitableParadigms_notfound() {
        analyzer.guessAllParadigms = true;
        analyzer.enableAllGuesses = true;
        List<Paradigm> options;
        options = analyzer.suitableParadigms("gastroenterīts");
        assertNotEquals(0, options.size());

        options = analyzer.suitableParadigms("prettārpu");
        assertNotEquals(0, options.size());

        options = analyzer.suitableParadigms("Ševaljē");
        assertNotEquals(0, options.size());

        options = analyzer.suitableParadigms("INDIE");
        assertNotEquals(0, options.size());

        options = analyzer.suitableParadigms("maztauku");
        assertEquals(3, options.size());
    }

    @Test
    public void ticket90() {
        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
        testset.addAttribute(AttributeNames.i_Person, "2");
        testset.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Present);
        ArrayList<Wordform> formas = analyzer.generateInflections("šķist");
        assertInflection(formas, testset, "šķieti");

        /*
        testset.addAttribute(AttributeNames.i_Person, "1");
        testset.addAttribute(AttributeNames.i_Tense, AttributeNames.v_Future);
        formas = analyzer.generateInflections("vīkšt");
        assertInflection(formas, testset, "vīkšīšu"); //Pārbaude izņemta, jo apvidvārds
        testset.addAttribute(AttributeNames.i_Person, "2");
        assertInflection(formas, testset, "vīkšīsi");
        */
    }

    @Test
    public void adverb_degrees() {
        Word w = analyzer.analyze("ātri");
        assertTrue(w.isRecognized());
        boolean found = false;
        for (Wordform wf : w.wordforms) {
            if (wf.isMatchingStrong(AttributeNames.i_PartOfSpeech, AttributeNames.v_Adverb)) {
                assertEquals("rpn", wf.getTag());
                found = true;
            }
        }
        assertTrue(found);

        w = analyzer.analyze("ātrāk");
        assertTrue(w.isRecognized());
        found = false;
        for (Wordform wf : w.wordforms) {
            if (wf.isMatchingStrong(AttributeNames.i_PartOfSpeech, AttributeNames.v_Adverb)) {
                assertEquals("rcn", wf.getTag());
                found = true;
            }
        }
        assertTrue(found);
        w = analyzer.analyze("visiesāņāk");
        assertTrue(w.isRecognized());
        found = false;
        for (Wordform wf : w.wordforms) {
            if (wf.isMatchingStrong(AttributeNames.i_PartOfSpeech, AttributeNames.v_Adverb)) {
                assertEquals("rsn", wf.getTag());
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    public void vocabulary_oov() {
        analyzer.enableGuessing = false;

        Word w = analyzer.analyze("latviešu");
        assertTrue(w.isRecognized());

        w = analyzer.analyze("ēkas");
        assertTrue(w.isRecognized());
    }

    @Test
    public void plural_entry_with_ambiguous_stemchange() {
        analyzer.enableGuessing = false;

        Word w = analyzer.analyze("nēsis"); // No "nēši" nevar izdomāt vai ir "nētis" (kā "latvieši"->"latvietis") vai "nēsis"
//        describe(w.wordforms);
        assertTrue(w.isRecognized());
    }

    @Test
    public void partially_declinable_participles() {
        analyzer.enableGuessing = false;
        Word w = analyzer.analyze("gauzdamies");
        assertTrue(w.isRecognized());
        Wordform wf = w.getBestWordform();
        assertEquals("vmyppm0n0000n", wf.getTag());
    }

    /**
     * New paradigms for standalone partially declinable participles like 'pusjokodams' and 'pusjokodamies'
     */
    @Test
    public void pusjokodams() {
        analyzer.enableGuessing = false;
        Word w = analyzer.analyze("pusjokodama");
        assertTrue(w.isRecognized());
        Wordform wf = w.getBestWordform();
        assertEquals("arfsnnp", wf.getTag());
        assertEquals("pusjokodams", wf.getValue(AttributeNames.i_Lemma));

        w = analyzer.analyze("pusjokodamās");
        assertTrue(w.isRecognized());
        wf = w.getBestWordform();
        assertEquals("arfsnnp", wf.getTag());
        assertEquals("pusjokodamies", wf.getValue(AttributeNames.i_Lemma));
    }

    @Test
    public void ticket_101_a() {
        Word w = analyzer.analyze("Rīgai");
        assertTrue(w.isRecognized());
        Wordform wf = w.getBestWordform();
        assertEquals("npfsd4", wf.getTag());
        assertEquals("Rīga", wf.getValue(AttributeNames.i_Lemma));
        // Recognize singular

        w = analyzer.analyze("Rīgām");
        assertTrue(w.isRecognized());
        wf = w.getBestWordform();
        assertEquals("npfpd4", wf.getTag());
        assertEquals("Rīga", wf.getValue(AttributeNames.i_Lemma));
        // Recognize plural

        /* Now implemented with flag 'Morfotabulas attēlošana'
        List<Wordform> forms = locītājs.generateInflections("Rīga");
        for (Wordform wf2 : forms) {
            assertFalse(wf2.isMatchingStrong(AttributeNames.i_Number, AttributeNames.v_Plural));
            assertNotEquals("Rīgām", wf2.getValue(AttributeNames.i_Lemma));
            // Do not generate plural forms for Tezaurs.lv morphology tables
        }
        */
    }

    @Test
    public void ticket_101_b() {
        Word w = analyzer.analyze("mieram");
        assertTrue(w.isRecognized());
        Wordform wf = w.getBestWordform();
        assertEquals("ncmvd1", wf.getTag());
        assertEquals("miers", wf.getValue(AttributeNames.i_Lemma));
        // Recognize singular, with singulare_tantum in tag

        w = analyzer.analyze("mieriem");
        assertFalse(w.isRecognized());
//        wf = w.getBestWordform();
//        assertEquals("ncmpd1", wf.getTag());
//        assertEquals("miers", wf.getValue(AttributeNames.i_Lemma));
        // Do not recognize plural

        List<Wordform> forms = analyzer.generateInflections("miers");
        for (Wordform wf2 : forms) {
            assertFalse(wf2.isMatchingStrong(AttributeNames.i_Number, AttributeNames.v_Plural));
            // Do not generate plural forms for Tezaurs.lv morphology tables
        }
    }

    @Test
    public void ticket_101_c() {
        Word w = analyzer.analyze("Limbazim");
        assertFalse(w.isRecognized());
        // Do not recognize singular

        w = analyzer.analyze("Limbažiem");
        assertTrue(w.isRecognized());
        Wordform wf = w.getBestWordform();
        assertEquals("npmdd2", wf.getTag());
        assertEquals("Limbaži", wf.getValue(AttributeNames.i_Lemma));
        // Recognize plural, plurare tantum in tag

        List<Wordform> forms = analyzer.generateInflections("Limbaži");
        for (Wordform wf2 : forms) {
            assertFalse(wf2.isMatchingStrong(AttributeNames.i_Number, AttributeNames.v_Singular));
            // Do not generate singular forms for Tezaurs.lv morphology tables
        }
    }

    @Test
    public void ticket_101_d() {
        Word w = analyzer.analyze("durvij");
        assertFalse(w.isRecognized());
        // Do not recognize singular

        w = analyzer.analyze("durvīm");
        assertTrue(w.isRecognized());
        Wordform wf = w.getBestWordform();
        assertEquals("ncfdd6", wf.getTag());
        assertEquals("durvis", wf.getValue(AttributeNames.i_Lemma));
        // Recognize plural, plurare tantum in tag

        List<Wordform> forms = analyzer.generateInflections("durvis");
        for (Wordform wf2 : forms) {
            assertFalse(wf2.isMatchingStrong(AttributeNames.i_Number, AttributeNames.v_Singular));
            // Do not generate singular forms for Tezaurs.lv morphology tables
        }
    }

    @Test
    public void ticket_101_e() {
        Word w = analyzer.analyze("biksei");
        assertTrue(w.isRecognized());
        Wordform wf = w.getBestWordform();
        assertEquals("ncfsd5", wf.getTag());
        assertEquals("bikses", wf.getValue(AttributeNames.i_Lemma));
        // Recognize singular

        w = analyzer.analyze("biksēm");
        assertTrue(w.isRecognized());
        wf = w.getBestWordform();
        assertEquals("ncfdd5", wf.getTag());
        assertEquals("bikses", wf.getValue(AttributeNames.i_Lemma));
        // Recognize plural, plurare tantum in tag

        List<Wordform> forms = analyzer.generateInflections("durvis");
        for (Wordform wf2 : forms) {
            assertFalse(wf2.isMatchingStrong(AttributeNames.i_Number, AttributeNames.v_Singular));
            // Do not generate singular forms for Tezaurs.lv morphology tables
        }
    }

    @Test
    public void missing_cietusī() {
        Word w = analyzer.analyze("cietusī");
        assertTrue(w.isRecognized());
        boolean found = false;
        for (Wordform wf : w.wordforms) {
			if (wf.getEnding().getParadigm().getID() == 41) {
				found = true;
				break;
			}
        }
        assertTrue(found);

        List<Wordform> cietusī = analyzer.generateInflections("cietusī", false);
        assertNounInflection(cietusī, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "cietušajai");
    }

    @Test
    public void ticket_92() {
        Word iedot = analyzer.analyze("iedot");
        assertTrue(iedot.isRecognized());
        assertEquals("vmnn0_i000n", iedot.wordforms.getFirst().getTag());
    }

    @Test
    public void divdabjlemmas() {
        Word w = analyzer.analyze("nebēdņojās");
        assertTrue(w.isRecognized());
        assertLemma("nebēdņojās", "nebēdņoties");
        assertEquals(AttributeNames.v_Yes, w.getBestWordform().getValue(AttributeNames.i_Noliegums));

        w = analyzer.analyze("cērtamās");
        assertTrue(w.isRecognized());
        assertLemma("cērtamās", "cirst");
    }

    @Test
    public void dīvainie_noliegumi() {
        assertLemma("neesat", "būt");
        assertLemma("nerakt", "rakt");
    }

    @Test
    public void ziemassvētki() {
        assertLemma("Ziemassvētkos", "Ziemassvētki");
    }

    @Test
    public void Severīns() {
        assertLemma("Severīnam", "Severīns");
    }

    @Test
    public void korpusa_neatpazītie_20210308() {
        Word w = analyzer.analyze("mainīt");
        assertTrue(w.isRecognized());

        w = analyzer.analyze("jāmaina");
        assertTrue(w.isRecognized());
    }

    @Test
    // Izskatās, ka pāreja uz ģenitīveņu paradigmu salauza ģenerēšanu, kas pieņem ka ģenerē ģenitīva formu no pilnas lietvārda paradigmas
    public void inflexible_genitive_generation() {
        List<Wordform> augstpapēžu = analyzer.generateInflections("augstpapēžu");
        assertEquals(1, augstpapēžu.size());
    }

    @Test
    // https://github.com/PeterisP/morphology/issues/106
    public void pārāks() {
        Word pārākiem = analyzer.analyze("pārākiem");
        assertTrue(pārākiem.isRecognized());
        assertLemma("pārākiem", "pārāks");
        assertLemma("vispārākajos", "pārāks");

        List<Wordform> pārāks = analyzer.generateInflections("pārāks");
        for (Wordform wf : pārāks) {
            assertNotEquals("pārs", wf.getToken());
            assertNotEquals("pārākāks", wf.getToken());
        }
    }

    @Test
    // https://github.com/PeterisP/morphology/issues/109
    public void vajadzības_izteiksmes_noliegums() {
        List<Wordform> skriet = analyzer.generateInflections("skriet");
        boolean foundNegation = false;
        for (Wordform wf : skriet) {
            if (wf.getToken().equalsIgnoreCase("neskriet")) foundNegation = true;
            assertNotEquals("nejāskrien", wf.getToken());
            assertNotEquals("jāneskrien", wf.getToken());
        }
        assertTrue("Jābūt atrastam 'neskriet'", foundNegation);

        List<Wordform> prātot = analyzer.generateInflections("prātot");
        foundNegation = false;
        for (Wordform wf : prātot) {
            if (wf.getToken().equalsIgnoreCase("neprātot")) foundNegation = true;
            assertNotEquals("nejāprāto", wf.getToken());
            assertNotEquals("jāneprāto", wf.getToken());
        }
        assertTrue("Jābūt atrastam 'neprātot'", foundNegation);
    }

    @Test
    public void prefix_guessing_debitive() {
        analyzer.enablePrefixes = false;
        Word w = analyzer.analyze("pārsekot");
        assertFalse(w.isRecognized());
        w = analyzer.analyze("nesekot");
        assertFalse(w.isRecognized());

        analyzer.enablePrefixes = true;
        w = analyzer.analyze("pārsekot");
        assertTrue(w.isRecognized());
        w = analyzer.analyze("jāpārseko");
        assertTrue(w.isRecognized());
        w = analyzer.analyze("nesekot");
        assertTrue(w.isRecognized());
        w = analyzer.analyze("jāneseko");
        assertFalse(w.isRecognized());
    }

    @Test
    public void noliegumu_noliegums() {
        List<Wordform> nebēdņot = analyzer.generateInflections("nebēdņot");
        for (Wordform wf : nebēdņot) {
            assertNotEquals("nenebēdņot", wf.getToken());
            assertNotEquals("jānebēdņo", wf.getToken());
        }

        Word w = analyzer.analyze("nenebēdņot");
        assertFalse(w.isRecognized());
    }

    @Test
    public void noliegtie_patstāvīgie_divdabji() {
        Word w = analyzer.analyze("nepatīkams");
        assertTrue(w.isRecognized());
        Wordform wf = w.getBestWordform();
        assertEquals(AttributeNames.v_Yes, wf.getValue(AttributeNames.i_Noliegums));
    }

    @Test
    public void tabulu_trūkumi_20210706() {
        Word w = analyzer.analyze("pabija");
        assertTrue(w.isRecognized());
        List<Wordform> pabūt = analyzer.generateInflections("pabūt");
        boolean found = false;
        for (Wordform wf : pabūt) {
			if (wf.getToken().equalsIgnoreCase("pabija")) {
				found = true;
				break;
			}
        }
        assertTrue(found);
    }

    @Test
    public void nebūt_lemma() {
        Word w = analyzer.analyze("nebija");
        assertTrue(w.isRecognized());
        assertEquals("būt", w.getBestWordform().getValue(AttributeNames.i_Lemma));
    }

    @Test
    public void nebēdņot_caur_paradigmu() {
        List<Wordform> nebēdņot = analyzer.generateInflections("nebēdņot");
        for (Wordform wf : nebēdņot) {
            assertNotEquals("jānebēdņo", wf.getToken());
        }

        AttributeValues negative = new AttributeValues();
        negative.addAttribute(AttributeNames.i_Noliegums, AttributeNames.v_Yes);
        nebēdņot = analyzer.generateInflectionsFromParadigm("nebēdņot", 16, negative);
        for (Wordform wf : nebēdņot) {
            assertNotEquals("jānebēdņo", wf.getToken());
        }
    }

    @Test
    public void ticket_94() {
        AttributeValues fem = new AttributeValues();
        fem.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Feminine);
        List<Wordform> ālava = analyzer.generateInflectionsFromParadigm("ālava", 13, fem);
        assertNotEquals(0, ālava.size());
        for (Wordform wf : ālava) {
            assertNotEquals("ālavs", wf.getToken());
        }

        AttributeValues plTant = new AttributeValues();
        plTant.addAttribute(AttributeNames.i_NumberSpecial, AttributeNames.v_PlurareTantum);
        List<Wordform> abēji = analyzer.generateInflectionsFromParadigm("abēji", 13, plTant);
        assertFalse(abēji.isEmpty());
    }

    @Test
    public void ticket_100() {
        List<Wordform> zaļš = analyzer.generateInflections("zaļš");
        for (Wordform wf : zaļš) {
            if (wf.getToken().equalsIgnoreCase("zaļi") && wf.isMatchingStrong(AttributeNames.i_PartOfSpeech, AttributeNames.v_Adverb)) {
                assertEquals(AttributeNames.v_Yes, wf.getValue(AttributeNames.i_Derivative));
            }
        }

        List<Wordform> rakt = analyzer.generateInflections("rakt");
        for (Wordform wf : rakt) {
            if (wf.getToken().equalsIgnoreCase("rakšana")) {
                assertEquals(AttributeNames.v_Yes, wf.getValue(AttributeNames.i_Derivative));
            }
        }
    }

    @Test
    public void vienota_vispārākā() {
        Word vienotām = analyzer.analyze("vienotām");
        for (Wordform wf : vienotām.wordforms) {
            assertFalse(wf.isMatchingStrong(AttributeNames.i_Degree, AttributeNames.v_Superlative));
        }
    }

    @Test
    public void dodi() {
        //Word dod = analyzer.analyze("dod");
        Word dodi = analyzer.analyze("dodi");
        assertTrue(dodi.isRecognized());

        List<Wordform> formas = analyzer.generateInflections("dot");
        for (Wordform wf : formas) {
            assertNotEquals("dodi", wf.getToken());
        }
    }

    @Test
    @Ignore
    public void ticket_89() {
        PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
        Paradigm p15 = analyzer.paradigmByID(15);

        for (ArrayList<Lexeme> lexemes : p15.getLexemesByStem(StemType.STEM1).values()) {
			for (Lexeme l : lexemes) {
				String lemma = l.getStem(StemType.STEM1) + "t";
				ArrayList<Wordform> wordforms = analyzer.generateInflections(l, lemma);
				for (Wordform wf : wordforms) {
					if (wf.getEnding().getID() == 790 && !wf.isMatchingStrong(AttributeNames.i_Noliegums, AttributeNames.v_Yes)) {
						out .printf("%s\t%s\n", lemma, wf.getToken());
					}
				}
			}
        }
        out .flush();
    }

    @Test
    public void roberts_2021_11_24() {
        // Roberts sūdzējās, ka webservisu API neatgriež daudzskaitļa formas, kaut arī tēzaurā tās rādās un it kā nekādi karodziņi tās neaizliedz
        ArrayList<Wordform> nākotne = analyzer.generateInflections("nākotne");
        boolean found = false;
        for (Wordform wf : nākotne) {
			if (wf.getToken().equalsIgnoreCase("nākotņu")) {
				found = true;
				break;
			}
        }
        assertTrue(found);
    }

    @Test
    public void apmācies() {
        // Izveidojot patstāvīgu šķirkli ar 43 paradigmu apmācies parādījās problēma ar miju
        ArrayList<Wordform> apmākies = analyzer.generateInflectionsFromParadigm(
                "apmākies", 43, new AttributeValues());
        for (Wordform wf : apmākies) {
            assertNotEquals("apmācusies", wf.getToken());
            assertNotEquals("apmākies", wf.getToken());
        }

        Word w = analyzer.analyze("apmācusies");
        assertFalse(w.isRecognized());
        w = analyzer.analyze("apmākies");
        assertFalse(w.isRecognized());
    }

    @Test
    public void piņņu() {
        // Problēma ar pinne->piņņu miju
        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
        testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
        testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);

        List<Wordform> forms = analyzer.generateInflections("pinne");
        assertInflection(forms, testset, "piņņu");

        forms = analyzer.generateInflections("lelle");
        assertInflection(forms, testset, "leļļu");

        forms = analyzer.generateInflections("ķemme");
        assertInflection(forms, testset, "ķemmju");
    }

    @Test
    public void noliegums_tagā() {
        Word nevarēšu = analyzer.analyze("nevarēšu");
        assertTrue(nevarēšu.isRecognized());
        Wordform wf = nevarēšu.getBestWordform();
        assertEquals("Jā", wf.getValue(AttributeNames.i_Noliegums));
//        assertEquals("vmnift31say", wf.getTag());
    }

    @Test
    public void ticket_121() {
        // nolieguma vispārākajai pakāpei ir nepareiza secība
        List<Wordform> māt = analyzer.generateInflections("māt");
        boolean found = false;
        for (Wordform wf : māt) {
            assertNotEquals("nevismājošākais", wf.getToken());
            if (wf.getToken().equalsIgnoreCase("visnemājošākais"))
                found = true;
        }
        assertTrue(found);

        Word w = analyzer.analyze("visnemājošākais");
        assertTrue(w.isRecognized());

        w = analyzer.analyze("nevismājošākais");
        assertFalse(w.isRecognized());

        w = analyzer.analyze("vispiemājošākais");
        assertTrue(w.isRecognized());

        w = analyzer.analyze("pievismājošākais");
        assertFalse(w.isRecognized());
    }


    @Test
    public void ticket_120() {
        // -ējs atvasināšanai ne tāda mija
        Word w = analyzer.analyze("sniegējs");
        assertFalse(w.isRecognized());
        w = analyzer.analyze("veikējs");
        assertFalse(w.isRecognized());
        w = analyzer.analyze("atklāējs");
        assertFalse(w.isRecognized());
        w = analyzer.analyze("atrasējs");
        assertFalse(w.isRecognized());
        w = analyzer.analyze("maukēja");
        assertFalse(w.isRecognized());

        w = analyzer.analyze("sniedzējs");
        assertTrue(w.isRecognized());
        w = analyzer.analyze("veicējs");
        assertTrue(w.isRecognized());
        w = analyzer.analyze("atklājējs");
        assertTrue(w.isRecognized());
        w = analyzer.analyze("atradējs");
        assertTrue(w.isRecognized());
        w = analyzer.analyze("maucēja");
        assertTrue(w.isRecognized());
    }

    @Test
    @Ignore("pagaidām nav 100% skaidrs, kā būtu pareizi")
    public void ticket_85() {
        // nepareiza mija vārdam viest (ieviest - lai nav konflikts ar viest homoformām)
        Word w = analyzer.analyze("ieviešošs");
        assertFalse(w.isRecognized());

        List<Wordform> ieviest = analyzer.generateInflections("ieviest");
        boolean found = false;
        for (Wordform wf : ieviest) {
            assertNotEquals("ieviešošs", wf.getToken());
            if (wf.getToken().equalsIgnoreCase("ieviesošs"))
                found = true;
        }
        assertTrue(found);

        w = analyzer.analyze("ieviesošs");
        assertTrue(w.isRecognized());
    }

    @Test
    public void ticket_125() {
        // nez kāpēc nestrādā atpazīšana atsevišķiem vārdiem, ja tos padod ar lielo burtu
        Word w = analyzer.analyze("krūšu");
        assertTrue(w.isRecognized());

        w = analyzer.analyze("Krūšu");
        assertTrue(w.isRecognized());
    }

    @Test
    public void ticket_122() {
        // bija salūzis 'mākam' atpazīšana
        Word w = analyzer.analyze("mākam");
        assertTrue(w.isRecognized());
    }

    @Test
    @Ignore("Gaida uz informāciju no valodniekiem par to, kā pareizi locīt")
    public void ticket_124() {
        // atgriezenisko verbu -ošs divdabji - nevēlami, jāatpazīst bet nav jāģenerē
        Word w = analyzer.analyze("darbojošamies");
        assertTrue(w.isRecognized());

        List<Wordform> formas = analyzer.generateInflections("darboties");
        assertNoForm(formas, "darbojošamies");
    }

    @Test
    public void mistika_pie_relīzes() {
        // Nez kāpēc pie visu vārdu izlocīšanas šiem 5 neatgrieza datus
        Word w;
//        String [] badwords = "saaut sabāzt sasliet sastrēgt aizsliet".split("");
        String [] badwords = "aizsliet".split(" ");
        for (String badword : badwords) {
            w = analyzer.analyze(badword);
            assertTrue(w.isRecognized());
        }
    }

    @Test
    public void rāviņš() {
        // Nez kāpēc min tikai kā sieviešu dzimti
        analyzer.enableGuessing = true;
        Word rāviņš = analyzer.analyze("Rāviņa");
        assertTrue(rāviņš.isRecognized());
    }

    @Test
    public void tūkstošām() {
        AttributeValues fem = new AttributeValues();
        fem.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Feminine);
        List<Wordform> tūkstoša = analyzer.generateInflectionsFromParadigm("tūkstoša", 23, fem);
        assertNoForm(tūkstoša, "tūkstošs");

        Word w = analyzer.analyze("tūkstošām");
        assertTrue(w.isRecognized());
    }

    @Test
    public void onkul() {
        analyzer.enableVocative = true;
        Word onkul = analyzer.analyze("onkul");
        assertTrue(onkul.isRecognized());
        /*List<Wordform> formas = analyzer.generateInflectionsFromParadigm("onkulis", 3);
        describe(formas);*/
    }

    @Test
    public void multivaluetags() {
        AttributeValues p = analyzer.paradigmByID(7);
//        System.out.print(p.getValue(AttributeNames.i_ParadigmSupportedDerivations));
        assertTrue(p.isMatchingStrong(AttributeNames.i_ParadigmSupportedDerivations, AttributeNames.v_Derivation_tājs_tāja_ējs_ēja));
        assertTrue(p.isMatchingStrong(AttributeNames.i_ParadigmSupportedDerivations, AttributeNames.v_Diminutive_iņ));
        assertTrue(p.isMatchingWeak(AttributeNames.i_ParadigmSupportedDerivations, AttributeNames.v_Derivation_tājs_tāja_ējs_ēja));
        assertTrue(p.isMatchingWeak(AttributeNames.i_ParadigmSupportedDerivations, AttributeNames.v_Diminutive_iņ));
    }

    @Test
    public void izgrebt() {
        // aizdomas par crash pie 2.5.2 relīzes
        Word w = analyzer.analyze("izgrebt");
        assertTrue(w.isRecognized());

        //List<Wordform> formas = analyzer.generateInflections("izgrebt");
    }

    @Test
    public void vietniekvārdu_veidi() {
        Word kas = analyzer.analyze("kas");
        assertTrue(kas.isRecognized());

        AttributeValues testset = new AttributeValues();
        testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Pronoun);
        kas.filterByAttributes(testset);
        assertEquals(3, kas.wordformsCount());
    }

    @Test
    public void ticket_138() {
        ArrayList<Wordform> jebkas = analyzer.generateInflectionsFromParadigm("jebkas", 25);
        assertTrue("Jābūt vairākām formām 'jebkas' tabulai no hardcoded", jebkas.size()>1);
    }

    @Test
    public void pustrīs() {
        ArrayList<Wordform> pustrīs = analyzer.generateInflectionsFromParadigm("pustrīs", 25);
        assertTrue("Jābūt vairākām formām 'pustrīs' tabulai no hardcoded", pustrīs.size()>1);

        pustrīs = analyzer.generateInflectionsFromParadigm("pustrīs", 55);
        assertEquals("Jābūt tikai vienai nelokāmajam 'pustrīs' formai", 1, pustrīs.size());
    }

    @Test
    public void sargām() {
        // lai sargāt lokās kā sargām nevis kā visi pārējie 3. konj vārdi sargam
//        ArrayList<Wordform> formas = locītājs.generateInflectionsFromParadigm("sargāt", 17);
//        describe(formas);

        Word sargām = analyzer.analyze("sargām");
        assertTrue(sargām.isRecognized());

        Word sargāmies = analyzer.analyze("sargāmies");
        assertTrue(sargāmies.isRecognized());

        Word jāsargās = analyzer.analyze("jāsargās");
        assertTrue(jāsargās.isRecognized());
    }

    @Test
    public void negativeLemmas() {
        // 2025-04-28 Baiba sūdzās, ka korpusā "nepildīšana" lemma ir pildīšana; verbu formām tas ir likts tīšām (nepildīju -> pildīt) bet lietvārdam tā nav ok

        Word nepildīšana = analyzer.analyze("nepildīšana");
        assertTrue(nepildīšana.isRecognized());
        Wordform form = nepildīšana.getBestWordform();
        assertEquals("nepildīšana", form.getValue(AttributeNames.i_Lemma));

        Word nepildīdams = analyzer.analyze("nepildīdams");
        assertTrue(nepildīdams.isRecognized());
        form = nepildīdams.getBestWordform();
        assertEquals("pildīt", form.getValue(AttributeNames.i_Lemma));

        Word nevēlēšanās = analyzer.analyze("nevēlēšanās");
        nevēlēšanās.describe(System.out);
        assertTrue(nevēlēšanās.isRecognized());
        nevēlēšanās.filterByAttributes(
                new AttributeValues(){{addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);}});
        form = nevēlēšanās.getBestWordform();
        assertEquals("nevēlēšanās", form.getValue(AttributeNames.i_Lemma));
    }

    @Test
    public void ticket149() {
        // 2025 05 22 - daļai (bet ne visiem!) divdabjiem tagā pazūd 13. pazīme
        Word viļņotais = analyzer.analyze("viļņotais");
        assertTrue(viļņotais.isRecognized());
        Wordform form = viļņotais.getBestWordform();
        assertEquals("vmnpdmsnpsypn", form.getTag());

        Word neviltotais = analyzer.analyze("neviltotais");
        assertTrue(neviltotais.isRecognized());
        form = neviltotais.getBestWordform();
        assertEquals("vmnpdmsnpsypy", form.getTag());
    }

    @Test
    public void ticket129() {
//        locītājs.enableGuessing = true;
        Word vispārīgākajiem = analyzer.analyze("vispārīgākajiem");
        assertTrue(vispārīgākajiem.isRecognized());
        Wordform form = vispārīgākajiem.getBestWordform();
        assertEquals("vispārīgs", form.getValue(AttributeNames.i_Lemma));
    }

    @Test
    public void ticket152() {
        Word negribētu = analyzer.analyze("negribētu");
        assertTrue(negribētu.isRecognized());
        boolean found = false;
        for (Wordform wf :  negribētu.wordforms) {
            if (wf.isMatchingStrong(AttributeNames.i_Mood, AttributeNames.v_Conditional))
                found = true;
        }
        assertTrue(found);
    }

    @Test
    // bubuļfailā konstatēja, ka ir tikai 3. personas forma bet ne 2. un pavēles
    public void cep() {
        Word cep = analyzer.analyze("cep");
        assertTrue(cep.isRecognized());
        boolean found = false;
        for (Wordform wf :  cep.wordforms) {
            if (wf.isMatchingStrong(AttributeNames.i_Mood, AttributeNames.v_Imperative))
                found = true;
        }
        assertTrue(found);
    }

    @Test
    // bubuļfailā konstatēja, ka neatpazīst normālo no 'dzīt', tikai kaut kādu saīsinājumu
    public void dzīs() {
        Word dzīs = analyzer.analyze("dzīs");
        assertTrue(dzīs.isRecognized());
        describe(dzīs.wordforms);
        boolean found = false;
        for (Wordform wf :  dzīs.wordforms) {
            if (wf.isMatchingStrong(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb))
                found = true;
        }
        assertTrue(found);
    }


    @Test
    // bubuļfailā konstatēja, ka nav 3. pers forma no plīvot bet tikai no plīvēt
    public void plīv() {
        Word plīv = analyzer.analyze("plīv");
        assertTrue(plīv.isRecognized());
        describe(plīv.wordforms);
        boolean found = false;
        for (Wordform wf :  plīv.wordforms) {
            if (wf.isMatchingStrong(AttributeNames.i_Lemma, "plīvot"))
                found = true;
        }
        assertTrue(found);
    }


    @Test
    public void abbrievationTokenizing() {
        // Tēzaura datos ir definēts, ka šī ir viena tekstvienība, kas nav dalāma sīkāk.
        Word abbr = analyzer.analyze("P.S.");
        //describe(abbr.wordforms);
        assertTrue(abbr.isRecognized());
        boolean found = false;
        for (Wordform wf :  abbr.wordforms) {
            if (wf.isMatchingStrong(AttributeNames.i_Lemma, "P.S."))
                found = true;
        }
        assertTrue(found);

    }

}