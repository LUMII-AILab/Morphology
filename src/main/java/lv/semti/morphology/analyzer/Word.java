package lv.semti.morphology.analyzer;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;

import lv.semti.morphology.attributes.TagSet;
import org.json.simple.JSONValue;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lv.semti.morphology.attributes.AttributeNames;
import lv.semti.morphology.attributes.AttributeValues;
import lv.semti.morphology.corpus.Statistics;

/**
 * Morphologically analyzed token with potentially multiple variants of
 * analysis.
 * 
 * @author Pēteris Paikensklons
 */
public class Word extends Observable implements Cloneable{

	private String token;
	public ArrayList<Wordform> wordforms = new ArrayList<>();
	private Wordform correctWordform = null;

	public Word (String token) {
		this.token = token.trim();
		this.wordforms = new ArrayList<>(1);
	}

	public Word(Node node) {
		if (node.getNodeName().equalsIgnoreCase("Word")) {
			NodeList nodes = node.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node n = nodes.item(i);
				if (n.getNodeName().equalsIgnoreCase("Wordform"))
					wordforms.add(new Wordform(n));
			}

			Node n = node.getAttributes().getNamedItem("Word");
			if (n != null)
				token = n.getTextContent();
			n = node.getAttributes().getNamedItem("correctWordform");
			if (n != null)
				setCorrectWordform(wordforms.get(Integer.parseInt(n.getTextContent())));

		} else if (node.getNodeName().equalsIgnoreCase("Wordform")) {
			token = node.getAttributes().getNamedItem("token").getTextContent();
			wordforms.add(new Wordform(node));
		} else throw new Error("Node " + node.getNodeName() + " nav ne Word, ne Wordform");
	}

	@Override
	public String toString() {
		return token;
	}

	@Override
	public Object clone() {
		try {
			Word wordCopy = (Word)super.clone();
			wordCopy.token = this.token;
			wordCopy.wordforms = new ArrayList<>();
			for (Wordform wordform : wordforms) {
				Wordform wordformCopy = (Wordform) wordform.clone();
				wordCopy.wordforms.add(wordformCopy);
				if (this.getCorrectWordform() == wordform)
					wordCopy.setCorrectWordform(wordformCopy);
			}
			return wordCopy;
        } catch (CloneNotSupportedException e) {
            throw new Error("Gļuks - nu vajag varēt klasi Word noklonēt.");
        }
	}
	
	@Override
	public boolean equals(Object o)
	{
		try
		{
			Word w = (Word)o;
			if (token == null ^ w.token == null
					|| wordforms == null ^ w.wordforms == null
					|| correctWordform == null ^ w.correctWordform == null) return false;
			return (token == w.token || token.equals(w.token))
				&& (wordforms == w.wordforms || wordforms.equals(w.wordforms))
				&& (correctWordform == w.correctWordform || correctWordform.equals(w.correctWordform));
		} catch (ClassCastException e)
		{
			return false;
		}
	}
	
	@Override
	public int hashCode()
	{
		//return 0;
		String signature = "1117 " + token + " " + wordforms;
		// TODO: Ilmaar, paskaties.
		// It's a kind of magic: adding the lower one makes Word-s unfindable in 
		// LinkedHashMap, even there exists an key to which .equals gives true
		// and .hashCode gives the same value as for the searched object. 
//		signature = signature + " " + correctWordform + " ";
		return signature.hashCode();
	}
	
	public void addWordform (Wordform wordform){
		wordform.setToken(this.token);
		wordforms.add(wordform);
	}

	public boolean isRecognized(){
		return !wordforms.isEmpty();
	}

	public void print(PrintWriter stream){
		stream.format("Aprakstam vārdu '%s'%n", token);
		if (wordforms.isEmpty()) {
			stream.println("\tVārds nav atpazīts.\n");
		} else {
			if (wordforms.size() == 1) {
				stream.println("\tVārds ir atpazīts viennozīmīgi.\n");
				wordforms.getFirst().describe(stream);
			} else {
				stream.format("\tVārds ir atpazīts %d variantos%n", wordforms.size());
				for (Wordform variants : wordforms) {
					stream.format("\tVariants %d%n",wordforms.indexOf(variants)+1);
					variants.describe(stream);
				}
			}
		}
		stream.flush();
	}

	public void printShort(PrintWriter stream){
		if (wordforms.isEmpty()) {
			stream.printf("%s : nav atpazīts.\n", token);
		} else {
			for (Wordform variants : wordforms)
				variants.shortDescription(stream);
		}
		stream.flush();
	}

	public void addAttribute(String attribute, String value) {
		for (Wordform variants : wordforms)
			variants.addAttribute(attribute, value);
	}

	/**
	 * Gets rid of those wordforms that match (weakly) the attributes provided. Destructive!
	 */
	public void filterByAttributes(AttributeValues attributes) {
		ArrayList<Wordform> matchedForms = new ArrayList<>();

		for (Wordform wordform : wordforms) {
			if (wordform.isMatchingWeak(attributes)) matchedForms.add(wordform);
		}

		wordforms = matchedForms;
	}

	public String getToken() {
		return token;
	}

	// variantuSkaits
	public int wordformsCount() {
		return wordforms.size();
	}

	public void setCorrectWordform(Wordform wordform) {
		if (!wordforms.contains(wordform))
			throw new Error(String.format("Vārdam %s mēģina uzlikt par pareizo svešu vārdformu %s.", token, wordform.getToken()));

		correctWordform = wordform;
	}

	public Wordform getCorrectWordform() {
		return correctWordform;
	}

	public void toXML(Writer stream) throws IOException {
		stream.write("<Word");
		stream.write(" token=\"" + token.replace("\"", "&quot;") + "\"");
		if (correctWordform != null)
			stream.write(" correctWordform=\""+wordforms.indexOf(correctWordform)+"\"");
		stream.write(">\n");
		for (Wordform wordform : wordforms) {
			wordform.toXML(stream);
		}
		stream.write("</Word>");
	}
	
	public String toJSON() {
		Iterator<Wordform> i = wordforms.iterator();
		StringBuilder out = new StringBuilder("[");
		while (i.hasNext()) {
			out.append(i.next().toJSON());
			if (i.hasNext()) out.append(", ");
		}
		out.append("]");
		return out.toString();
	}
	
	public String toJSONsingle() {
		if (isRecognized()) {
			/* šis ir tad, ja vajag tikai vienu - ticamāko formu. tā jau varētu atgriezt visu sarakstu. */
			Wordform maxwf = getBestWordform();
			//return maxwf.toJSON(); TODO - varbūt arī šo te vajag atgriezt
			return String.format("{\"Vārds\":\"%s\",\"Marķējums\":\"%s\",\"Pamatforma\":\"%s\"}", JSONValue.escape(maxwf.getToken()), JSONValue.escape(maxwf.getTag()), JSONValue.escape(maxwf.getValue(AttributeNames.i_Lemma)));
		} else 
			return String.format("{\"Vārds\":\"%s\",\"Marķējums\":\"-\",\"Pamatforma\":\"%s\"}", JSONValue.escape(getToken()), JSONValue.escape(getToken()));
	}

	public Wordform getBestWordform() {
		if (wordforms.isEmpty()) return null;
		Wordform maxwf = wordforms.getFirst();
		double bestEstimate = -1;
		for (Wordform wf : wordforms) {  // Paskatamies visus atrastos variantus un ņemam statistiski ticamāko
			//tag += String.format("%s\t%d\n", wf.getDescription(), MorphoServer.statistics.getTicamība(wf));
			double estimate = Statistics.getStatistics().getEstimate(wf);
			if (estimate > bestEstimate) {
				bestEstimate = estimate;
				maxwf = wf;
			}
		}
		return maxwf;
	}

	@SuppressWarnings("unused")
	public Wordform getMatchingWordform(String answerTag, boolean complain) {
		Wordform result = null;
		AttributeValues av = TagSet.getTagSet().fromTag(answerTag);
        AttributeValues original_av = new AttributeValues(av);
        av.removeAttribute(AttributeNames.i_VerbType); // Workaround tam, ka verbu tipi ir diezgan subjektīvi - bet ja leksikonā tāds nav, tad mēs vismaz zinam visu pārējo

        //FIXME - hardcoded workaround tagera kļūdai
        //TODO - notestēt vai vēl aktuāls
		if (this.getToken().endsWith("ais") && av.isMatchingStrong(AttributeNames.i_PartOfSpeech, AttributeNames.v_Adjective) 
										    && av.isMatchingStrong(AttributeNames.i_Definiteness, AttributeNames.v_Indefinite)) {
			av.addAttribute(AttributeNames.i_Definiteness, AttributeNames.v_Definite);
		}

		double bestEstimate = -100;
		for (Wordform wf : this.wordforms) {
			if (wf.isMatchingWeak(av)) {
                double estimate = Statistics.getStatistics().getEstimate(wf);
                if (!wf.isMatchingWeak(original_av))
                    estimate -= 5;
                if (estimate > bestEstimate) {
                    bestEstimate = estimate;
                    result = wf;
                }
            }
		}
		
		if (result == null) {
			result = new Wordform(this.getToken());
			result.addAttributes(av);
			result.addAttribute(AttributeNames.i_Source, "CMM tagger guess");
			result.addAttribute(AttributeNames.i_Lemma, this.getToken()); //FIXME - most likely wrong lemma, guesser should be used to obtain a realistic one
			if (complain) System.err.printf("Tagger chose a tag that's not one of analysis options for word %s tag %s\n", this.getToken(), answerTag);
			if (complain) this.addWordform(result); //FIXME - nav īsti atbilstošs complain
		}
		if (complain && (result.getValue(AttributeNames.i_Lemma).equalsIgnoreCase("nav") || result.getValue(AttributeNames.i_Lemma).equalsIgnoreCase("nenāk"))) {
			result.describe();
		}
		
		return result;
	}

	public String toTabSepsingle() { // Čakarīgs formāts haskell-pipe-export ātrdarbībai
		if (isRecognized()) {
			Wordform maxwf = getBestWordform();
			//return maxwf.toJSON(); TODO - varbūt arī šo te vajag atgriezt
			return String.format("%s\t%s\t%s", maxwf.getToken(), maxwf.getTag(), maxwf.getValue(AttributeNames.i_Lemma));
		} else 
			return String.format("%s\t-\t%s", getToken(), getToken());
	}

	public String toTabSep(boolean probabilities) { // Čakarīgs formāts postagera pitonam
		if (isRecognized()) {
			double sumEstimate = 0;
			for (Wordform wf : wordforms) sumEstimate += Statistics.getStatistics().getEstimate(wf);
			if (sumEstimate < 0.001) sumEstimate = 0.001;
			
			Iterator<Wordform> i = wordforms.iterator();
			StringBuilder out = new StringBuilder();
			while (i.hasNext()) {
				Wordform wf = i.next();
				out.append(String.format("%s\t%s\t%s", wf.getToken(), wf.getTag(), wf.getValue(AttributeNames.i_Lemma)));
				if (probabilities) out.append(String.format("\t%.5f", Statistics.getStatistics().getEstimate(wf) / sumEstimate));
				if (i.hasNext()) out.append("\t");
			}
			return out.toString();
		} else {
			String out = String.format("%s\t-\t%s", getToken(), getToken());
			if (probabilities) out += "\t1.0";
			return out;
		}
			
	}

	/**
	 * Checks if any of the wordforms has this attribute with the specified value.
	 */
	public boolean hasAttribute(String attribute, String value){
		boolean results = false;
		for (Wordform wordform : wordforms)
			if (wordform.isMatchingStrong(attribute, value)) results = true;
		return results;
	}

	public void describe(PrintWriter pipe) {
		pipe.println(this.token);
		for (Wordform wf : wordforms)
			wf.describe(pipe);
	}

	public void describe(PrintStream out) {
		this.describe(new PrintWriter(out));
	}

}
