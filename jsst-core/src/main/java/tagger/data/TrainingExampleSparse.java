package tagger.data;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.apache.log4j.Logger;

import tagger.core.ListInteger;
import tagger.features.FeatureDecoder;

public class TrainingExampleSparse {

static Logger logger = Logger.getLogger(TrainingInstance.class);
	
	
	final static double defaultWeigth=1.0;
	
	/// Features per Word
	public ListInteger[] W; 
	
	/// Target Feature per Word
	public int[] T; 
	
	///example id
	public String id;
	
	
		/// All examples have the same weight
	public double getWeight() {return defaultWeigth;}
	

	public int length() { return W.length;}

	public  TrainingExampleSparse(String id,ListInteger[] W, int[] T) {
		 //Straightforward ...
		 this.W=W;
		 this.T=T;
		 this.id=id;
	 }
	
// public  TrainingExampleSparse(String id,int[][] W, int[] T) {
//	 //Straightforward ...
//	 this.W=new ListInteger[W.length];
//	 for(int i=0;i<W.length;++i) {
//		 this.W[i]=(new ListInteger(W[i]));
//	 }
//	 this.T=T;
//	 this.id=id;
// }
 
 public void dump(Writer wr) throws IOException {
	 wr.append("ID="+id+"\n");
	   
	   wr.append("W ("+(W.length)+"):");
	   for(int i=0;i<W.length;++i) {
		   wr.append("("+W[i].size()+")");
		   for(int j=0;j<W[i].size();++j) {
			   wr.append(" "+W[i].get(j));
		   }
		   wr.append("\n");
	   }

	   wr.append("T ("+T.length+"):");
	   for(int i=0;i<T.length;++i)
		   wr.append(" "+T[i]);
	   wr.append("\n");
 }
 
 public void dump(Writer wr, FeatureDecoder fb) throws IOException {
	 wr.append("ID="+id+"\n");
	   
	 wr.append("W ("+(W.length)+"):");
	   for(int i=0;i<W.length;++i) {
		   wr.append("("+W[i].size()+")");
		   for(int j=0;j<W[i].size();++j) {
			   wr.append(" "+W[i].get(j)+" "+fb.decode(W[i].get(j)));//FSIS(W[i][j]));
		   }
		   wr.append("\n");
	   }

	   wr.append("T ("+T.length+"):");
	   for(int i=0;i<T.length;++i)
		   wr.append(" "+T[i]);
	   wr.append("\n");
	 }
 
 /**
  * 
  * TODO optimize the copy!
  * 
  * @param id
  * @param wordFeatures
  * @param labelperWord
  */
 public  TrainingExampleSparse(String id, List<List<Integer>> wordFeatures, List<Integer> labelperWord) {
	 //TODO
	 this.id=id;
 }
 
 
 
 public String toString() {
	 StringWriter res = new StringWriter();
	 try {
		dump(res);
	} catch (IOException e) {
		e.printStackTrace();
	}
	 return res.toString();
 }
 public boolean compare(TrainingInstance t){
	 
	 //TODO
		return true;
	}
}

