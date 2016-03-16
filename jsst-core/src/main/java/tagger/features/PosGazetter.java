package tagger.features;



import tagger.core.SttTagger;
import tagger.data.Instance;
import tagger.data.ModelDescription;
import tagger.utils.FileDescription;

/**
 * A feature Builder that extends a Gazetter and adds PoS
 * 
 * Gazzetter format
 * 
 *
 *  effort  FWNSS1=noun.act BIFWNSS1=noun.act       NWNSS1_nou=1
 * 
 * TODO: encoding error on triggers.GAZ
 * 
 * features
 * 
 *  NOUN_GAZ
 *  
 * 114265 BIFWNSS
 * 114265 FWNSS
 * 114265 NWNSS_nou
 * 
 *  ADJS_GAZ
 *  
 *  21285 BIFWNSS
 *  21285 FWNSS
 *  21285 NWNSS_adj
 *  
 *  ADV.GAZ
 *  
 *  640 BIFWNSS
 *  4640 FWNSS
 *  4640 NWNSS_adv
 *  
 *  VERBS.GAZ
 *  
 *  640 BIFWNSS
 *  4640 FWNSS
 *  4640 NWNSS_adv
 *  
 *  F500
 *  
 *  936 FRT_
 *  
 *  countries_and_territories.GAZ 
 *  
 *  471 CTRYs_AND_TRTRYs_
 *  
 *  world.GAZ
 *  
 *  4430 WORLD_
 *  
 *  perfirst.GAZ
 *  
 *  5461 PERFIRST
 *  
 *  perlast.GAZ
 *  
 *  88698 PERLAST
 *  
 *  triggers.GAZ
 *    7 TRIG_GENT_
 *  4 TRIG_LOC_
 *  6 TRIG_MISC_
 *  5 TRIG_ORG_
 * 31 TRIG_PER_
 *
 *  patterns used ot name features:
 *  
 *  
 *  WORLD_
 *  PERLAST
 *  PERFIRST
 *  TRIG_<NER CATEGORY>
 *  FWNSS<#token>=supersense
 *  BIFWNSS<#token>=supersense
 *  NWNSS<#token>_POS=#<freq>
 *   
 * @author jordi
 * 
 * @TODO Gazzetter should we implemented ina separated class
 * @TODO POS model should be configurable??
 */
public class PosGazetter extends FeatureBuilderGazetter {
	 private static final String[] EMPTYSTRINGARRAY = new String[0];
	SttTagger PoSTagger;
    FeatureBuilderBasic fb;
	 
	public PosGazetter(String basename, String fname, ModelDescription model,ModelDescription posModel,int p_maxspan,
			Lemmatizer morph) throws Exception {
		super(new FeatureHashEncoderDecoder(model),basename, fname, p_maxspan, morph);
	
		  fb = new  FeatureBuilderBasic();
		  PoSTagger= new  SttTagger(fd,fb,posModel, true);
		
	}
	
/// @TODO remove parameter EMPTYpos
	 public     void  extractFeatures(Instance instance, FeatureVector[] fv) {
		  String[] W = instance.get("WORD");		
		   String[] fvpos = {};
		  instance.set("POS", PoSTagger.tagSequence(W, fvpos,EMPTYSTRINGARRAY));
		  instance.set("LEMMA",EMPTYSTRINGARRAY);
		   super.extractFeatures(instance,fv); //empty lemma list      
	  }
	  
	  
}
