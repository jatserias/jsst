package tagger.examples;

import java.io.PrintStream;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import tagger.core.SttTagger;
import tagger.data.FV;
import tagger.data.ModelDescription;
import tagger.data.ReaderFV;
import tagger.data.SstTagSet;
import tagger.data.SstTagSetBIO;
import tagger.features.FeatureBuilderGazetter;
import tagger.features.FeatureEncoderDecoder;
import tagger.features.FeatureHashEncoderDecoder;
import tagger.features.MorphCache;
import tagger.utils.FileDescription;

public class TagFromFeatures {
	

	private static final String ENCODING = "UTF-8";
	static String modelsFolder="MODELS/";
	static String tagsetFolder="TAGSETS/";
	static String dataFolder="DATA/";

static Logger logger = Logger.getLogger(TagWithSST.class);
	
	public static void main(String args[]) throws Exception{
	 BasicConfigurator.configure();
  
    if(args.length!=4) {
    	throw new Exception("four parameter needed: input-file output-file model tagset");
    }  
    
    // path to the sst models
    String  basename= System.getProperty("sst");
    if(basename==null) throw new Exception("property sst needed!");
  	
    logger.warn("warning Start");

	// Let's call the tagger 
     String inputFile=args[0];//e.g. /home/jordi/cawikiTok.txt
	 String outputFile =args[1];//e.g. /home/jordi/cawiki.sst
	 String modelName= args[2];// e.g.MODELS/"+"allnelemacatmodel
	 String tagsetname= args[3];//e.g.benchmarks/conll/CONLL03.TAGSET
	 
	 
	  //@TODO BIO / POS should be a highlevel argument or get it from tagset
	 SstTagSet tagset = new SstTagSetBIO(new FileDescription(tagsetname,ENCODING));
	 FeatureEncoderDecoder fe = new FeatureHashEncoderDecoder(new ModelDescription(false,basename+modelsFolder+"/WSJc_base_gaz10_up_25",tagset,ENCODING,false));
	 FeatureBuilderGazetter  fb = new  FeatureBuilderGazetter(fe,"",basename+dataFolder+"/gazlistall_minussemcor",
				4, 
				new MorphCache( new FileDescription(basename+dataFolder+"/MORPH_CACHE", ENCODING, false)));

	
	 
	 SttTagger myTagger= new  SttTagger(fe,fb,new ModelDescription(false, modelName, tagset, ENCODING,false), true);
	 
	 PrintStream out;
	 
     if(outputFile.compareTo("stdout")==0) 
     	out=System.out;
     else out= new PrintStream(outputFile);
     
     ReaderFV r = new ReaderFV(inputFile,ENCODING);
         
		while( r.hasNext()) {
		   FV feats=  r.next();		   
		   String[] res = myTagger.innerStringTagging(feats.features);
		   out.print(feats.id);
		   for(int i=0;i<res.length;++i) {
			   out.print("\t"+res[i]);
		   }
		   out.println();
		}
	 
	}
}
