package tagger.data;
import java.io.BufferedReader;
import java.io.IOException;
/**
 * 
 * a naif interface to decouple reading input format 
 * 
 * @author jordi
 *
 */
public interface TrainingFormatReader {
   
	public TrainingInstance readExample(BufferedReader buff) throws IOException;
	public TrainingInstance readExample(String buff) throws IOException;
	//public TrainingExample next() throws IOException;
}
