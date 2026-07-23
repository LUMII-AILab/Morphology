package lv.semti.morphology.analyzer;

import java.io.Serial;
import java.util.Map;
import java.util.LinkedHashMap;

public class Cache<K, V> extends LinkedHashMap<K, V> {
	@Serial
	private static final long serialVersionUID = -6922479578059244274L;
	private int maxSize = 10000;

	public Cache(){
		super(10, 0.75f, true);
	}

	public Cache(int maxSize){
		super(10, 0.75f, true);
		this.maxSize = maxSize;
	}
	
	public void setSize(int maxSize) {
		this.maxSize = maxSize;		
	}
	
	protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
		return size() > maxSize;
	} 
}
