package tagger.learning;

import java.util.List;

import tagger.data.SstTagSet;
import tagger.data.TrainingData;




/**
 * Class for evaluation 
 * @author massi,jordi
 *
 * refactored 
 */
public interface ET {

	/**
	 * 
	 * @param cvTestData
	 * @param tst_guess
	 * @param tagset
	 * @param size
	 * @param itemized_res
	 * @return
	 * @throws Exception
	 */
	double evaluate_pos(TrainingData cvTestData, int[][] tst_guess,
			SstTagSet tagset, int size, List<Double> itemized_res)  throws Exception;

	/**
	 * 
	 * @param cvTestData
	 * @param tst_guess
	 * @param tagset
	 * @param size
	 * @param tst_vr
	 * @return
	 * @throws Exception
	 */
	double evaluate_sequences(TrainingData cvTestData, int[][] tst_guess,
			SstTagSet tagset, int size, List<Verbose_res> tst_vr)throws Exception;

	
	

	
}
