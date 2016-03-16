package tagger.features;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * A naive implementation storing string
 * @author jordi
 *
 */
public class FeatureVector {

    static final String[] EMPTY =  new String[0];
    
	List<String> data;

	public FeatureVector() {
		data=new ArrayList<String>();
	}
	
	public void add(String kf) {
		data.add(kf);
	}

	/// Add the encoded value
	public void add(int i) {
		// TODO Auto-generated method stub		
	}

	public String get(int i) {
		return data.get(i);
	}

	public int size() {
		return data.size();
	}

	public String[] getStringFeatures() {
		return data.toArray(EMPTY);
	}
		
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("JORDI");
		for(String fea:data) {
			sb.append(" ");
			sb.append(fea);
		}
		return sb.toString();
	}
}
