package tagger.core;

import org.apache.log4j.Logger;

import tagger.data.TrainingData;

public abstract class ChainSparse {
static Logger logger = Logger.getLogger(ChainSparse.class);
	
	/// size of state space
	  public int k;       			
	  
	  /// separate parameters for end transition or not
	  protected boolean no_special_end_transition_sym;	
	  
 
	 
	  
	  public ChainSparse() {k=0;no_special_end_transition_sym=false;}
	  
	


///////////////////////////////////////////////////////////////////////
//Viterbi:

	  protected void  viterbi_tj(double[][] delta, ListInteger x, int t, int j,  int[][] psi ) {
		 
		  try {
		  double wc = get_word_contribution(x, j, t );
		  if ( t == 0 ){
			  psi[t][j]= -1 ; 
			  delta[t][j]=   wc + get_phi_s(j);
			  return;
		  }
		  double max = Double.NEGATIVE_INFINITY; //-1e100; 
		  int argmax = 0;
		  for (int i = 0; i < k; ++i){
			  double score_ij = delta[t-1][i] + wc + get_phi_b( i, j );
		
			  if (score_ij > max ){
				  max = score_ij;
				  argmax = i;
			  }
			
		  }
		  psi[t][j]=argmax;
		  delta[t][j]=max;
		
		  }catch(Exception e) {
			  e.printStackTrace();
		  }
	  }


protected abstract double get_phi_b(int i, int j);

protected abstract double get_phi_f(int i);

protected abstract double get_phi_s(int j);

protected abstract double get_word_contribution(ListInteger x, int j, int t);


/**
 * WARNING WARNING
 * 
 * TODO add a sparse representation of the encoded vector
 * TODO check the function as there was extrange  iterator to fill u_
 * @param delta
 * @param psi
 * @param argmax_t
 * @return
 */
protected int[] decode_s(final double[][] delta, final int[][] psi,int argmax_t){
int N = delta.length;
int[] _u = new int[N];
int best = argmax_t;
_u[N-1]=argmax_t;
for ( int t = N - 1 ; t > 0 ; --t ){
  _u[t-1]=psi[t][best];
  best = psi[t][best];
}
return _u;
}


///Stte seems the same but  Prediction is different
protected int[] viterbi(final ListInteger[] W) {


try {

int N = W.length, argmax_t = 0;
if(N<=0) return new int[0];

double[][] delta= new double[N][k];
int[][] psi = new int[N][k];


for( int t = 0; t < N; ++t ) 
	for ( int j = 0; j < k; ++j ) {
		viterbi_tj( delta, W[t], t, j, psi );
	}

double max_t = Double.NEGATIVE_INFINITY;
for (int j = 0; j < k; ++j){
	double last_bit = delta[N-1][j] + get_phi_f( j );
	if ( last_bit > max_t ){
		max_t = last_bit;
		argmax_t = j;
	}
}

return decode_s(delta, psi, argmax_t );

}catch(Exception e) {
	e.printStackTrace();
}

return null;
}

///////////////////////////////////////////////////////////////////////
//Backward Viterbi



///////////////////////////////////////////////////////////////////////
//Decode:
public int[][] guess_sequences(TrainingData EVAL_D) throws Exception { 
	int _E_ = EVAL_D.length();
	int[][] EVAL_L = new int[_E_][];
	
	int tenth = (int) (_E_/10.0);
	if (tenth < 1) tenth = 1;

	for ( int i = 0; i < _E_; ++i ){
		int[] res =viterbi(EVAL_D.getTrainingExampleSparse(i).W);
		EVAL_L[i]=res;
		if (i%tenth==0) logger.info(".");
	}
	return EVAL_L;
}

}
