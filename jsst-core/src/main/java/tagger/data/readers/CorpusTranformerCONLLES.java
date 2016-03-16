package tagger.data.readers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Vector;

import tagger.core.SttTagger;
import tagger.data.ModelDescription;
import tagger.data.SstTagSetPOS;
import tagger.features.FeatureBuilderBasic;
import tagger.features.FeatureEncoderDecoder;
import tagger.features.FeatureHashEncoderDecoder;
import tagger.utils.FileDescription;


public class CorpusTranformerCONLLES {

	private static final String ENCODING = "UTF-8";

	public static void  main(String args[]) throws Exception  {
		   
		 String [] margs = {"/Users/jordi/development/lingpipe-4.1.0/ner/data/esp.train", "/Users/jordi/development/lingpipe-4.1.0/ner/data/esp.testa"};
		 boolean useUNK=false;

		 String tagsetPosName= 	"/home/jordi/data/projects/cap/bcndata/es/espos.tagset";
		 String modelPosName="/home/jordi/data/projects/cap/bcndata/es/esposmodel";
		
		 SstTagSetPOS posTagset = new SstTagSetPOS(new FileDescription(tagsetPosName, ENCODING),useUNK);
		ModelDescription posModel = new ModelDescription(modelPosName, posTagset, ENCODING, false);
		 FeatureEncoderDecoder posfe = new FeatureHashEncoderDecoder(posModel);
		 FeatureBuilderBasic posfb = new  FeatureBuilderBasic();
		 SttTagger posTagger= new  SttTagger(posfe,posfb, posModel, true);
		 
		 for(String file:margs) {
		  PrintStream fnerc = new PrintStream(file+".sst", ENCODING);
		
		   BufferedReader fin = new FileDescription(file, "ISO-8859-1", false,false).open();
		       
		   String buff;
		   Vector<String> words = new Vector<String>();
		   Vector<String> tags = new Vector<String>();
			
			
			int nsen=1;  
			while( (buff=fin.readLine()) != null) {	
			    if(buff.trim().length()==0) {
			    	String[] fvpos = posTagger.tagSequence(words.toArray(new String[0]), new String[0]);
			    	// new sentence
			    	int i=0;
			    	fnerc.print(nsen);
			    	for(String w:words) {
			    		fnerc.print("\t"+w+" "+fvpos[i]+" "+w+" "+tags.get(i));
			    		++i;
			    	}
			    	fnerc.println();
			    	words.clear();
			    	tags.clear();
			    	++nsen;
			    }
			    else {
				  String[] fields = buff.split("[ \t]");
				  words.add(fields[0]);
				  if(fields[1].compareTo("O")==0)  tags.add("0"); else tags.add(fields[1]);
			    }
			}
	  fnerc.close();
	}
		 
	}
		
}
