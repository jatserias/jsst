package tagger.core;


import java.io.IOException;
import java.io.StringWriter;

import tagger.core.SttTagger;
import tagger.data.ModelDescription;
import tagger.data.SstTagSet;
import tagger.features.FeatureBuilder;
import tagger.features.FeatureEncoder;
import tagger.learning.ET_BIO_UNK;

/**
 * ?? tagging should be the same
 * 
 * @author jordi
 *
 */
public class STTTaggerwithUNK extends SttTagger {
	
	//private static final String morphEncoding = "UTF-8";
	
	
	public STTTaggerwithUNK(FeatureEncoder fe, FeatureBuilder fb, ModelDescription model, boolean ergodic) throws Exception {
		super(fe,fb,model,ergodic);
		if(!LABELS.contains(SstTagSet.UNK_LABEL))LABELS.add(SstTagSet.UNK_LABEL);
		et = new ET_BIO_UNK(model.tagset.unknown_id);
	}
	
	public STTTaggerwithUNK(FeatureEncoder fe,FeatureBuilder fb, SstTagSet tagset, boolean ergodic) throws Exception {
		super(fe,fb,tagset,ergodic);
		if(!LABELS.contains(SstTagSet.UNK_LABEL))LABELS.add(SstTagSet.UNK_LABEL);
		ps_hmm = new PS_HMMwithUNK(tagset);
	}
	/*DEPRECATED
	protected void loadLabels(BufferedReader filename) throws IOException {
          super.loadLabels(filename);
          
		if(!LABELS.contains(SstTagSet.UNK_LABEL)) LABELS.add(SstTagSet.UNK_LABEL);
  	      et = new ET_BIO_UNK(tagset.unknown_id);
  	      logger.info("redump");
  	      StringWriter sw = new StringWriter();
  	      tagset.dump(sw);
  	      logger.info(sw);
  		  logger.info("UNK added at "+tagset.unknown_id);
	}
	**/		
	@Override
	protected void loadLabels() throws IOException {
        super.loadLabels();
        
		if(!LABELS.contains(SstTagSet.UNK_LABEL)) LABELS.add(SstTagSet.UNK_LABEL);
	      et = new ET_BIO_UNK(tagset.unknown_id);
	      logger.info("redump");
	      StringWriter sw = new StringWriter();
	      tagset.dump(sw);
	      logger.info(sw);
		  logger.info("UNK added at "+tagset.unknown_id);
	}
}
