package tagger.examples;

import tagger.core.SttTagger;
import tagger.data.SstTagSet;
import tagger.data.SstTagSetBIO;
import tagger.features.FeatureBuilderBasic;
import tagger.features.FeatureEncoderDecoder;
import tagger.features.FeatureHashEncoderDecoder;
import tagger.utils.FileDescription;

/**
 * tag a BIO feature file and evaluate
 * 
 * @author jordi
 *
 */
public class EvalSST {

	public static void main(String args[]) throws Exception {
		
		
		 FeatureBuilderBasic fb = new  FeatureBuilderBasic();
		 String tagsetFile = "/Users/jordi/Desktop/NEGerman/conll2003.tagset";
		 String trainfeatdataFile = "/Users/jordi/Desktop/NEGerman/deu.train.utf8.feat.sst";
		 String testfeatdataFile = "/Users/jordi/Desktop/NEGerman/deu.testb.utf8.feat.sst";
			
			
	     SstTagSet tagset = new SstTagSetBIO(new FileDescription(tagsetFile, "UTF-8",false));
		 FeatureEncoderDecoder fe = new FeatureHashEncoderDecoder();
		 SttTagger learnTagger= new  SttTagger(fe,fb,tagset,true);
	
		 learnTagger.eval_light(trainfeatdataFile, testfeatdataFile, "UTF-8", fb, tagset , false, 10, 4, false, 1.0, "");
		
	}
}
