package tagger.data.readers;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.Vector;

import tagger.core.SttTagger;
import tagger.features.FeatureBuilderBasic;
import tagger.features.FeatureEncoderDecoder;
import tagger.features.FeatureHashEncoderDecoder;
import tagger.utils.FileDescription;

public class CorpusTransformerCONLLDE {
	private static final String ENCODING = "UTF-8";
	public static void  main(String args[]) throws Exception  {
		   
		 String [] margs = {
			      "/Users/jordi/Desktop/NEGerman/deu.train.utf8", 
		             "/Users/jordi/Desktop/NEGerman/deu.testa.utf8",
					 "/Users/jordi/Desktop/NEGerman/deu.testb.utf8"
				             

		 
		 };
		 boolean useUNK=false;

		
		 for(String file:margs) {
		  PrintStream fnerc = new PrintStream(file+".sst", ENCODING);
		
		   BufferedReader fin = new FileDescription(file, ENCODING, false,false).open();
		       
		   String buff;
		   Vector<String> words = new Vector<String>();
		   Vector<String> pos = new Vector<String>();
		   Vector<String> tags = new Vector<String>();
		   Vector<String> lemmas = new Vector<String>();
			
			int nsen=1;  
			while( (buff=fin.readLine()) != null) {	
				if(!buff.startsWith("-DOC")) {
			    if(buff.trim().length()==0) {
			    	if(!words.isEmpty()){
			    
			    	
			    	// new sentence
			    	int i=0;
			    	fnerc.print(nsen);
			    	for(String w:words) {
			    		fnerc.print("\t"+w+" "+pos.get(i)+" "+lemmas.get(i)+" "+tags.get(i));
			    		++i;
			    	}
			    	fnerc.println();
			    	words.clear();
			    	tags.clear();
			    	++nsen;
			    	}
			    }
			    else {
			    
				  String[] fields = buff.split("[ \t]");
				  words.add(fields[0]);
				  lemmas.add(fields[1]);
				  pos.add(fields[2]);
				  if(fields[4].compareTo("O")==0)  tags.add("0"); else tags.add(fields[4]);
			    }
			    
			}
			}
	  fnerc.close();
	}
		 
	}

}
