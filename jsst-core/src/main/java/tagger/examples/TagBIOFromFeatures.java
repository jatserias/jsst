package tagger.examples;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Vector;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.martiansoftware.jsap.JSAP;

import tagger.core.SttTagger;
import tagger.core.SttTaggerSparse;
import tagger.data.FV;
import tagger.data.ModelDescription;
import tagger.data.ReaderFV;
import tagger.data.SstTagSet;
import tagger.data.SstTagSetBIO;
import tagger.data.SstTagSetPOS;
import tagger.features.FeatureBuilderBasic;
import tagger.features.FeatureEncoderDecoder;
import tagger.features.FeatureHashEncoderDecoder;
import tagger.utils.FileDescription;

public class TagBIOFromFeatures {

	private static final String ENCODING = "UTF-8";
	static Logger logger = Logger.getLogger(TagPosAndNER.class);

	public static void main(String args[]) throws Exception{
		 BasicConfigurator.configure();
	  
		 final com.martiansoftware.jsap.SimpleJSAP jsap = new com.martiansoftware.jsap.SimpleJSAP( TagWithSST.class.getName(), 
					"SST Tagging",
					new com.martiansoftware.jsap.Parameter[] {
			 				new com.martiansoftware.jsap.UnflaggedOption( "input", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "input" ),
			 				new com.martiansoftware.jsap.UnflaggedOption( "output", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "output" ),
			 				new com.martiansoftware.jsap.UnflaggedOption( "model", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "Model Name" ),
							new com.martiansoftware.jsap.UnflaggedOption( "tagset", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "tagset" ),	
							new com.martiansoftware.jsap.Switch("lemma", 'l', "lemma", "has lemma" ),
							new com.martiansoftware.jsap.Switch("pos", 'p', "pos", "has pos" ),
							new com.martiansoftware.jsap.Switch("res", 'r', "res", "has res" ),
						}		
					);
			
			final com.martiansoftware.jsap.JSAPResult jsapResult = jsap.parse( args );
			if ( jsap.messagePrinted() ) return;
 
		
	    logger.warn("warning Start");

		
	    
	     String inputFile=	 jsapResult.getString("input");	
		 String outputFile =jsapResult.getString("output");	
		 String modelName= jsapResult.getString("model");
		 String tagsetname= jsapResult.getString("tagset");	 
		 boolean hasRes=false;
		 boolean useUNK=false;
		 
		 SstTagSet tagset = new SstTagSetBIO(new FileDescription(tagsetname, ENCODING),useUNK);
		 ModelDescription model = new ModelDescription(modelName, tagset, ENCODING, false);
		 FeatureEncoderDecoder fe = new FeatureHashEncoderDecoder(model);
		 FeatureBuilderBasic	fb = new  FeatureBuilderBasic();			 
		 SttTaggerSparse catTagger= new  SttTaggerSparse(fe,fb, model, true);
		  
		        PrintStream out;
		        if(outputFile.compareTo("stdout")==0) 
		        	out=System.out;
		        else out= new PrintStream(outputFile);
		        
		    try {    
			    BufferedReader fin;
		        if(inputFile.compareTo("stdin")==0)	fin= new BufferedReader(new InputStreamReader(System.in));
		        else fin = new BufferedReader(new FileReader(inputFile));
			       
			   String buff;
								 
				while( (buff=fin.readLine()) != null) {	
					Vector<String> tokens = new Vector<String>();
					Vector<String> res = new Vector<String>();
					Vector<String> lemma = new Vector<String>();
					Vector<String> pos = new Vector<String>();
					

			    String[] fvpos = pos.toArray(new String[0]);	
				String[] fvlemma = lemma.toArray(new String[0]);	
			
				tokens.addAll(Arrays.asList(buff.split("[ ]")));	
				
				String[] rfinalRes = catTagger.tagSequence(tokens.toArray(new String[0]), fvpos,fvlemma);
			
				for(int rj=0;rj<rfinalRes.length;++rj) {
					out.print(tokens.get(rj)+"\t"+fvpos[rj]);
					if(rfinalRes[rj].charAt(0)=='0') out.println("\tO");
					else out.println("\t"+rfinalRes[rj]);
					if(hasRes) out.println("\t"+res.get(rj));
				}
				out.println("");
				}
		    }	
	     finally{
	    	 out.close();
	     }
	  }
}
