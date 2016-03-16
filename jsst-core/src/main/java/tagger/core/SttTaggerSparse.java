package tagger.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import tagger.core.SttTagger.tagmode;
import tagger.data.InstanceRaw;
import tagger.data.ModelDescription;
import tagger.data.ModelFormatReader;
import tagger.data.SstTagSet;
import tagger.data.TrainingData;
import tagger.data.TrainingDataSST;
import tagger.data.TrainingDataSSTonline;
import tagger.data.TrainingInstance;
import tagger.data.TrainingFormatReader;
import tagger.data.TrainingFormatReaderSSTfeatures;
import tagger.features.FeatureBuilder;
import tagger.features.FeatureDecoder;
import tagger.features.FeatureEncoder;
import tagger.features.FeatureHashEncoderDecoder;
import tagger.features.FeatureVector;
import tagger.learning.ET;
import tagger.learning.ET_BIO;
import tagger.learning.LearningStatistics;
import tagger.learning.Stats;
import tagger.learning.Verbose_res;
import tagger.utils.ClassChooser;
import tagger.utils.Utils;

public class SttTaggerSparse implements TaggerAPI {
	private static final String[] EMPTY_STRING_ARRAY = new String[0];
	private static final int[] EMPTY_INT_ARRAY = new int[0];
	private static final String ALL_LABEL = "ALL";

	/// Should this be part of the tagger ??
	TrainingFormatReader freader;
	
	static Logger logger = Logger.getLogger(SttTaggerSparse.class);

	/// BIO for BIO tagtset or POS for no BIO
	//public enum tagmode { BIO, POS };

	///Ergodic (all transition allow) or non ergodic
	boolean ergodic = true;
	
	/// LABEL SET (WITHOUT B / I ), used only for evaluation?
	protected  Set<String> LABELS;

	/// Model Description
	ModelDescription model;
	
	/// tagging BIO or not
	private tagmode mode;

	/// Tagset name
	private String tagsetname;

	///tagset
	public SstTagSet tagset;
	
	
	
	
	
	/// Feature Builder
	public FeatureBuilder fb;

	/// Feature Encoder
	public FeatureEncoder fe;
	
	/// Evaluator
	protected ET et;
	/// Hide Markov Model Average Perceptron
	public PS_HMM_Sparse ps_hmm;

	


	
	public void dump(Writer out) throws IOException {	  
	       for(String label : LABELS) out.append("LABEL:"+label+"\n");
	       if(model==null) {out.append("uninitialized model description\n");} 
	       else model.dump(out);
		
	}
	
	

	private void minit(FeatureEncoder fe, final FeatureBuilder fb, final SstTagSet tagset, boolean ergodic) throws Exception {
		this.ergodic=ergodic;
		this.fb = fb; 
		this.fe=fe;
		this.tagset= tagset; //new SstTagSet(tagset,"UTF-8");
		mode=tagset.mode;
		ps_hmm = new PS_HMM_Sparse(this.tagset);
		et = new ET_BIO();
		//BItag=(mode==SttTagger.tagmode.BIO);
		
		freader= new TrainingFormatReaderSSTfeatures(tagset, fb, fe);
		
	}
	
	/// Constructor for tagging
	public SttTaggerSparse(FeatureEncoder fe, final FeatureBuilder fb, final ModelDescription model, boolean ergodic) throws Exception {
		minit(fe, fb, model.tagset, ergodic);	
		this.model=model;
		//@JAB BFS loadLabels(model.tagset.getName());
		loadLabels();
		ps_hmm.jabLoadModel(model);
	}
	
	/**
	 *  Constructor for learning
	 * 
	 * WARN not working ? we do not have a model
	 * 
	 * @param fb2
	 * @param tagset
	 * @param ergodic
	 * @throws Exception
	 */
	public SttTaggerSparse(FeatureEncoder fe,FeatureBuilder fb2, SstTagSet tagset, boolean ergodic) throws Exception {
		minit(fe, fb2, tagset,ergodic);	
		//Not sure we should always load (probably only need for training)
		//@JAB BFS loadLabels(tagset.getName());
		//MODEL IS NOT CREATED?? 
		//loadLabels(model.openTagSetFile());
		loadLabels();
	}
	
/**
 * @TODO check labels to discard and unify with the laodLabels from a FILE
 * @param tagset2
 */
	protected void loadLabels() throws IOException {
		LABELS = new HashSet<String>();
		 int k=0;
		for(String input: tagset.getListLabel()) {
			//TODO NOT USE 0
			try {
			if(input.compareTo("0")!=0 && mode!=SttTagger.tagmode.POS) LABELS.add(input.substring(2));
	     	}
			catch(Exception e) {				
				throw new RuntimeException("Unparsable BIO label:"+input+"\n Shoudl be 0 or start with B- or I- prefix");
			}
			//@TODO check k is total or just the labels loaded
			++k;
			//logger.info("Label "+LABELS.size()+" "+input);
		}
		logger.info("k set to "+k);
		this.ps_hmm.k=k;
		//@TODO JAB (??)
		LABELS.add(ALL_LABEL);
		
		
	}

	
	/**
	 * 
	 * Tagging a sequence
	 * 
	 * @param vs	wordforms
	 * @param fvpos pos
	 * @param fvlemma lemmas
	 * @return sequence of tags
	 */
	public String[]  tagSequence(final String[] vs,final String [] fvpos, final String [] fvlemma) {
		FeatureVector[] fv = new FeatureVector[vs.length];
		for(int i=0; i<fv.length;++i) fv[i]= new FeatureVector();
		fb.extractFeatures(new InstanceRaw(vs,fvpos,fvlemma,null),fv);
		
		//for(int i=0; i<fv.length;++i) System.err.println("feat:"+fv[i].toString());
		return innerStringTagging(fv);
	}

	public int[]  tagSequenceInt(final String[] vs,final String [] fvpos, final String [] fvlemma) {
		FeatureVector[] fv = new FeatureVector[vs.length];
		fb.extractFeatures(new InstanceRaw(vs,fvpos,fvlemma,null),fv);
		return innerTagging(fv);
	}

	/**
	 * 
	 * Tagging a sequence
	 * 
	 * @param vs	wordforms
	 * @param fvpos pos
	 * @return sequence of tags
	 */
	public String[]  tagSequence(final String[] vs, final String [] fvpos) {
		if(vs.length==0) {
			logger.warn("Trying to tag and empty sequence: IGNORING");
			return EMPTY_STRING_ARRAY;
		}
		FeatureVector[] fv = new FeatureVector[vs.length];
		for(int i=0;i<fv.length;++i) fv[i]=new FeatureVector();
		
		fb.extractFeatures(new InstanceRaw(vs,fvpos,EMPTY_STRING_ARRAY,null),fv);
		return innerStringTagging(fv);
	}	

	public int[]  tagSequenceInt(final String[] vs, final String [] fvpos) {
		if(vs.length==0) {
			logger.warn("Trying to tag and empty sequence: IGNORING");
			return EMPTY_INT_ARRAY;
		}
		FeatureVector[] fv = new FeatureVector[vs.length];
		fb.extractFeatures(new InstanceRaw(vs,fvpos,EMPTY_STRING_ARRAY,null),fv);
		return innerTagging(fv);
	}	
	/**
	 * 
	 * shortcut for tagging a PoS sequence
	 **/
	public String[]  tagSequence(final List<String> vs) {
		return  tagSequence(vs.toArray(EMPTY_STRING_ARRAY), EMPTY_STRING_ARRAY);
	}
	
	public int[]  tagSequenceInt(final List<String> vs) {
		return  tagSequenceInt(vs.toArray(EMPTY_STRING_ARRAY), EMPTY_STRING_ARRAY);
	}
	/**
	 * encode a List of features
	 * 
	 * @param O_str
	 * @param secondorder
	 * @param fe
	 * @return
	 */
	public static  final ListInteger[]  encode(final FeatureVector[] O_str, final boolean secondorder, FeatureEncoder fe) {
		 int _O_ = O_str.length;
		 ListInteger[] O = new ListInteger[_O_];
  
   	for (int i = 0; i < _O_; ++i){
   		FeatureVector list = O_str[i];
			int n = list.size();
   		ListInteger O_i = new ListInteger(n);
   		for (int j = 0; j < n; ++j){
   			String a = list.get(j);
   			//@JAB?? adding only to the map (??)
   			O_i.set(j,(int) fe.encode(a));
   			
   			//SecondOrder Features
   			if (secondorder){
   				for (int r = j+1; r < n; ++r){
   					String b = list.get(r);
   					if (a.compareTo(b)>0) //?? a< b
   						O_i.set(r,(int) fe.encode(""+a+"-"+b));
   					else 
   						O_i.set(r,(int) fe.encode(""+b+"-"+a));
   				}
   			}
   		}
   		O[i]=O_i;
   	}
    return O;
}

	/// Internal call for tagging from vector of features
	public int[] innerTagging(final FeatureVector[] res) {
		ListInteger[] resint = SttTaggerSparse.encode(res , model.secondOrder,fe);
		return ps_hmm.viterbi(resint);
	}
	
	/// Internal call for tagging from vector of features
	public String[] innerStringTagging(final FeatureVector[] res) {
		///checkConsistence  if BIO 
		int ms[] = innerTagging(res);
		return mode==tagmode.BIO ? tagset.checkConsistency(ms) : tagset.decode_tags(ms);
		
	}







	//
	// Training evaluation methods
	//


	/**
	 * 
	 * 
	 * @param traindata
	 * @param testdata
	 * @param tagsetname
	 * @param secondorder
	 * @param T
	 * @param CV
	 * @param mode
	 * @param R
	 * @param ww
	 * @param thetafile
	 * @throws Exception 
	 */ 
	public void eval_light(String traindata,String testdata, String encoding, FeatureBuilder fb, SstTagSet tagsetname,boolean secondorder, int T, int CV,Boolean R, Double ww,String thetafile) throws Exception{
		logger.info("\neval-light("
				+ "\n\ttraindata:"   + traindata
				+ "\n\ttestdata:"    + testdata
				+ "\n\ttagsetname:"  + tagsetname
				+ "\n\tsecondorder=" + secondorder
				+ "\n\tT = "         + T
				+ "\n\tCV = "        + CV 
				+ "\n\tmode = "      + mode
				+ "\n\tR = "         + R 
				+ "\n\twordweight = "  + ww 
				+ "\n\ttheta = "  + thetafile
				+ " )");
		//@DEPRECATED init("NULL",tagsetname,mode,fb);
		
		//@JAB implementation
		List<List<Integer> > G = ClassChooser.createListListInteger();//(List<List<Integer> >)(List<?>) new Vector<Vector<Integer> >();
		List<List<Integer> > G2=  ClassChooser.createListListInteger();//(List<List<Integer> >)(List<?>)new Vector<Vector<Integer> >();
		List<String> ID =  ClassChooser.createListString(); // new Vector<String> ();
		List<String> ID2 = ClassChooser.createListString(); //new Vector<String>();
        // Std binary features data
	
		List<List<List<Integer>>> D =  ClassChooser.createListListListInteger(); //((List<List<List<Integer>>>) (List<?>)new Vector<Vector<Vector<Integer>>>());
		List<List<List<Integer>>> D2=  ClassChooser.createListListListInteger(); //((List<List<List<Integer>>>) (List<?>)new Vector<Vector<Vector<Integer>>>());
		load_data(traindata,encoding, D,G,ID,secondorder,true);
		load_data(testdata,encoding, D2,G2,ID2,secondorder,false);

		logger.info("@JAB load ends");

		String tagsetsuff, trainsuff, testsuff;

		trainsuff = new File(traindata).getName();
		testsuff = new File(testdata).getName();
		tagsetsuff = tagset.getName();

		String description = tagsetsuff+"_"+trainsuff+"_"+testsuff+".results";
		eval(D,D2,G,G2,T,CV,description,mode,ww,thetafile);
		

	}

	/**
	 * 
	 * 
	 * @param traindata
	 * @param testdata
	 * @param tagsetname
	 * @param secondorder
	 * @param T
	 * @param CV
	 * @param mode
	 * @param R
	 * @param ww
	 * @param thetafile
	 * @throws Exception 
	 */ 
	public void eval_light(TrainingData traindata, TrainingData testdata, FeatureBuilder fb, SstTagSet tagset,boolean secondorder, int T, int CV, Boolean R, Double ww,String thetafile) throws Exception{
		logger.info("\neval-light("
				+ "\n\ttraindata:"   + traindata
				+ "\n\ttestdata:"    + testdata
				+ "\n\ttagsetname:"  + tagsetname
				+ "\n\tsecondorder=" + secondorder
				+ "\n\tT = "         + T
				+ "\n\tCV = "        + CV 
				+ "\n\tmode = "      + mode
				+ "\n\tR = "         + R 
				+ "\n\twordweight = "  + ww 
				+ "\n\ttheta = "  + thetafile
				+ " )");
		//init("NULL",tagsetname,mode,fb);


		logger.info("@JAB load ends");

		String tagsetsuff, trainsuff, testsuff;

		trainsuff = new File(traindata.getName()).getName();
		testsuff = new File(testdata.getName()).getName();
		tagsetsuff = new File(tagsetname).getName();

		String description = tagsetsuff+"_"+trainsuff+"_"+testsuff+".results";
		eval(traindata,testdata,T,CV,description,mode,ww,thetafile);
		

	}
	

	

	/*
	 * FIX can not return arrays!!!
	 * 
	 * @TODO we should use a formatReader the problem is that currently implementation of TrainingData 
	 * NEEDSto know the size of the training corpus in advance
	 */
	 public TrainingData	 load_data(String dname, String encoding, boolean secondorder)throws IOException{

		List<List<List<Integer> > > ND= ((List<List<List<Integer>>>) (List<?>)new Vector<Vector<Vector<Integer> > >());
		List<List<Integer> > NG= ((List<List<Integer>>) (List<?>) new Vector<Vector<Integer> >());
		List<String> NID = new Vector<String>();
		//call 
		load_data(dname,encoding, ND, NG,NID, secondorder,true);
		TrainingData td = new TrainingDataSST(ND, NG,NID);
		td.setName(dname);
		
		return td;	
	}

	/**
	 * loads data from dname
	 * @param dname
	 * @param D
	 * @param G
	 * @param ID
	 * @param secondorder
	 * @throws IOException
	 */
	void load_data(String dname, String encoding,
		 final	List<List<List<Integer> > > D,
		 final	List<List<Integer> > G,
		 final	List<String> ID,
		 boolean secondorder, 
		 boolean updateFeatureBuilder) throws IOException{
		logger.info("\ntagger_light::load_data("+dname+")");

		BufferedReader fin =		Utils.getBufferedReader(dname,encoding);

		String buff;

		while((buff=fin.readLine())!=null) {			
//			System.err.println("read line "+buff);
			//processLine( buff,  D, G, ID, secondorder, updateFeatureBuilder);
			TrainingInstance te = freader.readExample( buff);
			if(te!=null) {
				
				// See if something looks wrong
////				
//				StringWriter r = new StringWriter();
//				te.dump(r);
//				System.err.println("READED\n "+r);
//				System.exit(0);
				
			ID.add(te.id);
			//@JAB implemtation
			List<Integer> td = new Vector<Integer>();
			for(int i=0;i<te.T.length;++i) td.add(te.T[i]);
			G.add(td);
			//@JAB implementaiton
			List<List<Integer> > tg = ((List<List<Integer>>) (List<?>)new Vector<Vector<Integer>>());
			for(int j=0;j<te.W.length;++j) {
			//@JAB implementation
			  List<Integer>	tgi = new Vector<Integer>();
			  for(int i=0;i<te.W[j].length;++i) tgi.add(te.W[j][i]);
			  tg.add(tgi);
			}
			D.add(tg);
			}
		}

		logger.info("\t|D| = " +D.size() +"\t|G| = " +G.size() +"\t|ID| = " +ID.size() +"\t|FIS| = ?"); // +fb.FSIS_hsize());


	}


	
   
/**
 * 
 * version of reading TrainingData using vectors
 * 
 * @param buff
 * @param D
 * @param G
 * @param ID
 * @param secondorder
 * @throws Exception 
 */
	public void processLine( String buff, 
			final List<List<List<Integer> > > D,
			final List<List<Integer> > G,
			final List<String> ID,
			boolean secondorder, 
			boolean updateFeatureBuilder) throws Exception {
		String fields[] =	buff.split("[\t]");

		String id = fields[0];

		if (id != ""){
			//@JAB implementation
			List<Integer> L = new Vector<Integer>();
			List<List<Integer> > S = ((List<List<Integer>>) (List<?>)new Vector<Vector<Integer> >());
			ID.add(id);

			//logger.warn("sentence id ="+ID);


			for(int tj=1;tj<fields.length;tj++){
				//@JAB implementation
				List<String> O = new Vector<String>();
				List<Integer> O_int = new Vector<Integer>();

				String tokens[]= fields[tj].split("[ ]");
				//String f = fields[0];
				//O.add(f);
				//log.warn("READ:"+fields[tj]);
				int ti=0;
				while (ti<tokens.length){
					String f=tokens[ti++];
					O.add(f);
				}

				int _O_ = O.size();
				if (_O_ < 2){
					logger.error("\ntoo few info in id: "+id);
				}
				int i;
				for (i=0; i < _O_-1; ++i){
					//@JAB Changing elementAt to get
					String a = O.get(i);
					
					Long v = fe.encode(a); //fb.FSIS_update_hmap(a,updateFeatureBuilder);
					if(v!=null) O_int.add(v.intValue());
					else {logger.info("Feature "+a+" not encoded");}
					
					//@JAB todo looks like reimplemented in FeatureBuilder
					if (secondorder){
						for (int j = i+1; j < _O_-1; ++j){
							//@JAB changing elementAt for get
							String b = O.get(j);
					
							//JAB CHECK is the same in C++ a<b
							if (a.compareTo(b)<0) {
							    v = fe.encode(a+"-"+b); //FSIS_update_hmap(a+"-"+b,updateFeatureBuilder);
								if(v!=null) O_int.add(v.intValue());
								else {logger.info("Feature "+a+"-"+b+" not encoded");}
								
							}
							else {
								v = fe.encode(b+"-"+a); //FSIS_update_hmap(b+"-"+a,updateFeatureBuilder);
								if(v!=null) O_int.add(v.intValue());
								else {logger.info("Feature "+b+"-"+a+" not encoded");}
								
							}
						}
					}
				}
				S.add(O_int);
				
				//@JAB check Adding the last label (Training) if we use FSIS will not be in the right order
				//@JAB changing element at to get
				L.add(tagset.LSIS_add_update_hmap(O.get(i)));
			}
			D.add(S);
			G.add(L);
		}
	}



	/**
	 * @param D
	 * @param D2
	 * @param G
	 * @param G2
	 * @param T
	 * @param CV
	 * @param descr
	 * @param mode
	 * @param ww
	 * @param thetafile
	 * @throws Exception
	 */
	public void eval(
			final List<List<List<Integer> > > D,
			final List<List<List<Integer>>>   D2,
			final List<List<Integer> > G,
			final List<List<Integer> > G2,
			int T,
			int CV,
			String descr,
			SttTagger.tagmode mode,
			Double ww,
			String thetafile) throws Exception{
		TrainingData cvTrainData= new  TrainingDataSST(D,G);
		TrainingData cvTestData= new  TrainingDataSST(D2,G2);
		eval(cvTrainData,cvTestData, T, CV,descr,mode,ww, thetafile);
	}

	////////////////////////////////////EVAL


	public void eval(TrainingData cvTrainData,
			TrainingData cvTestData,
			int T,
			int CV,
			String descr,
			SttTagger.tagmode mode,
			Double ww,
			String thetafile) throws Exception{
		boolean no_special_symbol = false;

		//Evaluation object
		LearningStatistics res = new LearningStatistics();
		// Initialization for evaluation

		res.init(CV,T,LABELS);
		//@JAB implementation
		List<List<Double> > CV_T_ALL = ((List<List<Double>>) (List<?>)new Vector<Vector<Double> >());
		List<List<List<Double> > > CV_POS_RES = ((List<List<List<Double>>>) (List<?>)new  Vector<Vector<Vector<Double> > >());


		//Crossvalidation loop
		for (int cv = 0; cv < CV; ++cv){
			logger.info("\nCrosValidation cv = " + cv);
			//@JAB implementaiton
			List<Double> T_ALL = new Vector<Double>();
			List<List<Double> > POS_RES= ((List<List<Double>>) (List<?>) new Vector<Vector<Double> >());

			PS_HMM_Sparse ps_hmm_cv= new PS_HMM_Sparse(tagset);
			ps_hmm_cv.init(fe,no_special_symbol, ww);


			for (int t = 0; t < T; ++t){
				logger.info("Iteration T "+t);	   
				ps_hmm_cv.train(cvTrainData);// (cvTrainData.D,cvTrainData.G); //D1, G1
				int[][] tst_guess = ps_hmm_cv.guess_sequences(cvTestData);//(cvTestData.D); //DA"
				double tst_f = 0;      
				//dump sequence
				//TRACE Utils.arraydump(D2,"inputtst_guess", "inputguess.dlog");
				//TRACE Utils.arraydump(DA2,"inputtst_guess", "realinputguess.dlog");
				//TRACE Utils.arraydump(tst_guess,"tst_guess", "guess.dlog");

				if (mode == SttTagger.tagmode.POS){
					// ERROR possible itemized_res is return
					//@JAB implementation
					List<Double> itemized_res =new Vector<Double>();
					//@TODO FIX size problem fb.LSIS_size() LABELS.size()
					//tst_f = ET.evaluate_pos(cvTestData.G,tst_guess,fb,LABELS.size(),itemized_res); //GA2
					tst_f = et.evaluate_pos(cvTestData,tst_guess,tagset,LABELS.size(),itemized_res); //GA2
					POS_RES.add(itemized_res);
					T_ALL.add(tst_f);
				}
				else {

					//@TODO PROBLEM tst_vr returned 
					//@JAB implementation
					List<Verbose_res> tst_vr = new  Vector<Verbose_res>();
					//@TODO FIX size problem fb.LSIS_size() LABELS.size()
					//tst_f = ET.evaluate_sequences(cvTestData.G,tst_guess,fb, LABELS.size(),tst_vr); // GA"
					tst_f = et.evaluate_sequences(cvTestData,tst_guess,tagset, LABELS.size(),tst_vr); // GA"

					res.update(cv,t,tst_f,tst_vr);

				}
				logger.info("\ttst = " + tst_f);
			}
			CV_T_ALL.add(T_ALL);
			CV_POS_RES.add(POS_RES);
		}

		if (mode != SttTagger.tagmode.POS)
			res.print_res(descr+".eval",CV,T);
		else {

			FileOutputStream fos = new FileOutputStream(descr+".eval_pos");
			PrintStream out = new PrintStream(fos);
			for (int j = 0; j < T; ++j){    
				//@JAB implemtation
				List<Double> all_j = new  Vector<Double>();
				for (int i = 0; i < CV; ++i)
					all_j.add(CV_T_ALL.get(i).get(j));
				double mu_all_j = Stats.mean(all_j);
				double err_all_j =  Stats.std_err(all_j,mu_all_j);
				out.println( mu_all_j + "\t" + err_all_j);
			}


			out .println();out .println();

			for (int r = 0; r <tagset.LSIS_size(); ++r)
				out.print(tagset.LSIS(r) + " ");
			out .println();

			for (int j1 = 0; j1 < T; ++j1){
				for (int r = 0; r<tagset.LSIS_size(); ++r){
					//@JAB implementation
					List<Double> pos_tj = new Vector<Double>();
					for (int i = 0; i < CV; ++i)
						pos_tj.add(CV_POS_RES.get(i).get(j1).get(r));
					double mu_tj = Stats.mean(pos_tj);
					out.print(mu_tj + " ");
				}
				out .println();
			} 
			out.close();
		}

	}

	


	/**
	 * A method for training a model
	 * @throws Exception 
	 * 
	 * @train TrainingData
	 * @T number of epochs
	 * @modename Name of the output model
	 **/
	public void train(TrainingData train,int T,String modelname, FeatureDecoder fd) throws Exception{
		
		Double ww = 1.0;
		boolean no_special_symbol = false;
		ps_hmm.init(fe, no_special_symbol, ww);
		
		for (int t = 1; t <= T; ++t)
			ps_hmm.train(train); //ps_hmm.train(D,G);
		
		ModelFormatReader.SaveModel(ps_hmm,modelname,fd);
	}

	/**
	 * 
	 * A method to train the parser
	 * 
	 * @param modelname
	 * @param traindata
	 * @param tagsetname
	 * @param secondorder
	 * @param T
	 * @param mode
	 * @throws Exception
	 */
	public void train_light(String modelname, String traindata, String encoding, boolean secondorder, int T, FeatureDecoder fd) throws Exception{
		logger.info("\ntrain-light("
				+ "\n\tmodelname = "     + modelname
				+ "\n\ttraindata = "     + traindata
				+ "\n\ttagsetname = "    + tagset.getName()
				+ "\n\tsecondorder = "   + secondorder
				+ "\n\tT = "             + T 
				+ "\n\tmode = "  + mode  + " )");
		
		//SttTagger TL = new SttTagger(fb, tagset, ergodic);
		TrainingData TD = load_data(traindata, encoding, secondorder);
		train(TD,T,modelname,fd);//TL.train(TD.D,TD.G,T,modelname);
	}	 

	/**
	 * 
	 * A method to train the parser
	 * 
	 * @param modelname
	 * @param traindata
	 * @param tagsetname
	 * @param secondorder
	 * @param T
	 * @param mode
	 * @throws Exception
	 */
	public void train_light_online(String modelname, String traindata, String encoding,boolean secondorder, int T, FeatureDecoder fd) throws Exception{
		logger.info("\ntrain-light("
				+ "\n\tmodelname = "     + modelname
				+ "\n\ttraindata = "     + traindata
				+ "\n\ttagsetname = "    + tagset.getName()
				+ "\n\tsecondorder = "   + secondorder
				+ "\n\tT = "             + T 
				+ "\n\tmode = "  + mode  + " )");
		
	//	SttTagger TL = new SttTagger(fb, tagset, ergodic);
		TrainingData TD = new TrainingDataSSTonline(traindata, new TrainingFormatReaderSSTfeatures(tagset,fb, new FeatureHashEncoderDecoder()));
		train(TD,T,modelname,fd);
	}	 
}