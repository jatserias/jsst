package tagger.examples;

import java.io.BufferedReader;

import tagger.data.SstTagSet;
import tagger.data.SstTagSetBIO;
import tagger.data.SstTagSetPOS;
import tagger.data.TrainingFormatReader;
import tagger.data.TrainingFormatReaderSSTfeatures;
import tagger.data.TrainingInstance;
import tagger.features.FeatureBuilderBasic;
import tagger.features.FeatureHashEncoderDecoder;
import tagger.utils.FileDescription;
import tagger.utils.Utils;

public class TestEncoder {

	public static void main(String args[]) throws Exception {
		 boolean bio=true;
		 String encoding="UTF-8";
		 String tagsetfile="/Users/jordi/Desktop/NEGerman/tagsetNEG1.txt";
		 String dname="/Users/jordi/Desktop/NEGerman/NER-de-train.tsv.feat.sst";
		 boolean useUNK=false;
		 
		 FeatureBuilderBasic fb = new  FeatureBuilderBasic();
		  
		  SstTagSet tagset = bio ?  new SstTagSetBIO(new FileDescription(tagsetfile,encoding),useUNK): new SstTagSetPOS(new FileDescription(tagsetfile,encoding),useUNK);
		  //FeatureEncoderDecoder
		  FeatureHashEncoderDecoder fe = new FeatureHashEncoderDecoder();
		  
		  BufferedReader fin =		Utils.getBufferedReader(dname,encoding);

			String buff;
			TrainingFormatReader freader= new TrainingFormatReaderSSTfeatures(tagset, fb, fe);
			int i=0;
			while((buff=fin.readLine())!=null && i<100) {			
//				System.err.println("read line "+buff);
				//processLine( buff,  D, G, ID, secondorder, updateFeatureBuilder);
				TrainingInstance te = freader.readExample( buff);
				++i;
			}
			fe.dump();
	}
}
