package tagger.examples;



import org.apache.log4j.Logger;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;

import tagger.core.SttTagger;
import tagger.data.SstTagSet;
import tagger.data.SstTagSetBIO;
import tagger.data.SstTagSetPOS;
import tagger.data.TrainingData;
import tagger.data.TrainingDataSSTonline;
import tagger.data.TrainingFormatReaderSSTfeatures;
import tagger.features.FeatureBuilderBasic;
import tagger.features.FeatureEncoderDecoder;
import tagger.features.FeatureHashEncoderDecoder;
import tagger.utils.FileDescription;

/*
 * An example of train a new model 
 * 
 */
public class TrainModelWithSST {
	static Logger logger = Logger.getLogger(TrainModelWithSST.class);
	private static final String encoding = "UTF-8";
	
	
	public static void main(String[] args) {
	 
	
		com.martiansoftware.jsap.SimpleJSAP jsap;
		try {
			jsap = new com.martiansoftware.jsap.SimpleJSAP( TrainModelWithSST.class.getName(), 
					"SST Training",
					new com.martiansoftware.jsap.Parameter[] {
							new com.martiansoftware.jsap.UnflaggedOption( "tagset", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "tagset" ),
							new com.martiansoftware.jsap.UnflaggedOption( "modelname", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "Resulting Model Name" ),
							new com.martiansoftware.jsap.UnflaggedOption( "corpus", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "Training Data" ),		
							new com.martiansoftware.jsap.UnflaggedOption( "epochs", JSAP.INTEGER_PARSER, "14", JSAP.NOT_REQUIRED, JSAP.NOT_GREEDY, "Number of epoch (iterations)" ),		
							new com.martiansoftware.jsap.Switch("bio", 'b', "bio", "BIO Format" ),
							new com.martiansoftware.jsap.Switch("ergodic", 'e', "ergodic", "Filter punctuation" ),
							new com.martiansoftware.jsap.Switch("secondorder", 's', "secondorder", "Generate second order features" ),
							new com.martiansoftware.jsap.Switch("unk", 'u', "unk", "Use UNK label" ),
							new com.martiansoftware.jsap.Switch("mem", 'm', "mem", "Load the whole training in memory" ),
						}		
					);
		
		
		final com.martiansoftware.jsap.JSAPResult jsapResult = jsap.parse( args );
		if ( jsap.messagePrinted() ) return;
		
		
		  int T= jsapResult.getInt( "epochs" );
		  
		  boolean ergodic= jsapResult.getBoolean("ergodic");
		  boolean secondorder=jsapResult.getBoolean("secondorder");
		  String tagsetfile = jsapResult.getString("tagset");
		  String modelname =jsapResult.getString("modelname");
		  String conffile=jsapResult.getString("corpus");
		  boolean bio = jsapResult.getBoolean("bio");
		  boolean useUNK=jsapResult.getBoolean("unk");
		  boolean IN_MEMORY=jsapResult.getBoolean("mem");;
	  // If you want to replicate the experiment you can set the random seed e.g.
	  // learnTagger.ps_hmm.randGen=new Random(1144556677);		 
	  
	  // T (number of epoch, i.e. iterations) must be provided (could be found using eval see FindTparamWithSST)
	    //int T=10;
		//learnTagger.train_light("necatmodel", "benchmarks/ancora/ancora_alltrain.feat", "benchmarks/conll/CONLL03.TAGSET", false, T, "BIO");
		//learnTagger.train_light("nelemacatmodel", "/home/jordi/Escriptori/carlos/trainingPLNiso.feat", "benchmarks/conll/CONLL03.TAGSET", false, T, "BIO");
		
	  
	  /** V0
	  FeatureBuilderBasic fb = new  FeatureBuilderBasic();
	  
	  
	  SstTagSet tagset = new SstTagSetBIO("benchmarks/conll/CONLL03.TAGSET","UTF-8");
	  SttTagger learnTagger= new  SttTagger(fb,tagset,true); 
	  learnTagger.train_light("allnelemacatmodel", "/home/jordi/Escriptori/carlos/alltrainingPLNiso.feat", "UTF-8", false, T);
		**/
	  //@TODO fixed parameters

	  long tStart = System.currentTimeMillis();
	   
	  FeatureBuilderBasic fb = new  FeatureBuilderBasic();
	  
	  SstTagSet tagset = bio ?  new SstTagSetBIO(new FileDescription(tagsetfile,encoding),useUNK): new SstTagSetPOS(new FileDescription(tagsetfile,encoding),useUNK);
	  //FeatureEncoderDecoder
	  FeatureHashEncoderDecoder fe = new FeatureHashEncoderDecoder();
	  SttTagger learnTagger= new   SttTagger(fe,fb,tagset,ergodic); 
	   
	  // learnTagger.dump(new OutputStreamWriter(System.err));
	 
	  //OLD command:  learnTagger.train_light(modelname, featfile, encoding,  secondorder, T);
	  
	  //@TODO parametrize online / non online training
	 
	  TrainingData TD;
	   
	  //@TODO fix TrainingDataSST
	  if(IN_MEMORY)  
		  TD = learnTagger.load_data(conffile, encoding, secondorder); //new TrainingDataSST(conffile,new TrainingFormatReaderSST(tagset,fb));
	  else 
		  TD= new TrainingDataSSTonline(conffile,new TrainingFormatReaderSSTfeatures(tagset,fb, fe));
	     
	   System.err.println("Features loaded: "+fe.size()+ " "+fe.esize());
	   System.err.println("Features loaded tagger: "+learnTagger.fe.esize());
	  
	   //fe.dump();	   
	   learnTagger.train(TD,T,modelname, fe);
	   //fe.dump();
	  System.err.println("Training done in  "+(System.currentTimeMillis()-tStart));
	  logger.info("Training done in  "+(System.currentTimeMillis()-tStart));
	  
	 
	  // learnTagger.dump(new OutputStreamWriter(System.err));
	 logger.info("Training on mem done!");
	/**  
	 FeatureBuilderBasic fbd = new  FeatureBuilderBasic();
	 SstTagSet tagsetd = new SstTagSetBIO(tagsetfile,encoding,true);
	 SttTagger learnTaggerd= new  SttTagger(fbd,tagsetd,ergodic); //STTTaggerwithUNK(fbd,tagsetd,ergodic);
	  
	  learnTaggerd.train_light_online(modelname+"ONLINE", featfile, encoding, secondorder, T);
	  **/
	
	} catch (JSAPException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
	
}
