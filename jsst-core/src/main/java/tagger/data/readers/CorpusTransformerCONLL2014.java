package tagger.data.readers;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.Vector;

import tagger.utils.FileDescription;

public class CorpusTransformerCONLL2014 {
	private static final String ENCODING = "UTF-8";
	public static void  main(String args[]) throws Exception  {
		   
		 String [] margs = {
			      "/Users/jordi/Desktop/NEGerman/NER-de-train.tsv", 
			      "/Users/jordi/Desktop/NEGerman/NER-de-dev.tsv", 
			     
		 };
		 
		 if(args!=null && args.length>0) margs=args;
		 
		 boolean useUNK=false;
		 
		 for(String file:margs) {
		  PrintStream fnerc = new PrintStream(file+".sst", ENCODING);
		
		   BufferedReader fin = new FileDescription(file, ENCODING, false,false).open();
		       
		   String buff;
		   Vector<String> words = new Vector<String>();
		   Vector<String> pos = new Vector<String>();
		   Vector<String> tags = new Vector<String>();
		   Vector<String> lemmas = new Vector<String>();
		   Vector<String> atts = new Vector<String>();
			int nsen=1;  
			while( (buff=fin.readLine()) != null) {	
				if(!buff.startsWith("#\t")) {
			    if(buff.trim().length()==0) {
			    	if(!words.isEmpty()){
			    
			    	
			    	// new sentence
			    	int i=0;
			    	fnerc.print(nsen);
			    	for(String w:words) {
			    		fnerc.print("\t"+w+" "+atts.get(i)+" "+tags.get(i));
			    		++i;
			    	}
			    	fnerc.println();
			    	words.clear();
			    	tags.clear();
			    	atts.clear();
			    	++nsen;
			    	}
			    }
			    else {
			    
				  String[] fields = buff.split("[ \t]");
				  words.add(fields[1]);
				  //lemmas.add(fields[1]);
				  //pos.add(fields[2]);
				 
				  if(fields[2].compareTo("O")==0)  atts.add("0"); else atts.add(fields[2]);
				  if(fields[3].compareTo("O")==0)  tags.add("0"); else tags.add(fields[3]);
			    }
			    
			}
			}
	  fnerc.close();
	}
		 
	}
}