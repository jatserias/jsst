package tagger.learning;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;


import tagger.data.SstTagSet;
import tagger.data.TrainingData;
import tagger.data.TrainingInstance;

/**
 * 
 * @author jordi
 *
 */
public class ET_BIO implements ET {
	
	///
	enum braquets { none, SBrac, BBrac, EBrac,CBrac};
	
	static Logger logger = Logger.getLogger(ET_BIO.class);
	
	
	///
	List<Integer> label;
	///
	List<List<Double> > score;
	///
	int position;
	///
	double tot_score;
	
	
	public ET_BIO() { 
		label=new Vector<Integer>();
		score= ((List<List<Double>>) (List<?>) new Vector<Vector<Double>>());  
		position = 0;  
		tot_score = 0.0;
		}
	
	void empty_it() { position = 0; label.clear(); score.clear();}
	
	void update( int l, List<Double> s ) {
		label.add(l);
		score.add(s);
	}
	
	void update_score( double s ) { tot_score += s; }

	void add_nonnames( Vector<Double> s ) {
		score.add(s);
		label.add(0);
		tot_score += s.get(0);
	}

	void take_copy( ET_BIO orig , double tot_val ) {
		position = orig.position;
		int N = orig.label.size();
		//    label.resize(N);
		//score.resize(N);
		for( int i = 0; i < N ; ++i ) {
			label.set(i, orig.label.get(i));
			int dim = orig.score.get(i).size();
			// score.get(i).resize(dim);
			for( int j = 0; j < dim ; ++j ) 
				score.get(i).set(j,orig.score.get(i).get(j));
		}
		tot_score = tot_val;
	}

	/**
	 * @TODO JAB FIX STRONG ASSUMPTIONS!!!
	 * Assumes an encoding of following format
	 * nonname nametype1: 1:B1 2:I1 nametype2 3:B2 4:I2 ...
	 * braket types: 0 nonname  1:single entity	2:beginning	3:end	4:cont
	 */
	static braquets class_type(  int label ) { 
		if (label==0)  return braquets.none;  
		return  braquets.values()[( ( label - 1 ) / 2 ) + 1]; 
	}
	
	/**
	 * @TODO JAB FIX STRONG ASSUMPTIONS!!!
	 * 
	 */
	static   int reverse_class_type(  int label ) { 
		if (label==0)  return 0;  
		return  ( ( label - 1 ) * 2 ) + 1; 
	}

	static  braquets braket_type( int n, List<Integer> seq ) { 
		int label = seq.get(n);
		int N = seq.size();

		if ( label==0 ) return braquets.none;
		if ( label % 2 == 1 ) {
			// Beginning label
			if ( n + 1 == N ||
					n + 1 < N && seq.get(n+1) != label + 1 )  // single word entity
				return braquets.SBrac;
			return braquets.BBrac;
		} else {
			// Continuation label
			if( n + 1 == N ||  
					n + 1 < N && label != seq.get(n+1)  )	   // next label is different 
				return braquets.EBrac;
			return braquets.CBrac;
		}
	}


	static  braquets braket_type( int n, int[] seq ) { 
		int label = seq[n];
		int N = seq.length;

		if ( label==0 ) return braquets.none;
		if ( label % 2 == 1 ) {
			// Beginning label
			if ( n + 1 == N ||
					n + 1 < N && seq[n+1] != label + 1 )  // single word entity
				return braquets.SBrac;
			return braquets.BBrac;
		} else {
			// Continuation label
			if( n + 1 == N ||  
					n + 1 < N && label != seq[n+1]  )	   // next label is different 
				return braquets.EBrac;
			return braquets.CBrac;
		}
	}
	static  int getsum( List<Integer> list_val ) {
		int num = 0;
		for (  int i = 0 ; i < list_val.size(); ++i )
			num += list_val.get(i);
		return num;
	}

	static  int getsum( int[] list_val ) {
		int num = 0;
		for (  int i = 0 ; i < list_val.length; ++i )
			num += list_val[i];
		return num;
	}

	static  double compute_score( int numcrt, int numans, int numref , String info, boolean verbose) {
		double precision, recall, fscore;

		recall = ( numref == 0 ) ? 0 : (double)numcrt/numref;
		precision = ( numans == 0 ) ? 0 : (double)numcrt/numans;
		fscore = ( precision + recall == 0 ) ? 0 : 2 * precision * recall /
				( precision + recall );
		if ( verbose )
			logger.info( "    " + info + " with class info: "+ numcrt +" "+recall + " " + precision + " " +fscore);
		return fscore;
	}



	////////////////////////////////////
	static double compute_score(int numcrt,int numans,int numref,String info,Verbose_res vr) {
		Double precision, recall, fscore;
		recall = ( numref == 0 ) ? 0 : (double)numcrt/numref;
		precision = ( numans == 0 ) ? 0 : (double)numcrt/numans;
		fscore = ( precision + recall == 0 ) ? 0 : 2 * precision * recall /( precision + recall );
		vr.nfullycorrect = numcrt;
		vr.R = recall;
		vr.P = precision;
		vr.F = fscore;
		return fscore;
	}

	
	
	
	/**
	 * @param ga2
	 * @param tst_guess
	 * @param fb
	 * @param num_labels
	 * @param itemized
	 * @return
	 * @throws Exception
	 */
	public double evaluate_pos(//int[][] ga2, 
			TrainingData ga2,
			int[][] tst_guess,
			SstTagSet tagset, // Fb IS NOT USED (?)
	         int num_labels,
			List<Double> itemized) throws Exception{
		
		int num_seq = ga2.length();
		
		List<Integer> hits_pos = new Vector<Integer>();
		double hits = 0, den = 0;
		for (int r = 0; r < num_labels; ++r){
			itemized.add((double)0);
			hits_pos.add(0);
		}
		
		for (int i = 0; i < num_seq ; ++i){                                 // For each sequence
			TrainingInstance exga2 = ga2.getTrainingExample(i); 
			int N = exga2.length();
			if ( N != tst_guess[i].length ) {
				throw new Exception("Unmatching length of labels for sentence" + i + "\n"); 
			}
			for (int j = 0; j < N; ++j){
				if (exga2.T[j] == tst_guess[i][j]){
					hits += 1;
					hits_pos.set(exga2.T[j],hits_pos.get(exga2.T[j])+1); //hits_pos[goldS[i][j]] += 1;
				}
				itemized.set(exga2.T[j],itemized.get(exga2.T[j])+1);//itemized[goldS[i][j]] += 1;
				den += 1;
			}
		}
		
		for (int r = 0; r < num_labels; ++r)
			itemized.set(r, hits_pos.get(r)/itemized.get(r));
		return hits/den;
	}
	
/*
 * used function to evaluate sequences
 * @TODO write junit test
 */
	public double evaluate_sequences(TrainingData goldS, int[][] predicted, SstTagSet tagset, int num_class,List<Verbose_res> VR) throws Exception{
		  
	//(int[][] goldS, int[][] predicted,FeatureBuilder fb, int num_class,Vector<Verbose_res> VR) throws Exception{
			//int num_labels,Vector<Verbose_res> VR) throws Exception{
		int numtag,numctag,numbtag;
		numtag=numctag=numbtag=0;
		//int num_class =  ( num_labels / 2 ) + 1;

		int[] ccount = new int[num_class];
		int[] pcount = new int[num_class];
		int[] ncorrect = new int[num_class];
		int[] nleft = new int[num_class];
		int[] nright = new int[num_class];
		int[] nbcorrect = new int[num_class];
		int[] nbleft = new int[num_class];
		int[] nbright = new int[num_class];

		boolean match,bmatch;
		match=false;
		bmatch=false;

		int num_seq = goldS.length();

		for (int i = 0; i < num_seq ; ++i){                                 // For each sequence
			int N = goldS.getTrainingExample(i).length();
			if ( N != predicted[i].length ) {
				throw new Exception("Unmatching length of labels for sentence"+i); 
			}
			for ( int n = 0 ; n < N ; ++n, ++numtag )   {
				int  clabel = goldS.getTrainingExample(i).T[n];
				braquets cclass = class_type( clabel );
				braquets cbraket = braket_type( n,  goldS.getTrainingExample(i).T);
				if ( cbraket == braquets.SBrac || cbraket == braquets.BBrac ) {
					ccount[cclass.ordinal()]++;
				}

				int plabel = predicted[i][n];
				braquets pclass = class_type( plabel );
				braquets pbraket = braket_type( n, predicted[i] );
				if ( pbraket == braquets.SBrac || pbraket == braquets.BBrac ) {
					pcount[pclass.ordinal()]++;
				}

				if ( cbraket == pbraket ) ++numbtag;
				if ( clabel == plabel && cbraket == pbraket ) ++numctag;
				if ( pbraket == braquets.SBrac  &&  cbraket == braquets.SBrac ) {
					nbcorrect[pclass.ordinal()]++; nbleft[pclass.ordinal()]++; nbright[pclass.ordinal()]++;
					if( pclass == cclass ) { 
						ncorrect[pclass.ordinal()]++; nleft[pclass.ordinal()]++; nright[pclass.ordinal()]++; 
					}
				}
				else if ( pbraket == braquets.SBrac  &&  cbraket == braquets.EBrac ) { 
					nbright[pclass.ordinal()]++;
					if( pclass == cclass ) { nright[pclass.ordinal()]++; }
				}
				else if ( pbraket == braquets.SBrac  &&  cbraket == braquets.BBrac ) { 
					nbleft[pclass.ordinal()]++;
					if( pclass == cclass ) { nleft[pclass.ordinal()]++; }
				}
				else if ( pbraket == braquets.BBrac  &&  cbraket == braquets.SBrac ) { 
					nbleft[pclass.ordinal()]++;
					if( pclass == cclass ) { nleft[pclass.ordinal()]++; }
				}
				else if ( pbraket == braquets.BBrac  &&  cbraket == braquets.BBrac ) { 
					nbleft[pclass.ordinal()]++;   bmatch=true;
					if( pclass == cclass ) { nleft[pclass.ordinal()]++; match=true;}
				}
				if ( pbraket != cbraket || pbraket == braquets.none )  bmatch = false;
				if ( cclass != pclass || !bmatch )            match = false;

				if ( pbraket == braquets.EBrac && cbraket == braquets.SBrac )  {
					nbright[pclass.ordinal()]++;
					if( pclass == cclass ) { nright[pclass.ordinal()]++; }
				}
				if ( pbraket == braquets.EBrac && cbraket == braquets.EBrac ) {
					if( bmatch )  nbcorrect[pclass.ordinal()]++;
					nbright[pclass.ordinal()]++;
					if( pclass == cclass ) {
						if ( match && ncorrect[pclass.ordinal()] <= pcount[pclass.ordinal()]) 
							ncorrect[pclass.ordinal()]++;
						nright[pclass.ordinal()]++;	
					}
				}
			}
		}



		int numans = getsum(pcount), numref = getsum(ccount);
		Verbose_res vr = new Verbose_res("ALL",numref,numans);
		if ( numref==0 ) logger.warn( "\n\nNo names in reference!");
		if ( numans==0 ) logger.warn( "\n\nNo names in answers!");
		double ret_val = compute_score(getsum(ncorrect),numans,numref,"FULLY CORRECT answer",vr);
		VR.add(vr);

		if (num_class > 2)
			for( int cl = 1 ; cl < pcount.length ; ++cl ) {
				String cl_str = tagset.LSIS(reverse_class_type(cl));
				Verbose_res vr_cl = new Verbose_res(cl_str.substring(2),ccount[cl],pcount[cl]);
				double score = compute_score(ncorrect[cl],pcount[cl],ccount[cl],"FULLY CORRECT answer",vr_cl);
				VR.add(vr_cl);
			}
		if (ret_val>1.0) ret_val=1.0;
		return ret_val;
	}

	

}
