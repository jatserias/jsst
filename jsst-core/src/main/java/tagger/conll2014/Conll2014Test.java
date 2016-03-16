package tagger.conll2014;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Vector;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import tagger.core.SttTaggerSparse;
import tagger.data.InstanceRaw;
import tagger.data.ModelDescription;
import tagger.data.SstTagSet;
import tagger.data.SstTagSetBIO;
import tagger.data.SstTagSetPOS;
import tagger.examples.TagWithSST;
import tagger.extra.TagsetParole.features;
import tagger.features.FeatureBuilderBasic;
import tagger.features.FeatureBuilderGazetter;
import tagger.features.FeatureEncoderDecoder;
import tagger.features.FeatureHashEncoderDecoder;
import tagger.features.FeatureVector;
import tagger.features.MorphCache;
import tagger.utils.FileDescription;

import com.martiansoftware.jsap.JSAP;

public class Conll2014Test {
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
			new com.martiansoftware.jsap.UnflaggedOption( "gazfile", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, JSAP.NOT_GREEDY, "Gazzetters" ),
			new com.martiansoftware.jsap.UnflaggedOption( "morphcache", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, JSAP.NOT_GREEDY, "MorphCache" ),			
			
			
			new com.martiansoftware.jsap.Switch("bio", 'b', "bio", "BIO tagset" ),
			new com.martiansoftware.jsap.Switch("lemma", 'l', "lemma", "has lemma" ),
			new com.martiansoftware.jsap.Switch("uselemma", 'u', "uselemma", "use lemma" ),
			new com.martiansoftware.jsap.Switch("pos", 'p', "pos", "has pos" ),
			
			new com.martiansoftware.jsap.Switch("res", 'r', "res", "has res" ),
		}		
				);

		final com.martiansoftware.jsap.JSAPResult jsapResult = jsap.parse( args );
		if ( jsap.messagePrinted() ) return;


		logger.warn("warning Start");




		String inputFile=	 jsapResult.getString("input");	
		String outputFile =jsapResult.getString("output");	
		String modelNameL1= jsapResult.getString("model")+"L1";
		String modelNameL2= jsapResult.getString("model")+"L2";
		String tagsetname= jsapResult.getString("tagset");	 
		String gazfile = jsapResult.getString("gazfile");	
		String morphCache = jsapResult.getString("morphCache");
		boolean useLemaFeature=jsapResult.getBoolean("uselemma");
	    boolean hasLemma=jsapResult.getBoolean("lemma");
	    boolean hasPos=jsapResult.getBoolean("pos");
		boolean bio= true;
		boolean useUNK=false;

		
		SstTagSet tagset = new SstTagSetBIO(new FileDescription(tagsetname, ENCODING),useUNK);
		ModelDescription model = new ModelDescription(modelNameL1, tagset, ENCODING, false);
		FeatureEncoderDecoder fe = new FeatureHashEncoderDecoder(model);
		
		boolean cacheIsCompress=false;
		MorphCache M = morphCache==null ? null  : new MorphCache(new FileDescription(morphCache,ENCODING,cacheIsCompress));	
		FeatureBuilderGazetter fb = new  FeatureBuilderGazetter(fe,"",gazfile, 4,  M);
		SttTaggerSparse catTagger= new  SttTaggerSparse(fe,fb, model, true);

		fb.defaultF.USE_LEMMA=useLemaFeature;
		
		SstTagSet tagset2 = new SstTagSetBIO(new FileDescription(tagsetname, ENCODING),useUNK);
		ModelDescription model2 = new ModelDescription(modelNameL2, tagset2, ENCODING, false);
		FeatureEncoderDecoder fe2 = new FeatureHashEncoderDecoder(model2);
		FeatureBuilderBasic	fb2 = new  FeatureBuilderGazetter(fe,"",gazfile, 4,  M);
		SttTaggerSparse catTagger2= new  SttTaggerSparse(fe2,fb2, model2, true);
		
		fb2.defaultF.USE_LEMMA=useLemaFeature;
		
		PrintStream out;
		if(outputFile.compareTo("stdout")==0) 
			out=System.out;
		else out= new PrintStream(outputFile);

		try {    
			BufferedReader fin;
			if(inputFile.compareTo("stdin")==0)	fin= new BufferedReader(new InputStreamReader(System.in));
			else fin = new BufferedReader(new FileReader(inputFile));

			String buff;
			Vector<String> l1lab = new Vector<String>();
			Vector<String> tokens = new Vector<String>();
			Vector<String> lemma = new Vector<String>();
			Vector<String> pos = new Vector<String>();
			Vector<String> vbuff = new Vector<String>();
			String[] fvpos = pos.toArray(new String[0]);	
			String[] fvlemma = lemma.toArray(new String[0]);
			String[] slabel = l1lab.toArray(new String[0]);
			while( (buff=fin.readLine()) != null) {	

				if(buff.startsWith("#\t") || buff.trim().isEmpty()) { 
					
					if(!tokens.isEmpty()) {
						

						//for(String t:tokens) {System.err.print(" "+t);}System.err.println();
						FeatureVector[] fv = new FeatureVector[tokens.size()];
						for(int i=0; i<fv.length;++i) fv[i]= new FeatureVector();
					    if(hasLemma) fvlemma = lemma.toArray(new String[0]);
					    if(hasPos) fvpos = pos.toArray(new String[0]); 
					    //System.err.println("POS="+Arrays.toString(fvpos));
						fb.extractFeatures(new InstanceRaw(tokens.toArray(new String[0]),fvlemma,fvpos,slabel),fv);
						String[] rfinalRes = catTagger.innerStringTagging(fv);
							//String[] rfinalRes = catTagger.tagSequence(tokens.toArray(new String[0]), fvpos,fvlemma);
						
						
						for(int i=0; i<fv.length;++i)  fv[i].add("slab="+rfinalRes[i]);
						
						  String[] rfinalRes2 = catTagger2.innerStringTagging(fv);
					

						
						
						for(int rj=0;rj<rfinalRes.length;++rj) {
							// dump  1 w x x
							String[] fields =vbuff.get(rj).split("\t");
							
							out.print(fields[0]);
							out.print('\t');
							out.print(fields[1]);
							out.print('\t');
							out.print(fields[4]);
							out.print('\t');
							out.print(fields[5]);
							out.print('\t');
							if(rfinalRes[rj].charAt(0)=='0')out.print("O");
							else 
								out.print(rfinalRes[rj]);
							out.print('\t');
							if(rfinalRes2[rj].charAt(0)=='0')out.println("O");
							else 
								out.println(rfinalRes2[rj]);

						}
						
						out.println(buff);
						tokens.clear();
						lemma.clear();
						pos.clear();
						vbuff.clear();
					}
					
					out.println(buff);
				} 
				else { 					
					vbuff.add(buff);
					//System.err.println(buff.split("[ \t]")[1]);
					String[] fields = buff.split("[ \t]");
					tokens.add(fields[1]);
					if(hasPos)  pos.add(fields[3]);
					if(hasLemma)  lemma.add(fields[2]);
					
				}
			}
		}
		finally{
			out.close();
		}
	}
}
