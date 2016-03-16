package tagger.features;
/**
 * Morph cache format per line
 *  
 * PoS \t wordForm \t Lemma
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;



import org.apache.log4j.Logger;

import tagger.utils.ClassChooser;
import tagger.utils.FileDescription;



/**
 * @author jordi
 * 
 * @TODO reduce memory fingerprint (e.g. implementing a SMPH)
 * 
 * an object for doing morphological analysis using a cache
 */
public class MorphCache implements Lemmatizer {
	
static Logger logger = Logger.getLogger(MorphCache.class);
	
/// Two hashmap POS => HASH<word form => lemma>	
HashMap<String, HashMap<String,String> > morph_cache;

/// Maximum PoS Length for POS cache
final static int posMaxLength=2;

///get WordNet lemma
public  String get_lemma(String wl,String pos){
	 //@TODO cache is lower case 
	 if (pos.length() > posMaxLength) //JAB FIX
	      pos = pos.substring(0,posMaxLength);
	  Map<String,String> morph=morph_cache.get(pos);
	  if (morph!=null){
	    String w_i = morph.get(wl);
	    if (w_i != null) {
	    	return w_i;
	    }
	    else { 
	    	wl =  wl.toLowerCase(); 
	    	w_i = morph.get(wl);
		    if (w_i != null) {
		    	return w_i;
		    }	
	    }
	  }
	 // Check / trace unkown Pos
	 // else{
	 //   logger.warn("PoS >"+pos+"< not found");
	 // }
	  
	  return wl;
 }


public MorphCache(FileDescription file) throws FileNotFoundException, IOException {
	morph_cache= new HashMap<String, HashMap<String,String> >();
	//@JAB BFS load(file.path,file.encoding);
	load(file.open());
}


void load(BufferedReader fin) throws IOException {
	String input;
while ((input=fin.readLine())!=null){
    
    String [] buff= input.split("[\t ]");
    String pos = buff[0];
    String w = buff[1];
    String wm = buff[2];
    
    if (pos.length() > posMaxLength) //JAB FIX
      pos = pos.substring(0,posMaxLength);
    
    //This is quite inconsistent with the way we look up things
      String wl = w.toLowerCase();
    if (wl != wm){
    	HashMap<String,String>val = morph_cache.get(pos);
      if (val==null){
	    HashMap<String,String> tmp=new HashMap<String,String>();
		tmp.put(wl,wm);
		morph_cache.put(pos,tmp);
      }
      else {
    	  String wl_val = val.get(wl);
    	  if (wl_val==null)
    		  val.put(wl,wm);
      	}
    }
  }
  logger.info("\t|M| = "+morph_cache.size());
}

int size() { return morph_cache.size();}
/**
 * @BUG
 * We should return lemma's list 
 * lemma's feature should be provided (if requested) but they are  calculated after call basic extractFeatures (??) 
 *
 * extract features should be redesigned?
 * 
 * The file input has 3 field per line tab separated: PoS WordForm Lemma
 * 
 * JJ      11th    11th
 * 
 */
//public  List<String> getLemmasVector(String[] L, String[] P) {
//	//@JAB implementation
//	List<String> WM = ClassChooser.createListString(); //new Vector<String>();
//	int _L_ = L.length;
//	for (int i = 0; i < _L_; ++i){
//		WM.add(get_lemma(L[i], P[i]));
//	}
//	return WM;
//}
/**
* 
* @TODO P length and L length differs (??)
* 
* @param L array containing the word forms
* @param P array containing the PoS
* @return
*/
//public  String[] getLemmas(final String[] L, final String[] P) {
//	int _L_ = L.length;
//	String[] WM = new String[_L_];
//
//	for (int i = 0; i < _L_; ++i){
//		WM[i]=(get_lemma(L[i], P[i]));
//	}
//	return WM;
//}




@Override
public String get_lemma(String wordForm) {
	// return first matching
	for(Entry<String, HashMap<String, String>> x : morph_cache.entrySet()) {
		HashMap<String, String> h = x.getValue();
		if(h.containsKey(wordForm)) return h.get(wordForm);
	}
	
	return wordForm;
}



}