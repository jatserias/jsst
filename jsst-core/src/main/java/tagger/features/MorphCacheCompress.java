package tagger.features;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import it.unimi.dsi.bits.TransformationStrategies;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.io.BinIO;

import it.unimi.dsi.io.FileLinesCollection;
import it.unimi.dsi.io.FileLinesCollection.FileLinesIterator;
import it.unimi.dsi.lang.MutableString;
import it.unimi.dsi.sux4j.mph.MWHCFunction;
import it.unimi.dsi.util.FrontCodedStringList;
import it.unimi.dsi.util.ShiftAddXorSignedStringMap;

public class MorphCacheCompress implements Lemmatizer, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int posMaxLength = 1;
	
	///stores  wordform+"_"+pos id to lemma id
	Int2IntOpenHashMap wordform2lemmaMap = new Int2IntOpenHashMap();
	
	/// mph  to map string (lemmas/wordforms) into ids
	ShiftAddXorSignedStringMap mph;
	
	/// FrontcodedStringlist  to recover the string values
	FrontCodedStringList fcl;
	
	/// all pos values
	Set<String> posSet;
	
	/**
	 * It should be more a transformation than a constructor
	 * 
	 * @throws IOException
	 */
	public MorphCacheCompress(String filename) throws IOException {
		posSet= new HashSet<String>();
		SortedSet<String> ss = new TreeSet<String>();
		FileLinesCollection flc = new FileLinesCollection(filename, "UTF-8");
		FileLinesIterator it = flc.iterator();
		SortedMap<String, HashMap<String, String>> morph_cache = new TreeMap<String, HashMap<String,String> >();
		while(it.hasNext()) {
			MutableString s=it.next();
			String [] buff= s.toString().split("[\t ]");
		    String pos = buff[0];
		    String w = buff[1];
		    String wm = buff[2];
		    
		    if (pos.length() > posMaxLength) //JAB FIX
		      pos = pos.substring(0,posMaxLength);
		    String wl = w.toLowerCase();
		    if(w.compareTo(wm)!=0) {
		     ss.add(wl+"_"+pos);
		     ss.add(wm);
		     posSet.add(pos);
		     
		    //This is quite inconsistent with the way we look up things
		    //if (wl != wm){
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
		
		// keys of the map are the POS so no need to keep all values
		// needs to be a sort iterator
		Iterator<? extends CharSequence> words = ss.iterator();//morph_cache.keySet().iterator();
		fcl = new FrontCodedStringList(words,32, true);
		
		
	
		
		//MinimalPerfectHashFunction<? extends CharSequence> j = new MinimalPerfectHashFunction(fcl, TransformationStrategies.prefixFreeUtf16());
		MWHCFunction<? extends CharSequence> j = new MWHCFunction(fcl, TransformationStrategies.prefixFreeUtf16());
		
		words = ss.iterator();
	    mph = new ShiftAddXorSignedStringMap(words, j);
	 
		System.err.println(fcl.size());
		//BinIO.storeObject(fcl,"wf.ser");
		//traves the hash
		for(Entry<String, HashMap<String, String>> es : morph_cache.entrySet()) {
			for(Entry<String, String> x : es.getValue().entrySet()) {
				Long k=mph.get(x.getKey()+"_"+es.getKey());
				Long v=mph.get(x.getValue());
				if(x.getKey().compareTo("busied")==0) 
					System.err.println("value:"+x.getKey()+"_"+es.getKey()+":"+k+" "+x.getValue()+":"+v);
				
				if(wordform2lemmaMap.containsKey(k.intValue())) {System.err.println("KK KEY:"+k);}
				wordform2lemmaMap.put(k.intValue(), v.intValue());
			}
		}
		
	}
	
	@Override
	public String get_lemma(String wordForm) {
		Iterator<String> it = posSet.iterator();
		while(it.hasNext()) {
			String r= get_lemma(wordForm,it.next());
			if(r!=null) return r;
		}
		return wordForm;
	}

	@Override
	public String get_lemma(String wordForm, String pos) {
		//System.err.println("WF:"+mph.get(wordForm+"_"+pos).intValue());
		//System.err.println("LEMMA:"+wordform2lemmaMap.get(mph.get(wordForm+"_"+pos).intValue()));
		Long wid = mph.get(wordForm+"_"+pos);
		if(wid==null) return wordForm;
		return fcl.get(wordform2lemmaMap.get(wid.intValue())).toString();
	}

	
	public static void main(String args[]) throws Exception {
		MorphCacheCompress r = new MorphCacheCompress("DATA/MORPH_CACHE");
		BinIO.storeObject(r,"wf.ser");
		System.err.println("LOOKUP:"+r.get_lemma("busied","V"));
		MorphCacheCompress r2 =  (MorphCacheCompress) BinIO.loadObject("wf.ser");
		System.err.println("LOOKUP:"+r2.get_lemma("busied","V"));
	}
}
