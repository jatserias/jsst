package tagger.data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;

import tagger.core.PS_HMM;
import tagger.core.SttTagger;
import tagger.core.SttTagger.tagmode;
import tagger.utils.FileDescription;
import tagger.utils.Utils;

/**
 * @author jordi
 * A simple class to wrap the description of a model 
 */
public class ModelDescription extends FileDescription {
	
	 
	 
	  
	  /// name of the tagset used
	  public SstTagSet tagset;
	  
	  /// to use second order features [@TODO NOT TESTED]
	  public boolean secondOrder=false;
	  
	  
	  ///@TODO save ergodic on the model
	  public boolean ergodic;
	  
	
	  
	 
	  /// BIO o POS 
	  SttTagger.tagmode mode;
	  
	  
	  
	  
	  
	  public ModelDescription(boolean inJar,String path, SstTagSet tagset, String encoding,boolean compress, boolean secondorder) {
		  super(path,encoding, compress, inJar);
			
		  this.mode=tagset.mode;
		  this.tagset=tagset;
		 
		  
		  //@TODO READ these from the model
		  ergodic=true;
		  this.secondOrder=secondorder;
	  }
	  
	  public ModelDescription(String path, SstTagSet tagset, String encoding,boolean compress) {
			 super(path,encoding, compress);
			
			 this.inJar=false;
			  this.mode=tagset.mode;
			  this.tagset=tagset;
			 
			  
			  //@TODO READ these from the model
			  ergodic=true;
			  this.secondOrder=false;
		  }

	  public ModelDescription(boolean inJar,String path, SstTagSet tagset, String encoding,boolean compress) {
		 super(path,encoding, compress);
		
		 this.inJar=inJar;
		  this.mode=tagset.mode;
		  this.tagset=tagset;
		 
		  
		  //@TODO READ these from the model
		  ergodic=true;
		  this.secondOrder=false;
	  }

	  
	public BufferedReader openFeatureFile() throws FileNotFoundException, IOException {
		String filename = path+PS_HMM.FEATURE_MODEL_FILENAME_EXTENSION;
		//if(compress) filename=filename+Utils.GZIP_FILENAME_EXTENSION;
		return new FileDescription(filename, encoding, compress,inJar).open();
	}
	
@Override
	public String buildFilename() {
		String modelname = path + PS_HMM.PS_HMM_MODEL_FILENAME_EXTENSION;
	   if(compress) modelname += Utils.GZIP_FILENAME_EXTENSION;
	   return modelname;	

	}
	public void dump(Writer out) throws IOException {
		out.append("Model Settings:");
		out.append("\nErgodic: "); out.append(ergodic ? "YES" : "NO");
		out.append("\nMODE: "); out.append(tagset.mode==tagmode.BIO ? "BIO" : "POS");
		out.append("\n2ndOrder: "); out.append(secondOrder? "YES" : "NO");
		
		tagset.dump(out);
	}

}
