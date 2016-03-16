package tagger.data.readers;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.xml.sax.SAXException;

public class AncoraConlHandler extends AncoraHandler{
    PrintStream out;
	AncoraConlHandler() throws FileNotFoundException,
			UnsupportedEncodingException {
		super();
		out= new PrintStream("ancora.conll","UTF-8");
	}
	
	@Override
	public void endElement(String uri, String localName,
			String qName) throws SAXException {
		if (qName.equals(S)) {
			sen = false;
			if(P.size()>0) {
				for(int i=0;i<P.size();++i) {
					int nw=0;
					for(String mw: W.get(i).split("[_]")) {
						out.print(mw);//+M.get(i)); 
						out.println(" "+(L.get(i).equals("0") ? "O" : ( (nw==0 ? "B-" : "I-") + CorpusTransformationAncora2CONLL.catmap.get(L.get(i)))));
						nw++;
					}
				}
				out.println();
				


				nex++;	

			}
		}
	}
}
