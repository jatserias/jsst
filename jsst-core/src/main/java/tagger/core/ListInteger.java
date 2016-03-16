package tagger.core;

import java.util.Iterator;

/**
 * 
 * A sparse representation for EncodedFeatures
 * @author jordi
 *
 */
public class ListInteger {

	
	private int[] data;
	
	public ListInteger(int i) {
		data = new int[i];
	}
	
	//initilize form array
	public ListInteger(int[] is) {
		data=is;
	}
	
	public void set(int i, int v) { data[i]=v;}
	public int get(int i) { return data[i];}
	public int size() {
		return data.length;
	}


	public int[] toArray() {
		return data;
	}

	
}
