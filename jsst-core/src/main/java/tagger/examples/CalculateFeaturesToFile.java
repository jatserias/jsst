package tagger.examples;

import org.apache.log4j.Logger;

import com.martiansoftware.jsap.JSAP;

import tagger.features.FeatureBuilder;
import tagger.features.FeatureBuilderBasic;
import tagger.features.FeatureBuilderGazetter;
import tagger.features.FeatureEncoderDecoder;
import tagger.features.FeatureHashEncoderDecoder;
import tagger.features.MorphCache;
import tagger.utils.FileDescription;

/**
 * 
 * This is a simple example of how to call a feature extractor and save the result into a file
 * 
 * 
 * TODO: Implement a Factory over FeatureExtractor
 * TODO: disable gazzetter
 * TODO: parametrize lemmatizer
 * 
 * @author jordi
 *
 */
public class CalculateFeaturesToFile {
	private static final String encoding = "UTF-8";
	static Logger logger = Logger.getLogger(CalculateFeaturesToFile.class);
	static void usage() {
		logger.error("Incorrect Number of parameters");
		logger.error("tagsetfile");
		logger.error("morphCache");
		logger.error("gazzeter file ");
		logger.error("Model file");
		logger.error("inputFile");
		logger.error("outputFile ");
		System.exit(-1);

	}
	/**
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

		final com.martiansoftware.jsap.SimpleJSAP jsap = new com.martiansoftware.jsap.SimpleJSAP( CalculateFeaturesToFile.class.getName(), 
				"SST Calculate Features",
				new com.martiansoftware.jsap.Parameter[] {
			new com.martiansoftware.jsap.UnflaggedOption( "tagset", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "tagset" ),
			new com.martiansoftware.jsap.UnflaggedOption( "input", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "raw SSR input file" ),
			new com.martiansoftware.jsap.UnflaggedOption("in_enc", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "Input file encoding" ),
			new com.martiansoftware.jsap.UnflaggedOption( "output", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "output file with the encoded features" ),
			new com.martiansoftware.jsap.UnflaggedOption("out_enc", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "Output file encoding" ),

			// this is only for creating a Gazzetter FeatureBuilder
			new com.martiansoftware.jsap.UnflaggedOption( "gazfile", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, JSAP.NOT_GREEDY, "Gazzetters" ),
			new com.martiansoftware.jsap.UnflaggedOption( "morphcache", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, JSAP.NOT_GREEDY, "MorphCache" ),			
			new com.martiansoftware.jsap.UnflaggedOption( "modelfile", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, JSAP.NOT_GREEDY, "Restrict features to the ones in Model modelname" ),

			// ??
			new com.martiansoftware.jsap.Switch("pos", 'p', "pos", "Input has pos" ),
			new com.martiansoftware.jsap.Switch("lemma", 'l', "lemma", "Input has lemma" ),
			new com.martiansoftware.jsap.Switch("slabel", 's', "slabel", "Input has slabel" ),
			new com.martiansoftware.jsap.Switch("uselemma", 'u', "uselemma", "Use lemma" ),
			new com.martiansoftware.jsap.Switch("mem", 'm', "mem", "Load the whole training in memory" ),
		}		
				);

		final com.martiansoftware.jsap.JSAPResult jsapResult = jsap.parse( args );
		if ( jsap.messagePrinted() ) return;	

		String morphCache= jsapResult.getString("morphcache");;//e.g. DATA/MORPH_CACHE";
		String gazfile =jsapResult.getString("gazfile");//e.g. DATA/gazlistall_minussemcor"
		String inputFile = jsapResult.getString("input");// e.g. benchmarks/CONLL03.BI_dev"
		String outputFile = jsapResult.getString("output");//e.g. benchmarks/CONLL03.BI_dev.feat"
		boolean useLemaFeature=jsapResult.getBoolean("uselemma");
		boolean hasPoS=jsapResult.getBoolean("pos");
		boolean hasLemma=jsapResult.getBoolean("lemma");
		boolean hasSlabel=jsapResult.getBoolean("slabel");
		boolean cacheIsCompress=false;



		FeatureBuilder fb;

		if(gazfile==null || gazfile.length()==0) {
			logger.warn("Not using Gazzetter");
			fb= new FeatureBuilderBasic();
		}
		else {
			logger.warn("Using Gazzetter "+gazfile);
			FeatureEncoderDecoder fe= new FeatureHashEncoderDecoder();
			MorphCache M = morphCache==null ? null  : new MorphCache(new FileDescription(morphCache,encoding,cacheIsCompress));	
			fb = new  FeatureBuilderGazetter(fe,"",gazfile, 4,  M);
		}

		fb.setUseLemma(useLemaFeature);
		fb.tagfile(inputFile,outputFile,encoding, encoding,hasPoS,hasLemma, hasSlabel);
	}
}


