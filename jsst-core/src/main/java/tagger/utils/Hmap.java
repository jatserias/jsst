package tagger.utils;
import java.io.BufferedReader;
import java.io.StringWriter;

import java.io.IOException;

import java.io.Writer;
import java.util.HashMap;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * @author jordi
 *
 * A class to deal with the LABEL to ID and ID to LABEL conversions
 * it is used for both Labels and features 
 * @TODO split the code for Labels and features (load functions) in two classes?
 * 
 * decoding features depend on the model
 * but calculating the string features does not !!!
 */
public class Hmap {
	
	static Logger logger = Logger.getLogger(Hmap.class);

	
	///modes 
	public static final int modeList=0;
	public static final int modeFeatures=1;
	
	/// LABEL (String) to ID (int) map 
	public HashMap<String,Integer> h;
	
	/// ID (int) to LABLE (String) map
	private Vector<String> V_STRINGS;
	
	
	public int size() { return V_STRINGS.size();}
	
	public String  getLabel(int pos) { return V_STRINGS.get(pos); }
	
	///??
	void loadLabels() {
		
	/**
	 LABELS.insert("ALL");
	  char strbuf[100];
	  while (in.peek() != -1){
	    string l = "";
	    in >> l;
	    in.getline(strbuf,100);
	    if (l != ""){
	      update_hmap(LSI,l,LIS);
	      if (l != "0" && mode != "POS")
		LABELS.insert(string(l,2,int(l.size())-2));
	    }
	  }
	  **/
	}
	
	/**
	 * @JAB BFS 
	void loadFISI(String filename, String encoding) throws IOException {	
			 loadFISI(Utils.getBufferedReader(filename, encoding));
   }
	
	void loadLISI(String filename, String encoding) throws IOException {
			loadLISI(Utils.getBufferedReader(filename, encoding));
	}
	**/
	void loadLISI(FileDescription f) throws IOException {
		loadLISI(f.open());
	}
	
	void loadFISI(FileDescription f) throws IOException {	
		 loadFISI(f.open());
}
	
	void loadLISI(BufferedReader fin) throws IOException {
	 	 
		  String input; int x_i=0;
		  while ((input=fin.readLine())!=null && input.length()>0) {
			  h.put(input, x_i);
		      V_STRINGS.add(input);
		      ++x_i;
		  }

		//  dump();
	}
	
	
	
	public void dump(Writer dlog) throws IOException {
		String keyset[]= new String[h.size()];
		for (Entry<String, Integer> k: h.entrySet())
			 keyset[k.getValue()]=k.getKey();
		for(int i=0;i<keyset.length;++i)
			dlog.write( keyset[i]+":"+i+ "\n");
		dlog.write("\n");

		for(int i=0;i<V_STRINGS.size();++i)
			dlog.write(i+" "+(V_STRINGS.get(i))+"\n");
		dlog.write("\n");
	}
	
	 void loadFISI(BufferedReader fin) throws IOException {
	 	 
		  logger.info("Loading HMAP in mode Features");
		  String input;
		  while ((input=fin.readLine())!=null && input.length()>0) {
			String buff[] =  input.split("[\t]");
		    int x_i = Integer.parseInt(buff[0]);
		    String x = buff[1];
		     Integer k =h.get(x);
		      if(k!=null) {
		    	  if(k.intValue()!=x_i) logger.error("Internal Error registering "+x);
		    	  throw new RuntimeException("HMAP inconsistence: Registering >"+x+"< returns "+k.intValue()+" but model expected "+x_i);
		      }
		      h.put(x, x_i);
		      V_STRINGS.insertElementAt(x, x_i);
		  }
	 }
	 
	/// Constructor
	 /*@JAB BFS
	public Hmap(int mode, String filename, String encoding) throws Exception{
		h =new HashMap<String,Integer>();
		V_STRINGS = new Vector<String>();
		loadLabels();
		switch(mode) {
		case modeList: loadLISI(filename,encoding); 
			break;
		case modeFeatures: loadFISI(filename,encoding);
			break;
		}
	}
	*/
	 
	public Hmap(int mode, BufferedReader buff) throws Exception{
		h =new HashMap<String,Integer>();
		V_STRINGS = new Vector<String>();
		loadLabels();
		switch(mode) {
		case modeList: loadLISI(buff); 
			break;
		case modeFeatures: loadFISI(buff);
			break;
		}
	}
	public Hmap() {
		h =new HashMap<String,Integer>();
		V_STRINGS = new Vector<String>();
	} 
	
	/// hash witch keeps counting the occurrences of strings w
	 void update_hmap_count(String w){
		  Integer n= (Integer) h.get(w);
		  if(n==null) 
		    h.put(w,1);
		  else
			h.put(w,n+1); 
		}

	 /// gets the id, the hash that keeps gives elements a different ids (serial) returns the id  
	 public Integer DEPRECATEDupdate_hmap(String w){
		  int ans = h.size();
		  Integer n= (Integer) h.get(w);
		  if (n==null)
		    h.put(w,ans);
		  else
		    ans = n;
		  return ans;
		  //if(n==null) logger.warn("Feature:"+w+"not found");
		  //return n;
		}

	 /** 
	  * gets the id, the hash that keeps gives elements a different ids (serial) returns the id. 
	  *  also adds the key to the vector iif the key is found (??)
	  ***/
		public int  add_update_hmap(String w){
		  int ans = h.size();
		  Integer n= (Integer) h.get(w);
		  if (n==null) {
		    h.put(w,ans);
		    V_STRINGS.add(w);
		  }
		  else
		    ans = n;
		  return ans;
		}

	
		/**
		 * @TODO what to return if updateFeature is set to False and teh feature does not exists?f
		 * 
		 * @param w
		 * @param update
		 * @return
		 * @throws Exception 
		 */
		public int  update_hmap(String w, boolean update) throws Exception{
			  int ans = h.size();
			  Integer n= (Integer) h.get(w);
			  if (n==null) {
				if(!update) {
					logger.warn("hmap consult try to updated a new value >"+w+"< (labels)");
					StringWriter sw = new StringWriter();
					dump(sw);
					logger.warn(sw);
					throw new Exception("hmap consult try to updated a new value >"+w+"< (labels)");  
				}
			    h.put(w,ans);
			    V_STRINGS.add(w);
			  }
			  else
			    ans = n;
			  return ans;
			}

		public int getLabelId(String label) {
			return V_STRINGS.indexOf(label);
		}
		
		public Vector<String> getListLabel() {
			return V_STRINGS;
		}

}
