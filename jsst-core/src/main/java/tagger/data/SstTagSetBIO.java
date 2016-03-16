package tagger.data;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Vector;

import tagger.core.SttTagger.tagmode;
import tagger.utils.FileDescription;


/**
 * A Tagset class for BIO tagsets
 * 
 * @author jordi
 *
 */
public class SstTagSetBIO extends SstTagSet {
	
	 
	public static final String O_TAG = "0";
	public static final char I_TAGPREF = 'I';
	public static final char B_TAGPREF = 'B';
	public static final String encodingDefault="UTF-8";


	public SstTagSetBIO(FileDescription tagset) throws Exception {
	    super();
		init(tagset,false);
		mode=tagmode.BIO;
	}
	
	 
	 public SstTagSetBIO(FileDescription tagset, boolean addUnk) throws Exception {
		 super();
		init(tagset,addUnk);
		mode=tagmode.BIO;
		//this.dump();
	 }



	@Override
	   /// fix bIO tags @TODO Vectro-array
	   public   Vector<String> checkConsistency(Vector<Integer> ms){
	     	Vector<String> OUT = new Vector<String>();
	     	  long N = ms.size();
	     	  String prev = O_TAG;
	     	  for (int i = 0; i < N; ++i){
	     		String swi = LSIS.getLabel(ms.get(i));
	     	    
	     		  if (swi.charAt(0) == I_TAGPREF && (prev.equals(O_TAG) || !(prev.substring(1).equals(swi.substring(1))))) {
	     			  swi = B_TAGPREF+swi.substring(1);
	     		  }
	     	    OUT.add(swi); 
	     	    prev=swi;  
	     	  }
	     	  return OUT;
	     	}
	   
	   /// fix bIO tags @TODO Vectro-array
	   @Override
	   public   String[] checkConsistency(int[] ms) {
	   	 String[] OUT = new String[ms.length];
	   	  long N = ms.length;
	   	  String prev = O_TAG;
	   	  for (int i = 0; i < N; ++i){
	   		
	   		  // start error check
	   		if(i>=ms.length) {logger.warn("Error on pos "+i+" > "+ms.length);}
	   		if(ms[i]>LSIS.size()) {logger.warn("Error on label "+ms[i]+" > "+LSIS.size());}
	   		if(ms[i]>=LSIS.size()) {logger.warn("Error on label "+ms[i]+" > "+LSIS.size()); StringWriter sw = new StringWriter();try {
				LSIS.dump(sw);
			} catch (IOException e) {
				e.printStackTrace();
			}logger.warn(sw);}
	   		 // end error check
	   		String swi = LSIS.getLabel(ms[i]);
	   	    
	   	    if (swi.charAt(0) == I_TAGPREF && (prev.equals(O_TAG) || !(prev.substring(1).equals(swi.substring(1))))) {
	   	    	//LOG logger.warn("ReWritting inconsistent sequence:"+prev+" , "+swi);
	   	    	swi = B_TAGPREF+swi.substring(1);
	   	    }
	   	    OUT[i]=(swi); 
	   	    prev=swi;  
	   	  }
	   	  return OUT;
	   	}
	   
}
