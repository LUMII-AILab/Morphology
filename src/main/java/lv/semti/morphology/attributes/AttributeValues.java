package lv.semti.morphology.attributes;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.json.simple.JSONValue;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//TODO - būtu vienkārši jāinherito HashMap<String, String>
public class AttributeValues implements FeatureStructure, Cloneable {
	protected HashMap<String, String> attributes = new HashMap<>();

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String,String> attribute : attributes.entrySet()) {
			sb.append(String.format("%s = %s; ", attribute.getKey(),attribute.getValue()));
		}
		return sb.toString();
	}

	public void describe() {
		PrintWriter out;
		out = new PrintWriter(new OutputStreamWriter(System.err, StandardCharsets.UTF_8));
		this.describe(out);
		out.flush();
	}

	public void describe(PrintStream pipe) {
		this.describe(new PrintWriter(pipe));
	}
	
	public void describe(PrintWriter pipe) {
		pipe.printf("\t%s\n", this.getTag());
		for (Entry<String,String> attribute : attributes.entrySet()) {
			pipe.format("\t\t%s = %s%n", attribute.getKey(),attribute.getValue());
		}
		pipe.flush();
	}

	public void addAttribute(String attribute, String value) {
		//FIXME - vajag nodalīt īpašību pielikšanu no īpašību aizvietošanas
		attributes.put(attribute, value);
	}

	public void removeAttribute(String attribute) {
		attributes.remove(attribute);
	}

	public void addAttributes(HashMap<String,String> newAttributes) {
		this.attributes.putAll(newAttributes);
		//FIXME - a ko tad, ja kautkas konfliktē??
	}

	public void addAttributes(AttributeValues newAttributes) {
		this.attributes.putAll(newAttributes.attributes);
		//FIXME - a ko tad, ja kautkas konfliktē??
	}
	
	/**
	 * Remove all attributes except those listed.
	 */
	public void filterAttributes(Collection<String> leaveAttributes) {
		attributes.keySet().retainAll(leaveAttributes);
	}

	/***
	 *  Returns null if attribute does not exist
	 */
	public String getValue(String attribute) {
		return attributes.get(attribute);
	}

	/**
	 * Returns true either if the attribute exists and matches the provided
	 * value or if attribute doesn't exist and provided value is null.  
	 */
	public boolean isMatchingStrong (String attribute, String value) {
		String result = attributes.get(attribute);
		if (result == null && value == null) return true;
		if (result == null) return false;
		if (result.contains("|")) {  // Multivalue support
			for (String v : result.split("\\|"))
				if (v.equals(value)) return true;
			return false;
		} else return result.equalsIgnoreCase(value);
	}

	/**
	 * Returns true if all attributes in provided test set are strongly
	 * matching on this, and if all attributes in this are strongly matching on
	 * attributes provided in test set.
	 */
	public boolean isMatchingStrong(AttributeValues testSet) {
		boolean match = true;
		for (Entry<String,String> aVPair : testSet.entrySet()) {
			if (!this.isMatchingStrong(aVPair.getKey(), aVPair.getValue()))
				match = false;
		}
		for (Entry<String,String> aVPair : this.entrySet()) {
			if (!testSet.isMatchingStrong(aVPair.getKey(), aVPair.getValue()))
				match = false;
		}
		return match;
	}

	/**
	 * Returns true if all attributes in provided test set are strongly
	 * matching on this, i.e. they must exist and match.
	 */
	public boolean isMatchingStrongOneSide(AttributeValues testSet) {
		boolean match = true;
		for (Entry<String,String> aVPair : testSet.entrySet()) {
			if (!this.isMatchingStrong(aVPair.getKey(), aVPair.getValue()))
				match = false;
		}
		return match;
	}
	
	/**
	 * Returns true either if the attribute exists and matches the provided
	 * value or if attribute doesn't exist.
	 */
	public boolean isMatchingWeak (String attribute, String value) {
		String result = attributes.get(attribute);
		if (result == null) return true;
		if (result.contains("|")) {  // Multivalue support
			for (String v : result.split("\\|"))
				if (v.equals(value)) return true;
			return false;
		} else return result.equalsIgnoreCase(value);
	}	// Atshkjiriiba no checkAttribute - ja atribuuta nav, bet padotaa veertiiba nav null.
		// Shii metode dod true, check attribute - false.

	/**
	 * Returns true if all attributes provided in test set weakly matches on
	 * this.
	 */
	public boolean isMatchingWeak(AttributeValues testSet) {
		if (testSet == null) return true;
		boolean der = true;
		for (Entry<String,String> attribute : testSet.entrySet()) {
			if (!this.isMatchingWeak(attribute.getKey(), attribute.getValue()))
				der = false;
		}
		return der;
	}

	
	public void toXML (Writer out) throws IOException {
		out.write("<Attributes");
		for (Entry<String,String> attribute : attributes.entrySet()) {
			String attrKey = attribute.getKey().replace(" ", "_").replace("\"", "&quot;").replace("&", "&amp;");
			if (attrKey.isEmpty()) continue;
			String attrVal = attribute.getValue().replace("\"", "&quot;").replace("&", "&amp;");
			out.write(" "+attrKey+"=\""+attrVal+"\"");
		}
		out.write("/>");
	}
	
	public String toJSON() {
		return JSONValue.toJSONString(attributes);
	}

	public Entry<String,String> get(int nr) {
	//FIXME - atgriež rediģējamu pāri... netīri kautkā, tas ir kā getteris domāts, nevis rakstīšanai..
	//jāmaina pieeja tur kur to sauc.

		Entry<String,String> result = null;
		int i=0;
		for (Entry<String,String> attribute : attributes.entrySet()) {
			if (i==nr) result = attribute;
			i++;
		}
		return result;
	}

	public int size() {
		return attributes.size();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object clone() throws CloneNotSupportedException {
		try {
			AttributeValues avClone = (AttributeValues)super.clone();
			avClone.attributes = (HashMap<String,String>)attributes.clone();
			return avClone;
        } catch (CloneNotSupportedException e) {
            throw new Error("Problem: should be able to clone AttributeValues.");
        }
	}

	public String getDescription() {
		String result = "";
		for (Entry<String,String> attribute : attributes.entrySet()) {
			if (!attribute.getKey().startsWith("Nozīme")) {
			if (result.isEmpty()) {
				result = attribute.getValue();
			} else {
				result = result + ", "/* + Īpašība.getKey() + " = "*/ + attribute.getValue();
			}
			}
		}
		return result;
	}

	public Set<Entry<String,String>> entrySet() {
	//FIXME - jākopē, lai nav editējams - vai jāmaina pieeja tur kur šo sauc.
		return attributes.entrySet();
	}

	public Set<String> keySet()
	{
		return attributes.keySet();
	}

	public AttributeValues(Node node) {
		NodeList nodes = node.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeName().equals("Attributes"))
				for (int j = 0; j < nodes.item(i).getAttributes().getLength(); j++) {
					Node n = nodes.item(i).getAttributes().item(j);
					addAttribute(n.getNodeName().replace("_", " "), n.getTextContent());
				}
		}
	}

	public AttributeValues() {
		//irok
	}

	/**
	 * Creates a new set of AttributeValues, initializing the contents from a source AV object
	 */
	public AttributeValues(AttributeValues source) {
		this.addAttributes(source);
	}

	public void clear() {
		attributes.clear();
	}
	
	/**
	 * Returns Semti-Kamols style positional morphosyntactic markup tag of this set of attributes
	 */
	public String getTag() {
		return TagSet.getTagSet().toTag(this);
	}	

	/**
	 * Removes a set of attributes that are considered not target of POS/morphotagging; mainly lexical features.
	 * NB! This set also defines which attributes will not be guessed/tagged by the automated tagger.
	 * FIXME - confusing name of function?
	 */
	public void removeNonlexicalAttributes() {
		removeAttribute(AttributeNames.i_Transitivity);
//		removeAttribute(AttributeNames.i_VerbType);
//		removeAttribute(AttributeNames.i_NounType);
		removeAttribute(AttributeNames.i_Declension);
		removeAttribute(AttributeNames.i_Konjugaacija);
		
		// removeAttribute(AttributeNames.i_ApstTips);
		removeAttribute(AttributeNames.i_SaikljaTips);
		removeAttribute(AttributeNames.i_SkaitljaTips);
		removeAttribute(AttributeNames.i_AdjectiveType);
		removeAttribute(AttributeNames.i_Uzbuuve);
		removeAttribute(AttributeNames.i_Order);
		//removeAttribute(AttributeNames.i_VvTips);
		removeAttribute(AttributeNames.i_Noliegums);
		removeAttribute(AttributeNames.i_VietasApstNoziime);
		
		if (isMatchingStrong(AttributeNames.i_PartOfSpeech, AttributeNames.v_Preposition)) {			
			removeAttribute(AttributeNames.i_Novietojums);
			//removeAttribute(AttributeNames.i_Rekcija);
			//removeAttribute(AttributeNames.i_Number);
		}
		
		//Principā analizators no galotnes varētu izdomāt, BET ir nepieciešams lai samazinātu klašu skaitu CRF tagerim
		removeAttribute(AttributeNames.i_Degree);
		removeAttribute(AttributeNames.i_Reflexive);
//		removeAttribute(AttributeNames.i_Laiks);  // FIXME - piemēram, ēdu mēdzu zīmēju nestrādās nošķirt pagātne tagadne
		removeAttribute(AttributeNames.i_Voice);
	}

	/**
	 * Removes a set of attributes that are ignored in the MorphoEvaluate test for corpus comparison
	 */
	public void removeAttributesForCorpusTest() {
		removeAttribute(AttributeNames.i_Transitivity);
		removeAttribute(AttributeNames.i_ApstTips);
		removeAttribute(AttributeNames.i_AdjectiveType);
//		removeAttribute(AttributeNames.i_VietasApstNoziime);
		if (isMatchingStrong(AttributeNames.i_PartOfSpeech, AttributeNames.v_Preposition)) {
			removeAttribute(AttributeNames.i_Novietojums);
		}
	}
	
	public void removeTechnicalAttributes() {
		removeAttribute(AttributeNames.i_LexemeID);
		removeAttribute(AttributeNames.i_EndingID);
		removeAttribute(AttributeNames.i_ParadigmID);
		removeAttribute(AttributeNames.i_Source);
		removeAttribute(AttributeNames.i_Word);
		removeAttribute(AttributeNames.i_Mija);
		removeAttribute(AttributeNames.i_Guess);
		removeAttribute(AttributeNames.i_Generate);
		removeAttribute(AttributeNames.i_Konjugaacija);
		removeAttribute(AttributeNames.i_Declension);
	}

	public StringBuilder pipeDelimitedEntries() {
		StringBuilder s = new StringBuilder();
		for (Entry<String, String> entry : this.entrySet()) { // visi attributevalue paariishi
			 s.append(entry.getKey().replace(' ', '_'));
			 s.append('=');
			 s.append(entry.getValue().replace(' ', '_'));
			 s.append('|');
		}
		return s;
	}

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof AttributeValues)) {
            return false;
        }
        AttributeValues that = (AttributeValues) other;

        if (this.attributes == null) {
			return that.attributes == null;
        } else return this.attributes.equals(that.attributes);
	}

    @Override
    public int hashCode() {
        return this.attributes.hashCode();
    }
}
