package tagger.examples;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.apache.log4j.BasicConfigurator;

import com.martiansoftware.jsap.JSAP;

import tagger.core.SttTagger;
import tagger.data.FV;
import tagger.data.InstanceRaw;
import tagger.data.ModelDescription;
import tagger.data.SstTagSet;
import tagger.data.SstTagSetBIO;
import tagger.features.FeatureBuilderGazetter;
import tagger.features.FeatureEncoderDecoder;
import tagger.features.FeatureHashEncoderDecoder;
import tagger.features.FeatureVector;
import tagger.features.MorphCache;
import tagger.utils.FileDescription;

public class TagSSTFormat {
	private static final String encoding = "UTF-8";
	private static final boolean SstOutputformat = false;

	/**
	 * Tags a SST format file calculating the features on the fly
	 * 
	 * @param args
	 * @param1 tagsetfile
	 * @param2 morphcachefile
	 * @param3 gazetterfile
	 * 
	 * @param inputfile
	 * @param outputfile
	 * 
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
	
		 BasicConfigurator.configure();
		  
		 final com.martiansoftware.jsap.SimpleJSAP jsap = new com.martiansoftware.jsap.SimpleJSAP( TagWithSST.class.getName(), 
					"SST Tagging",
					new com.martiansoftware.jsap.Parameter[] {
			 				new com.martiansoftware.jsap.UnflaggedOption( "input", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "input" ),
			 				new com.martiansoftware.jsap.UnflaggedOption( "output", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "output" ),
			 				new com.martiansoftware.jsap.UnflaggedOption( "model", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "Model Name" ),
			 				new com.martiansoftware.jsap.UnflaggedOption( "gaz", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "Gazzetter list file" ),
			 				new com.martiansoftware.jsap.UnflaggedOption( "morph", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "morphcache" ),
							
			 				new com.martiansoftware.jsap.UnflaggedOption( "tagset", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "tagset" ),	
							new com.martiansoftware.jsap.Switch("lemma", 'l', "lemma", "has lemma" ),
							new com.martiansoftware.jsap.Switch("pos", 'p', "pos", "has pos" ),
							new com.martiansoftware.jsap.Switch("gold", 'g', "gold", "has gold label" ),
						}		
					);
			
			final com.martiansoftware.jsap.JSAPResult jsapResult = jsap.parse( args );
			if ( jsap.messagePrinted() ) return;

		

		
	    
	    String inputFile=	 jsapResult.getString("input");	//benchmarks/CONLL03.BI_dev"

		String tagsetfile = jsapResult.getString("tagset");//TAGSETS/WNSS_07.TAGSET"
		String morphCache= jsapResult.getString("morph");//DATA/MORPH_CACHE";
		String gazfile =   jsapResult.getString("gaz");//DATA/gazlistall_minussemcor"
		String modelfile =  jsapResult.getString("model");//MODELS/SEM07_base_gaz10_up_12"
		String outputFile =  jsapResult.getString("output");//benchmarks/CONLL03.BI_dev.feat"
		//boolean useLemaFeature=false;
		boolean hasPoS= jsapResult.getBoolean("pos");//true;
		boolean hasLemma=jsapResult.getBoolean("lemma");//false;
		boolean modelIsCompress=false;
		boolean cacheIsCompress=false;
		boolean hasGold=jsapResult.getBoolean("gold");//true;
		
		boolean useUNK=false;
		SstTagSet tagset = new SstTagSetBIO(new FileDescription(tagsetfile,encoding),useUNK);
				
		 ModelDescription m = new ModelDescription(modelfile,tagset,encoding,modelIsCompress);
		 FeatureEncoderDecoder fe = new FeatureHashEncoderDecoder(m);
		 FeatureBuilderGazetter  fb = new  FeatureBuilderGazetter(fe,"",gazfile,
					4,
					new MorphCache(new FileDescription(morphCache, encoding, cacheIsCompress)));

		
		 fb.defaultF.USE_LEMMA=false;
		 
		 SttTagger myTagger= new  SttTagger(fe,fb, m, true);
		
		
		
		/**
		 * TAG from features
		 */
		
		 PrintStream  out = new PrintStream(outputFile, encoding);
			// read a line
			BufferedReader fin =		new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), encoding));
			String buff;
			
			  String olddocid="";
				
			while((buff=fin.readLine())!=null) {
				String[] WB = buff.split("\t");
				String[] W =  new String[WB.length-1]; // first field is sentence id
			    String[] G = new String[WB.length-1]; // gold
			//P is the POS vector
			String[] P = hasPoS ? new String[WB.length-1] :new String[0];
			//L lemma vector
			String[] L = hasLemma ? new String[WB.length-1]  : new String[0];
				
			// First element is the ID?
			for(int i=1;i<WB.length;++i){
				String [] fields=WB[i].split(" ");
				int field=0;
				W[i-1]=fields[field];
				if(hasPoS) P[i-1]=fields[++field];
				if(hasLemma) L[i-1]=fields[++field];
				if(hasGold) G[i-1]=fields[++field];
			}
			
			
			
			/**
			 * @TODO What if we have to calculate Pos and Lemma on the fly ??
			 */
			FeatureVector[]  features =  new FeatureVector[W.length];
			for(int i=0;i<features.length;++i)
				features[i]=new FeatureVector();
			
			fb.extractFeatures(new InstanceRaw(W, P, L,null),features);
		    
			
			 FV feats=  new FV(WB[0],features);
		     //feats.dump();
			 //	myTagger.dump();
			  String[] res = myTagger.innerStringTagging(feats.features);
			  
			  // Output The results
			 if(SstOutputformat) {
			    
			   out.print(feats.id);
			   for(int i=0;i<res.length;++i) {
				   out.print("\t"+W[i]+" "+P[i]+" "+res[i]);
			   }
			   out.println();
			 }
			 else {
				 String docid=feats.id.split("\\.")[0];
				 if(docid.compareTo(olddocid)!=0) {
					 out.println("-DOCSTART- -X- O O O\n"); 
				     olddocid=docid;
				  }
				 for(int i=0;i<res.length;++i) {
					 if(res[i].charAt(0)=='0') res[i]="O";
					 if(G[i].charAt(0)=='0') G[i]="O";
					 out.println(W[i]+" "+P[i]+" I-NP "+res[i]+" "+G[i]);
				 }
				 out.println();
			 }
			 
			 
			}
			fin.close();
			out.close();
	}
}
