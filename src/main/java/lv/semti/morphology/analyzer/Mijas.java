/*******************************************************************************
 * Copyright 2008, 2009, 2014, 2024-2026
 * Institute of Mathematics and Computer Science, University of Latvia
 * Author: Pēteris Paikens, Lauma Pretkalniņa
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package lv.semti.morphology.analyzer;

import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lv.semti.morphology.attributes.AttributeNames;
import lv.semti.morphology.attributes.AttributeValues;


/**
 * This class is responsible for implementing the most complex part of the
 * Latvian and Latgalian morphological analysis and synthesis -- various rulles
 * on how stems are changed for various wordforms.
 * Latvian word "Mija" is kept to describe this phenomenon through the code on
 * purpose: for one, because English lacks term with comparatively wide scope,
 * but also as a tribute to the fact that originally all class and variable
 * names was in Latvian as a test for then new Unicode capabilities.
 */
public abstract class Mijas {
	/**
	 * Procedure who actually does all the stem changes to get lemma form any
	 * given form: consonant changes, verbs forms, devitives, superlatives, etc.
	 */
	public static ArrayList<StemVariant> applyFormToLemmaMija(String stem, int stemChange, boolean properName) {
		// TODO - iznest 'stemVariants.add(new Variants(... kā miniprocedūriņu.
		// TODO - iekļaut galotnē(?) kā metodi

		ArrayList<StemVariant> stemVariants = new ArrayList<>(1);
		if (stem.trim().isEmpty()) return stemVariants;

		int mija;
		String resultStem;

		try {
			switch (stemChange) { //TODO - uz normālāku struktūru
				case 4: // vajadzības izteiksmes jā-
					if (stem.startsWith("jā") && stem.length() >= 4) {
						resultStem = stem.substring(2);
						mija = 0;
					} else return stemVariants;
					break;
				case 5: // vajadzības izteiksme 3. konjugācijai bez mijas
					if (stem.startsWith("jā") && stem.length() >= 4) {
						resultStem = stem.substring(2);
						mija = 9;
					} else return stemVariants;
					break;
				case 12: // vajadzības izteiksme 3. konjugācijai atgriezeniskai bez mijas
					if (stem.startsWith("jā") && stem.length() >= 4) {
						resultStem = stem.substring(2);
						mija = 8;
					} else return stemVariants;
					break;
				case 19: // vajadzības_vēlējuma izteiksme 3. konjugācijai bez mijas (jāmācot)
					if (stem.startsWith("jā") && stem.length() >= 4) {
						resultStem = stem.substring(2);
						mija = 2;
					} else return stemVariants;
					break;
				case 28: // vajadzības_vēlējuma izteiksme 3. konjugācijai ar miju (jāmākot)
					if (stem.startsWith("jā") && stem.length() >= 4) {
						resultStem = stem.substring(2);
						mija = 20;
					} else return stemVariants;
					break;
				case 29: // vajadzības izteiksme 3. konjugācijai atgriezeniskai ar miju
					if (stem.startsWith("jā") && stem.length() >= 4) {
						resultStem = stem.substring(2);
						mija = 27;
					} else return stemVariants;
					break;
				case 31: // vajadzības izteiksme 3. konjugācijai ar miju
					if (stem.startsWith("jā") && stem.length() >= 4) {
						resultStem = stem.substring(2);
						mija = 30;
					} else return stemVariants;
					break;
				case 37: // vajadzības izteiksme 1. konjugācijai ar miju (jāiet)
					if (stem.startsWith("jā") && stem.length() >= 4) {
						resultStem = stem.substring(2);
						mija = 36;
					} else return stemVariants;
					break;
				// latgalieši
				case 150: // vajadzības izteiksme 2. konjugācijai
					if (stem.startsWith("juo") && stem.length() >= 5) {
						resultStem = stem.substring(3);
						mija = 110;
					} else return stemVariants;
					break;
				case 151: // vajadzības izteiksme 3. konjugācijai -eit ar patskaņu miju bez līdzskaņu mijas
					if (stem.startsWith("juo") && stem.length() >= 5) {
						resultStem = ltgVowelMijaFormToLemma(stem.substring(3));
						mija = 119;
					} else return stemVariants;
					break;
				case 152: // vajadzības izteiksme 3. konjugācijai -eit ar patskaņu miju ar līdzskaņu miju
					if (stem.startsWith("juo") && stem.length() >= 5) {
						resultStem = ltgVowelMijaFormToLemma(stem.substring(3));
						mija = 122;
					} else return stemVariants;
					break;
				case 153: // vajadzības izteiksme 3. konjugācijai -ēt ar burtu miju bez līdzskaņu un patskaņu mijas
					if (stem.startsWith("juo") && stem.length() >= 5) {
						resultStem = stem.substring(3);
						mija = 125;
					} else return stemVariants;
					break;
				case 160: // patskaņu mija 2. konjugācijas supīnam, vēlējuma izteiksmei un pagātnes divdabim
					resultStem = ltgVowelMijaFormToLemma(stem);
					mija = 114;
					break;
				case 161: // patskaņu mija 2. konjugācijas pagātnes divdabja pārākajai un vispārākajai pakāpei
					resultStem = ltgVowelMijaFormToLemma(stem);
					mija = 116;
					break;
				case 162: // patskaņu mija 3. konjugācijas standarta -eit tagadnei bez līdzskaņu mijas
					resultStem = ltgVowelMijaFormToLemma(stem);
					mija = 119;
					break;
				case 163: // patskaņu mija 3. konjugācijas standarta -eit bez līdzskaņu mijas tagadnes divdabja pārākajai un vispārākajai pakāpei
					resultStem = ltgVowelMijaFormToLemma(stem);
					mija = 120;
					break;
				case 164: // patskaņu mija 3. konjugācijas standarta -eit tagadnei ar līdzskaņu miju
					resultStem = ltgVowelMijaFormToLemma(stem);
					mija = 122;
					break;
				case 165: // patskaņu mija 3. konjugācijas standarta -eit līdzskaņu mijas tagadnes divdabja pārākajai un vispārākajai pakāpei
					resultStem = ltgVowelMijaFormToLemma(stem);
					mija = 123;
					break;
				case 166: // patskaņu mija 3. konjugācijas standarta -ēt 1. pers. tagadnei bez līdzskaņu un burtu mijas
					resultStem = ltgVowelMijaFormToLemma(stem);
					mija = 124;
					break;
				case 167: // patskaņu mija 3. konjugācijas standarta -ēt bez līdzskaņu un burtu mijas tagadnes divdabja pārākajai un vispārākajai pakāpei
					resultStem = ltgVowelMijaFormToLemma(stem);
					mija = 126;
					break;
				default:
					resultStem = stem;
					mija = stemChange;
			}


			switch (mija) {
				case 0: stemVariants.add(new StemVariant(resultStem)); break;  // nav mijas

				case 1: // lietvārdu līdzskaņu mija
					// sākam ar izņēmumgadījumiem.
					if (resultStem.endsWith("š")) {
						if (resultStem.endsWith("kš")) {
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "kst", "Mija", "kst -> kš"));
						}
						if (resultStem.endsWith("nš")) {
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "nst", "Mija", "nst -> nš"));
						}
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "s", "Mija", "s -> š"));
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "t", "Mija", "t -> š"));
					}
					else if (resultStem.endsWith("ž")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"z","Mija","z -> ž"));
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"d","Mija","d -> ž"));
					}
					// ... dž <> dd ?????
					else if (resultStem.endsWith("č")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"c","Mija","c -> č"));}
					else if (resultStem.endsWith("ļ")) {
						if (resultStem.endsWith("šļ")) {
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"sl","Mija","sl -> šļ"));}
						else if (resultStem.endsWith("žļ")) {
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"zl","Mija","zl -> žļ"));}
						else if (resultStem.endsWith("ļļ")) {
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"ll","Mija","ll -> ļļ"));}
						else stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"l","Mija","l -> ļ"));
					}
					else if (resultStem.endsWith("ņ")) {
						if (resultStem.endsWith("šņ")) {
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"sn","Mija","sn -> šņ"));}
						else if (resultStem.endsWith("žņ")) {
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"zn","Mija","zn -> žņ"));}
						else if (resultStem.endsWith("ļņ")) {
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"ln","Mija","ln -> ļņ"));}
						else if (resultStem.endsWith("ņņ")) {
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"nn","Mija","nn -> ņņ"));}
						else if (!(resultStem.endsWith("zņ") || resultStem.endsWith("sņ") || resultStem.endsWith("lņ") || resultStem.endsWith("ņņ"))) {
							//stemVariants.add(new Variants(resultStem.substring(0,resultStem.length()-1)+"l","Mija", "l -> ņ ??"));
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"n","Mija", "n -> ņ"));
						}
					}
					else if (resultStem.endsWith("j")) {
						if (resultStem.endsWith("pj") || resultStem.endsWith("bj") || resultStem.endsWith("mj") || resultStem.endsWith("vj")
								|| resultStem.endsWith("fj")) { 	//	 ... nj <> n ??
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1),"Mija","p->pj (u.c.)"));
						/*} else if (resultStem.endsWith("fj")) { // žirafju -> žirafe; žirafu->žirafe
							stemVariants.add(new Variants(resultStem.substring(0,resultStem.length()-1),"Mija","p->pj (u.c.)"));
							stemVariants.add(new Variants(resultStem));*/
						} else stemVariants.add(new StemVariant(resultStem));
					}
					else if (!(resultStem.endsWith("p") || resultStem.endsWith("b") || resultStem.endsWith("m") || resultStem.endsWith("v") ||
							resultStem.endsWith("t") || resultStem.endsWith("d") || resultStem.endsWith("c") || resultStem.endsWith("z") ||
							resultStem.endsWith("s") || resultStem.endsWith("n") || resultStem.endsWith("l") || resultStem.endsWith("f")) )
						stemVariants.add(new StemVariant(resultStem));
					break;
				case 2: //  dv. 3. konjugācijas (bezmiju!) formas, kas noņem celma pēdējo burtu
					stemVariants.add(new StemVariant(resultStem +"ā"));
					stemVariants.add(new StemVariant(resultStem +"ī"));
					stemVariants.add(new StemVariant(resultStem +"ē"));
					break;
				case 3: // īpašības vārdiem -āk- un vis-
					if (resultStem.endsWith("āk") && resultStem.length() > 3) {
						if (resultStem.startsWith("vis")) stemVariants.add(new StemVariant(resultStem.substring(3, resultStem.length()-2),AttributeNames.i_Degree,AttributeNames.v_Superlative));
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2),AttributeNames.i_Degree,AttributeNames.v_Comparative));
					}
					stemVariants.add(new StemVariant(resultStem,AttributeNames.i_Degree, AttributeNames.v_Positive));
					break;
				case 6: // 1. konjugācijas nākotne
					if (resultStem.endsWith("dī") || resultStem.endsWith("tī") || resultStem.endsWith("sī")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"s"));
					else if (resultStem.endsWith("šī")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1))); // lūzt, griezt
					else if (resultStem.endsWith("zī")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1))); // lūzt, griezt
						stemVariants.add(new StemVariant(resultStem)); // atzīšos
					}
					else if (!resultStem.endsWith("d") && !resultStem.endsWith("t") && !resultStem.endsWith("s") && !resultStem.endsWith("z")) stemVariants.add(new StemVariant(resultStem));
					break;
				case 7: // 1. konjugācijas 2. personas tagadne
				case 23: // 1. konjugācijas 2. personas tagadne - ja pēc tam seko garā galotne kā -iet
					if (resultStem.endsWith("s")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"š"));   //pievēršu -> pievērs
						stemVariants.add(new StemVariant(resultStem));   //  atnest -> atnes
					}
					else if ((mija == 7) && (resultStem.endsWith("odi") || resultStem.endsWith("ūdi") || resultStem.endsWith("opi") || resultStem.endsWith("ūpi") ||
							resultStem.endsWith("oti") || resultStem.endsWith("ūti") || resultStem.endsWith("īti") || resultStem.endsWith("ieti") || resultStem.endsWith("sti")))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)));
					else if ((mija == 23) && (resultStem.endsWith("od") || resultStem.endsWith("ūd") || resultStem.endsWith("op") || resultStem.endsWith("ūp") ||
							resultStem.endsWith("ot") || resultStem.endsWith("ūt") || resultStem.endsWith("īt") || resultStem.endsWith("st")))
						stemVariants.add(new StemVariant(resultStem));
					else if (resultStem.endsWith("t")) {
						// tikai vārdiem 'mest' un 'cirst'. pārējiem visiem 2. personas tagadnei jābūt galā -i, piem. 'krīti', 'plūsti'
						if (resultStem.endsWith("met") || resultStem.endsWith("cērt")) stemVariants.add(new StemVariant(resultStem));
						else stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"š"));  // pūšu -> pūt, ciešu -> ciet
					}
					else if (resultStem.endsWith("d")) {
						//tikai attiecīgajiem vārdiem, pārējiem visiem 2. personas tagadnei jābūt galā -i, piem. 'pazūdi', 'atrodi'
						if (resultStem.endsWith("dod") || resultStem.endsWith("ved") || (resultStem.endsWith("ēd") && !resultStem.endsWith("sēd")) )
							stemVariants.add(new StemVariant(resultStem));
						else stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"ž"));  // kožu -> kod
					}
					else if (resultStem.endsWith("l")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"ļ"));
					else if (!resultStem.endsWith("ņem") && (resultStem.endsWith("m") || resultStem.endsWith("b")))	stemVariants.add(new StemVariant(resultStem +"j")); //stumju -> stum
					else if (resultStem.endsWith("p")) {
						stemVariants.add(new StemVariant(resultStem)); // cep -> cep
						stemVariants.add(new StemVariant(resultStem +"j")); // cepj -> cep
					}
					else if (resultStem.endsWith("c")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"k"));  // raku -> racis
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"c"));  // veicu -> veicis
					}
//					else if (resultStem.endsWith("dz")) stemVariants.add(new Variants(resultStem.substring(0,resultStem.length()-2)+"g"));
					else if (resultStem.endsWith("z") && !resultStem.endsWith("dz")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"ž"));

						//TODO - šī nākamā rinda ir jātestē vai tok visos gadījumos. pielikta, jo "rok" overģenerēja kā arī bez mijas.
					else if (!(resultStem.endsWith("š") || resultStem.endsWith("ž") || resultStem.endsWith("ļ") //|| resultStem.endsWith("j")
							|| resultStem.endsWith("k") || resultStem.endsWith("g") ))
						stemVariants.add(new StemVariant(resultStem));
					break;
				case 8: // -ams -āms 3. konjugācijai bezmiju gadījumam, un arī mēs/jūs formas
					if (resultStem.endsWith("inā") || resultStem.endsWith("sargā")) stemVariants.add(new StemVariant(resultStem)); // nav else, jo piemēram vārdam "mainās" arī ir beigās -inās, bet tam vajag -īties likumu;
					if (resultStem.endsWith("ā")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"ī"));
					if (resultStem.endsWith("a")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"ē"));
						if (!resultStem.endsWith("ina") && !resultStem.endsWith("sarga")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"ā"));
					}
					break;
				case 9: // 3. konjugācija 3. pers. tagadne bez mijas
					if (resultStem.endsWith("ina") || resultStem.endsWith("sarga")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"ā")); // nav else, jo piemēram vārdam "jāmaina" arī ir beigās -ina, bet tam vajag -a nevis -ina likumu;
					if (resultStem.endsWith("a")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"ī"));
					else {
						stemVariants.add(new StemVariant(resultStem +"ē")); // if (resultStem.endsWith("i")) stemVariants.add(resultStem.substring(0,resultStem.length()-1)+"ē");
						stemVariants.add(new StemVariant(resultStem +"ā"));
						stemVariants.add(new StemVariant(resultStem +"o")); // plīvot -> plīv
					}
					break;
				case 10: // īpašības vārds -āk- un vis-, -i apstākļa formai
					if (resultStem.endsWith("i")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1),AttributeNames.i_Degree, AttributeNames.v_Positive));
					if (resultStem.endsWith("āk")) {
						if (resultStem.startsWith("vis")) stemVariants.add(new StemVariant(resultStem.substring(3, resultStem.length()-2),AttributeNames.i_Degree,AttributeNames.v_Superlative));
						else stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2),AttributeNames.i_Degree,AttributeNames.v_Comparative));
					}
					break;
				case 11: // -uša, arī mijas pie 1. konj noteiktās formas: veicu -> veikušais, beidzu->beigušais; raku -> rakušais; sarūgu -> sarūgušais;
					if (!resultStem.endsWith("c") && !resultStem.endsWith("dz")) {
						stemVariants.add(new StemVariant(resultStem));
					}
					if (resultStem.endsWith("k")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"c"));
					if (resultStem.endsWith("g")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"dz"));
					break;
				case 13: // īpašības vārdiem -āk- un vis-, ar š->s nominatīva formā (zaļš -> zaļāks) ?? Lexicon.xml izskatās tikai pēc apstākļvārdu atvasināšanas?? FIXME, nešķiet tīri
					if (resultStem.endsWith("āk")) {
						if (resultStem.startsWith("vis")) stemVariants.add(new StemVariant(resultStem.substring(3, resultStem.length()-2),AttributeNames.i_Degree,AttributeNames.v_Superlative)); // FIXME te arī jāskatās vai ir -āk
						else stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2),AttributeNames.i_Degree,AttributeNames.v_Comparative));
					}
					break;
				case 14: // 1. konjugācijas "-is" forma
					if (resultStem.endsWith("c")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"k"));  // raku -> racis
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"c"));  // veicu -> veicis
					}
					else if (resultStem.endsWith("dz")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"g")); // sarūgu -> sarūdzis
						stemVariants.add(new StemVariant(resultStem)); // lūdzu -> lūdzis
					}
					else stemVariants.add(new StemVariant(resultStem));
					break;
				case 15: // pūst->pūzdams nopūzdamies s -> z mija
					stemVariants.add(new StemVariant(resultStem));  // šis pievienos arī pūst -> pūsdams; taču to pēc tam atpakaļlocīšana (kam būs info par pagātnes celmu) nofiltrēs
					if (resultStem.endsWith("z")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"s"));
					}
					break;
				case 16: // 1. konjugācijas "-šana" atvasināšana
					if (!resultStem.endsWith("s") && !resultStem.endsWith("z")) {
						stemVariants.add(new StemVariant(resultStem));
						stemVariants.add(new StemVariant(resultStem +"s"));  // nest -> nešana
						stemVariants.add(new StemVariant(resultStem +"z"));  // mēzt -> mēšana
					}
					break;
				case 17: // īsā sieviešu dzimtes vokatīva forma "kristīnīt!" "margriet!"
					if (syllables(resultStem) >= 2 || resultStem.endsWith("iņ") || resultStem.endsWith("īt"))
						stemVariants.add(new StemVariant(resultStem));
					break;
				/*
				// 2025-06-12 Baiba izskaidro, ka pēc mūsdienu latviešu valodas
				// normām visiem 4. un 5. deklinācijas vārdiem pienākas viens
				// vienskaitļa vokatīvs, kas sakrīt vienskaitļa nominatīvu
				case 18: // garā sieviešu dzimtes vokatīva forma "laura!" "margrieta!"
					if (syllables(resultStem) <= 2 && !resultStem.endsWith("iņ") && !resultStem.endsWith("īt"))
						stemVariants.add(new Variants(resultStem));
					if (syllables(resultStem) > 1 && (resultStem.endsWith("kāj") || resultStem.endsWith("māj")))
						stemVariants.add(new Variants(resultStem));
					break;*/
				case 20: //  dv. 3. konjugācijas tagadnes mija 1. personas tagadnei, -ot divdabim un vajadzībai - atšķiras no 26. mijas 'gulēt' un 'tecēt'
					if (resultStem.endsWith("guļ") || resultStem.endsWith("gul")) // FIXME - dēļ 'gulošs' pieļaujam formu 'es gulu' ????  FIXME - Varbūt jāņem ārā, jo tagad ir divas paradigmas
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"lē")); //gulēt -> guļošs un arī gulošs
					if (resultStem.endsWith("k")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "cī")); //sacīt -> saku
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"cē")); //mācēt -> māku
					} else if (resultStem.endsWith("g") ) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "dzī")); //slodzīt -> slogu
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"dzē")); //vajadzēt -> vajag
					} else if (resultStem.endsWith("ž") ) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "dē")); //sēdēt -> sēžu
					}
					if (resultStem.endsWith("loc") || resultStem.endsWith("moc") || resultStem.endsWith("urc"))
						stemVariants.add(new StemVariant(resultStem +"ī")); // alternatīvā forma
					break;
				case 21: // -is -ušais pārākā un vispārākā pakāpe - visizkusušākais saldējums
					if (resultStem.startsWith("vis")) {
						stemVariants.add(new StemVariant(resultStem.substring(3), AttributeNames.i_Degree, AttributeNames.v_Superlative));
					} else {
						stemVariants.add(new StemVariant(resultStem, AttributeNames.i_Degree, AttributeNames.v_Comparative));
					}
					break;
				case 22: // jaundzimušais -> jaundzimusī
					if (resultStem.endsWith("us"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"uš"));
					break;
				case 24: //  analoģiski case 2, bet ar pārāko / vispārāko pakāpi - visizsakošākais
					String pakāpe = AttributeNames.v_Comparative;
					if (resultStem.startsWith("vis")) {
						pakāpe = AttributeNames.v_Superlative;
						resultStem = resultStem.substring(3);
					}
					stemVariants.add(new StemVariant(resultStem +"ā", AttributeNames.i_Degree, pakāpe));
					stemVariants.add(new StemVariant(resultStem +"ī", AttributeNames.i_Degree, pakāpe));
					stemVariants.add(new StemVariant(resultStem +"ē", AttributeNames.i_Degree, pakāpe));
					break;
				case 25: // analoģiski #8, bet ar pārākajām pakāpēm priekš -amāks formām
					pakāpe = AttributeNames.v_Comparative;
					if (resultStem.startsWith("vis")) {
						pakāpe = AttributeNames.v_Superlative;
						resultStem = resultStem.substring(3);
					}
					if (resultStem.endsWith("inā") || resultStem.endsWith("sargā")) stemVariants.add(new StemVariant(resultStem, AttributeNames.i_Degree, pakāpe)); // nav else, jo piemēram vārdam "mainās" arī ir beigās -inās, bet tam vajag -īties likumu;
					if (resultStem.endsWith("ā")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"ī", AttributeNames.i_Degree, pakāpe));
					else if (resultStem.endsWith("a")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"ē", AttributeNames.i_Degree, pakāpe));
						if (!resultStem.endsWith("ina") && !resultStem.endsWith("sarga")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"ā", AttributeNames.i_Degree, pakāpe));
					}
					break;
				case 26: //  dv. 3. konjugācijas miju gadījuma formas - otrās personas tagadne, pavēles izteiksme
					if (resultStem.endsWith("gul"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"lē")); // guli -> gulēt
					if (resultStem.endsWith("tec")) {
						stemVariants.add(new StemVariant(resultStem +"ē")); // teci -> tecēt
					} else if (resultStem.endsWith("k") && !resultStem.endsWith("tek")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "cī")); // saki -> sacīt
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"cē")); //māki -> mācēt
					} else if (resultStem.endsWith("g") ) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "dzī")); //slogi -> slodzīt
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"dzē")); //vajag -> vajadzēt
					} else if (resultStem.endsWith("loc") || resultStem.endsWith("moc") || resultStem.endsWith("urc"))
						stemVariants.add(new StemVariant(resultStem +"ī")); // alternatīvā forma
					else stemVariants.add(new StemVariant(resultStem +"ē")); // sēdies -> sēdēties
					break;
				case 27: // -ams -āms 3. konjugācijai miju gadījumam, un arī mēs/jūs formas
					if (resultStem.endsWith("kā"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"cī")); //sacīt
					else if (resultStem.endsWith("gā") )
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"dzī")); //slodzīt -> slogu
					else if (resultStem.endsWith("ka") )
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"cē")); //mācēt -> mākam
					else if (resultStem.endsWith("ža") )
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"dē")); //sēdēt -> sēžam
					else if (resultStem.endsWith("ļa")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"lē")); //gulēt -> guļam
					else if (resultStem.endsWith("ga")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"dzē")); //vajadzēt -> vajag
					break;
				case 30: // 3. konjugācija 3. pers. tagadne ar miju
					if (resultStem.endsWith("vajadz")) break; //izņēmums - lai korekti ir 'vajadzēt' -> 'vajag'
					else if (resultStem.endsWith("ka") )
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"cī")); //sacīt
					else if (resultStem.endsWith("ga"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"dzī")); //slodzīt -> sloga
					else if (resultStem.endsWith("k") )
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"cē")); //mācēt -> māk
					else if (resultStem.endsWith("ž") )
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"dē")); //sēdēt -> sēž
					else if (resultStem.endsWith("ļ")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"lē")); // "guļ"->"gulēt"
					else if (resultStem.endsWith("vajag")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"dzē")); //vajadzēt -> vajag
					break;
				case 32: //  analoģiski case 20, bet ar pārāko / vispārāko pakāpi - visizsakošākais
					pakāpe = AttributeNames.v_Comparative;
					if (resultStem.startsWith("vis")) {
						pakāpe = AttributeNames.v_Superlative;
						resultStem = resultStem.substring(3);
					}

					if (resultStem.endsWith("k") ) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "cī", AttributeNames.i_Degree, pakāpe)); //sacīt -> sakošākais
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"cē", AttributeNames.i_Degree, pakāpe)); //mācēt -> mākošākais
					} else if (resultStem.endsWith("g")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "dzī", AttributeNames.i_Degree, pakāpe)); //slodzīt -> slogošākais
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "dzē", AttributeNames.i_Degree, pakāpe)); //vajadzēt -> vajagošākais
					} else if (resultStem.endsWith("ž") ) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "dē", AttributeNames.i_Degree, pakāpe)); //sēdēt -> sēžu
					} else if (resultStem.endsWith("ļ")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"lē", AttributeNames.i_Degree, pakāpe)); //gulēt -> guļošākais un arī gulošākais
					break;
				case 33: // analoģiski #27, bet ar pārākajām pakāpēm priekš -amāks formām
					pakāpe = AttributeNames.v_Comparative;
					if (resultStem.startsWith("vis")) {
						pakāpe = AttributeNames.v_Superlative;
						resultStem = resultStem.substring(3);
					}
					if (resultStem.endsWith("kā"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"cī", AttributeNames.i_Degree, pakāpe)); //sacīt
					else if (resultStem.endsWith("gā"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"dzī", AttributeNames.i_Degree, pakāpe)); //slodzīt -> slogu
					else if (resultStem.endsWith("ka"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"cē", AttributeNames.i_Degree, pakāpe)); //mācēt -> mākam
					else if (resultStem.endsWith("ga"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"dzē", AttributeNames.i_Degree, pakāpe)); //vajadzēt -> vajag
					else if (resultStem.endsWith("ža") )
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"dē", AttributeNames.i_Degree, pakāpe)); //sēdēt -> sēžam
					else if (resultStem.endsWith("guļa"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"lē", AttributeNames.i_Degree, pakāpe)); //gulēt -> guļam
					break;
				case 34: // īpašības vārdiem -āk- un vis- izskaņām kā -ajam: liekam nevis zaļ-š->zaļ-ajam, bet zaļ-š->zaļ-a-jam, bet pēdēj-ais -> pēdē-jam/pēdēj-a-jam
					if (resultStem.endsWith("āka") && resultStem.length() > 4) {
						if (resultStem.startsWith("vis")) stemVariants.add(new StemVariant(resultStem.substring(3, resultStem.length()-3),AttributeNames.i_Degree,AttributeNames.v_Superlative));
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-3),AttributeNames.i_Degree,AttributeNames.v_Comparative)); // nav else, jo ir vārdi kas reāli sākas ar 'vis' kā vispārīgs utt
					}
					if (resultStem.endsWith("a")) // zaļa-jam -> zaļ; pēdēja-jam -> pēdēj
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1) ,AttributeNames.i_Degree, AttributeNames.v_Positive));
					else if (resultStem.endsWith("ē")) // pēdē-jam -> pēdēj
						stemVariants.add(new StemVariant(resultStem +"j",AttributeNames.i_Degree, AttributeNames.v_Positive));
					break;
				case 35: // substantivizējušos "īpašības vārdu" izskaņas kā -ajam: liekam nevis zaļ-š->zaļ-ajam, bet zaļ-š->zaļ-a-jam, bet pēdēj-ais -> pēdē-jam/pēdēj-a-jam; bez pārākās/vispārākās pakāpes
					if (resultStem.endsWith("a")) // zaļa-jam -> zaļ; pēdēja-jam -> pēdēj
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1) ,AttributeNames.i_Degree, AttributeNames.v_Positive));
					else if (resultStem.endsWith("ē")) // pēdē-jam -> pēdēj
						stemVariants.add(new StemVariant(resultStem +"j",AttributeNames.i_Degree, AttributeNames.v_Positive));
					// citiem pareiziem variantiem IMHO te nevajadzētu būt
					break;
				case 36: // 'iet' speciālgadījums - normāli 3. personas tagadnei atbilstošais resultStem būtu 'ej', bet ir 'iet'.
					stemVariants.add(new StemVariant(resultStem));
					if (resultStem.endsWith("iet"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-3)+"ej"));
					break;
				case 38: // Apstākļa vārdi ar gradāciju
					if (resultStem.endsWith("āk") && resultStem.length() > 3) {
						if (resultStem.startsWith("vis")) {
							stemVariants.add(new StemVariant(resultStem.substring(3, resultStem.length()-2),AttributeNames.i_Degree,AttributeNames.v_Superlative));
							stemVariants.add(new StemVariant(resultStem.substring(3, resultStem.length()-2) + "i",AttributeNames.i_Degree,AttributeNames.v_Superlative));
							stemVariants.add(new StemVariant(resultStem.substring(3, resultStem.length()-2) + "u",AttributeNames.i_Degree,AttributeNames.v_Superlative));
						} else {
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2),AttributeNames.i_Degree,AttributeNames.v_Comparative));
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2) + "i",AttributeNames.i_Degree,AttributeNames.v_Comparative));
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2) + "u",AttributeNames.i_Degree,AttributeNames.v_Comparative));
						}
					} else stemVariants.add(new StemVariant(resultStem,AttributeNames.i_Degree, AttributeNames.v_Positive));
					break;

				// ------ LATGALIAN from here -----
				case 99: // puse no latgaliešu 'burtu mijas' (case 100) - tikai paradigmām, kur ir garantēts, ka bezmijas resultStem beidzas mīkstu līdzskani
					stemVariants.add(new StemVariant(ltgLetterMijaHardToSoftUnambiguous(resultStem), "Mija", "ļņķģ -> lnkg"));
					break;
				case 100: // latgaliešu 'burtu mija', kur pirms -e, -i, -ī, -ē, -ie ļ, ņ, ķ, ģ kļūst par l, n, k, g
					String ltgBurtuMijasCelms = ltgLetterMijaHardToSoftUnambiguous(resultStem);
					stemVariants.add(new StemVariant(ltgBurtuMijasCelms, "Mija", "lnkg -> lļnņkķgģ"));
					if (!ltgBurtuMijasCelms.equals(resultStem))
						stemVariants.add(new StemVariant(resultStem, "Mija", "lnkg -> lļnņkķgģ"));
					break;
				case 101: // latgaliešu līdzskaņu mija lietvārdiem, parastās galotnes (izņemot -i, -e, -ī, -ē, -ie, -ei)
					// Mijas no Leikumas "Vasals!"
					if (resultStem.endsWith("kš")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "kst", "Mija", "kst -> kš"));
					} else if (resultStem.endsWith("šļ")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "sl", "Mija", "sl -> šļ"));
					} else if (resultStem.endsWith("žļ")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "zl", "Mija", "zl -> žļ"));
					} else if (resultStem.endsWith("šm")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "sm", "Mija", "sm -> šm"));
					} else if (resultStem.endsWith("šņ")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "sn", "Mija", "sn -> šņ"));
					} else if (resultStem.endsWith("žņ")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "zn", "Mija", "zn -> žņ"));
					} else if (resultStem.endsWith("ļļ")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "ll", "Mija", "ll -> ļļ"));
					} else if (resultStem.endsWith("ņņ")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "nn", "Mija", "nn -> ņņ"));
					} else if (resultStem.endsWith("č")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "c", "Mija", "c -> č"));
					} else if (resultStem.endsWith("ž")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "d", "Mija", "d -> ž"));
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "z", "Mija", "z -> ž"));
					} else if (resultStem.endsWith("š")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "t", "Mija", "t -> š"));
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "s", "Mija", "s -> š"));
						// Citas mijas
					} else if (resultStem.endsWith("ķ")) { // Andronovs?
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "k", "Mija", "k -> ķ"));
					} else if (resultStem.endsWith("ļ")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "l", "Mija", "l -> ļ"));
					} else if (resultStem.endsWith("ņ")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "n", "Mija", "n -> ņ"));
					} else {
						stemVariants.add(new StemVariant(resultStem));
					}
					break;
				case 102: // latgaliešu līdzskaņu mīkstināšana lietvārdiem, e, i, ē, ī, ie galotnes
					if (resultStem.endsWith("kš")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "kst", "Mija", "kst -> kš"));
					} else // Burtu miju ietekmētās vairāksimbolu mijas
					if (resultStem.endsWith("šl")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "šļ", "Mija", "šļ -> šl"));
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "sl", "Mija", "sl -> šl"));
					} else if (resultStem.endsWith("žl")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "žļ", "Mija", "žļ -> žl"));
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "zl", "Mija", "zl -> žl"));
					} else if (resultStem.endsWith("šm")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "šm", "Mija", "šm -> šm"));
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "sm", "Mija", "sn -> šn"));
					} else if (resultStem.endsWith("šn")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "šņ", "Mija", "šņ -> šn"));
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "sn", "Mija", "sn -> šn"));
					} else if (resultStem.endsWith("žn")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "žņ", "Mija", "žņ -> žn"));
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "zn", "Mija", "zn -> žn"));
					} else if (resultStem.endsWith("ll")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "ļļ", "Mija", "ļļ -> ll"));
					} else if (resultStem.endsWith("nn")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "ņņ", "Mija", "ņņ -> nn"));
						// Parastās, mijīgās, mijas
					} else if (resultStem.endsWith("č")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "c", "Mija", "c -> č"));
					} else if (resultStem.endsWith("š")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "t", "Mija", "t -> š"));
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "s", "Mija", "s -> š"));
					} else if (resultStem.endsWith("ž")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "d", "Mija", "d -> ž"));
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "z", "Mija", "z -> ž"));
						// Burtu miju ietekmētās viensimbola mijas
					} else if (resultStem.endsWith("l")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "ļ", "Mija", "ļ -> l"));
					} else if (resultStem.endsWith("n")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "ņ", "Mija", "ņ -> n"));
					} else if (resultStem.endsWith("k")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "ķ", "Mija", "ķ -> k"));
					} else if (resultStem.endsWith("g")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "ģ", "Mija", "ģ -> g"));
					} else {
						stemVariants.add(new StemVariant(resultStem));
					}
					break;
				case 103: // līdzīgi `case 3` - īpašības vārdiem -uok- un vys- / vysu-
					if (resultStem.endsWith("uok") && resultStem.length() > 3) {
						if (resultStem.startsWith("vysu"))
							stemVariants.add(new StemVariant(resultStem.substring(4, resultStem.length()-3), ltgDegreeFlags(AttributeNames.v_Superlative)));
						else if (resultStem.startsWith("vys"))
							stemVariants.add(new StemVariant(resultStem.substring(3, resultStem.length()-3), ltgDegreeFlags(AttributeNames.v_Superlative)));
						else
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-3), ltgDegreeFlags(AttributeNames.v_Comparative)));
					}
					stemVariants.add(new StemVariant(resultStem, ltgDegreeFlags(AttributeNames.v_Positive)));
					break;
				case 104: //  īpašības vārdiem -uok- un vys- / vysu-, pamata pakāpei burtu mija
					if (resultStem.endsWith("uok") && resultStem.length() > 3) {
						if (resultStem.startsWith("vysu"))
							stemVariants.add(new StemVariant(resultStem.substring(4, resultStem.length()-3), ltgDegreeFlags(AttributeNames.v_Superlative)));
						else if (resultStem.startsWith("vys"))
							stemVariants.add(new StemVariant(resultStem.substring(3, resultStem.length()-3), ltgDegreeFlags(AttributeNames.v_Superlative)));
						else
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-3), ltgDegreeFlags(AttributeNames.v_Comparative)));
					} else {
						stemVariants.add(new StemVariant(ltgLetterMijaHardToSoftUnambiguous(resultStem), ltgDegreeFlags(AttributeNames.v_Positive)));
					}
					break;
				case 105: // līdzīgi kā `case 34` - īpašības vārdiem -uok- un vys- / vysu- izskaņām kā -ajam: liekam nevis moz-s->moz-ajam, bet moz-s->moz-a-jam, bet senej-ais -> sene-jam/senej-a-jam
					if (resultStem.endsWith("uoka") && resultStem.length() > 4) {
						if (resultStem.startsWith("vysu"))
							stemVariants.add(new StemVariant(resultStem.substring(4, resultStem.length()-4), ltgDegreeFlags(AttributeNames.v_Superlative)));
						else if (resultStem.startsWith("vys"))
							stemVariants.add(new StemVariant(resultStem.substring(3, resultStem.length()-4), ltgDegreeFlags(AttributeNames.v_Superlative)));
						else
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-4), ltgDegreeFlags(AttributeNames.v_Comparative)));
					}
					if (resultStem.endsWith("a")) // moz-jam -> moz; seneja-jam -> senej
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1), ltgDegreeFlags(AttributeNames.v_Positive)));
					else if (resultStem.endsWith("ē") || resultStem.endsWith("e")) // sene-jam -> senej
						stemVariants.add(new StemVariant(resultStem +"j", ltgDegreeFlags(AttributeNames.v_Positive)));
					break;
				case 106: // līdzīgi `case 13` - apstākļa vārdiem -uok- un vys- / vysu-
					if (resultStem.endsWith("uok") && resultStem.length() > 3) {
						if (resultStem.startsWith("vysu"))
							stemVariants.add(new StemVariant(resultStem.substring(4, resultStem.length()-3), ltgDegreeFlags(AttributeNames.v_Superlative)));
						else if (resultStem.startsWith("vys"))
							stemVariants.add(new StemVariant(resultStem.substring(3, resultStem.length()-3), ltgDegreeFlags(AttributeNames.v_Superlative)));
						else
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-3), ltgDegreeFlags(AttributeNames.v_Comparative)));
					}
					break;
				case 107: // latgaliešu 'burtu mijas' inverss - kad pamatformas galotne ir -e, -i, -ī, -ē, -ie, un l, n, k, g kļūst par ļ, ņ, ķ, ģ pirms citām galotnēm (slapnis)
					stemVariants.add(new StemVariant(ltgLetterMijaSoftToHard(resultStem)));
					break;
				case 108: // 107 + 106 priekš slapnis
					if (resultStem.endsWith("uok") && resultStem.length() > 3) {
						if (resultStem.startsWith("vysu"))
							stemVariants.add(new StemVariant(
									ltgLetterMijaSoftToHard(resultStem.substring(4, resultStem.length()-3)),
									ltgDegreeFlags(AttributeNames.v_Superlative)));
						else if (resultStem.startsWith("vys"))
							stemVariants.add(new StemVariant(
									ltgLetterMijaSoftToHard(resultStem.substring(3, resultStem.length()-3)),
									ltgDegreeFlags(AttributeNames.v_Superlative)));
						else
							stemVariants.add(new StemVariant(
									ltgLetterMijaSoftToHard(resultStem.substring(0, resultStem.length()-3)),
									ltgDegreeFlags(AttributeNames.v_Comparative)));
					}
					break;
				case 109: // Apstākļa vārdi ar gradāciju, bet bez burtu mijas
					if (resultStem.endsWith("uok") && resultStem.length() > 4) {
						if (resultStem.startsWith("vysu")) {
							stemVariants.add(new StemVariant(resultStem.substring(4, resultStem.length()-3), ltgDegreeFlags(AttributeNames.v_Superlative)));
							stemVariants.add(new StemVariant(resultStem.substring(4, resultStem.length()-3) + "i", ltgDegreeFlags(AttributeNames.v_Superlative)));
							stemVariants.add(new StemVariant(resultStem.substring(4, resultStem.length()-3) + "a", ltgDegreeFlags(AttributeNames.v_Superlative)));
							stemVariants.add(new StemVariant(resultStem.substring(4, resultStem.length()-3) + "ai", ltgDegreeFlags(AttributeNames.v_Superlative)));
						} else if (resultStem.startsWith("vys")) {
							stemVariants.add(new StemVariant(resultStem.substring(3, resultStem.length()-3), ltgDegreeFlags(AttributeNames.v_Superlative)));
							stemVariants.add(new StemVariant(resultStem.substring(3, resultStem.length()-3) + "i", ltgDegreeFlags(AttributeNames.v_Superlative)));
							stemVariants.add(new StemVariant(resultStem.substring(3, resultStem.length()-3) + "a", ltgDegreeFlags(AttributeNames.v_Superlative)));
							stemVariants.add(new StemVariant(resultStem.substring(3, resultStem.length()-3) + "ai", ltgDegreeFlags(AttributeNames.v_Superlative)));
						} else {
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-3), ltgDegreeFlags(AttributeNames.v_Comparative)));
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-3) + "i", ltgDegreeFlags(AttributeNames.v_Comparative)));
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-3) + "a", ltgDegreeFlags(AttributeNames.v_Comparative)));
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-3) + "ai", ltgDegreeFlags(AttributeNames.v_Comparative)));
						}
					} else stemVariants.add(new StemVariant(resultStem, ltgDegreeFlags(AttributeNames.v_Positive)));
					break;
				// Te sākas verbu mijas
				case 110: // 2. konjugācija, vienkāršās tagadnes mija
					if (resultStem.endsWith("e")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "ei", "Mija", "ei -> e"));
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "ē", "Mija", "ē -> e"));
					} else if (resultStem.endsWith("o")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "uo", "Mija", "uo -> o"));
					}
					break;
				case 111: // 2. konjugācija, vienkāršās pagātnes vsk. 1., 2. pers. mija
					if (resultStem.endsWith("uoj")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1), "Mija", "uo -> uoj"));
					} else if (resultStem.endsWith("ov")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "uo", "Mija", "uo -> ov"));
					} else if (resultStem.endsWith("iej")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 3) + "ē", "Mija", "ē -> iej"));
					} else if (resultStem.endsWith("ej")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "ie", "Mija", "ei -> ej"));
					}
					break;
				case 112: // 2. konjugācija, vienkāršās pagātnes dsk. un 3. pers. mija
					if (resultStem.endsWith("uoj")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1), "Mija", "uo -> uoj"));
					} else if (resultStem.endsWith("ov")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "uo", "Mija", "uo -> ov"));
					} else if (resultStem.endsWith("ēj")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1), "Mija", "ē -> ēj"));
					} else if (resultStem.endsWith("ej")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "ie", "Mija", "ei -> ej"));
					}
					break;
				case 113: // 2. konjugācija, vienkāršās nākotnes vsk. 1., 2. pers. mija
					if (resultStem.endsWith("ie")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "ē", "Mija", "ē -> ie"));
					} else if (resultStem.endsWith("uo") || resultStem.endsWith("ei")) {
						stemVariants.add(new StemVariant(resultStem));
					}
					break;
				case 114: // 2. konjugācija, vēlējuma izteiksme, supīns un ciešamās kārtas pagātnes (-ts) divdabis, dams divdabis
					if (resultStem.endsWith("ā")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "ē", "Mija", "ē -> ā"));
					} else if (resultStem.endsWith("uo") || resultStem.endsWith("ei")) {
						stemVariants.add(new StemVariant(resultStem));
					}
					break;
				case 115: // 2. konjugācija, divdabju formu vispārākā pakāpe (121.) + tagadnes mija (110.)
					String degree = AttributeNames.v_Comparative;
					if (resultStem.startsWith("vysu")) {
						degree = AttributeNames.v_Superlative;
						resultStem = resultStem.substring(4);
					} else if (resultStem.startsWith("vys")) {
						degree = AttributeNames.v_Superlative;
						resultStem = resultStem.substring(3);
					}

					if (resultStem.endsWith("e")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "ei", ltgDegreeFlags(degree)));
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "ē", ltgDegreeFlags(degree)));
					} else if (resultStem.endsWith("o")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "uo", ltgDegreeFlags(degree)));
					}
					break;
				case 116: // 2. konjugācija, divdabju formu vispārākā pakāpe (121.) + vēlējuma/supīna mija (114.) (-ts divdabim)
					degree = AttributeNames.v_Comparative;
					if (resultStem.startsWith("vysu")) {
						degree = AttributeNames.v_Superlative;
						resultStem = resultStem.substring(4);
					} else if (resultStem.startsWith("vys")) {
						degree = AttributeNames.v_Superlative;
						resultStem = resultStem.substring(3);
					}

					if (resultStem.endsWith("ā")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "ē", ltgDegreeFlags(degree)));
					} else if (resultStem.endsWith("uo") || resultStem.endsWith("ei")) {
						stemVariants.add(new StemVariant(resultStem, ltgDegreeFlags(degree)));
					}
					break;
				case 117:  // 2. konjugācija, pagātnes mija -s, -use divdabim (vienkāršota 111.)
					if (resultStem.endsWith("uo")) {
						stemVariants.add(new StemVariant(resultStem));
					} else if (resultStem.endsWith("ie")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "ē", "Mija", "ē -> ie"));
					} else if (resultStem.endsWith("e")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "ie", "Mija", "ei -> e"));
					}
					break;
				case 118: // 2. konjugācija, divdabju formu vispārākā pakāpe (121.) + pag. mija -s, -use divdabim (117.)
					degree = AttributeNames.v_Comparative;
					if (resultStem.startsWith("vysu")) {
						degree = AttributeNames.v_Superlative;
						resultStem = resultStem.substring(4);
					} else if (resultStem.startsWith("vys")) {
						degree = AttributeNames.v_Superlative;
						resultStem = resultStem.substring(3);
					}

					if (resultStem.endsWith("ie")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "ē", ltgDegreeFlags(degree)));
					} else if (resultStem.endsWith("e")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "ie", ltgDegreeFlags(degree)));
					} else if (resultStem.endsWith("uo")) {
						stemVariants.add(new StemVariant(resultStem, ltgDegreeFlags(degree)));
					}
					break;
				case 119: // 3. konjugācija, standarta -eit, tagadne un pagātne (līdzskaņu mijas nekad nav)
					stemVariants.add(new StemVariant(resultStem + "ei", "Mija", "ei -> "));
					break;
				case 120: // 3. konjugācija, standarta -eit bez līdskaņu mijas, divdabju formu vispārākā pakāpe + tagadnes un pagātnes mija (119.)
					degree = AttributeNames.v_Comparative;
					if (resultStem.startsWith("vysu")) {
						degree = AttributeNames.v_Superlative;
						resultStem = resultStem.substring(4);
					} else if (resultStem.startsWith("vys")) {
						degree = AttributeNames.v_Superlative;
						resultStem = resultStem.substring(3);
					}
					stemVariants.add(new StemVariant(resultStem + "ei", ltgDegreeFlags(degree)));
					break;
				case 121: // 3. konjugācija, divdabju formu vispārākā pakāpe bez mijas
					degree = AttributeNames.v_Comparative;
					if (resultStem.startsWith("vysu")) {
						degree = AttributeNames.v_Superlative;
						resultStem = resultStem.substring(4);
					} else if (resultStem.startsWith("vys")) {
						degree = AttributeNames.v_Superlative;
						resultStem = resultStem.substring(3);
					}
					stemVariants.add(new StemVariant(resultStem, ltgDegreeFlags(degree)));
					break;
				case 122: // 3. konjugācija, standarta -eit, tagadne ar līdzskaņu miju
					if (resultStem.endsWith("ld")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "ļdei", "Mija", "ļdei -> ld"));
					} else if (resultStem.endsWith("nd")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "ņdei", "Mija", "ņdei -> nd"));
					} else if (resultStem.endsWith("g")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "dzei", "Mija", "dzei -> g"));
					} else if (resultStem.endsWith("k")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "cei", "Mija", "cei -> k"));
					} else if (resultStem.endsWith("ļ")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "lei", "Mija", "lei -> ļ"));
					} else if (resultStem.endsWith("ņ")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "nei", "Mija", "nei -> ņ"));
					}
					break;
				case 123: // 3. konjugācija, standarta -eit ar līdskaņu miju, divdabju formu vispārākā pakāpe + tagadnes mija (122.)
					degree = AttributeNames.v_Comparative;
					if (resultStem.startsWith("vysu")) {
						degree = AttributeNames.v_Superlative;
						resultStem = resultStem.substring(4);
					} else if (resultStem.startsWith("vys")) {
						degree = AttributeNames.v_Superlative;
						resultStem = resultStem.substring(3);
					}

					if (resultStem.endsWith("ld")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "ļdei", ltgDegreeFlags(degree)));
					} else if (resultStem.endsWith("nd")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "ņdei", ltgDegreeFlags(degree)));
					} else if (resultStem.endsWith("g")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "dzei", ltgDegreeFlags(degree)));
					} else if (resultStem.endsWith("k")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "cei", ltgDegreeFlags(degree)));
					} else if (resultStem.endsWith("ļ")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "lei", ltgDegreeFlags(degree)));
					} else if (resultStem.endsWith("ņ")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "nei", ltgDegreeFlags(degree)));
					}
					break;
				case 124: // 3. konjugācija, standarta -ēt, tagadne un pagātne bez līdskaņu un burtu mijas
					stemVariants.add(new StemVariant(resultStem + "ē", "Mija", "ē -> "));
					break;
				case 125: // 3. konjugācija, standarta -ēt, tagadne bez līdskaņu mijas ar burtu miju
					stemVariants.add(new StemVariant(ltgLetterMijaSoftToHard(resultStem) + "ē", "Mija", "ē -> "));
					break;
				case 126: // 3. konjugācija, standarta -ēt bez līdskaņu un burtu mijas, divdabju formu vispārākā pakāpe + tagadnes un pagātnes mija (119.)
					degree = AttributeNames.v_Comparative;
					if (resultStem.startsWith("vysu")) {
						degree = AttributeNames.v_Superlative;
						resultStem = resultStem.substring(4);
					} else if (resultStem.startsWith("vys")) {
						degree = AttributeNames.v_Superlative;
						resultStem = resultStem.substring(3);
					}
					stemVariants.add(new StemVariant(resultStem + "ē", ltgDegreeFlags(degree)));
					break;
				case 127: // 3. konjugācija, standarta -ēt bez līdskaņu mijas ar inverso burtu miju, divdabju formu vispārākā pakāpe + tagadnes un pagātnes mija (119.)
					degree = AttributeNames.v_Comparative;
					if (resultStem.startsWith("vysu")) {
						degree = AttributeNames.v_Superlative;
						resultStem = resultStem.substring(4);
					} else if (resultStem.startsWith("vys")) {
						degree = AttributeNames.v_Superlative;
						resultStem = resultStem.substring(3);
					}
					stemVariants.add(new StemVariant(ltgLetterMijaSoftToHard(resultStem) + "ē", ltgDegreeFlags(degree)));
					break;
				default:
					System.err.printf("Invalid StemChange ID, stem '%s', stemchange %d\n", resultStem, mija);
			}
		} catch (StringIndexOutOfBoundsException e){
			try {
				new PrintStream(System.err, true, "UTF-8").printf(
						"StringIndexOutOfBounds, resultStem '%s', mija %d\n", stem, stemChange);
				e.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		}

		return stemVariants;
	}

	/**
	 * Try to apply inverted Mija the produced stem variant to check if the
	 * original stem can be achieved and, thus, process has been applied
	 * correctly.
	 * Has exceptions for cases where form analysis and synthesis are not
	 * symmetric.
	 */
	public static boolean backwardsVerification(
			StemVariant stemVariant, String stem, int stemChange, String thirdStem, boolean properName) {
		// Verifikācija, vai variantu izlokot tiešām sanāk tas kas vajag.

		if (Arrays.asList(18,20,34,35).contains(stemChange)) {
			// 18. mijā neierobežojam, jo tur ir nesimetrija - vokatīvu silvij! atpazīstam bet neģenerējam.
			// 20. mijā ir arī alternatīvas - guļošs un gulošs
			// 34/35 mijā - pēdējajam, zaļoksnējajam atpazīstam bet neģenerējam
			return true;
		}

		if (stemChange == 6 && thirdStem.endsWith("ī")) thirdStem = thirdStem.substring(0, thirdStem.length()-1);
		ArrayList<StemVariant> backwardsMijaApplied = applyLemmaToFormMija(
				stemVariant.stem, stemChange, thirdStem, stemVariant.isMatchingStrong(AttributeNames.i_Degree, AttributeNames.v_Superlative), properName);
		boolean isFound = false;
		for (StemVariant variant : backwardsMijaApplied) {
			if (variant.stem.equalsIgnoreCase(stem))
			{
				isFound = true;
				break;
			}
		}

		if (!isFound && Arrays.asList(1,2,5,6,7,8,9,14,15,17,23,26,36,37).contains(stemChange)) {
			if (stemChange == 7 && stemVariant.stem.endsWith("dod")) return true; // izņēmums, ka "dodi" atpazīst bet neģenerē
			if (properName) {
				// pie atpazīšanas properName var būt nepareizs, jo lielie burti ir arī citos gadījumos - teikuma sākumā utt
				return backwardsVerification(stemVariant, stem, stemChange, thirdStem, false);
			}

			// System.err.printf("Celmam '%s' ar miju %d sanāca '%s' - noraidījām dēļ atpakaļlocīšanas verifikācijas.\n", stem, stemChange, variants.celms);
			return false;
		} else {
			if (!isFound) { //debuginfo.
				// FIXME - šo principā vajadzētu realizēt kā karodziņu - ka ieliekam Variant klasē zīmi, ka šis ir neiesakāms, un tad nebrīnamies, ja ģenerācija to neiedod; vai arī lai ģenerācija dod tos variantus ar tādu karodziņu un tad šeit tos ieraugam
				System.err.printf("Celms '%s' ar miju %d sanāca '%s'. Bet atpakaļ lokot:\n", stem, stemChange, stemVariant.stem);
				for (StemVariant locītais : backwardsMijaApplied) {
					System.err.printf("\t'%s'\n", locītais.stem);
				}
			}
			return true;
		}
	}

	private static int syllables(String word) {
		int counter = 0;
		boolean in_vowel = false;
		HashSet<Character> vowels = new HashSet<>( Arrays.asList(new Character[] {'a','ā','e','ē','i','ī','o','u','ū'}));

		for (char c : word.toCharArray()) {
			if (!in_vowel && vowels.contains(c))
				counter++;
			in_vowel = vowels.contains(c);
		}
		return counter;
	}

	/**
	 * Procedure who actually does all the stem changes to get any form from
	 * given lemma: consonant changes, verbs forms, devitives, superlatives, etc.
	 * @return an array with variants - FIXME - principā vajadzētu būt vienam; izņēmums ir pārākās/vispārākās formas
	 */
	public static ArrayList<StemVariant> applyLemmaToFormMija(
			String stem, int stemChange, String thirdStem,
			boolean addSuperlative, boolean properName) {

		ArrayList<StemVariant> stemVariants = new ArrayList<>(1);
		if (stem.trim().isEmpty()) return stemVariants;

		int mija;
		String resultStem;

		try {
			switch (stemChange) { //TODO - uz normālāku struktūru
				case 4: // vajadzības izteiksmes jā-
					resultStem = "jā" + stem;
					mija = 0;
					break;
				case 5: // vajadzības izteiksme 3. konjugācijai bez mijas
					resultStem = "jā" + stem;
					mija = 9;
					break;
				case 12: // vajadzības izteiksme 3. konjugācijai atgriezeniskai bez mijas
					resultStem = "jā" + stem;
					mija = 8;
					break;
				case 19: // vajadzības_vēlējuma izteiksme 3. konjugācijai bez mijas (jāmācot)
					resultStem = "jā" + stem;
					mija = 2;
					break;
				case 28: // vajadzības_vēlējuma izteiksme 3. konjugācijai ar miju (jāmākot)
					resultStem = "jā" + stem;
					mija = 20;
					break;
				case 29: // vajadzības izteiksme 3. konjugācijai atgriezeniskai ar miju
					resultStem = "jā" + stem;
					mija = 27;
					break;
				case 31: // vajadzības izteiksme 3. konjugācijai ar miju
					resultStem = "jā" + stem;
					mija = 30;
					break;
				case 37: // vajadzības izteiksme 1. konjugācijai ar miju
					resultStem = "jā" + stem;
					mija = 36;
					break;
				// latgalieši
				case 150: // vajadzības izteiksme 2. konjugācijai
					resultStem = "juo" + stem;
					mija = 110;
					break;
				case 151: // vajadzības izteiksme 3. konjugācijai -eit ar patskaņu miju bez līdzskaņu mijas
					resultStem = "juo" + ltgVowelMijaLemmaToForm(stem);
					mija = 119;
					break;
				case 152: // vajadzības izteiksme 3. konjugācijai -eit ar patskaņu miju ar līdzskaņu miju
					resultStem = "juo" + ltgVowelMijaLemmaToForm(stem);
					mija = 122;
					break;
				case 153: // vajadzības izteiksme 3. konjugācijai -ēt ar burtu miju bez līdzskaņu un patskaņu mijas
					resultStem = "juo" + stem;
					mija = 125;
					break;
				// patskaņu mijas verbiem
				case 160: // patskaņu mija 2. konjugācijas supīnam, vēlējuma izteiksmei un pagātnes divdabim
					resultStem = ltgVowelMijaLemmaToForm(stem);
					mija = 114;
					break;
				case 161: // patskaņu mija 2. konjugācijas divdabja pārākajai un vispārākajai pakāpei
					resultStem = ltgVowelMijaLemmaToForm(stem);
					mija = 116;
					break;
				case 162: // patskaņu mija 3. konjugācijas standarta -eit tagadnei bez līdzskaņu mijas
					resultStem = ltgVowelMijaLemmaToForm(stem);
					mija = 119;
					break;
				case 163: // patskaņu mija 3. konjugācijas standarta -eit bez līdzskaņu mijas grupas tagadnes divdabja pārākajai un vispārākajai pakāpei
					resultStem = ltgVowelMijaLemmaToForm(stem);
					mija = 120;
					break;
				case 164: // patskaņu mija 3. konjugācijas standarta -eit grupas tagadnei ar līdzskaņu miju
					resultStem = ltgVowelMijaLemmaToForm(stem);
					mija = 122;
					break;
				case 165: // patskaņu mija 3. konjugācijas standarta -eit līdzskaņu mijas grupas tagadnes divdabja pārākajai un vispārākajai pakāpei
					resultStem = ltgVowelMijaLemmaToForm(stem);
					mija = 123;
					break;
				case 166: // patskaņu mija 3. konjugācijas standarta -ēt 1. pers. tagadnei bez līdzskaņu un burtu mijas
					resultStem = ltgVowelMijaLemmaToForm(stem);
					mija = 124;
					break;
				case 167: // patskaņu mija 3. konjugācijas standarta -ēt bez līdzskaņu un burtu mijas tagadnes divdabja pārākajai un vispārākajai pakāpei
					resultStem = ltgVowelMijaLemmaToForm(stem);
					mija = 126;
					break;
				default:
					resultStem = stem;
					mija = stemChange;
			}

			switch (mija) {
				case 0: stemVariants.add(new StemVariant(resultStem)); break;  // nav mijas

				case 1: // lietvārdu līdzskaņu mija
					// Personvārdu mijas - Valdis-Valda; Gatis-Gata. Eglīts - Eglīša.  Vēl ir literatūrā minēts izņēmums -skis -ckis (Čaikovskis, Visockis), taču tiem tāpat viss šķiet ok.
					// 2025-06-02: Gatis un Valdis jau sen iet paradigmā pie tēta un viesa,
					//             Eglīts un Ķezbers iet paradigmā pie suņa.
					if (properName && resultStem.endsWith("t")) {// && !resultStem.endsWith("īt")) {
						//stemVariants.add(new Variants(resultStem));
						//if (syllables(resultStem) > 1)
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"š","Mija","t -> š"));
					}
					else if (properName && resultStem.endsWith("d") ) {
						//if (syllables(resultStem) > 1)
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"ž","Mija","d -> ž"));
						//else stemVariants.add(new Variants(resultStem));
					}
					else if (resultStem.endsWith("s") || resultStem.endsWith("t")) {
						if (resultStem.endsWith("kst")) {
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-3)+"kš","Mija","kst -> kš"));
						} else if (resultStem.endsWith("nst")) { // skansts -> skanšu
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 3) + "nš", "Mija", "nst -> nš"));
						} else if (resultStem.endsWith("s")) {
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"š","Mija","s -> š"));
						}
						else if (resultStem.endsWith("t")) {
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"š","Mija","t -> š"));
						}
					}
					else if (resultStem.endsWith("z")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"ž","Mija","z -> ž"));
					}
					else if (resultStem.endsWith("d")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"ž","Mija","d -> ž"));
					}
					else if (resultStem.endsWith("c")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"č","Mija","c -> č"));}
					else if (resultStem.endsWith("l")) {
						if (resultStem.endsWith("sl")) {
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"šļ","Mija","sl -> šļ"));}
						else if (resultStem.endsWith("zl")) {
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"žļ","Mija","zl -> žļ"));}
						else if (resultStem.endsWith("ll")) {
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"ļļ","Mija","ll -> ļļ"));}
						else stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"ļ","Mija","l -> ļ"));
					}
					else if (resultStem.endsWith("n")) {
						if (resultStem.endsWith("sn")) {
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"šņ","Mija","sn -> šņ"));}
						else if (resultStem.endsWith("zn")) {
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"žņ","Mija","zn -> žņ"));}
						else if (resultStem.endsWith("ln")) {
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"ļņ","Mija","ln -> ļņ"));}
						else if (resultStem.endsWith("nn")) {
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"ņņ","Mija","nn -> ņņ"));}
						else /*if (!(resultStem.endsWith("zņ") || resultStem.endsWith("sņ") || resultStem.endsWith("lņ")))*/ {
							//stemVariants.add(new Variants(resultStem.substring(0,resultStem.length()-1)+"ņ","Mija", "l -> ņ ??"));
							stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"ņ","Mija", "n -> ņ"));
						}
					}
					else if (resultStem.endsWith("p") || resultStem.endsWith("b") || resultStem.endsWith("m") || resultStem.endsWith("v") || resultStem.endsWith("f")) {
						stemVariants.add(new StemVariant(resultStem +"j","Mija","p->pj (u.c.)"));
					}
					/*
					// Tēzaurā valodnieki liek 2 leksēmas no laika gala.
					else if (resultStem.endsWith("f")) { // Žirafu -> žirafju, žirafu
						Variants v = new Variants(resultStem+"j","Mija","p->pj (u.c.)");
						v.addAttribute(AttributeNames.i_Recommended, AttributeNames.v_Yes);
						stemVariants.add(v);
						stemVariants.add(new Variants(resultStem));
					}*/
					else if (!(resultStem.endsWith("p") || resultStem.endsWith("b") || resultStem.endsWith("m") || resultStem.endsWith("v") ||
							resultStem.endsWith("t") || resultStem.endsWith("d") || resultStem.endsWith("c") || resultStem.endsWith("z") ||
							resultStem.endsWith("s") || resultStem.endsWith("n") || resultStem.endsWith("l") || resultStem.endsWith("f")) )
						stemVariants.add(new StemVariant(resultStem));
					break;
				case 2: //  dv. 3. konjugācijas tagadne, kas noņem celma pēdējo burtu
					if (resultStem.endsWith("ī") || resultStem.endsWith("inā") || resultStem.endsWith("sargā"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1), "Garā", "ā"));
					else stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)));
					break;
				case 3: // īpašības vārdiem pieliekam -āk- un vis-
					stemVariants.add(new StemVariant(resultStem,AttributeNames.i_Degree,AttributeNames.v_Positive));
					if (!resultStem.endsWith("āk")) {
						stemVariants.add(new StemVariant(resultStem + "āk", AttributeNames.i_Degree, AttributeNames.v_Comparative));
						if (addSuperlative)
							stemVariants.add(new StemVariant("vis" + resultStem + "āk", AttributeNames.i_Degree, AttributeNames.v_Superlative));
					}
					break;
				case 6: // 1. konjugācijas nākotne
					if (resultStem.endsWith("s")) {
						if (thirdStem.endsWith("d")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"dī"));
						else if (thirdStem.endsWith("t")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"tī"));
						else if (thirdStem.endsWith("s")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"sī"));
						else stemVariants.add(new StemVariant(resultStem));
					} else if (resultStem.endsWith("z") || resultStem.endsWith("š")) {
						stemVariants.add(new StemVariant(resultStem +"ī"));
					}
					else stemVariants.add(new StemVariant(resultStem));
					break;
				case 7: // 1. konjugācijas 2. personas tagadne
				case 23: // 1. konjugācijas 2. personas tagadne - ja pēc tam seko garā galotne kā -iet
					if (resultStem.endsWith("š") && thirdStem.endsWith("s")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"s"));
					else if (resultStem.endsWith("š") && thirdStem.endsWith("t")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"t"));
					else if ( (resultStem.endsWith("od") && !resultStem.endsWith("dod")) || resultStem.endsWith("ūd") || resultStem.endsWith("op") || resultStem.endsWith("ūp") || resultStem.endsWith("ot") || resultStem.endsWith("ūt") || resultStem.endsWith("īt") || resultStem.endsWith("iet")  || resultStem.endsWith("st")) {
						if (mija == 7)
							stemVariants.add(new StemVariant(resultStem +"i"));
						else stemVariants.add(new StemVariant(resultStem));
					}
					else if (resultStem.endsWith("ļ")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"l"));
					else if (resultStem.endsWith("mj") || resultStem.endsWith("bj") || resultStem.endsWith("pj"))	stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)));
					else if (resultStem.endsWith("k")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"c"));
					else if (resultStem.endsWith("g")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"dz"));
					else if (resultStem.endsWith("ž")) {
						// stemVariants.add(new Variants(resultStem.substring(0,resultStem.length()-1)+"z")); // griez -> griežu
						stemVariants.add(new StemVariant(thirdStem)); // skaužu -> skaud, laužu -> lauz; sanāk atbilstoši pagātnes celmam
					} else stemVariants.add(new StemVariant(resultStem));
					break;
				case 8: // -ams -āms 3. konjugācijai bezmiju gadījums
					if (resultStem.endsWith("inā") || resultStem.endsWith("sargā")) stemVariants.add(new StemVariant(resultStem, "Garā", "ā"));
					else if (resultStem.endsWith("ī")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"ā", "Garā", "ā"));
					else if (resultStem.endsWith("ē")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"a"));
					else if (resultStem.endsWith("ā")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"a"));
					else stemVariants.add(new StemVariant(resultStem));
					break;
				case 9: // 3. konjugācija 3. pers. tagadne bez mijas
					if (resultStem.endsWith("dā")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1))); // dzied, raud
					else if (resultStem.endsWith("ā") || resultStem.endsWith("ī"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"a"));
					else stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)));
					break;
				case 10: // īpašības vārds -āk- un vis-, -i apstākļa formai
					stemVariants.add(new StemVariant(resultStem,AttributeNames.i_Degree,AttributeNames.v_Positive));
					stemVariants.add(new StemVariant(resultStem + "āk",AttributeNames.i_Degree,AttributeNames.v_Comparative));
					if (addSuperlative)
						stemVariants.add(new StemVariant("vis" + resultStem + "āk",AttributeNames.i_Degree,AttributeNames.v_Superlative));
					break;
				case 11: // -uša formas
					if (resultStem.endsWith("c")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"k"));
					else if (resultStem.endsWith("dz")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"g"));
					else stemVariants.add(new StemVariant(resultStem));
					break;
				case 13: // īpašības vārdiem -āk-, ar š->s nominatīva formā (zaļš -> zaļāks
					stemVariants.add(new StemVariant(resultStem +"āk", AttributeNames.i_Degree, AttributeNames.v_Comparative));
					if (addSuperlative)
						stemVariants.add(new StemVariant("vis" + resultStem + "āk",AttributeNames.i_Degree, AttributeNames.v_Superlative));
					break;
				case 14: // 1. konjugācijas "-is" forma
					if (resultStem.endsWith("k")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"c"));
					else if (resultStem.endsWith("g")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"dz"));
					else stemVariants.add(new StemVariant(resultStem));
					break;
				case 15: // pūst -> pūzdams nopūzdamies s -> z mija tad, ja 3. sakne (pagātnes sakne) beidzas ar t/d
					if (resultStem.endsWith("s") && (thirdStem.endsWith("t") || thirdStem.endsWith("d"))) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"z"));
					} else stemVariants.add(new StemVariant(resultStem));
					break;
				case 16: // 1. konjugācijas "-šana" atvasināšana
					if (resultStem.endsWith("s") || resultStem.endsWith("z"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)));    // nest -> nešana
					else stemVariants.add(new StemVariant(resultStem));
					break;
				case 17: // īsā sieviešu dzimtes vokatīva forma "kristīnīt!" "margriet!"
					if (syllables(resultStem) >= 2 &&
							!(resultStem.endsWith("kāj") || resultStem.endsWith("māj")) )
						stemVariants.add(new StemVariant(resultStem));
					break;
				/*
				// 2025-06-12 Baiba izskaidro, ka pēc mūsdienu latviešu valodas
				// normām visiem 4. un 5. deklinācijas vārdiem pienākas viens
				// vienskaitļa vokatīvs, kas sakrīt vienskaitļa nominatīvu
				case 18: // garā sieviešu dzimtes vokatīva forma "laura!" "margrieta!"
					if (syllables(resultStem) < 2 || // NB! te ir < 2 bet pie atpazīšanas <= 2 - ar 2 zilbēm pagaidām atpazīst abus un ģenerē vienu
							!(resultStem.endsWith("ij") || resultStem.endsWith("īn") || resultStem.endsWith("īt") || resultStem.endsWith("ān") || resultStem.endsWith("iņ") || resultStem.endsWith("ēn") || resultStem.endsWith("niec") || resultStem.endsWith("āj")) )
						stemVariants.add(new Variants(resultStem));
					if (syllables(resultStem) > 1 && (resultStem.endsWith("kāj") || resultStem.endsWith("māj")))
						stemVariants.add(new Variants(resultStem));
					break;*/
				case 20: //  dv. 3. konjugācijas tagadnes mija 1. personas tagadnei, -ot divdabim un vajadzībai - atšķiras no 26. mijas 'gulēt' un 'tecēt'
					if (resultStem.endsWith("gulē")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"ļ")); //gulēt -> guļu
						// variantu ar -l (gulošs) atpazīstam bet neģenerējam
					} else if (resultStem.endsWith("cī") )
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"k", "Garā", "ā")); //sacīt
					else if (resultStem.endsWith("cē") )
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"k")); //mācēt -> māku
					else if (resultStem.endsWith("dē") )
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"ž")); //sēdēt -> sēžu
					else if (resultStem.endsWith("dzē") || resultStem.endsWith("dzī"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-3)+"g")); //vajadzēt -> vajag, slodzīt -> slogu
					break;
				case 21: // divdabju formas ar pārāko/vispārāko pakāpi
					stemVariants.add(new StemVariant(resultStem, AttributeNames.i_Degree, AttributeNames.v_Comparative));
					if (addSuperlative)
						stemVariants.add(new StemVariant("vis" + resultStem, AttributeNames.i_Degree, AttributeNames.v_Superlative));
					break;
				case 22: // jaundzimušais -> jaundzimusī
					if (resultStem.endsWith("uš"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"us"));
					break;
				case 24: //  analoģiski 2, bet ar pārākajām / vispārākajām pakāpēm
					if (resultStem.endsWith("ī") || resultStem.endsWith("inā") || resultStem.endsWith("sargā"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1), AttributeNames.i_Degree, AttributeNames.v_Comparative));
					else stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1), AttributeNames.i_Degree, AttributeNames.v_Comparative));

					if (addSuperlative) {
						if (resultStem.endsWith("ī") || resultStem.endsWith("inā") || resultStem.endsWith("sargā"))
							stemVariants.add(new StemVariant("vis" + resultStem.substring(0, resultStem.length()-1), AttributeNames.i_Degree, AttributeNames.v_Superlative));
						else stemVariants.add(new StemVariant("vis" + resultStem.substring(0, resultStem.length()-1), AttributeNames.i_Degree, AttributeNames.v_Superlative));
					}
					break;
				case 25: //  analoģiski 8, bet ar pārākajām / vispārākajām pakāpēm. DRY :( :(
					if (resultStem.endsWith("inā") || resultStem.endsWith("sargā")) stemVariants.add(new StemVariant(resultStem, AttributeNames.i_Degree, AttributeNames.v_Comparative));
					else if (resultStem.endsWith("ī")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"ā", AttributeNames.i_Degree, AttributeNames.v_Comparative));
					else if (resultStem.endsWith("ē")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"a", AttributeNames.i_Degree, AttributeNames.v_Comparative));
					else if (resultStem.endsWith("ā")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1)+"a", AttributeNames.i_Degree, AttributeNames.v_Comparative));
					else stemVariants.add(new StemVariant(resultStem, AttributeNames.i_Degree, AttributeNames.v_Comparative));

					if (addSuperlative) {
						if (resultStem.endsWith("inā") || resultStem.endsWith("sargā")) stemVariants.add(new StemVariant("vis" + resultStem, AttributeNames.i_Degree, AttributeNames.v_Superlative));
						else if (resultStem.endsWith("ī")) stemVariants.add(new StemVariant("vis" + resultStem.substring(0, resultStem.length()-1)+"ā", AttributeNames.i_Degree, AttributeNames.v_Superlative));
						else if (resultStem.endsWith("ē")) stemVariants.add(new StemVariant("vis" + resultStem.substring(0, resultStem.length()-1)+"a", AttributeNames.i_Degree, AttributeNames.v_Superlative));
						else if (resultStem.endsWith("ā")) stemVariants.add(new StemVariant("vis" + resultStem.substring(0, resultStem.length()-1)+"a", AttributeNames.i_Degree, AttributeNames.v_Superlative));
						else stemVariants.add(new StemVariant("vis" + resultStem, AttributeNames.i_Degree, AttributeNames.v_Superlative));
					}
					break;
				case 26:  //  dv. 3. konjugācijas miju gadījuma formas - otrās personas tagadne, pavēles izteiksme
					if (resultStem.endsWith("lē")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1))); //gulēt -> guli
					else if (resultStem.endsWith("cī") )
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"k", "Garā", "ā")); //sacīt->saki
					else if (resultStem.endsWith("tecē"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"c")); //tecēt -> teci
					else if (resultStem.endsWith("cē"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"k")); //mācēt -> māki
					else if (resultStem.endsWith("dzē") || resultStem.endsWith("dzī"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-3)+"g")); //vajadzēt -> vajag, slodzīt -> slogi
					else
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1))); // sēdē-ties -> sēd-ies
					break;
				case 27: // -ams -āms 3. konjugācijai miju gadījums
					if (resultStem.endsWith("cī"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"kā", "Garā", "ā")); //sacīt->sakām
					else if (resultStem.endsWith("dzī"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-3)+"gā")); //slodzīt -> slogām
					else if (resultStem.endsWith("cē"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"ka")); //mācēt -> mākam
					else if (resultStem.endsWith("gulē")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"ļa")); //gulēt -> guļam
					else if (resultStem.endsWith("dē") )
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"ža")); //sēdēt -> sēžam
					else if (resultStem.endsWith("dzē"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-3)+"ga")); //vajadzēt -> vajagam
					break;
				case 30: // 3. konjugācija 3. pers. tagadne ar miju
					if (resultStem.endsWith("cī"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"ka")); // "saka"
					else if (resultStem.endsWith("dzī"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-3)+"ga")); //slodzīt -> sloga
					else if (resultStem.endsWith("cē"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"k")); //mācēt -> māk
					else if (resultStem.endsWith("dē") )
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"ž")); //sēdēt -> sēž
					else if (resultStem.endsWith("dzē"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-3)+"g")); //vajadzēt -> vajag
					else if (resultStem.endsWith("lē")) stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"ļ")); //gulēt -> guļ
					break;
				case 32: //  analoģiski 20, bet ar pārākajām / vispārākajām pakāpēm
					if (resultStem.endsWith("cī") || resultStem.endsWith("cē"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"k", AttributeNames.i_Degree, AttributeNames.v_Comparative)); //sacīt
					else if (resultStem.endsWith("dzī") || resultStem.endsWith("dzē"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-3)+"g", AttributeNames.i_Degree, AttributeNames.v_Comparative)); //slodzīt -> slogu
					else if (resultStem.endsWith("dē") )
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"ž", AttributeNames.i_Degree, AttributeNames.v_Comparative)); //sēdēt -> sēž
					else if (resultStem.endsWith("lē"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"ļ", AttributeNames.i_Degree, AttributeNames.v_Comparative)); //gulēt -> guļu
					else
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1), AttributeNames.i_Degree, AttributeNames.v_Comparative));

					if (addSuperlative) {
						// TODO :( :( DRY
						if (resultStem.endsWith("cī") || resultStem.endsWith("cē"))
							stemVariants.add(new StemVariant("vis" + resultStem.substring(0, resultStem.length()-2)+"k", AttributeNames.i_Degree, AttributeNames.v_Superlative)); //sacīt
						else if (resultStem.endsWith("vajadzē"))
							stemVariants.add(new StemVariant("vis" + resultStem.substring(0, resultStem.length()-3)+"g", AttributeNames.i_Degree, AttributeNames.v_Superlative)); //vajadzēt -> vajag
						else if (resultStem.endsWith("dzī") || resultStem.endsWith("dzē"))
							stemVariants.add(new StemVariant("vis" + resultStem.substring(0, resultStem.length()-3)+"g", AttributeNames.i_Degree, AttributeNames.v_Superlative)); //slodzīt -> slogu
						else if (resultStem.endsWith("dē") )
							stemVariants.add(new StemVariant("vis" + resultStem.substring(0, resultStem.length()-2)+"ž", AttributeNames.i_Degree, AttributeNames.v_Superlative)); //sēdēt -> sēž
						else if (resultStem.endsWith("gulē"))
							stemVariants.add(new StemVariant("vis" + resultStem.substring(0, resultStem.length()-2)+"ļ")); //gulēt -> guļu
						else
							stemVariants.add(new StemVariant("vis" + resultStem.substring(0, resultStem.length()-1), AttributeNames.i_Degree, AttributeNames.v_Superlative));
					}
					break;
				case 33: //  analoģiski 27, bet ar pārākajām / vispārākajām pakāpēm. DRY :( :(
					if (resultStem.endsWith("cī"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"kā", AttributeNames.i_Degree, AttributeNames.v_Comparative)); //sacīt
					else if (resultStem.endsWith("dzī"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-3)+"gā", AttributeNames.i_Degree, AttributeNames.v_Comparative)); //slodzīt -> slogu
					else if (resultStem.endsWith("cē"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"ka", AttributeNames.i_Degree, AttributeNames.v_Comparative)); //mācēt -> māk
					else if (resultStem.endsWith("lē"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"ļa", AttributeNames.i_Degree, AttributeNames.v_Comparative)); //gulēt -> guļam
					else if (resultStem.endsWith("dē") )
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"ža", AttributeNames.i_Degree, AttributeNames.v_Comparative)); //sēdēt -> sēž
					else if (resultStem.endsWith("dzē"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-3)+"ga", AttributeNames.i_Degree, AttributeNames.v_Comparative)); //vajadzēt -> vajag

					if (addSuperlative) {
						if (resultStem.endsWith("cī"))
							stemVariants.add(new StemVariant("vis" + resultStem.substring(0, resultStem.length()-2)+"kā", AttributeNames.i_Degree, AttributeNames.v_Superlative)); //sacīt
						else if (resultStem.endsWith("dzī"))
							stemVariants.add(new StemVariant("vis" + resultStem.substring(0, resultStem.length()-3)+"gā", AttributeNames.i_Degree, AttributeNames.v_Superlative)); //slodzīt -> slogu
						else if (resultStem.endsWith("cē"))
							stemVariants.add(new StemVariant("vis" + resultStem.substring(0, resultStem.length()-2)+"ka", AttributeNames.i_Degree, AttributeNames.v_Superlative)); //mācēt -> māk
						else if (resultStem.endsWith("lē"))
							stemVariants.add(new StemVariant("vis" + resultStem.substring(0, resultStem.length()-2)+"ļa", AttributeNames.i_Degree, AttributeNames.v_Superlative)); //gulēt -> guļam
						else if (resultStem.endsWith("dē") )
							stemVariants.add(new StemVariant("vis" + resultStem.substring(0, resultStem.length()-2)+"ža", AttributeNames.i_Degree, AttributeNames.v_Superlative)); //sēdēt -> sēž
						else if (resultStem.endsWith("dzē"))
							stemVariants.add(new StemVariant("vis" + resultStem.substring(0, resultStem.length()-3)+"ga", AttributeNames.i_Degree, AttributeNames.v_Superlative)); //vajadzēt -> vajag
					}
					break;
				case 34: // īpašības vārdiem -āk- un vis- izskaņām kā -ajam: liekam nevis zaļ-š->zaļ-ajam, bet zaļ-š->zaļ-a-jam, bet pēdēj-ais -> pēdē-jam/pēdēj-a-jam
					if (resultStem.endsWith("ēj")) // pēdēj-ais -> pēdē-jam
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1),AttributeNames.i_Degree,AttributeNames.v_Positive));
					else // zaļ-š -> zaļa-jam
						stemVariants.add(new StemVariant(resultStem +"a",AttributeNames.i_Degree,AttributeNames.v_Positive));

					stemVariants.add(new StemVariant(resultStem + "āka",AttributeNames.i_Degree,AttributeNames.v_Comparative));
					if (addSuperlative)
						stemVariants.add(new StemVariant("vis" + resultStem + "āka",AttributeNames.i_Degree,AttributeNames.v_Superlative));
					break;
				case 35: // Substantivizējušamies "īpašības vārdiem" izskaņām kā -ajam: liekam nevis zaļ-š->zaļ-ajam, bet zaļ-š->zaļ-a-jam, bet pēdēj-ais -> pēdē-jam/pēdēj-a-jam; bez pārākās/vispārākās pakāpes
					if (resultStem.endsWith("ēj")) // pēdēj-ais -> pēdē-jam
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1),AttributeNames.i_Degree,AttributeNames.v_Positive));
					else // zaļ-š -> zaļa-jam
						stemVariants.add(new StemVariant(resultStem +"a",AttributeNames.i_Degree,AttributeNames.v_Positive));
					break;
				case 36: // 'iet' speciālgadījums - normāli 3. personas tagadnei atbilstošais resultStem būtu 'ej', bet ir 'iet'.
					if (resultStem.endsWith("ej") && thirdStem.endsWith("gāj"))
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-2)+"iet"));
					else stemVariants.add(new StemVariant(resultStem));
					break;
				case 38: // apstākļa vārdi ar gradāciju
					stemVariants.add(new StemVariant(resultStem,AttributeNames.i_Degree,AttributeNames.v_Positive));
					if (resultStem.endsWith("i") || resultStem.endsWith("u")) {
						resultStem = resultStem.substring(0, resultStem.length()-1);
					}
					stemVariants.add(new StemVariant(resultStem + "āk",AttributeNames.i_Degree,AttributeNames.v_Comparative));
					if (addSuperlative)
						stemVariants.add(new StemVariant("vis" + resultStem + "āk",AttributeNames.i_Degree,AttributeNames.v_Superlative));
					break;

				// ------ LATGALIAN from here -----
				case 99: // puse no latgaliešu 'burtu mijas' (case 100) - tikai paradigmām, kur ir garantēts, ka bezmijas resultStem beidzas mīkstu līdzskani
					stemVariants.add(new StemVariant(ltgLetterMijaSoftToHard(resultStem)));
					break;
				case 100: // // latgaliešu 'burtu mija', kur pirms -e, -i, -ī, -ē, -ie ļ, ņ, ķ, ģ kļūst par l, n, k, g (bruoļs -> bruoli)
					stemVariants.add(new StemVariant(ltgLetterMijaSoftToHard(resultStem)));
					break;
				case 101: // latgaliešu līdzskaņu mija lietvārdiem, parastās galotnes (izņemot -i, -e, -ī, -ē, -ie, -ei)
					// Mijas no Leikumas "Vasals!"
					if (resultStem.endsWith("kst")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 3) + "kš"));
					} else if (resultStem.endsWith("sl")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "šļ"));
					} else if (resultStem.endsWith("zl")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "žļ"));
					} else if (resultStem.endsWith("sm")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "šm"));
					} else if (resultStem.endsWith("sn")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "šņ"));
					} else if (resultStem.endsWith("zn")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "žņ"));
					} else if (resultStem.endsWith("ll")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "ļļ"));
					} else if (resultStem.endsWith("nn")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "ņņ"));
					} else if (resultStem.endsWith("c")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "č"));
					} else if (resultStem.endsWith("d")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "ž"));
					} else if (resultStem.endsWith("s")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "š"));
					} else if (resultStem.endsWith("t")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "š"));
					} else if (resultStem.endsWith("z")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "ž"));
						// Citas mijas
					} else if (resultStem.endsWith("k")) { // Andronovs?
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "ķ"));
					} else if (resultStem.endsWith("l")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "ļ"));
					} else if (resultStem.endsWith("n")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "ņ"));
					} else {
						stemVariants.add(new StemVariant(resultStem));
					}
					break;
				case 102: // // latgaliešu līdzskaņu mīkstināšana lietvārdiem, e, i, ē, ī, ie galotnes
					if (resultStem.endsWith("kst")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 3) + "kš"));
					} else // Burtu miju ietekmētās vairāksimbolu mijas
					if (resultStem.endsWith("šļ") || resultStem.endsWith("sl")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "šl"));
					} else if (resultStem.endsWith("žļ") || resultStem.endsWith("zl")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "žl"));
					} else if (resultStem.endsWith("šm") || resultStem.endsWith("sm")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "šm"));
					} else if (resultStem.endsWith("šņ") || resultStem.endsWith("sn")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "šn"));
					} else if (resultStem.endsWith("žņ") || resultStem.endsWith("zn")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "žn"));
					} else if (resultStem.endsWith("ļļ")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "ll"));
					} else if (resultStem.endsWith("ņņ")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "nn"));
						// Parastās, mijīgās, mijas
					} else if (resultStem.endsWith("c")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "č"));
					} else if (resultStem.endsWith("s")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "š"));
					} else if (resultStem.endsWith("t")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "š"));
					} else if (resultStem.endsWith("z")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "ž"));
					} else if (resultStem.endsWith("d")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "ž"));
						// Burtu miju ietekmētās viensimbola mijas
					} else if (resultStem.endsWith("ļ")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "l"));
					} else if (resultStem.endsWith("ņ")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "n"));
					} else if (resultStem.endsWith("ķ")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "k"));
					} else if (resultStem.endsWith("ģ")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "g"));
					} else {
						stemVariants.add(new StemVariant(resultStem));
					}
					break;
				case 103: // līdzīgi 'case 3' - īpašības vārdiem pieliekam -uok- un vys- / vysu-
					stemVariants.add(new StemVariant(resultStem, ltgDegreeFlags(AttributeNames.v_Positive)));
					if (!resultStem.endsWith("uok")) {
						stemVariants.add(new StemVariant(resultStem + "uok", ltgDegreeFlags(AttributeNames.v_Comparative)));
						if (addSuperlative) {
							stemVariants.add(new StemVariant("vys" + resultStem + "uok", ltgDegreeFlags(AttributeNames.v_Superlative)));
							stemVariants.add(new StemVariant("vysu" + resultStem + "uok", ltgDegreeFlags(AttributeNames.v_Superlative)));
						}
					}
					break;
				case 104: // īpašības vārdiem pieliekam -uok- un vys- / vysu- + burtu mija
					stemVariants.add(new StemVariant(ltgLetterMijaSoftToHard(resultStem), ltgDegreeFlags(AttributeNames.v_Positive)));
					if (!resultStem.endsWith("uok")) {
						stemVariants.add(new StemVariant(resultStem + "uok", ltgDegreeFlags(AttributeNames.v_Comparative)));
						if (addSuperlative) {
							stemVariants.add(new StemVariant("vys" + resultStem + "uok", ltgDegreeFlags(AttributeNames.v_Superlative)));
							stemVariants.add(new StemVariant("vysu" + resultStem + "uok", ltgDegreeFlags(AttributeNames.v_Superlative)));
						}
					}
					break;
				case 105: // līdzīgi kā case 34 -  īpašības vārdiem -uok- un vys- / vysu- izskaņām kā -ajam: liekam nevis moz-s->moz-ajam, bet moz-s->moz-a-jam, bet senej-ais -> sene-jam/sene-a-jam
					if (resultStem.endsWith("ēj") || resultStem.endsWith("ej")) // senej-ais -> sene-jam
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length()-1), ltgDegreeFlags(AttributeNames.v_Positive)));
					else // moz-s -> moza-jam
						stemVariants.add(new StemVariant(resultStem +"a", ltgDegreeFlags(AttributeNames.v_Positive)));

					stemVariants.add(new StemVariant(resultStem + "uoka", ltgDegreeFlags(AttributeNames.v_Comparative)));
					if (addSuperlative) {
						stemVariants.add(new StemVariant("vys" + resultStem + "uoka", ltgDegreeFlags(AttributeNames.v_Superlative)));
						stemVariants.add(new StemVariant("vysu" + resultStem + "uoka", ltgDegreeFlags(AttributeNames.v_Superlative)));
					}
					break;
				case 106: // līdzīgi 'case 13' - apstākļa vārdiem pieliekam -uok- un vys- / vysu- - būtībā 103, bet bez pamatformas
					if (!resultStem.endsWith("uok")) {
						stemVariants.add(new StemVariant(resultStem + "uok", ltgDegreeFlags(AttributeNames.v_Comparative)));
						if (addSuperlative) {
							stemVariants.add(new StemVariant("vys" + resultStem + "uok", ltgDegreeFlags(AttributeNames.v_Superlative)));
							stemVariants.add(new StemVariant("vysu" + resultStem + "uok", ltgDegreeFlags(AttributeNames.v_Superlative)));
						}
					}
					break;
				case 107: // latgaliešu 'burtu mijas' inverss - kad pamatformas galotne ir -e, -i, -ī, -ē, -ie, un l, n, k, g kļūst par ļ, ņ, ķ, ģ pirms citām galotnēm (slapnis)
					stemVariants.add(new StemVariant(ltgLetterMijaHardToSoftUnambiguous(resultStem)));
					break;
				case 108: // 107 + 106 priekš slapnis
					if (!resultStem.endsWith("uok")) {
						stemVariants.add(new StemVariant(ltgLetterMijaHardToSoftUnambiguous(resultStem) + "uok", ltgDegreeFlags(AttributeNames.v_Comparative)));
						if (addSuperlative) {
							stemVariants.add(new StemVariant("vys" + ltgLetterMijaHardToSoftUnambiguous(resultStem) + "uok", ltgDegreeFlags(AttributeNames.v_Superlative)));
							stemVariants.add(new StemVariant("vysu" + ltgLetterMijaHardToSoftUnambiguous(resultStem) + "uok", ltgDegreeFlags(AttributeNames.v_Superlative)));
						}
					}
					break;
				case 109: // apstākļa vārdi ar gradāciju, bet bez burtu mijas
					stemVariants.add(new StemVariant(resultStem, ltgDegreeFlags(AttributeNames.v_Positive)));
					if (resultStem.endsWith("ai")) {
						resultStem = resultStem.substring(0, resultStem.length()-2);
					} else if (resultStem.endsWith("i") || resultStem.endsWith("a")) {
						resultStem = resultStem.substring(0, resultStem.length()-1);
					}
					stemVariants.add(new StemVariant(resultStem + "uok", ltgDegreeFlags(AttributeNames.v_Comparative)));
					if (addSuperlative) {
						stemVariants.add(new StemVariant("vys" + resultStem + "uok", ltgDegreeFlags(AttributeNames.v_Superlative)));
						stemVariants.add(new StemVariant("vysu" + resultStem + "uok", ltgDegreeFlags(AttributeNames.v_Superlative)));
					}
					break;
				// Te sākas verbu mijas
				case 110: // 2. konjugācija, vienkāršās tagadnes mija
					if (resultStem.endsWith("uo")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "o"));
					} else if (resultStem.endsWith("ei")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "e"));
					} else if (resultStem.endsWith("ē")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "e"));
					}
					break;
				case 111: // 2. konjugācija, vienkāršās pagātnes vsk. 1., 2. pers. mija
					if (resultStem.endsWith("uo")) {
						stemVariants.add(new StemVariant(resultStem + "j"));
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "ov"));
					} else if (resultStem.endsWith("ei")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "ej"));
					} else if (resultStem.endsWith("ē")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "iej"));
					}
					break;
				case 112: // 2. konjugācija, vienkāršās pagātnes dsk. un 3. pers. mija
					if (resultStem.endsWith("uo")) {
						stemVariants.add(new StemVariant(resultStem + "j"));
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "ov"));
					} else if (resultStem.endsWith("ei")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "ej"));
					} else if (resultStem.endsWith("ē")) {
						stemVariants.add(new StemVariant(resultStem + "j"));
					}
					break;
				case 113: // 2. konjugācija, vienkāršās nākotnes vsk. 1., 2. pers. mija
					if (resultStem.endsWith("ē")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "ie"));
					} else if (resultStem.endsWith("ei") || resultStem.endsWith("uo")) {
						stemVariants.add(new StemVariant(resultStem));
					}
					break;
				case 114: // 2. konjugācija, vēlējuma izteiksme, supīns un ciešamās kārtas pagātnes (-ts) divdabis, -dams divdabis
					if (resultStem.endsWith("ē")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "ā"));
					} else if (resultStem.endsWith("ei") || resultStem.endsWith("uo")) {
						stemVariants.add(new StemVariant(resultStem));
					}
					break;
				case 115: // 2. konjugācija, divdabju formu vispārākā pakāpe (121.) + tagadnes mija (110.)
					if (resultStem.endsWith("uo")) {
						String atvCelms = resultStem.substring(0, resultStem.length() - 2) + "o";
						stemVariants.add(new StemVariant(atvCelms, ltgDegreeFlags(AttributeNames.v_Comparative)));
						if (addSuperlative) {
							stemVariants.add(new StemVariant("vys" + atvCelms, ltgDegreeFlags(AttributeNames.v_Superlative)));
							stemVariants.add(new StemVariant("vysu" + atvCelms, ltgDegreeFlags(AttributeNames.v_Superlative)));
						}
					} else if (resultStem.endsWith("ei")) {
						String atvCelms = resultStem.substring(0, resultStem.length() - 2) + "e";
						stemVariants.add(new StemVariant(atvCelms, ltgDegreeFlags(AttributeNames.v_Comparative)));
						if (addSuperlative) {
							stemVariants.add(new StemVariant("vys" + atvCelms, ltgDegreeFlags(AttributeNames.v_Superlative)));
							stemVariants.add(new StemVariant("vysu" + atvCelms, ltgDegreeFlags(AttributeNames.v_Superlative)));
						}
					} else if (resultStem.endsWith("ē")) {
						String atvCelms = resultStem.substring(0, resultStem.length() - 1) + "e";
						stemVariants.add(new StemVariant(atvCelms, ltgDegreeFlags(AttributeNames.v_Comparative)));
						if (addSuperlative) {
							stemVariants.add(new StemVariant("vys" + atvCelms, ltgDegreeFlags(AttributeNames.v_Superlative)));
							stemVariants.add(new StemVariant("vysu" + atvCelms, ltgDegreeFlags(AttributeNames.v_Superlative)));
						}
					}
					break;
				case 116: // 2. konjugācija, divdabju formu vispārākā pakāpe (121.) + vēlējuma/supīna mija (114.) (-ts divdabim)
					if (resultStem.endsWith("ē")) {
						String atvCelms = resultStem.substring(0, resultStem.length() - 1) + "ā";
						stemVariants.add(new StemVariant(atvCelms, ltgDegreeFlags(AttributeNames.v_Comparative)));
						if (addSuperlative) {
							stemVariants.add(new StemVariant("vys" + atvCelms, ltgDegreeFlags(AttributeNames.v_Superlative)));
							stemVariants.add(new StemVariant("vysu" + atvCelms, ltgDegreeFlags(AttributeNames.v_Superlative)));
						}
					} else if (resultStem.endsWith("ei") || resultStem.endsWith("uo")) {
						stemVariants.add(new StemVariant(resultStem, ltgDegreeFlags(AttributeNames.v_Comparative)));
						if (addSuperlative) {
							stemVariants.add(new StemVariant("vys" + resultStem, ltgDegreeFlags(AttributeNames.v_Superlative)));
							stemVariants.add(new StemVariant("vysu" + resultStem, ltgDegreeFlags(AttributeNames.v_Superlative)));
						}
					}
					break;
				case 117: // 2. konjugācija, pagātnes mija -s, -use divdabim (vienkāršota 111.)
					if (resultStem.endsWith("uo")) {
						stemVariants.add(new StemVariant(resultStem));
					} else if (resultStem.endsWith("ei")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2) + "e"));
					} else if (resultStem.endsWith("ē")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1) + "ie"));
					}
					break;
				case 118: // 2. konjugācija, divdabju formu vispārākā pakāpe (121.) + pag. mija -s, -use divdabim (117.)
					if (resultStem.endsWith("ei")) {
						String atvCelms = resultStem.substring(0, resultStem.length() - 2) + "e";
						stemVariants.add(new StemVariant(atvCelms, ltgDegreeFlags(AttributeNames.v_Comparative)));
						if (addSuperlative) {
							stemVariants.add(new StemVariant("vys" + atvCelms, ltgDegreeFlags(AttributeNames.v_Superlative)));
							stemVariants.add(new StemVariant("vysu" + atvCelms, ltgDegreeFlags(AttributeNames.v_Superlative)));
						}
					} else if (resultStem.endsWith("ē")) {
						String atvCelms = resultStem.substring(0, resultStem.length() - 1) + "ie";
						stemVariants.add(new StemVariant(atvCelms, ltgDegreeFlags(AttributeNames.v_Comparative)));
						if (addSuperlative) {
							stemVariants.add(new StemVariant("vys" + atvCelms, ltgDegreeFlags(AttributeNames.v_Superlative)));
							stemVariants.add(new StemVariant("vysu" + atvCelms, ltgDegreeFlags(AttributeNames.v_Superlative)));
						}
					} else if (resultStem.endsWith("uo")) {
						stemVariants.add(new StemVariant(resultStem, ltgDegreeFlags(AttributeNames.v_Comparative)));
						if (addSuperlative) {
							stemVariants.add(new StemVariant("vys" + resultStem, ltgDegreeFlags(AttributeNames.v_Superlative)));
							stemVariants.add(new StemVariant("vysu" + resultStem, ltgDegreeFlags(AttributeNames.v_Superlative)));
						}
					}
					break;
				case 119: // 3. konjugācija, standarta -eit, tagadne un pagātne (līdzskaņu mijas nekad nav)
					if (resultStem.endsWith("ei")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 2)));
					}
					break;
				case 120: // 3. konjugācija, standarta -eit bez līdskaņu mijas, divdabju formu vispārākā pakāpe + tagadnes un pagātnes mija (119.)
					if (resultStem.endsWith("ei")) {
						String atvCelms = resultStem.substring(0, resultStem.length() - 2);
						stemVariants.add(new StemVariant(atvCelms, ltgDegreeFlags(AttributeNames.v_Comparative)));
						if (addSuperlative) {
							stemVariants.add(new StemVariant("vys" + atvCelms, ltgDegreeFlags(AttributeNames.v_Superlative)));
							stemVariants.add(new StemVariant("vysu" + atvCelms, ltgDegreeFlags(AttributeNames.v_Superlative)));
						}
					}
					break;
				case 121: // 3. konjugācija, divdabju formu vispārākā pakāpe bez mijas
					stemVariants.add(new StemVariant(resultStem, ltgDegreeFlags(AttributeNames.v_Comparative)));
					if (addSuperlative) {
						stemVariants.add(new StemVariant("vys" + resultStem, ltgDegreeFlags(AttributeNames.v_Superlative)));
						stemVariants.add(new StemVariant("vysu" + resultStem, ltgDegreeFlags(AttributeNames.v_Superlative)));
					}
					break;
				case 122: // 3. konjugācija, standarta -eit, tagadne ar līdzskaņu miju
					if (resultStem.endsWith("ļdei")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 4) + "ld"));
					} else if (resultStem.endsWith("ņdei")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 4) + "nd"));
					} else if (resultStem.endsWith("dzei")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 4) + "g"));
					} else if (resultStem.endsWith("cei")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 3) + "k"));
					} else if (resultStem.endsWith("lei")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 3) + "ļ"));
					} else if (resultStem.endsWith("nei")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 3) + "ņ"));
					}
					break;
				case 123: // 3. konjugācija, standarta -eit ar līdskaņu miju, divdabju formu vispārākā pakāpe + tagadnes mija (122.)
					String atvCelms = resultStem;
					if (resultStem.endsWith("ļdei")) {
						atvCelms = resultStem.substring(0, resultStem.length() - 4) + "ld";
					} else if (resultStem.endsWith("ņdei")) {
						atvCelms = resultStem.substring(0, resultStem.length() - 4) + "nd";
					} else if (resultStem.endsWith("dzei")) {
						atvCelms = resultStem.substring(0, resultStem.length() - 4) + "g";
					} else if (resultStem.endsWith("cei")) {
						atvCelms = resultStem.substring(0, resultStem.length() - 3) + "k";
					} else if (resultStem.endsWith("lei")) {
						atvCelms = resultStem.substring(0, resultStem.length() - 3) + "ļ";
					} else if (resultStem.endsWith("nei")) {
						atvCelms = resultStem.substring(0, resultStem.length() - 3) + "ņ";
					} else break;

					stemVariants.add(new StemVariant(atvCelms, ltgDegreeFlags(AttributeNames.v_Comparative)));
					if (addSuperlative) {
						stemVariants.add(new StemVariant("vys" + atvCelms, ltgDegreeFlags(AttributeNames.v_Superlative)));
						stemVariants.add(new StemVariant("vysu" + atvCelms, ltgDegreeFlags(AttributeNames.v_Superlative)));
					}
					break;
				case 124: // 3. konjugācija, standarta -ēt, tagadne un pagātne bez līdzskaņu un burtu mijas
					if (resultStem.endsWith("ē")) {
						stemVariants.add(new StemVariant(resultStem.substring(0, resultStem.length() - 1)));
					}
					break;
				case 125: // 3. konjugācija, standarta -ēt, tagadne bez līdzskaņu mijas ar inverso burtu miju
					if (resultStem.endsWith("ē")) {
						stemVariants.add(new StemVariant(ltgLetterMijaHardToSoftUnambiguous(resultStem.substring(0, resultStem.length() - 1))));
					}
					break;
				case 126: // 3. konjugācija, standarta -ēt bez līdskaņu un burtu mijas, divdabju formu vispārākā pakāpe + tagadnes un pagātnes mija (119.)
					if (resultStem.endsWith("ē")) {
						atvCelms = resultStem.substring(0, resultStem.length() - 1);
						stemVariants.add(new StemVariant(atvCelms, ltgDegreeFlags(AttributeNames.v_Comparative)));
						if (addSuperlative) {
							stemVariants.add(new StemVariant("vys" + atvCelms, ltgDegreeFlags(AttributeNames.v_Superlative)));
							stemVariants.add(new StemVariant("vysu" + atvCelms, ltgDegreeFlags(AttributeNames.v_Superlative)));
						}
					}
					break;
				case 127: // 3. konjugācija, standarta -ēt bez līdskaņu mijas ar inverso burtu miju, divdabju formu vispārākā pakāpe + tagadnes un pagātnes mija (119.)
					if (resultStem.endsWith("ē")) {
						atvCelms = ltgLetterMijaHardToSoftUnambiguous(resultStem.substring(0, resultStem.length() - 1));
						stemVariants.add(new StemVariant(atvCelms, ltgDegreeFlags(AttributeNames.v_Comparative)));
						if (addSuperlative) {
							stemVariants.add(new StemVariant("vys" + atvCelms, ltgDegreeFlags(AttributeNames.v_Superlative)));
							stemVariants.add(new StemVariant("vysu" + atvCelms, ltgDegreeFlags(AttributeNames.v_Superlative)));
						}
					}
					break;
				default:
					System.err.printf("Invalid StemChange ID, stem '%s', stemchange %d\n", resultStem, mija);
			}
		} catch (StringIndexOutOfBoundsException e){
			new PrintWriter(new OutputStreamWriter(System.err, StandardCharsets.UTF_8)).printf(
					"StringIndexOutOfBounds, resultStem '%s', mija %d\n", stem, stemChange);
			e.printStackTrace();
		}

		return stemVariants;
	}

	protected static String ltgVowelMijaLemmaToForm(String celms)
	{
		Pattern p = Pattern.compile("(.*?)(ai|ei|ui|oi|ie|[aāeēiīouūy])([bcčdfgģhjkķlļmnņprŗsštvzž]+[aāeēiīyoōuū]*)$");
		Matcher m = p.matcher(celms);
		if (m.matches()) {
			switch (m.group(2)) {
				case "a":
					return m.group(1) + 'o' + m.group(3);
				case "e":
					return m.group(1) + 'a' + m.group(3);
				case "ē":
					return m.group(1) + 'ā' + m.group(3);
				case "i":
					return m.group(1) + 'y' + m.group(3);
				default:
					return celms;
			}
		}
		return celms;
	}

	protected static String ltgVowelMijaFormToLemma(String celms)
	{
		Pattern p = Pattern.compile("(.*?)(uo|[aāeēiīouūy]|)([bcčdfgģhjkķlļmnņprŗsštvzž]+[aāeēiīyoōuū]*)$");
		Matcher m = p.matcher(celms);
		if (m.matches()) {
			switch (m.group(2)) {
				case "a":
					return m.group(1) + 'e' + m.group(3);
				case "ā":
					return m.group(1) + 'ē' + m.group(3);
				case "y":
					return m.group(1) + 'i' + m.group(3);
				case "o":
					return m.group(1) + 'a' + m.group(3);
				default:
					return celms;
			}
		}
		return celms;
	}

	/**
	 * Latgalian 'letter change' ("burtu mija):
	 * from ļ, ņ, ķ, ģ to l, n, k, g to . Used if:
	 * - lemma to form transfromation when form ending is -e, -i, -ī, -ē, -ie (bruoļs -> bruoli),
	 * - form to lemma for slapnis.
	 */
	protected static String ltgLetterMijaSoftToHard(String celms)
	{
		if (celms.endsWith("ļļ")) {
			return celms.substring(0, celms.length() - 2) + "ll";
		} else if (celms.endsWith("ņņ")) {
			return celms.substring(0, celms.length() - 2) + "nn";
		} else if (celms.endsWith("ļ")) {
			return celms.substring(0, celms.length() - 1) + "l";
		} else if (celms.endsWith("ņ")) {
			return celms.substring(0, celms.length() - 1) + "n";
		} else if (celms.endsWith("ķ")) {
			return celms.substring(0, celms.length() - 1) + "k";
		} else if (celms.endsWith("ģ")) {
			return celms.substring(0, celms.length() - 1) + "g";
		} else {
			return celms;
		}
	}

	/**
	 * Inverted Latgalian 'letter change' ("burtu mija):
	 * from l, n, k, g to ļ, ņ, ķ, ģ. Used if:
	 * - lemma to form transfromation when lemma ending is -e, -i, -ī, -ē, -ie (slapnis),
	 * - form to lemma when paradigm solves ambiguity.
	 */
	protected static String ltgLetterMijaHardToSoftUnambiguous(String stem)
	{
		if (stem.endsWith("ll")) {
			return stem.substring(0, stem.length() - 2) + "ļļ";
		} else if (stem.endsWith("nn")) {
			return stem.substring(0, stem.length() - 2) + "ņņ";
		} else if (stem.endsWith("l")) {
			return stem.substring(0, stem.length() - 1) + "ļ";
		} else if (stem.endsWith("n")) {
			return stem.substring(0, stem.length() - 1) + "ņ";
		} else if (stem.endsWith("k")) {
			return stem.substring(0, stem.length() - 1) + "ķ";
		} else if (stem.endsWith("g")) {
			return stem.substring(0, stem.length() - 1) + "ģ";
		} else {
			return stem;
		}
	}

	/**
	 * For latgalian superlative degree made with `vys` or `vysu` is always
	 * undesirable. The grammatically correct way to make superlative is to make
	 * an analytical form with `pots`.
	 */
	protected static AttributeValues ltgDegreeFlags (String degree)
	{
		AttributeValues feats = new AttributeValues();
		feats.addAttribute(AttributeNames.i_Degree, degree);
		if (degree != null && degree.equals(AttributeNames.v_Superlative))
			feats.addAttribute(AttributeNames.i_Normative, AttributeNames.v_Undesirable);
		return feats;
	}
}
