package tagger.data.readers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * read file formated as  ancora-2.0 xml tbf
 * and generates  SST corpora for NERC (fner.sst) and POS (fpos.sst)
 * 
 * every tag with a pos attribute is a word
 * 
 * sentence <S>
 * 
 * multiwords issue:
 * 
 * Named entities 
 * locuciones
 * dates
 * 
 *  break then and use the same pos (?) 
 * 
 * @author jordi
 *
 */
public class CorpusTransformerAncora {

	
	public static final String S = "sentence";
	protected static final int MAX_POS_LENGTH = 10;
    public static HashMap<String,String> catmap = new HashMap<String,String>();
    public static String ancoraPath = "/opt/data/corpus/es/ancora-2.0/";

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


    		DefaultHandler handler = new DefaultHandler() {

    			PrintStream fpos = new PrintStream("fpos.sst","UTF-8");
    			PrintStream fne = new PrintStream("fner.sst","UTF-8");
    			
    			boolean sen = false;



    			Vector<String> P = new Vector<String>();
    			Vector<String> W= new Vector<String>();
    			Vector<String> L= new Vector<String>();
    			Vector<String> M= new Vector<String>();

    			int nex=1;


    			
    			
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
    								fne.print(" "+(L.get(i).equals("0") ? "0" : ( (nw==0 ? "B-" : "I-") + catmap.get(L.get(i)))));
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

    		AncoraHandler aHandler = new AncoraHandler();
    		File[] folderlist = {new File(ancoraPath+"CESS-CAST-A/"),  new File(ancoraPath+"CESS-CAST-AA/"), new File(ancoraPath+"CESS-CAST-P/")};
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
		 
		
		
