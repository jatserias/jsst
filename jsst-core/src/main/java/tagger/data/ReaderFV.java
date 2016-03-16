package tagger.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import tagger.features.FeatureVector;
import tagger.utils.Utils;

/**
 * 
 * A reader for Feature Files
 * @author jordi
 *
 */
public class ReaderFV {
BufferedReader fin;
String buff=null;

   public ReaderFV(String filename, String encoding) throws IOException {
	   open(filename,encoding);
   }
	void open(String inputFile,String encoding) throws IOException {
		     if(inputFile.compareTo("stdin")==0)	fin= new BufferedReader(new InputStreamReader(System.in));
		     else fin = Utils.getBufferedReader(inputFile,encoding);
	} 
	
	public boolean hasNext() {
		if(buff==null)
			try {
				buff=fin.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return (buff!=null);
	}
	
	public FV next(){
		 //@JAB implementation
		 
		 String id=null;
		 FeatureVector[] feats=null; 
		 
		 try {    
			 
		       
			 if(buff==null) buff=fin.readLine();
			 
			 if(buff!= null) {
					
					 String[] words= buff.split("\t");
					 feats=  new FeatureVector[words.length-1];
					 id=words[0];
					 //first is the id
				   for(int j=1;j<words.length;++j){
					   //List<String>vword = new Vector<String>();
					   String[] feas = words[j].split(" ");
					   //last feature is the actual tag
					   for(int i=0;i<feas.length-1;++i) {
						  // System.err.println("added feat:"+feas[i]);
					    feats[j-1].add(feas[i]);
				      }
					  //feats.add(vword);
				   }
				   
				}
			 
			 }
		 catch(Exception e) {
			 e.printStackTrace();
		 }
		 buff=null;
		 return new FV(id,feats);
	}
	
	void close() { try {
		fin.close();
	} catch (IOException e) {
		e.printStackTrace();
	}}
		
}

