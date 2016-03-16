package tagger.data;



import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.apache.log4j.Logger;

import tagger.features.FeatureDecoder;

/**
 * representation of a training example with encoded features (Features already calculated)
 * @author jordi
 *
 */
public class TrainingInstance  {
	
	static Logger logger = Logger.getLogger(TrainingInstance.class);
	
	
	final static double defaultWeigth=1.0;
	
	/// Features per Word
	public int[][] W; 
	
	/// Target Feature per Word
	public int[] T; 
	
	///example id
	public String id;
	
	
		/// All examples have the same weight
	public double getWeight() {return defaultWeigth;}
	

	public int length() { return W.length;}
	
 public  TrainingInstance(String id,int[][] W, int[] T) {
	 this.W=W;
	 this.T=T;
	 this.id=id;
 }
 
 public void dump(Writer wr) throws IOException {
   wr.append("ID="+id+"\n");
   
   wr.append("W ("+(W.length)+"):");
   for(int i=0;i<W.length;++i) {
	   wr.append("("+W[i].length+")");
	   for(int j=0;j<W[i].length;++j) {
		   wr.append(" "+W[i][j]);
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
		   wr.append("("+W[i].length+")");
		   for(int j=0;j<W[i].length;++j) {
			   wr.append(" "+W[i][j]+" "+fb.decode(W[i][j]));//FSIS(W[i][j]));
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
 public  TrainingInstance(String id, List<List<Integer>> wordFeatures, List<Integer> labelperWord) {
	 this.W= new int[wordFeatures.size()][];
	 this.T= new int[labelperWord.size()];
	 int i=0;
	 for(List<Integer> vi:wordFeatures) {
		 int j=0;
		 W[i]=new int[vi.size()];
		 for(Integer v: vi) {
			 W[i][j]=v;
			 ++j;
		 }
		 
		 ++i;
	 }
	 
	 i=0;
	 for(Integer vi:labelperWord) {
		T[i]=vi;
		 ++i;
	 }
	  
	 this.id=id;
 }
 
 static public boolean compare(int[] a, int[] b) {
	if(a.length!=b.length) { logger.info("Different length");return false;}
	int i=0;
	while(i<a.length && a[i]==b[i]) {++i;}
	if(i<a.length) logger.info("differ at pos "+(i-1)+" "+a[i]+" vs "+b[i]);
	return (i>=a.length); 
 }
 
 static public boolean compare(int[][] a, int[][] b) {
		if(a.length!=b.length) { logger.info("length differs");return false;}
		int i=0;
		while(i<a.length && compare(a[i],b[i])) {++i;}
		if(i<a.length) logger.info("differ at pos "+(i-1)+" "+a[i]+" vs "+b[i]);
		return (i>=a.length); 
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
		boolean res=true;
		if(t.id.compareTo(t.id)!=0) {
			res=false;
			logger.info("IDs differ "+t.id+" vs "+id);
		}
		if(!compare(T,t.T)) {
			res=false;
			logger.info(t.id+" T differs");
		}
		
		if(! compare(W,t.W)) {
			res=false;
			logger.info(t.id+" W differs");
		}
		return res;
	}



}
