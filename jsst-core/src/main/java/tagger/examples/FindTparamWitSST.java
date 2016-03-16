package tagger.examples;



import tagger.core.SttTagger;
import tagger.data.SstTagSet;
import tagger.data.SstTagSetBIO;
import tagger.features.FeatureBuilderBasic;
import tagger.features.FeatureEncoderDecoder;
import tagger.features.FeatureHashEncoderDecoder;
import tagger.utils.FileDescription;

public class FindTparamWitSST {

	public static void main() throws Exception {
	  
	 // If you want to replicate the experiment you can set the random seed e.g.
	 //learnTagger.ps_hmm.randGen=new Random(1144556677);
	 //learnTagger.eval_light("benchmarks/conll/CONLL03.BI_trn.feat", "benchmarks/conll/CONLL03.BI_dev.feat", "benchmarks/conll/CONLL03.TAGSET", false, 10, 4, "BIO", false, 1.0, "");

	 FeatureBuilderBasic fb = new  FeatureBuilderBasic();
	 SstTagSet tagset = new SstTagSetBIO(new FileDescription("benchmarks/conll/CONLL03.TAGSET", "UTF-8",false));
	 FeatureEncoderDecoder fe = new FeatureHashEncoderDecoder();
	 SttTagger learnTagger= new  SttTagger(fe,fb,tagset,true);
	 learnTagger.eval_light("/home/jordi/Escriptori/carlos/trainingPLNiso.feat", "/home/jordi/Escriptori/carlos/devPLNiso.fea", "UTF-8", fb, tagset , false, 10, 4, false, 1.0, "");
		
	} 
}
