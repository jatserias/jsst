package tagger.examples;

import org.apache.log4j.Logger;

import tagger.core.STTTaggerwithUNK;
import tagger.core.SttTagger;
import tagger.data.SstTagSet;
import tagger.data.SstTagSetBIO;
import tagger.data.TrainingData;
import tagger.data.TrainingDataSSTonline;
import tagger.data.TrainingFormatReaderSSTfeatures;
import tagger.features.FeatureBuilderBasic;
import tagger.features.FeatureEncoderDecoder;
import tagger.features.FeatureHashEncoderDecoder;
import tagger.utils.FileDescription;

	/*
	 * An example of train a new model using UNK SST with mixed weigth
	 * 
	 */
	public class TrainModelWithSSTUNK {
		static Logger logger = Logger.getLogger(TrainModelWithSSTUNK.class);
		private static final String encoding = "UTF-8";

		/**
		 * 
		 * @param args
		 * @param1 tagset
		 * @param2 modelname
		 * @param3 FeatureFile
		 * 
		 * @throws Exception
		 */
		public static void main(String[] args) throws Exception {
		 	
		 //@TODO fixed parameters
		  int T=14;
		  boolean ergodic=true;
		//  boolean secondorder=false;
		  
		  int arg=0;
		  String tagsetfile = args[arg++];
		  String modelname=args[arg++];
		  String conffile=args[arg++];
		  long tStart = System.currentTimeMillis();
		   
		  FeatureBuilderBasic fb = new  FeatureBuilderBasic();
		  boolean useUNK=true;
		  SstTagSet tagset = new SstTagSetBIO(new FileDescription(tagsetfile,encoding),useUNK); 
		  FeatureEncoderDecoder fe = new FeatureHashEncoderDecoder();
		  SttTagger learnTagger= new   STTTaggerwithUNK(fe,fb,tagset,ergodic); 
		   
		 
		  TrainingData TD = new TrainingDataSSTonline(conffile,new TrainingFormatReaderSSTfeatures(tagset,fb, new FeatureHashEncoderDecoder()));
			  
		  learnTagger.train(TD,T,modelname,fe);
	  
		  
		  logger.info("Training done in  "+(System.currentTimeMillis()-tStart));
		  
		  logger.info("Training on mem done!");
		
		}
	}


