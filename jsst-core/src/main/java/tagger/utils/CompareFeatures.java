package tagger.utils;

import org.apache.log4j.Logger;

import tagger.data.FV;
import tagger.data.ReaderFV;

public class CompareFeatures {
	static Logger logger = Logger.getLogger(CompareFeatures.class);
	public static void main(String args[]) throws Exception {
		ReaderFV fa =new ReaderFV("/home/jordi/CONLL03.BI_dev.cpp.feat","UTF-8");
		ReaderFV fb =new ReaderFV("/home/jordi/benchmarks/CONLL03.BI_dev.feat","UTF-8");
		int l=0;
     while(fa.hasNext() && fb.hasNext()){
    	 FV rfa = fa.next(); FV rfb= fb.next();
		if(FV.compare(rfa,rfb,"cpp "+l,"java "+l)){
		 //rfa.dump();
		 //rfb.dump();
		}	
		if(l++ % 10 == 0) logger.info(".");
     }
	}
}
