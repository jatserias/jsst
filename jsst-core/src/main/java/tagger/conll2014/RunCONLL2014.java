package tagger.conll2014;

import tagger.data.readers.CorpusTransformerCONLL2014;

public class RunCONLL2014 {

	private static final String ENCODING = "UTF-8";

	/**
	 * First you need to run jab.tagCONLL2014.main(String[]) to add lemma and pos info
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		
		String BASE_PATH = "/Users/jordi/Desktop/NEGerman/";
		String VERSION_FOLDER = "v1/";
		String tagset = BASE_PATH+"tagsetNEG1.txt";
		String model_prefix = "conll2014delp";
		String model_prefix_L1 = model_prefix+"L1";
		String model_prefix_L2 = model_prefix+"L2";
		
		
		String [] margs = {
			      BASE_PATH+VERSION_FOLDER+"NER-de-train.tsv.lp.txt", 
			      BASE_PATH+VERSION_FOLDER+"NER-de-dev.tsv.lp.txt", 			     
		 };
		
		
		//tagger.data.readers.CorpusTransformerCONLL2014lemmapos.main(margs);
		
		
		
		String pargs[] = {
				 tagset, 
				 BASE_PATH+VERSION_FOLDER+"NER-de-train.tsv.lp.txt.sst",ENCODING,
				 BASE_PATH+VERSION_FOLDER+"NER-de-train.tsv.feat.sst",ENCODING,			 
				 //with GAZ
				  BASE_PATH+"gaz.txt",
				 "-l", "-p", "-u"
		};
		
	//	tagger.examples.CalculateFeaturesToFile.main(pargs);
		String targs[] = { tagset,model_prefix_L1,BASE_PATH+VERSION_FOLDER+"NER-de-train.tsv.feat.sst", "-e","-b"};
		//tagger.examples.TrainModelWithSST.main(targs);
		
	 /**
	  * L2
	  * check Label tagset
	  */
		String p2args[] = {
				 tagset, 
				 BASE_PATH+VERSION_FOLDER+"NER-de-train.tsv.lp.txt.sst",ENCODING,
				 BASE_PATH+VERSION_FOLDER+"NER-de-train.tsv.featL2.sst",ENCODING,			 
				 //with GAZ
				 BASE_PATH+"gaz.txt",
				 "-l", "-p", "-u", "-s"
		};
		
		//tagger.examples.CalculateFeaturesToFile.main(p2args);
		
		
		String t2args[] = { tagset,model_prefix_L2,BASE_PATH+VERSION_FOLDER+"NER-de-train.tsv.featL2.sst","-b","-e"};
		//tagger.examples.TrainModelWithSST.main(t2args);
		
	
		String testargs[] ={BASE_PATH+VERSION_FOLDER+"NER-de-dev.tsv.lp.txt",BASE_PATH+"NER-de-dev.out",model_prefix,tagset,
				BASE_PATH+"gaz.txt",
				"-l","-p","-u"};
	tagger.conll2014.Conll2014Test.main(testargs);
		
		
	}
}
