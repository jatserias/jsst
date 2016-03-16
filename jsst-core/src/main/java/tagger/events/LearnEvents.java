package tagger.events;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import tagger.core.SttTagger;
import tagger.data.Instance;
import tagger.data.InstanceRaw;
import tagger.data.SstTagSet;
import tagger.data.SstTagSetBIO;
import tagger.data.SstTagSetPOS;
import tagger.data.TrainingData;
import tagger.data.TrainingInstance;
import tagger.data.TrainingFormatReader;
import tagger.examples.TrainModelWithSST;
import tagger.extra.TimeML.EventClass;
import tagger.features.FeatureBuilderBasic;
import tagger.features.FeatureBuilderBasic.FeatureOption;
import tagger.features.FeatureEncoder;
import tagger.features.FeatureEncoderDecoder;
import tagger.features.FeatureHashEncoderDecoder;
import tagger.features.FeatureVector;
import tagger.utils.FileDescription;

public class LearnEvents implements TrainingFormatReader {

	static Logger logger = Logger.getLogger(LearnEvents.class);
/**
 * 
 * Read the event props dump an creates training examples
 * 
 * @param args
 * @throws Exception
 */
	public static void main(String[] args) throws Exception {
		
		 long tStart = System.currentTimeMillis();
		 
		 
		 int T=10;
		 String modelname="eventmodel";
		 
		String encoding = "UTF-8";
		boolean secondorder=false;
		  FeatureBuilderBasic fb = new  FeatureBuilderBasic();
		  
		String tagsetfile="src/test/resources/EVENT.TAGSET";
		boolean useUNK=false;
		boolean ergodic=false;
		SstTagSet tagset = new SstTagSetPOS(new FileDescription(tagsetfile,encoding),useUNK);
		FeatureEncoderDecoder fe = new FeatureHashEncoderDecoder();
		SttTagger learnTagger= new   SttTagger(fe,fb,tagset,ergodic);
		String conffile="../demo_nelinking/events.sst";
		
		TrainingData TD = learnTagger.load_data(conffile, encoding, secondorder);
		
		learnTagger.train(TD,T,modelname, fe);
		   
		  
		  logger.info("Training done in  "+(System.currentTimeMillis()-tStart));
		  
		  
		  
	}
	
		public static void oldtest(String[] args) throws Exception {
				
		String line="104_20020502.txt.6\t"+
		"El da0ms0 El OTHER NONE NONE NONE NONE NONE\t"+
		"elemento ncms000 elemento NOUN NONE NONE NONE NONE NONE\t"+
		"más rg más OTHER NONE NONE NONE NONE NONE\t"+
		"espectacular aq0cs0 espectacular ADJECTIVE NONE NONE NONE NONE NONE\t"+
		"del spcms del OTHER NONE NONE NONE NONE NONE\t"+
		"mercado ncms000 mercado NOUN NONE NONE NONE NONE NONE\t"+
		"es vsip3s0 es VERB NONE PRESENT IMPERFECTIVE INDICATIVE STATE\t"+
		"su dp3cs0 su OTHER NONE NONE NONE NONE NONE\t"+
		"cubierta ncfs000 cubierta NOUN NONE NONE NONE NONE NONE\t"+ 
		", fc , OTHER NONE NONE NONE NONE NONE\t"+
		"una di0fs0 una OTHER NONE NONE NONE NONE NONE\t"+
		"gran aq0cs0 gran ADJECTIVE NONE NONE NONE NONE NONE\t"+
		"escultura-techo nc00000 escultura-techo NOUN NONE NONE NONE NONE NONE\t"+
		"con sps00 con OTHER NONE NONE NONE NONE NONE\t"+
		"bóvedas ncfp000 bóvedas NOUN NONE NONE NONE NONE NONE\t"+
		"a sps00 a OTHER NONE NONE NONE NONE NONE\t"+
		"la da0fs0 la OTHER NONE NONE NONE NONE NONE\t"+
		"catalana ncfs000 catalana NOUN NONE NONE NONE NONE NONE\t"+
		"construida aq0fsp construida VERB PARTICIPLE NONE NONE NONE STATE\t"+
		"con sps00 con OTHER NONE NONE NONE NONE NONE\t"+
		"madera ncfs000 madera NOUN NONE NONE NONE NONE NONE\t"+
		"laminada aq0fs0 laminada ADJECTIVE NONE NONE NONE NONE NONE\t"+
		". fp . OTHER NONE NONE NONE NONE NONE";
		
		String[] wordfeats =line.split("[\t]");
	
		int[] t = new int[wordfeats.length-1];
		String[] P = new String[wordfeats.length-1];
		String[] W = new String[wordfeats.length-1];
		String[] L= new String[wordfeats.length-1];
		int j=0;
		
		String is=wordfeats[0];
		FeatureVector[] rfv = new FeatureVector[wordfeats.length-1];
		for(int k=1;k<wordfeats.length;++k) {
		  String wf = wordfeats[k];
		  String[] fv = wf.split("[ ]");
		  int i=-1;
		  String word= fv[++i];
		  String pos =fv[++i];
		  String lemma=fv[++i];
		  // this are word features
		  String epos = fv[++i];
		  String vform = fv[++i];
		  String tense = fv[++i];
		  String aspect = fv[++i];
		  String mood = fv[++i];
		  
		  ///We could also add window properties
		  rfv[j]=new FeatureVector();
		  rfv[j].add("epos="+epos);
		  rfv[j].add("vform="+vform);
		  rfv[j].add("tense="+tense);
		  rfv[j].add("aspect="+tense);
		  rfv[j].add("mood="+tense);
		  
		  // this is the word label
		  t[j] = EventClass.valueOf(fv[++i]).ordinal();
		  W[j]=word;
		  P[j]=pos;
		  L[j]=lemma;
		  ++j;
		}
		/// extract features
		FeatureBuilderBasic fb = new FeatureBuilderBasic();
		
		
	
	
		
		fb.extractFeatures(new InstanceRaw(W, P, L,null), rfv);
		
		
		//encode properties
		int[][] v = new int[rfv.length][];
		
		
		FeatureEncoder fe = new FeatureHashEncoderDecoder();
		/// create training example
		int i=0;
		for(FeatureVector mfv : rfv) {
			int ij=0;
			String[] stringFeatures = mfv.getStringFeatures();
			v[i] = new int[stringFeatures.length];
			System.err.println(mfv);
			
			for(String feature : stringFeatures) {
				 long fev = fe.encode(feature);
				 v[i][ij]=(int) fev;
				 ++ij;
			}
			++i;
		}
		
	
		
		TrainingInstance e = new TrainingInstance(is,v,t);
		
		
	}

	
	FeatureBuilderBasic fb;
	FeatureEncoder fe;
	
	
	public LearnEvents() throws Exception{
		fb = new FeatureBuilderBasic();
		fe = new FeatureHashEncoderDecoder();
	}
	
@Override
public TrainingInstance readExample(BufferedReader buff) throws IOException {	
	return  readExample(buff.readLine());
}

@Override
public TrainingInstance readExample(String buff) throws IOException {
	String[] wordfeats =buff.split("[\t]");
	
	int[] t = new int[wordfeats.length-1];
	String[] P = new String[wordfeats.length-1];
	String[] W = new String[wordfeats.length-1];
	String[] L= new String[wordfeats.length-1];
	int j=0;
	
	String is=wordfeats[0];
	FeatureVector[] rfv = new FeatureVector[wordfeats.length-1];
	for(int k=1;k<wordfeats.length;++k) {
	  String wf = wordfeats[k];
	  String[] fv = wf.split("[ ]");
	  int i=-1;
	  String word= fv[++i];
	  String pos =fv[++i];
	  String lemma=fv[++i];
	  // this are word features
	  String epos = fv[++i];
	  String vform = fv[++i];
	  String tense = fv[++i];
	  String aspect = fv[++i];
	  String mood = fv[++i];
	  
	  ///We could also add window properties
	  rfv[j]=new FeatureVector();
	  rfv[j].add("epos="+epos);
	  rfv[j].add("vform="+vform);
	  rfv[j].add("tense="+tense);
	  rfv[j].add("aspect="+tense);
	  rfv[j].add("mood="+tense);
	  
	  // this is the word label
	  t[j] = EventClass.valueOf(fv[++i]).ordinal();
	  W[j]=word;
	  P[j]=pos;
	  L[j]=lemma;
	  ++j;
	}
	/// extract features
	
	
	


	
	fb.extractFeatures(new InstanceRaw(W, P, L,null), rfv);
	
	
	//encode properties
	int[][] v = new int[rfv.length][];
	
	
	
	/// create training example
	int i=0;
	for(FeatureVector mfv : rfv) {
		int ij=0;
		String[] stringFeatures = mfv.getStringFeatures();
		v[i] = new int[stringFeatures.length];
		System.err.println(mfv);
		
		for(String feature : stringFeatures) {
			 long fev = fe.encode(feature);
			 v[i][ij]=(int) fev;
			 ++ij;
		}
		++i;
	}
	

	
	return new TrainingInstance(is,v,t);
	
	
}
}
