package tagger.data.readers;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.Vector;

import tagger.utils.FileDescription;

public class CorpusTransformerCONLLDUTCH {
	private static final String ENCODING = "UTF-8";

	public static void  main(String args[]) throws Exception  {
		   
		 String [] margs = {
//				 			"/Users/jordi/Desktop/NEGerman/conll2002/ner/data/ned.utf8.train", 
//				             "/Users/jordi/Desktop/NEGerman/conll2002/ner/data/ned.utf8.testa",
//		 					 "/Users/jordi/Desktop/NEGerman/conll2002/ner/data/ned.utf8.testb",
				             
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
			
			
			int nsen=1;  
			while( (buff=fin.readLine()) != null) {	
				if(!buff.startsWith("-DOC")) {
			    if(buff.trim().length()==0) {
			    	
			    	// new sentence
			    	int i=0;
			    	fnerc.print(nsen);
			    	for(String w:words) {
			    		fnerc.print("\t"+w+" "+pos.get(i)+" "+w+" "+tags.get(i));
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
				  pos.add(fields[1]);
				  if(fields[2].compareTo("O")==0)  tags.add("0"); else tags.add(fields[2]);
			    }
			}
			}
	  fnerc.close();
	}
		 
	}
}
