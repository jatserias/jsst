package tagger.data.readers;

import java.awt.image.CropImageFilter;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AncoraHandler extends DefaultHandler {
		public static final String S = "sentence";

		/// length of th epos albel (to make pos attribute / label simple )
		private static final int MAX_POS_LENGTH = 10;
	
		PrintStream fpos ;
		public PrintStream fne ;
		
		boolean sen;
		Vector<String> P;
		Vector<String> W;
		Vector<String> L;
		Vector<String> M;

		int nex=1;

		AncoraHandler() throws FileNotFoundException, UnsupportedEncodingException {
			 fpos = new PrintStream("fpos.sst","UTF-8");
			 fne = new PrintStream("fner.sst","UTF-8");			
			 sen = false;
			 P = new Vector<String>();
			 W= new Vector<String>();
			 L= new Vector<String>();
			 M= new Vector<String>();
		}
		
		
		public void startElement(String uri, String localName,String qName, 
				Attributes atts) throws SAXException {



			if (qName.equals(S)) {
				sen = true;
				P.clear();
				W.clear();
				L.clear();
				M.clear();
			}

			int length = atts.getLength();
			String pos=null;
			String w=null;
			String lem=null;
			String ne=null;

			for (int i=0; i<length; i++) {
				// Get names and values for each attribute
				String name = atts.getQName(i);
				String value = atts.getValue(i);



				// The uri of the attribute's namespace
				String nsUri = atts.getURI(i);

				// This is the name without the prefix
				String lName = atts.getLocalName(i);

				if(lName.compareTo("pos")==0) {
					pos=value;
				}

				if(lName.compareTo("lem")==0) {
					lem=value;
				}

				if(lName.compareTo("wd")==0) {
					w=value;
				}

				if(lName.compareTo("ne")==0) {
					ne=value;
				}

			}

			if(pos!=null && w!=null) {
				P.add(pos);
				W.add(w);
				if(lem!=null) M.add(lem);
				if(ne==null)  L.add("0");  else L.add(ne);
			}

		}

		public void endElement(String uri, String localName,
				String qName) throws SAXException {
			if (qName.equals(S)) {
				sen = false;
				if(P.size()>0) {
					fpos.print(nex);
					fne.print(nex);
					for(int i=0;i<P.size();++i) {
						int nw=0;
						for(String mw: W.get(i).split("[_]")) {
							fpos.print("\t"+mw+" "+P.get(i).substring(0,Math.min(MAX_POS_LENGTH,P.get(i).length())));
							fne.print("\t"+mw+" "+P.get(i).substring(0,Math.min(MAX_POS_LENGTH,P.get(i).length()))+" "+mw);//+M.get(i)); 
							fne.print(" "+(L.get(i).equals("0") ? "0" : ( (nw==0 ? "B-" : "I-") + CorpusTransformerAncora.catmap.get(L.get(i)))));
							nw++;
						}
					}
					fpos.println();
					fne.println();



					nex++;	

				}
			}


		}

		public void characters(char ch[], int start, int length) throws SAXException {



		}

	};
