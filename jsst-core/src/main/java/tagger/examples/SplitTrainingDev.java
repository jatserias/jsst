package tagger.examples;


import tagger.core.SttTagger;
import tagger.data.SstTagSet;
import tagger.data.SstTagSetBIO;
import tagger.data.TrainingData;
import tagger.features.FeatureBuilderBasic;
import tagger.features.FeatureEncoderDecoder;
import tagger.features.FeatureHashEncoderDecoder;
import tagger.utils.FileDescription;

public class SplitTrainingDev {
	public static void main(String args[]) throws Exception {
	 FeatureBuilderBasic fb = new  FeatureBuilderBasic();
	 SstTagSet tagset = new SstTagSetBIO(new FileDescription("benchmarks/conll/CONLL03.TAGSET","UTF-8",false));
	 FeatureEncoderDecoder fe = new FeatureHashEncoderDecoder();
	 SttTagger learnTagger= new  SttTagger(fe,fb,tagset,true);
	 TrainingData trainingdata;
	 String traindataFile="/home/jordi/Escriptori/carlos/trainingPLNiso.feat";
	 boolean secondOrder=false;
	 trainingdata = learnTagger.load_data(traindataFile,"UTF-8",secondOrder);
//	 TrainingData[] traincv= trainingdata.splitTrain(10);
//	 trainingdata =null;
//	 
//	 learnTagger.eval_light(traincv[0], traincv[1], fb, tagset, false, 15, 4, false, 1.0, "");
	}
}
