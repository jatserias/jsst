package tagger.core;

import tagger.core.PS_HMM;
import tagger.data.SstTagSet;

/**
 * PS_HMM with UNK (tagset should have UNK)
 * 
 * model should not have the  UNK label so tagging can be done with a normal PS_HMM
 * 
 * @author jordi
 *
 */
public class PS_HMMwithUNK extends PS_HMM {
	// Impossible unkown_id => same results
	public int unknown_id=-1;
	
	PS_HMMwithUNK(SstTagSet tagset) {
		super(tagset);
		//LOAD UNK label (??)
		unknown_id=tagset.unknown_id;
	}
	
/*
	void init(int k_val,FeatureBuilder fb, boolean no_spec, double wc, int unkid){
		 unknown_id=unkid;
		init(k_val, fb,  no_spec,  wc);
	}
*/
	
	@Override
	protected void update_boundary_feats(int bU, int bT,  int eU,  int eT ) {
		  if( no_special_end_transition_sym ) {
			    if (bT != unknown_id) {
			        phi_b[0][bU].upd_val -= 1;
			        phi_b[0][bT].upd_val += 1;
			    }
			    if (eT != unknown_id) {
			        phi_b[eU][0].upd_val -= 1;
			        phi_b[eT][0].upd_val += 1;
			    }
			  } else {
			    if (bT != unknown_id) {
			      phi_s.get(bU).upd_val -= 1;
			      phi_s.get(bT).upd_val += 1;
			    }
			    if (eT != unknown_id) {
			      phi_f.get(eU).upd_val -= 1;
			      phi_f.get(eT).upd_val += 1;
			    }
			  }

		  
		}
	
	
	
	 
	/**
	 *  AJC changed: if j is the state corresponding to the unknown ID, set
	 * values such that viterbi is extremely unlikely to predict the unknown
	 * state as the hidden label.
	 **/
	@Override
 protected void  viterbi_tj( double[][] delta, int[] x, int t, int j,int[][]psi ) {
	  if ((j == unknown_id) && (t == 0)) {
	    psi[t][j] = -1 ; 
	    delta[t][j] = Double.NEGATIVE_INFINITY;
	    return;
	  }
	  if ((j == unknown_id) && (t > 0)) {
	    psi[t][j] = 0 ; 
	    delta[t][j] = Double.NEGATIVE_INFINITY;
	    return;
	  }
	  super.viterbi_tj(delta, x, t, j,psi);
	}

	
    @Override
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
		//TRACE dlog.write("Size: N "+N+" k "+k+"\n");

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

		Double max_t = Double.NEGATIVE_INFINITY;
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

	
	 boolean update( int[][]W, int[]T,  int[] U ){
		  int N = U.length;
		  boolean s_update = false;
		  update_boundary_feats( U[0], T[0], U[N-1], T[N-1] );
		  
		// AJC CHANGE: if ignore errors when the true label is unknown.
		  if ( U[0] != T[0] && (T[0] != unknown_id) )  {
		      s_update = true;
		      cur_pos = 0;
		      update_word_feats( W[0], T[0], U[0] );
		  }
		  for ( int i = 1; i < N; ++i )
			  // AJC CHANGE: if ignore errors when the true label is unknown
		    if ( U[i] != T[i] && (T[i] != unknown_id)) {
		      s_update = true;
		      cur_pos = i;
		      update_word_feats( W[i], T[i], U[i] );
		      // AJC CHANGE: if added to only update transition features if the previous label is known
		      if (T[i-1] != unknown_id) 
		    	  update_transition_feats( T[i-1], T[i], U[i-1], U[i] );
		    }
		  if ( s_update ){
			// AJC CHANGE: don't need to update when the true label is unknown.  
		    if( U[0] != T[0] && (T[0] != unknown_id)) 
		       finalize_update_word_feats( W[0], T[0], U[0], s_update );
		    for ( int i = 1; i < N; ++i ) 
		         // AJC CHANGE: don't need to update when the true label is unknown.
		          if( U[i] != T[i] && (T[i] != unknown_id))
		        	  	finalize_update_word_feats( W[i], T[i], U[i],s_update);
		    finalize_transition_feats( s_update ); 
		  }
		  return s_update;
		}

}
