package tagger.data;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.log4j.Logger;

import tagger.features.FeatureVector;

/**
 * 
 * A featureVector plus an id
 * 
 * @author jordi
 *
 */
public class FV {
	static Logger logger = Logger.getLogger(FV.class);
	public String id;
	public FeatureVector[] features;

public FV(String id, FeatureVector[] features) {
	this.features = features;
	this.id=id;
}

public static boolean compareVectors(FeatureVector va, FeatureVector vb,String fa,String fb) {
	for(int j=0;j<va.size();++j) {
			boolean found=false;
	int z=0;
	while(!found && z<vb.size()) {
		found= (va.get(j).compareTo(vb.get(z))==0);
		++z;
	}
	if(!found && !va.get(j).startsWith("lm"))  { 
		 logger.info("Extra Feature in "+fa+":"+va.get(j));
	    return true;
	}   
	}
	
	return false;
}

public static boolean compare(FV a, FV b,String fa,String fb) {
	if(a.id.compareTo(b.id)!=0) {logger.warn("Different ids: >"+a.id+"< vs >"+b.id+"<");}
	boolean error=false;
	//For every word
	for(int i=0;i< Math.min(a.features.length, b.features.length);++i) {
		
			error = error || compareVectors(a.features[i], b.features[i],fa+" word "+i,fb);
			error = error || compareVectors(b.features[i], a.features[i],fb+" word "+i,fa);
	}
		
	return error;
}

public void dump(Writer wr) throws IOException {
	wr.append("Ex "+id+" \n");
	for(FeatureVector w: features) {
	 	for(String f: w.getStringFeatures())
	      wr.append(" "+f+"\n");
	    wr.append("\n");
	}
}


}
