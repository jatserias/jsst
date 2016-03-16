package tagger.core;

import java.util.Set;

import org.apache.log4j.Logger;

import tagger.data.TrainingData;


/**
 * @author jordi
 *
 * @TODO  REFACTOR into an interface?, DANGER empty methods implemented 
 */
public abstract class Chain {
	static Logger logger = Logger.getLogger(Chain.class);
	
	/// size of state space
	  public int k;       			
	  
	  /// separate parameters for end transition or not
	  protected boolean no_special_end_transition_sym;	
	  
     //@TODO just for limit TRACE 
	 // static int nvit=0;
	 
	  
	  public Chain() {k=0;no_special_end_transition_sym=false;}
	  
	


///////////////////////////////////////////////////////////////////////
//Viterbi:

	  protected void  viterbi_tj( double[][] delta, int[] x, int t, int j,int[][]psi ) {
		  try {
		  double wc = get_word_contribution( x, j, t );
		  //TRACE PrintStream out = new PrintStream(new FileOutputStream("kk.err",true));
		  //TRACE out.println("WC["+j+","+t+"]="+wc);
		  if ( t == 0 ){
			  psi[t][j] = -1 ; 
			  delta[t][j] =  wc + get_phi_s( j ) ;
			  return;
		  }
		  double max = Double.NEGATIVE_INFINITY; //-1e100; 
		  int argmax = 0;
		  for (int i = 0; i < k; ++i){
			  double score_ij = delta[t-1][i] + wc + get_phi_b( i, j );
			//TRACE out.println("delta["+(t-1)+","+i+"]="+delta[t-1][i]);
			//TRACE out.println("get_phi_b["+i+","+j+"]="+get_phi_b( i, j ));
			  if (score_ij > max ){
				  max = score_ij;
				  argmax = i;
			  }
			//TRACE out.println("max "+max+" argmax "+argmax);
		  }
		  psi[t][j] = argmax ;
		  delta[t][j] = max ;
		//TRACE out.close();
		  }catch(Exception e) {
			  e.printStackTrace();
		  }
	  }


protected abstract double get_phi_b(int i, int j);
//{throw new  RuntimeException("FAKE implementation");}

protected abstract double get_phi_f(int i);
//{throw new  RuntimeException("FAKE implementation"); }

protected abstract double get_phi_s(int j);
//{ throw new  RuntimeException("FAKE implementation"); }

protected abstract double get_word_contribution(int[] x, int j, int t);
//{throw new  RuntimeException("FAKE implementation");}


/**
 * WARNING WARNING
 * 
 * TODO check the function as there was extrange  iterator to fill u_
 * @param delta
 * @param psi
 * @param argmax_t
 * @return
 */
protected int[] decode_s(double[][] delta,int[][]psi,int argmax_t){
int N = delta.length;	
int [] _u = new int[N];
int best = argmax_t;
_u[N-1]=argmax_t;
for ( int t = N - 1 ; t > 0 ; --t ){
//System.err.println("t "+t+" size "+psi.length+"  best "+best+" siZe "+psi[t].length);	
_u[t-1]= psi[t][best];
  best = psi[t][best];
}
return _u;
}


///Stte seems the same but  Prediction is different
protected int[] viterbi( int[][] W) {
int [] U=null;

try {

//TRACE FileWriter dlog = new FileWriter(String.format("viterbi%04d.log",nvit));
//TRACE nvit++;
//TRACE dlog.write("W["+W.length+"]\n");
//TRACE for(int i=0;i<W.length;++i)
	//TRACE    for(int j=0;j<W[i].length;++j)
	//TRACE     dlog.write("W["+i+","+j+"]="+(W[i][j])+"\n");
	//TRACE     dlog.write("\n");

int N = W.length, argmax_t = 0;
//TRACE 
//logger.info("Size: N "+N+" k "+k+"\n");
if(N<=0) return new int[0];

double[][] delta= new double[N][k];
int[][] psi = new int[N][k];



//ATTENTION!!!
//Incorporate these lines into semisupervised code
//if( mode == Train ) 
//avr = false;


for( int t = 0; t < N; ++t ) 
	for ( int j = 0; j < k; ++j ) {
		viterbi_tj( delta, W[t], t, j, psi );
		//TRACE		dlog.write("W["+t+"]"+" j "+j+"\n");
		//TRACE		dlog.write("delta"+"\n");
		//TRACEfor(int i=0;i<delta.length;++i)
		//TRACE	for(int j1=0;j1<delta[i].length;++j1)
		//TRACE		dlog.write("delta["+i+","+j1+"]="+String.format("%.2f", (delta[i][j1]))+"\n");	
		//TRACEdlog.write("\n");
		//TRACEdlog.write("psi"+"\n");
		//TRACEfor(int i=0;i<psi.length;++i)
		//TRACE	for(int j1=0;j1<psi[i].length;++j1)
		//TRACE		dlog.write("psi["+i+","+j1+"]="+(psi[i][j1])+"\n");
		

	}

double max_t = Double.NEGATIVE_INFINITY;
for (int j = 0; j < k; ++j){
	double last_bit = delta[N-1][j] + get_phi_f( j );
	if ( last_bit > max_t ){
		max_t = last_bit;
		argmax_t = j;
	}
}


//TRACEdlog.write("Last delta"+"\n");
//TRACEfor(int i=0;i<delta.length;++i)
//TRACE	for(int j1=0;j1<delta[i].length;++j1)
//TRACE		dlog.write("ldelta["+i+","+j1+"]="+String.format("%.2f",(delta[i][j1]))+"\n");
//TRACEdlog.write("\n");
//TRACEdlog.write("RES MAX"+argmax_t+"\n");

U = decode_s(delta, psi, argmax_t );

//TRACEdlog.write("RES Decode U"+"\n");
//TRACEfor(int i=0;i<U.length;++i)
//TRACE	dlog.write("U["+i+"]="+(U[i])+"\n");
//TRACEdlog.write("\n");
//TRACEdlog.close();

}catch(Exception e) {
	e.printStackTrace();
}
return U;
}

///////////////////////////////////////////////////////////////////////
//Backward Viterbi



///////////////////////////////////////////////////////////////////////
//Decode:
public int[][] guess_sequences(TrainingData EVAL_D) throws Exception { //int[][][] EVAL_D){
	int _E_ = EVAL_D.length();
	int[][] EVAL_L = new int[_E_][];
	
	int tenth = (int) (_E_/10.0);
	if (tenth < 1) tenth = 1;

	int j=0;
	for ( int i = 0; i < _E_; ++i ){
		int[] GUESS_i=viterbi( EVAL_D.getTrainingExample(i).W);
		EVAL_L[j++]=( GUESS_i );
		if (i%tenth==0) logger.info(".");
	}
	return EVAL_L;
}

void guess_sequences( int[][][] EVAL_D, int[][] EVAL_L, Set<Integer> Index ){
	int _E_ = EVAL_D.length; int j=0;
	for ( int i = 0; i < _E_; ++i ){
		if ( Index.contains(i) ){
			int[] GUESS_i=viterbi( EVAL_D[i]);
			EVAL_L[j++]=GUESS_i;
		}
	}
}





	  
	  
}
