package tagger.core;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import tagger.data.ModelDescription;
import tagger.data.ModelFormatReader;
import tagger.data.SstTagSet;
import tagger.data.TrainingData;
import tagger.data.TrainingInstance;
import tagger.data.TrainingExampleSparse;
import tagger.features.FeatureEncoder;
import tagger.features.Fs;
import tagger.utils.ClassChooser;

public class PS_HMM_Sparse extends ChainSparse {
	
	
	static Logger logger = Logger.getLogger(PS_HMM_Sparse.class);
	
	private static final String BI_SEP = "-";
	private static final String START_BI = "#-";
	private static final String END_BI = "-#";
	public static final String FEATURE_MODEL_FILENAME_EXTENSION = ".F";
	public static final String PS_HMM_MODEL_FILENAME_EXTENSION = ".PS_HMM";
	
	
	public static final String SAVEMODEL_ENCODING = "UTF-8";
 	
	/// use weighted example to modify the update steps (=? seeing the example weight times)
	private double UPDATE_STEP = 1;


	///To allow debugging-reproduceable results using the same random seed
	public static Random randGen = new Random();
	
	
	/***
	 * 
	 * Model-State
	 * 
	 ****/
	/// size of state space/total number of iterations
	  public int it;       	
	  
	  /// Average mode on updating weights
	 public boolean av_mod;
	
	  /// when true, only use active features
	  public boolean only_active;   
	  
	  /// the ratio of word_contribution/label-label contr
	  public double word_weight;  
	  
	  /// phi start
	  public List<Fs> phi_s;
	  
	  /// phi end
	  public List<Fs> phi_f;
	  
	  
	  /// phi label-label
	  public Fs[][] phi_b;  
	  
	  ///
	  public List<HashMap<Integer,Fs> > phi_w;
	
	  
	  /**
	   * 
	   * End of the Model State
	   * 
	   */
	  
	  
	  
	  
	  
	  /// temporary variables useful for carrying around
	  int cur_ex;
  
	  ///
	  SstTagSet tagset;
	
	  ///
	  protected int cur_pos;   
	    
	  ///@jordi: active features (to filter and use only certain features, this option is controled by only_active boolean var)
	  List<HashSet<Integer> > active_phi_w;
	 
	  
	  
	  
	  
	public  PS_HMM_Sparse(final SstTagSet tagset) {
		super();
		this.tagset=tagset;
		it = 0; 
		only_active = false; 
		av_mod = true; 
		cur_ex = -1; 
		word_weight = 1;
		
		//@JAB implementation
		phi_w =ClassChooser.createHashMapIntegerFs();// new Vector<HashMap<Integer,Fs> >();
	    active_phi_w = ClassChooser.ListHashSetInteger();//  new Vector<HashSet<Integer> >();
	  
	}
	

	 
	 /**
	  * 
	  * @TODO
	  * 
	  * PS_HMM need to know in advance the size of the feature space
	  * 
	  * @param k_val size of the space (tagset)
	  * @param fb
	  * @param no_spec No special Transitions for first and last state
	  * @param wc
	  */
	public void init(FeatureEncoder fb, boolean no_spec, double wc){
	  logger.info("\nPS_HMM::init(" + tagset.size() + ")");
	  k = tagset.size();
	  word_weight = wc;
	  no_special_end_transition_sym = no_spec;
	  if( !no_spec ) {
		//?Constructor  
		  phi_s = new ArrayList<Fs>(k);
		//?Constructor 
		  phi_f= new ArrayList<Fs>(k);
		 
		  
		  
		 // bigrams begin
	     init_phi_sf(phi_s,fb,true,tagset);         
	     
	     // bigrams end
	     init_phi_sf(phi_f,fb,false,tagset);       
	     
	     
	     
	     logger.info("Phi length:"+phi_s.size());
	  }
	  
	  //?Constructor  of transition form label to label
	  phi_b = new Fs[k][k];
	  init_phi_b(k,phi_b,fb,tagset);               // std transitions
	  
	  // init_phi_w(k,phi_w,fb.FSIS_size(),active_phi_w);  // sparse matrix for std features
	  init_phi_w(k,phi_w,active_phi_w);  // sparse matrix for std features
	  logger.info("\tdone");
	}
	
	
	/***
	 * This 4 functions is what chain needs
	 * 
	 */
	
	 @Override
	 protected double get_phi_s( int j ) { return phi_s.get(j).avg_val( it, av_mod ); }
	 
	 @Override
	 protected double get_phi_f( int j ) { return phi_f.get(j).avg_val( it, av_mod ); }
	 
	 @Override
	  protected double get_phi_b( int i, int j ) { return phi_b[i][j].avg_val( it, av_mod ); }
	  
	 //get_word_contribution( int[]  x, int j, int pos ){
	 @Override 
	 protected double get_word_contribution(ListInteger x, int j, int t) {
		  double word_contribution = 0;
		  //int _x_ = x.size();
		  if( only_active ) 
		    for ( int v: x.toArray() ) {
		    	if(active_phi_w.get(j).contains( v )) {
		    		Fs mFs = phi_w.get(j).get( v);
			if ( mFs != null) word_contribution += mFs.avg_val( it, av_mod );
		      }
		    }
		  else 
		    for ( int v:x.toArray()){
		      Fs mFs = phi_w.get(j).get( v );
		      if ( mFs!= null) word_contribution += mFs.avg_val( it, av_mod );
		    }
		  return word_weight * word_contribution;
		}

	 // vector duality
	 double get_word_contribution( List<Integer> x, int j, int pos ){
		  double word_contribution = 0;
		  int _x_ = (int) x.size();
		  if( only_active ) 
		    for ( int i = 0; i < _x_; ++i ) {
		    	if( active_phi_w.get(j).contains( x.get(i)))  {
			     Fs mFs = phi_w.get(j).get( x.get(i) );
			if ( mFs!=null ) 
			  word_contribution += mFs.avg_val( it, av_mod );
		      }
		    }
		  else 
		    for ( int i = 0; i < _x_; ++i ){
		    	Fs mFs =phi_w.get(j).get( x.get(i) );
		      if ( mFs!=null )
			word_contribution += mFs.avg_val( it, av_mod );
		    }
		  return word_weight * word_contribution;
		}

	
	 
	 void decode_s(List<Integer> _u,List<List<Double> > delta,List<List<Integer> > psi,int argmax_t){
		  int N = delta.size();
		  int best = argmax_t;
		  
		  //@JAB implementation
		  List<Integer> r_u = ClassChooser.createListInteger(); //new Vector<Integer>();
		  r_u.add(argmax_t);
		  for (int t = N - 1 ; t > 0 ; --t){
			  best = psi.get(t).get(best);
		      r_u.add(best);
		  }	  
		  
		  //@JAB 
		  for (int t=r_u.size()-1;t>=0; --t){
			  _u.add(r_u.get(t));
		  }
		  
		  
		}
	 
	 /**
	  * Load Model
	  * 
	  * @param modelname
	  * @throws IOException
	  */
	 public void jabLoadModel(ModelDescription model) throws IOException{
		 logger.info("\nLoadModel("+ model.buildFilename() +")");
		 //@JAB	 
	     ModelFormatReader.jabLoadModel(this, model);
		 
	 }
	 

	 //
	 // methods call from evaluation training 
	 //
	
	
	 
	 /***
	  * 
	  *  TRAIN with all the examples  on trainset
	 * @throws Exception 
	  *  
	  */ 
	 public void train( TrainingData trainset) throws Exception {
		  int N = trainset.length();
		  Set<Integer> Index = new HashSet<Integer>();
		  for ( int i = 0; i < N; ++i )    Index.add( i );
		  train(trainset,Index);
		}
	 
	 /**
	  * jordi: random reorder of a vector (probalby can be better reimplemented)
	  * @param IN
	  * @return
	  */
	 public static int[] rand_reorder_vector(final int[] IN){
		  int N = IN.length;
		  int [] OUT = new int[N];
		  logger.info("\trand_reorder(|IN|=" + N + ",|OUT|=");
		  Set<Integer> visited = new HashSet<Integer>();
		  int j=0;
		  while( j < N ){
			 //jordi: use random instead of Math.random() so we can use a reproduce random seq (same seed)
			  int s = (int)( Math.floor( randGen.nextDouble() * N ));
			if(!visited.contains( s )){
		      visited.add( s );
		      OUT[j++]= IN[s];
		    }
		  }
		  logger.info(OUT.length +")");
		  return OUT;
		}
	 
	 
	 
	 
	 
	 /**
	  * train on a sample of examples
	 *  the order of the examples is  randomized 
	 * @param trainset the associated labels
	 * @param Index  (sample of id of the examples to use for training) 
	 * @throws Exception 
	 */
	 void train( TrainingData trainset, Set<Integer> Index 
			 ) throws Exception{
		  int updates = 0, counter = 0, N = Index.size();
		  logger.info("\ntrain(|Index| = " +N + ")");
		  int[] IN = new int[N];
		  int[] OUT;
		  int j=0;
		  for ( Iterator<Integer> I_i = Index.iterator(); I_i.hasNext();  )
		     IN[j++]= (Integer)I_i.next() ;
		  
		  //TO TRACE with NORANDOM 
		  //OUT=IN;
		  
		   OUT = rand_reorder_vector(IN);
		  
		  for ( int i = 0; i < N; ++i ){
		    ++it;
		    cur_ex = OUT[i]; 
		    //@TODO jab training corpus should provide a way  to get a single example
		   // boolean updated_s = train_on_s( W[OUT[i]], T[OUT[i]]);
		    TrainingExampleSparse t = trainset.getTrainingExampleSparse(OUT[i]);
		    
		    //t.dump();
		    
		    try {
		    boolean updated_s = train_on_s( t );
		    
		    //TRACE NORANDOM cur_ex = IN[i];
		    //TRACE NORANDOM boolean updated_s = train_on_s( W[IN[i]], T[IN[i]]);
		    if ( updated_s )
		      ++updates;
		    if ( ++counter % 1000 == 0 ) logger.info(".");
		    }catch(Exception e) {
		    	logger.error("problem trainning with example:");
		    	StringWriter sw = new StringWriter();
		    	t.dump(sw);
		    	logger.error(sw);
		    }
		  }
		  logger.info( "\ti = " + it + "\ts-error = " +(double) updates / N);
		}
	 
	 
	 /**
	  * to train on one example
	 * @param W example
	 * @param T assoaciated labels
	 * @return true if PS_HMM needs has to be update (? a.k.o. lazzy evaluation of the PS_HMM weight)
	 * @throws IOException 
	 */
	boolean  train_on_s( TrainingExampleSparse e) throws IOException {
	
		
		 try {
			 
		 //@TODO investigate why av_mod is set to false for viterbi	 
		  av_mod = false;
		  
		//TRACE String filename = String.format("train%04d.jlog",nit); nit++;
		//TRACE  System.err.println("Log:"+filename);
		//TRACE  FileWriter tlog = new FileWriter(filename);

		  int[] _u = viterbi(e.W);

		  //@TODO check-find out why av_mod needs to be changed true/false (?)
		  av_mod = true;
		 
		  //trace imput
		  //TRACE  for(int i=0;i<T.length;++i)
		  //TRACE  	  tlog.write(i+":"+T[i]+" "+_u[i]+"\n");
		 
		  //@TODO JAB check hack: weight
		  this.UPDATE_STEP=1.0;//e.getWeight();
		  
		  boolean s_update  = update( e.W, e.T, _u );

		 
		  cur_ex = -1; //??
		  return s_update;
		 }catch (Exception e1) {
			 StringWriter sw = new StringWriter();
			 e.dump(sw);
			 logger.error(sw);
			 logger.error(e1);
			 System.exit(-1);
			 return false;
		 }
		}
	 
	 
	 
	 
	 boolean update(final ListInteger[] W, int[] T,  int[] U ){
		  int N = U.length;
		  boolean s_update = false;
		  update_boundary_feats( U[0], T[0], U[N-1], T[N-1] );
		  if ( U[0] != T[0] )  {
		      s_update = true;
		      cur_pos = 0;
		      update_word_feats( W[0], T[0], U[0] );
		  }
		  for ( int i = 1; i < N; ++i )
		    if ( U[i] != T[i] ) {
		      s_update = true;
		      cur_pos = i;
		      update_word_feats( W[i], T[i], U[i] );
		      update_transition_feats( T[i-1], T[i], U[i-1], U[i] );
		    }
		  if ( s_update ){
		    if( U[0] != T[0] )
		       finalize_update_word_feats( W[0], T[0], U[0], s_update );
		    for ( int i = 1; i < N; ++i ) 
		      if( U[i] != T[i] )
		        finalize_update_word_feats( W[i], T[i], U[i],s_update);
		    finalize_transition_feats( s_update ); 
		  }
		  return s_update;
		}
 
	 
	 protected void finalize_update_word_feats(final ListInteger W, int T, int U, boolean su){
		  int _X_ = W.size();
		  for ( int s = 0; s < _X_; ++s ){
		    finalize_feature_val( phi_w, U, W.get(s), it, only_active, active_phi_w, su );
		    finalize_feature_val( phi_w, T, W.get(s), it, only_active, active_phi_w, su );
		  }
		}

	 void finalize_feature_val(List<HashMap<Integer,Fs> > phi_w,int l, int feat,
			  int it, boolean active, List<HashSet<Integer> > act_phi_w, boolean s_upd){
		 //hashSet<Integer>::iterator active_F_i = act_phi_w[l].find( feat );
 if( !active || act_phi_w.get(l).contains(feat)) {
   Fs v = phi_w.get(l).get( feat );
   assert ( v!=null );
   //System.err.println("before:"+phi_w.get(l).get( feat ).dump());
   v.update( it, s_upd );
   //System.err.println("update:"+phi_w.get(l).get( feat ).dump());
 }
}
	 
	 
	 protected void update_word_feats(final ListInteger w, int T, int U ){
		  int _X_ = w.size();
		  for ( int s = 0; s < _X_; ++s ){
		    update_feature_val( phi_w, U, w.get(s), -UPDATE_STEP, only_active, active_phi_w );
		    update_feature_val( phi_w, T, w.get(s), UPDATE_STEP, only_active, active_phi_w );
		  }
		}
	 
	 /**
	  * 
	  * @param phi_w
	  * @param l
	  * @param feat
	  * @param val
	  * @param active
	  * @param active_phi_w
	  */
	 void update_feature_val(List<HashMap<Integer,Fs> > phi_w, int l, int feat,
				double val, boolean active, List<HashSet<Integer> > active_phi_w ) {
  
	  if( active && !active_phi_w.get(l).contains(feat) )    // was not active till now, add as active
	    active_phi_w.get(l).add( feat );
	  //@TODO check array index out of range
	  //System.err.println("accessing phi_w "+l+" max size "+phi_w.size());
	  if (!phi_w.get(l).containsKey(feat) ){
	    Fs f = new Fs();
	    phi_w.get(l).put( feat, f );
	  }
	  phi_w.get(l).get( feat ).upd_val += val;
	}
	 
	 protected void update_transition_feats( int bT, int eT, int bU, int eU ){
	      phi_b[bT][eT].upd_val += UPDATE_STEP;
	      phi_b[bU][eU].upd_val -= UPDATE_STEP;
	}

	 /**
	  * 
	  * ERROR CHECK THAT UPDATE is not returned!!!
	  * 
	  * @param s_update
	  */

	protected void finalize_transition_feats( boolean s_update) { 
	  int bs = phi_b.length;
	  for( int i = 0 ; i < bs; ++i ) 
	    for( int j = 0 ; j < bs; ++j ) 
	      phi_b[i][j].update( it, s_update );
	  if ( no_special_end_transition_sym ) 
	    for( int i = 0 ; i < bs; ++i ) {
	      phi_s.get(i).equate( phi_b[0][i] ); 
	      phi_f.get(i).equate( phi_b[i][0] ); 
	    }
	  else
	    for( int i = 0 ; i < bs; ++i )  {
	      phi_s.get(i).update( it, s_update );
	      phi_f.get(i).update( it, s_update );
	    }
	}
	
	protected void update_boundary_feats( int bU, int bT, int eU, int eT ) {
		  if( no_special_end_transition_sym ) {
		    phi_b[0][bU].upd_val -= UPDATE_STEP;
		    phi_b[0][bT].upd_val += UPDATE_STEP;
		    phi_b[eU][0].upd_val -= UPDATE_STEP;
		    phi_b[eT][0].upd_val += UPDATE_STEP;
		  } else {
		    phi_s.get(bU).upd_val -= UPDATE_STEP;
		    phi_s.get(bT).upd_val += UPDATE_STEP;
		    phi_f.get(eU).upd_val -= UPDATE_STEP;
		    phi_f.get(eT).upd_val += UPDATE_STEP;
		  }
		}
	
	
	 /*****************
	  * 
	  * 
	  *  @TODO those could be set as static methods?
	  * 
	  * 
	  * 
	  */

	
	 static void init_phi_b(int k, Fs[][] phi_b, FeatureEncoder fb, final SstTagSet ptagset){
	   logger.info("\n\tinit_phi_b(" + k + "): ");
	   
	   for (int tlx = 0; tlx < k; ++tlx){
	     Fs[] _Fb = new Fs[k];
	     for (int trx = 0; trx < k; ++trx){
	       Fs f = new Fs();
	       String id_str = ptagset.LSIS(tlx)+BI_SEP+ptagset.LSIS(trx);
	       //@TODO check what's up with f_id is not uset, should we register it
	        fb.encode(id_str);    //f_id = 0;
	       _Fb[trx]=f;
	     }
	     phi_b[tlx]=_Fb;
	   }
	   logger.info(" |phi_b| = " + phi_b.length + "*" + phi_b[0].length);
	 }

	 
	 
	 
	/**
	 * 
	 * initialize 
	 * an creates Feature for START_BI+label or   label+END_BI;
	 * 
	 * @param phi_sf
	 * @param fb Feature Builder
	 * @param is_s Is_start or is_end
	 * @param ptagset
	 */
	 static void init_phi_sf(List<Fs> phi_sf, FeatureEncoder fb,  boolean is_s, final SstTagSet ptagset){
	 //  cerr << "\n\tinit_phi_sf(" << is_s << ")";
		 
	 // R is the label of the SstTagset
	   for (String label : ptagset.getListLabel()){
	     
	     String  id_str  = is_s ?  START_BI+label :  label+END_BI;
	     	      
	     // we register the feature into the FeatureBuilder
	     long f = fb.encode(id_str);    //  f_id = 0;
	     //@JAB is phi_sf already initialized (not on training)??
	     phi_sf.add( new Fs());
	   }
	  // cerr << " |phi_sf| = " << phi_sf.size();
	 }
	 
	 
	 /**
	  * 
	  * k is the size of the feature space 
	  * @param k
	  * @param phi_w
	  * @param _wf_
	  * @param active_phi_w
	  */
	 static  void init_phi_w(int k, List<HashMap<Integer,Fs> > phi_w,List<HashSet<Integer>> active_phi_w){
			 logger.info("\n\tphi_w("+k+ ")");
	 
	   for (int i = 0; i < k; ++i){
	     phi_w.add(new  HashMap<Integer,Fs>());
	     active_phi_w.add(new HashSet<Integer>());
	      logger.info("\t|phi_w| = "+ phi_w.size());
	   }
	 }

	
	
public void dump(FileWriter tlog) throws IOException {
int i=0;
 for(Fs e: phi_s) {
	  tlog.write("phi_s["+i+"]="+e+"\n");
	  ++i;
 }
 
 i=0;
 for(Fs e: phi_f) {
	  tlog.write("phi_f["+i+"]="+e+"\n");
	  ++i;
 }
 
 i=0;
 for(Fs[] le: phi_b)  {
  int j=0;
	  for(Fs e: le){
		  tlog.write("phi_b["+i+","+j+"]="+e+"\n");
		  ++j;
	  	}
	  ++i;
 }
 
 
 // size is k but only some elements used (?)
 int p=0;
for(HashMap<Integer,Fs> e:  phi_w) {
	 Integer sk[] = e.keySet().toArray(new Integer[1]);
	 
	  Arrays.sort(sk);
	  for(Integer kk: sk) {
		 if(kk != null) 
			 tlog.write("phi_w["+p+"]="+e.get(kk)+"\n");
	  }
  	++p;
 }


// we are non using active_phi but initilizing it 
for(HashSet<Integer> ap: active_phi_w) {
	 Integer sk[] = ap.toArray(new Integer[1]);
	 Arrays.sort(sk);
	   for(Integer kk: sk) {
		 if(kk != null) 
		   tlog.write("active_phi_w  = "+k+"\n");
	   }
	   
}

 
 tlog.close();
 }


	 
}