package tagger.learning;

import java.util.Vector;

import tagger.data.SstTagSet;
import tagger.data.TrainingData;


/**
 * extend ET_BIO with UNK labels
 * probably is not a good dessign, the BIO and UNK sould be two classes
 * @author jordiatserias
 *
 */
public class ET_BIO_UNK extends ET_BIO {

	/*
	 * treat UNK label
	 */
	int unknownId;
	public ET_BIO_UNK(int unknownid) {this.unknownId=unknownid;}
	public double evaluate_sequences(TrainingData goldS, int[][] predicted, SstTagSet tagset ,int num_class,Vector<Verbose_res> VR) throws Exception{
		int numtag,numctag,numbtag;
		numtag=numctag=numbtag=0;
		
		
		//@JAB  There is a UNK we pass the number of classes
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
				int clabel = goldS.getTrainingExample(i).T[n];
				int cclass = class_type( clabel ).ordinal();
				braquets cbraket = braket_type( n, goldS.getTrainingExample(i).T );
				if ( cbraket == braquets.SBrac || cbraket == braquets.BBrac ) {
					ccount[cclass]++;
				}

				int plabel = predicted[i][n];
				int pclass = class_type( plabel ).ordinal();
				braquets pbraket = braket_type( n, predicted[i] );
				if ( pbraket == braquets.SBrac || pbraket == braquets.BBrac ) {
					pcount[pclass]++;
				}

				if ( cbraket == pbraket ) numbtag++;
				if ( clabel == plabel && cbraket == pbraket ) numctag++;
				if ( pbraket == braquets.SBrac  &&  cbraket == braquets.SBrac ) {
					nbcorrect[pclass]++; nbleft[pclass]++; nbright[pclass]++;
					if( pclass == cclass ) { 
						ncorrect[pclass]++; nleft[pclass]++; nright[pclass]++; 
					}
				}
				else if ( pbraket == braquets.SBrac  &&  cbraket == braquets.EBrac ) { 
					nbright[pclass]++;
					if( pclass == cclass ) { nright[pclass]++; }
				}
				else if ( pbraket == braquets.SBrac  &&  cbraket == braquets.BBrac ) { 
					nbleft[pclass]++;
					if( pclass == cclass ) { nleft[pclass]++; }
				}
				else if ( pbraket == braquets.BBrac  &&  cbraket == braquets.SBrac ) { 
					nbleft[pclass]++;
					if( pclass == cclass ) { nleft[pclass]++; }
				}
				else if ( pbraket == braquets.BBrac  &&  cbraket == braquets.BBrac ) { 
					nbleft[pclass]++;   bmatch=true;
					if( pclass == cclass ) { nleft[pclass]++; match=true;}
				}
				if ( pbraket != cbraket || pbraket == braquets.none )  bmatch = false;
				if ( cclass != pclass || !bmatch )            match = false;

				if ( pbraket == braquets.EBrac && cbraket == braquets.SBrac )  {
					nbright[pclass]++;
					if( pclass == cclass ) { nright[pclass]++; }
				}
				if ( pbraket == braquets.EBrac && cbraket == braquets.EBrac ) {
					if( bmatch )  nbcorrect[pclass]++;
					nbright[pclass]++;
					if( pclass == cclass ) {
						if ( match && ncorrect[pclass] <= pcount[pclass]) 
							ncorrect[pclass]++;
						nright[pclass]++;	
					}
				}
			}
		}



		int numans = getsum(pcount), numref = getsum(ccount);
		Verbose_res vr = new Verbose_res("ALL",numref,numans);
		if ( numref==0 ) logger.error( "\n\nNo names in reference!");
		if ( numans==0 ) logger.error( "\n\nNo names in answers!");
		double ret_val = compute_score(getsum(ncorrect),numans,numref,"FULLY CORRECT answer",vr);
		VR.add(vr);

		if (num_class > 2)
			  // AJC CHANGE: ignore the last class-- it's the unknown one. (??)
			for( int cl = 1 ; cl < pcount.length -1 ; ++cl ) {
				String cl_str = tagset.LSIS(reverse_class_type(cl));
				Verbose_res vr_cl = new Verbose_res(cl_str.substring(2),ccount[cl],pcount[cl]);
				double score = compute_score(ncorrect[cl],pcount[cl],ccount[cl],"FULLY CORRECT answer",vr_cl);
				VR.add(vr_cl);
			}
		if (ret_val>1.0) ret_val=1.0;
		return ret_val;
	}

}
