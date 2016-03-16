package tagger.data;

import java.io.IOException;
 

/**
 * 
 * ongoing refactoring 
 * 
 * Training data representation
 * @author jordi
 * @TODO fix the issue vector-array
 */
public interface TrainingData {
	
	  public  TrainingInstance getTrainingExample(int i) throws Exception;
	  //public TrainingData[] splitTrain(int propor);
	  public String getName();
	  public void setName(String dname);
	  public int length();
	  public TrainingExampleSparse getTrainingExampleSparse(int i) throws IOException;
}
