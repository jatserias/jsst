package tagger.data;

import java.io.IOException;
import java.io.Writer;
import java.util.Vector;

import org.apache.log4j.Logger;

import tagger.core.SttTagger.tagmode;
import tagger.utils.FileDescription;
import tagger.utils.Hmap;
/**
 * A super class to represent a TagSet
 * @author jordi
 *
 */
public class SstTagSet {
	static Logger logger = Logger.getLogger(SstTagSet.class);

	/// String label for UNKnown values
	public final static String UNK_LABEL ="UNK";
	/// Internal Integer id for UNKnown values
	public int unknown_id;
	
	/// The name of the TagSet (for pretty print only)
	protected String tagsetname;
	
	/// The integer -> Label map
	public Hmap LSIS;

	/// Tagging mode e.g. BIO or POS
	public tagmode mode;

	
	
	public void dump(Writer out) throws IOException {
	  out.append("\nName:"+tagsetname);
	  out.append("\nUNK ID:"+unknown_id);
	  out.append("\nLabels:\n");
	  LSIS.dump(out);
	  
	}
	
	

	void initLabels(FileDescription tagset) throws Exception {
	    	LSIS = new Hmap(Hmap.modeList, tagset.open());
	    	logger.info("***Labels in thetagset#"+LSIS.size());
	 }
	 
	
	
	 public void init(FileDescription tagset, boolean addUNK) throws Exception {
			tagsetname=tagset.getName();
			initLabels(tagset);
			if(addUNK) {
				unknown_id=LSIS.add_update_hmap(UNK_LABEL);
			}
			else{
				unknown_id=-1;
			}
			
		}

	    /// decode  int_features  into str_features @TODO Vector-array
	   public  Vector<String>  decode_tags(Vector<Integer> ms) {
	    	Vector<String> O_str = new Vector<String>();
	    	 for(int i=0;i<ms.size();++i) {
	    		    O_str.add(LSIS.getLabel(ms.get(i)));
	    		  }
	    	 return O_str;
	    }
	   
	 /// decode  int_features  into str_features @TODO Vectro-array
	   public  String[]  decode_tags(int[] ms) {
	    	String[] O_str = new String[ms.length];
	    	 for(int i=0;i<ms.length;++i) {
	    		    O_str[i]=(LSIS.getLabel(ms[i]));
	    		  }
	    	 return O_str;
	    }
	   
	   ///@TODO fix BIO TAGS Vector-array
	   public   Vector<String> checkConsistency(Vector<Integer> ms){
	     	Vector<String> OUT = new Vector<String>();
	     	  long N = ms.size();
	     	  //String prev = "0";
	     	  for (int i = 0; i < N; ++i){
	     		String swi = LSIS.getLabel(ms.get(i));
	     	    OUT.add(swi); 
	     	    //prev=swi;  
	     	  }
	     	  return OUT;
	     	}
	   
	   ///@TODO fix bIO tags @TODO Vectro-array
	   public   String[] checkConsistency(int[] ms){
	   	 String[] OUT = new String[ms.length];
	   	  long N = ms.length;
	   	  //String prev = "0";
	   	  for (int i = 0; i < N; ++i){
	   		String swi = LSIS.getLabel(ms[i]);
	   	    
	   	    OUT[i]=(swi); 
	   	   /// prev=swi;  
	   	  }
	   	  return OUT;
	   	}
	   

	   public int LSIS_add_update_hmap(String w) {
	   	return LSIS.add_update_hmap(w);
	   }
	   

		//@Override
		public String LSIS(int nid) {
			return LSIS.getLabel(nid);
		}

		public int size() {
			if(unknown_id==-1) return LSIS.size();
			else return (LSIS.size()-1);
		}
		
		//@Override
		public int LSIS_size() {
			if(unknown_id==-1) return LSIS.size();
			else return (LSIS.size()-1);
		}
		
		public Vector<String> getListLabel() {
			return LSIS.getListLabel();
		}


		public String getName() {
			return tagsetname;
		}
		
		public int getLabelId(String label) { return LSIS.getLabelId(label);}
}
