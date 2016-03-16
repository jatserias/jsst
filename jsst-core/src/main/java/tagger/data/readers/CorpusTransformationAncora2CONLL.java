package tagger.data.readers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import tagger.data.readers.CorpusTransformerAncora;

public class CorpusTransformationAncora2CONLL {

	public static HashMap<String,String> catmap = new HashMap<String,String>();
	
    public static void  main(String args[]) throws ParserConfigurationException, SAXException, IOException {
    	


    	catmap.put("organization","ORG");
    	catmap.put("person","PER");
    	catmap.put("location","LOC");
    	catmap.put("date","MISC"); //"DATE");
    	catmap.put("number","MISC"); // "NUMBER");
    	catmap.put("other","MISC"); //"NUMBER");
    	
    	try {
    	SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();

		AncoraConlHandler aHandler = new AncoraConlHandler();
		File[] folderlist = {new File(tagger.data.readers.CorpusTransformerAncora.ancoraPath+"CESS-CAST-A/"),  new File(tagger.data.readers.CorpusTransformerAncora.ancoraPath+"CESS-CAST-AA/"), new File(tagger.data.readers.CorpusTransformerAncora.ancoraPath+"CESS-CAST-P/")};
		for(File folder :  folderlist) {
			for(File f: folder.listFiles()) {	
				System.err.println("Processing ... "+f.getName());
				//aHandler.fne.println("%%#"+f.getName());
				if(f.getName().endsWith(".xml")) {
					try { saxParser.parse(f, aHandler); }
					catch(Exception e) { e.printStackTrace();}
				}
			}
		}

	} catch (Exception e) {
		e.printStackTrace();
	}
}
}
