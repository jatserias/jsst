package tagger.data;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.List;

import org.apache.log4j.Logger;

import tagger.core.SttTagger;
import tagger.features.FeatureBuilderBasic;
import tagger.features.FeatureEncoder;
import tagger.features.FeatureEncoderMurMurHash;
import tagger.features.FeatureHashEncoderDecoder;
import tagger.utils.ClassChooser;
import tagger.utils.FileDescription;

/**
 * 
 * @TODO only one line FORMAT currently supported
 * 
 * @author jordi
 *
 */
public class TrainingDataSSTonline implements TrainingData{
	static Logger logger = Logger.getLogger(TrainingDataSSTonline.class);
	List<Long> map;
	String name;
	RandomAccessFile rf;
	TrainingFormatReader formatReader;
	int length;
	
	public TrainingDataSSTonline(String filename, TrainingFormatReader formatReader) throws IOException {
		this.formatReader=formatReader;
		name=filename;
		// create an index
		map = ClassChooser.createListLong();//new Vector<Long>();
		RandomAccessFile reader=  new RandomAccessFile(new File(filename),"r"); 
		long offset=0;
		map.add(offset);
		String line;
		length=0;
		//@TODO call a reader.readExample() to support more than line formats
		while((line = reader.readLine())!=null) {
			//@TO BUILD SAME FEATURES THAN INMEM METHOD 
			TrainingInstance te = formatReader.readExample(line);
			offset=reader.getFilePointer();
			map.add(offset);
			++length;
		}
		reader.close();
		
		//@TODO something different delaing with encoding using RandomAccess or InputStreamFile
		rf = new RandomAccessFile(new File(filename),"r");
		
	}

	
	public TrainingDataSSTonline() {
		// TODO Auto-generated constructor stub
	}


	
	public String getName() {
		return name;
	}

	
	public TrainingInstance getTrainingExample(int i) throws IOException {
		rf.seek(map.get(i));
		String buff = rf.readLine();
		return formatReader.readExample(buff);
	}

	
	public int length() {
		return length;
	}


	
	public void setName(String dname) {
		name=dname;
	}

	
	public TrainingData[] splitTrain(int propor) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

	/**
	 * Snall test
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		String testfile = "/Users/jordi/benchmarks/enconlltest";//"../CrossNWikiNE/data/enwiki00.mtag"
	
		FeatureBuilderBasic fb = new FeatureBuilderBasic();
		SstTagSetBIO tagset = new SstTagSetBIO(new FileDescription("TAGSETS/CONLL03.TAGSET", "UTF-8",false));
		TrainingFormatReader formatReader= new TrainingFormatReaderSSTfeatures(tagset, fb, new FeatureHashEncoderDecoder());
		TrainingDataSSTonline tc = new TrainingDataSSTonline(testfile, formatReader);
		//tc.getTrainingExample(0);
		//tc.getTrainingExample(1);
		//tc.getTrainingExample(2);
		tc.getTrainingExample(3).dump(new OutputStreamWriter(System.err));
		
		boolean ergodic=true;
		
		FeatureEncoder fe = new FeatureEncoderMurMurHash(10000);
		
		FeatureBuilderBasic fbd = new FeatureBuilderBasic();
		SstTagSetBIO tagsetd = new SstTagSetBIO(new FileDescription("TAGSETS/CONLL03.TAGSET", "UTF-8",false));
		SttTagger TL = new SttTagger(fe,fbd, tagsetd, ergodic);
		TrainingData TD = TL.load_data(testfile,"UTF-8", false);
		TD.getTrainingExample(3).dump(new OutputStreamWriter(System.err));
		logger.info("Training data size online:"+tc.length());
		logger.info("Training data size memory:"+TD.length());
		
		for(int i=0;i<tc.length;++i) {
			
			if(! tc.getTrainingExample(i).compare(TD.getTrainingExample(i))) {
				logger.info("FAIL Ex "+i);
			}
		}
		
	}


	@Override
	public TrainingExampleSparse getTrainingExampleSparse(int i)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
