package lv.semti.morphology.corpus;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import lv.semti.morphology.attributes.AttributeNames;
import lv.semti.morphology.attributes.AttributeValues;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class handles frequency statistics that assess the more probable
 * analysis variant.
 */
public class Statistics {
	
	/**
	 * Default filename for statistics file.
	 */
	public final static String DEFAULT_STATISTICS_FILE = "Statistics.xml";
	public double lexemeWeight = 1000; //How much lexeme count should be weighed as a multiple of ending count
	// Determined/verified empirically by testing on corpus; coefficient of 100 instead of 1000 gives 0.2% decrease
	
	/**
	 * Lexeme frequencies, indexed by lexeme IDs.
	 */
	public HashMap<Integer, Integer> lexemeFrequency = new HashMap<>();
	/**
	 * Ending frequencies, indexed by ending IDs.
	 */
	public HashMap<Integer, Integer> endingFrequency = new HashMap<>();
	
	/**
	 * Add one occurrence of one lexeme.
	 * @param lexemeId	lexeme identifier.
	 */
	public void addLexeme(int lexemeId) {
		int count = 1;
		if (lexemeFrequency.get(lexemeId) != null)
			count = lexemeFrequency.get(lexemeId) + 1;
		lexemeFrequency.put(lexemeId, count);
	}
	/**
	 * Add one occurrence of one ending.
	 * @param endingId	ending identifier.
	 */
	public void addEnding(int endingId) {
		int count = 1;
		if (endingFrequency.get(endingId) != null)
			count = endingFrequency.get(endingId) + 1;
		endingFrequency.put(endingId, count);
	}

	/**
	 * Convert frequency data in XML format.
	 * @param stream	output stream.
	 */
	public void toXML (Writer stream)
	throws IOException {
		stream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		stream.write("<Statistics>\n");
		stream.write("<EndingFrequency\n");
		for (Entry<Integer,Integer> tuple : endingFrequency.entrySet()) {
			stream.write(" Ending_"+tuple.getKey().toString()+"=\""+tuple.getValue().toString()+"\"");
		}
		stream.write("/>\n");

		stream.write("<LexemeFrequency>\n");
		for (Entry<Integer, Integer> tuple : lexemeFrequency.entrySet()) {
			if (tuple.getValue() > 1) {
				stream.write("  <Lexeme id=\"" + tuple.getKey() + "\" count=\"" + tuple.getValue() + "\"/>\n");
			}
		}
		stream.write("</LexemeFrequency>\n");
		stream.write("</Statistics>\n");
		stream.flush();
	}

	private static Statistics singleton;
	
	public static Statistics getStatistics() {
		if (singleton == null)
			try {
			    singleton = new Statistics(DEFAULT_STATISTICS_FILE);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                singleton = new Statistics();
            }
		return singleton;
	}
	
	public static Statistics getStatistics(String fileName) {
		if (singleton == null)
			try {
				singleton = new Statistics(fileName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
                singleton = new Statistics();
			}
		return singleton;
	}

	
	/**
	 * Create empty statistics object.
	 */
	public Statistics() {
		// var arī bez nekā
	}

	/**
	 * Create statistics object from XML file.
	 * @param fileName	input file.
	 */
	private Statistics(String fileName)
	throws SAXException, IOException, ParserConfigurationException {

		Document doc;

		InputStream stream = this.getClass().getClassLoader().getResourceAsStream(fileName);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		// For ending frequencies all endings for some reason are attributes and
		// in case of Latgalian that is A LOT.
		dbf.setAttribute("jdk.xml.elementAttributeLimit", 20000);
		DocumentBuilder docBuilder = dbf.newDocumentBuilder();
		doc = docBuilder.parse(stream);

		Node node = doc.getDocumentElement();
		if (!node.getNodeName().equalsIgnoreCase("Statistics"))
			throw new Error("Node " + node.getNodeName() + " nav Statistics!");

		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeName().equals("EndingFrequency"))
				for (int j = 0; j < nodes.item(i).getAttributes().getLength(); j++) {
					Node n = nodes.item(i).getAttributes().item(j);
					int endingId = Integer.parseInt(n.getNodeName().substring(n.getNodeName().indexOf('_')+1));
					int count = Integer.parseInt(n.getTextContent());
					endingFrequency.put(endingId, count);
				}
			if (nodes.item(i).getNodeName().equals("LexemeFrequency")) {
				NodeList children = nodes.item(i).getChildNodes();
				for (int j = 0; j < children.getLength(); j++) {
					Node child = children.item(j);
					if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals("Lexeme")) {
						Element el = (Element) child;
						int lexemeId = Integer.parseInt(el.getAttribute("id"));
						int count = Integer.parseInt(el.getAttribute("count"));
						lexemeFrequency.put(lexemeId, count);
					}
				}
			}
		}
	}

	/**
	 * Cumulative frequency estimate for given wordform.
	 * @param wordform	wordform to describe.
	 * @return			lexeme frequency + ending frequency.
	 */
	public double getEstimate(AttributeValues wordform) {
		double estimate = 0.1;
		String endingIdStr = wordform.getValue(AttributeNames.i_EndingID);
		int endingId = (endingIdStr == null) ? -1 : Integer.parseInt(endingIdStr);
		if (endingFrequency.get(endingId) != null) estimate += endingFrequency.get(endingId);

		String lexemeIdStr = wordform.getValue(AttributeNames.i_LexemeID);
		int lexemeId = (lexemeIdStr == null) ? -1 : Integer.parseInt(lexemeIdStr);
		if (lexemeFrequency.get(lexemeId) != null) estimate += lexemeFrequency.get(lexemeId) * lexemeWeight;

		return estimate;
	}


}
