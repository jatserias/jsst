package tagger.examples;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Vector;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import tagger.core.SttTagger;
import tagger.core.SttTaggerSparse;
import tagger.data.ModelDescription;
import tagger.data.SstTagSet;
import tagger.data.SstTagSetBIO;
import tagger.data.SstTagSetPOS;
import tagger.features.FeatureBuilderBasic;
import tagger.features.FeatureEncoderDecoder;
import tagger.features.FeatureHashEncoderDecoder;
import tagger.utils.FileDescription;

import com.martiansoftware.jsap.JSAP;

public class TagPosAndNER {
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
							new com.martiansoftware.jsap.UnflaggedOption( "modelpos", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "Model Pos Name" ),
							new com.martiansoftware.jsap.UnflaggedOption( "tagsetpos", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "Pos tagset" ),	
							new com.martiansoftware.jsap.Switch("lemma", 'l', "lemma", "has lemma" ),
							new com.martiansoftware.jsap.Switch("pos", 'p', "pos", "has pos" ),
							new com.martiansoftware.jsap.Switch("res", 'r', "res", "has res" ),
						}		
					);
			
			final com.martiansoftware.jsap.JSAPResult jsapResult = jsap.parse( args );
			if ( jsap.messagePrinted() ) return;
//		 
//	    if(args.length!=4) {
//	    	throw new Exception("four parameter needed: input-file output-file model tagset");
//	    }  
		
	    logger.warn("warning Start");

//		// Let's call the tagger 
//	     String inputFile=args[0];//e.g. /home/jordi/cawikiTok.txt
//		 String outputFile =args[1];//e.g. /home/jordi/cawiki.sst
//		 String modelName= args[2];// e.g.MODELS/"+"allnelemacatmodel
//		 String tagsetname= args[3];//e.g.benchmarks/conll/CONLL03.TAGSET	 
//		 boolean hasLemma=true;
//		 boolean hasPOS=true;
//		 boolean hasRes=false;
		
	    
	     String inputFile=	 jsapResult.getString("input");	
		 String outputFile =jsapResult.getString("output");	
		 String modelName= jsapResult.getString("model");
		 String tagsetname= jsapResult.getString("tagset");	 
		 String modelPosName= jsapResult.getString("modelpos");
		 String tagsetPosName= jsapResult.getString("tagsetpos");	 
		 boolean hasRes=false;
		 boolean useUNK=false;
		 
		 SstTagSetPOS posTagset = new SstTagSetPOS(new FileDescription(tagsetPosName, ENCODING),useUNK);
		 ModelDescription posModel = new ModelDescription(modelPosName, posTagset, ENCODING, false);
		 FeatureEncoderDecoder posfe = new FeatureHashEncoderDecoder(posModel);
		 FeatureBuilderBasic posfb = new  FeatureBuilderBasic();
		 SttTaggerSparse posTagger= new  SttTaggerSparse(posfe,posfb, posModel, true);
		 
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
				fvpos  = posTagger.tagSequence(tokens.toArray(new String[0]), fvpos, fvlemma);
				
				for(String ppos: fvpos) {
					System.err.println(ppos);
				}
			
				
				System.err.println("Tagging");
				for(String w:tokens) {
					System.err.print(" "+w);
				}
				System.err.println();
				
				String[] rfinalRes = catTagger.tagSequence(tokens.toArray(new String[0]), fvpos,fvlemma);
			
				for(int rj=0;rj<rfinalRes.length;++rj) {
					out.print(tokens.get(rj)+"\t"+fvpos[rj]);
					if(rfinalRes[rj].charAt(0)=='0')out.println("\tO");
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
