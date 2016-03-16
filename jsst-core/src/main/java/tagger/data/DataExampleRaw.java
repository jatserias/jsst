package tagger.data;

/**
 * Data Example Representation (Straight forward Raw Strings Values). 
 * 
 * @author jordi
 *
 */
public class DataExampleRaw {

	boolean hasPOS;
	boolean hasLemma;
	
	int cpos=0;
	///Label
	String[] labels;
	
	// words
	String[] W;
	
	// POS
	String[] P;
	
	//LEMMA
	String[] L;
	
	//ID
	String id;
	
	public DataExampleRaw(int size) {
		cpos=0;
	    labels = new String[size];	
	    W = new String[size];	
	    P = new String[size];
	    L = new  String[size];
	}
	
	public String getWord(int i){return W[i];}
	public String getPos(int i){return P[i];}
	public String getLemma(int i){ return L[i];}
	
	public void setId(String id) {
		this.id=id;
	}
	
    ///@TODO implement this method
	public void addFeature(String feat) {
		
	}
	
	public void addLabel(String label, String word, String pos, String lemma) {
		W[cpos]=word;
		labels[cpos]=label;
		P[cpos]=pos;
		L[cpos]=lemma;
	}
	
}
