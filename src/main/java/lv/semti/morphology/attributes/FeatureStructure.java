package lv.semti.morphology.attributes;
//FIXME - jānosauc savādāk
import java.util.HashMap;
import java.util.Map.Entry;

public interface FeatureStructure {
	void addAttribute(String attribute, String value);
	void addAttributes(HashMap<String,String> attributes);
	String getValue(String attribute);
	boolean isMatchingStrong (String attribute, String value);
	Entry<String,String> get(int nr);
	int size();
}
