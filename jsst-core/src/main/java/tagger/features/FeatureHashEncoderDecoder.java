package tagger.features;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.apache.log4j.Logger;

import tagger.data.ModelDescription;
import tagger.utils.Hmap;

public class FeatureHashEncoderDecoder implements FeatureEncoderDecoder {
	  // A map for feature encoding
    protected Hmap FSIS;
    
	static Logger logger = Logger.getLogger(FeatureHashEncoderDecoder.class);


    void initFeatures(ModelDescription model) throws Exception {	
		FSIS = new Hmap(Hmap.modeFeatures, model.openFeatureFile());
		logger.info("***Features in the model #"+FSIS.size());
    }
    
    public FeatureHashEncoderDecoder() throws Exception {
    	FSIS = new Hmap();
    }
    
    //@TODO 
  public FeatureHashEncoderDecoder(ModelDescription model) throws Exception {
		//   use the model to constriant the features we want to encode/decode
	    logger.info("Restricting Features to a Model "+model.getName());
	     initFeatures(model);
	}

	/// encode  str_features into int_features @TODO Vectro-array
    public final  int[][] encode(final List<List<String> > O_str, final boolean secondorder) {
       	int _O_ = O_str.size();
        	int[][] O = new int[_O_][];
      
       	for (int i = 0; i < _O_; ++i){
       		List<String> list = O_str.get(i);
   			int n = list.size();
       		int[] O_i = new int[n];
       		for (int j = 0; j < n; ++j){
       			String a = list.get(j);
       			//@JAB?? adding only to the map (??)
       			O_i[j]=FSIS.add_update_hmap(a);
       			
       			//SecondOrder Features
       			if (secondorder){
       				for (int r = j+1; r < n; ++r){
       					String b = list.get(r);
       					if (a.compareTo(b)>0) //?? a< b
       						O_i[r]=(FSIS.add_update_hmap(""+a+"-"+b));
       					else 
       						O_i[r]=(FSIS.add_update_hmap(""+b+"-"+a));
       				}
       			}
       		}
       		O[i]=O_i;
       	}
        return O;
    }
    
 // encode  str_features into int_features  @TODO Vector-array
    public  final  int[][] encode(final String[][] O_str,final boolean secondorder) {
   	int _O_ = O_str.length;
    	int[][] O = new int[_O_][];
    	for (int i = 0; i < _O_; ++i){
    		String[] strings = O_str[i];
   		int n = strings.length;
    		int[] O_i = new int[n]; //@TODO if second order length is different!!!
    		for (int j = 0; j < n; ++j){
    			String a = strings[j];
    			//@JAB?? adding only to the map (??)
    			O_i[j]=(FSIS.add_update_hmap(a));
    			
    			//SecondOrder Features
    			if (secondorder){
    				for (int r = j+1; r < n; ++r){
    					String b = strings[r];
    					if (a.compareTo(b)>0) //?? a< b
    						O_i[r]=(FSIS.add_update_hmap(""+a+"-"+b));
    					else 
    						O_i[r]=(FSIS.add_update_hmap(""+b+"-"+a));
    				}
    			}
    		}
    		O[i]=O_i;
    	}
     return O;
}

   
    public boolean compare(FeatureHashEncoderDecoder fb) {
	
		if(FSIS.size()>fb.FSIS.size()) {logger.warn("FSIS size differs"); return false;}
		
		for(int i=0;i<FSIS.size();++i)
			if(FSIS.getLabel(i).compareTo(fb.FSIS.getLabel(i))!=0) {
				logger.warn("Differ at "+i+" "+FSIS.getLabel(i)+" "+fb.FSIS.getLabel(i));
				return false;
			}
			else{
				String v =FSIS.getLabel(i);
				if(FSIS.h.get(v).compareTo(fb.FSIS.h.get(v))!=0) {
					logger.warn("Differ at ("+i+")"+v+" "+FSIS.h.get(v)+" "+fb.FSIS.h.get(v));
				}
			}
		return true;
	}

	@Override
	public long encode(String feature) {
		return FSIS.add_update_hmap(feature);
	}

	@Override
	public String decode(long feature) {
		return FSIS.getLabel((int) feature);
	}

	@Override
	public long size() {
		return FSIS.size();
	}
    
	public void dump() throws IOException {
		StringWriter s = new StringWriter();
		FSIS.dump(s);
		System.err.println("JAB DUMPING LABELS:\n"+s);
	}

	@Override
	public long esize() {
		return size();
	}
}