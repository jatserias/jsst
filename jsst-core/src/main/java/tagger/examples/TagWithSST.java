package tagger.examples;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Vector;

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

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import com.martiansoftware.jsap.JSAP;

/**
 * An example class for tagging a text 
 * 
 * Needs sst property set to the path where models are
 * 
 * @author jordi
 *
 */
public class TagWithSST {
	
	private static final String ENCODING = "UTF-8";
	static Logger logger = Logger.getLogger(TagWithSST.class);
	
	
	
	
	public static void main(String args[]) throws Exception{
	 BasicConfigurator.configure();
  
	 final com.martiansoftware.jsap.SimpleJSAP jsap = new com.martiansoftware.jsap.SimpleJSAP( TagWithSST.class.getName(), 
				"SST Tagging",
				new com.martiansoftware.jsap.Parameter[] {
		 				new com.martiansoftware.jsap.UnflaggedOption( "input", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "input" ),
		 				new com.martiansoftware.jsap.UnflaggedOption( "output", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "output" ),
		 				new com.martiansoftware.jsap.UnflaggedOption( "model", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "Model Name" ),
						new com.martiansoftware.jsap.UnflaggedOption( "tagset", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "tagset" ),	
						new com.martiansoftware.jsap.Switch("bio", 'b', "bio", "BIO tagset" ),
						new com.martiansoftware.jsap.Switch("lemma", 'l', "lemma", "has lemma" ),
						new com.martiansoftware.jsap.Switch("pos", 'p', "pos", "has pos" ),
						new com.martiansoftware.jsap.Switch("res", 'r', "res", "has res" ),
					}		
				);
		
		final com.martiansoftware.jsap.JSAPResult jsapResult = jsap.parse( args );
		if ( jsap.messagePrinted() ) return;
//	 
//    if(args.length!=4) {
//    	throw new Exception("four parameter needed: input-file output-file model tagset");
//    }  
	
    logger.warn("warning Start");

//	// Let's call the tagger 
//     String inputFile=args[0];//e.g. /home/jordi/cawikiTok.txt
//	 String outputFile =args[1];//e.g. /home/jordi/cawiki.sst
//	 String modelName= args[2];// e.g.MODELS/"+"allnelemacatmodel
//	 String tagsetname= args[3];//e.g.benchmarks/conll/CONLL03.TAGSET	 
//	 boolean hasLemma=true;
//	 boolean hasPOS=true;
//	 boolean hasRes=false;
	
    
     String inputFile=	 jsapResult.getString("input");	
	 String outputFile =jsapResult.getString("output");	
	 String modelName= jsapResult.getString("model");
	 String tagsetname= jsapResult.getString("tagset");	 
//	 boolean hasLemma=true;
//	 boolean hasPOS=true;
	 boolean hasRes=false;
	 boolean bio= jsapResult.getBoolean("bio");	 
	 boolean useUNK=false;
	 
	 
	 SstTagSet tagset = bio ? new SstTagSetBIO(new FileDescription(tagsetname, ENCODING),useUNK) : new SstTagSetPOS(new FileDescription(tagsetname, ENCODING),useUNK);
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
				
//				try {
//				if( buff.startsWith("%%#D") ||  buff.startsWith("%%#P")) { out.println(buff);}
//				else {
//				boolean endOfSentence=(buff.length()==0) || buff.startsWith("<s>") || buff.startsWith("%%#");
//				while(fin.ready() && !endOfSentence) {
//					
//					
//					int nfield=0;
//					String fields[] = buff.split("\t");
//					
//					tokens.add(fields[nfield]);nfield++;
//					if(hasPOS) { 
//							if(fields.length>nfield) pos.add(fields[nfield]);
//							else  pos.add("NOPOS");
//						         nfield++;}
//					if(hasLemma) {
//							if(fields.length>nfield) lemma.add(fields[nfield]);
//							else lemma.add(fields[0]);
//							nfield++;
//						}
//					if(hasRes)
//						if(fields[nfield].charAt(0)=='O')  res.add("0");
//						else res.add(buff.split("\t")[1]);
//					buff=fin.readLine();
//					endOfSentence=(buff.length()==0) || buff.startsWith("<s>")|| buff.startsWith("%%#");
//				
//			}
				
			tokens.addAll(Arrays.asList(buff.split("[ ]")));		
			String[] fvpos = pos.toArray(new String[0]);	
			String[] fvlemma = lemma.toArray(new String[0]);	
		
			
//			System.err.println("Tagging");
//			for(String w:tokens) {
//				System.err.print(" "+w);
//			}
//			System.err.println();
			
			String[] rfinalRes = catTagger.tagSequence(tokens.toArray(new String[0]), fvpos,fvlemma);
		
			for(int rj=0;rj<rfinalRes.length;++rj) {
				out.print(tokens.get(rj));
				if(rfinalRes[rj].charAt(0)=='0')out.println("\tO");
				else out.println("\t"+rfinalRes[rj]);
				if(hasRes) out.println("\t"+res.get(rj));
			}
			out.println();
			}
//				}catch(Exception e) {
//					logger.error("Error processing line:"+buff);
//					throw e;
//				}
//		}
	    }	
     finally{
    	 out.close();
     }
  }
}
