package tagger.data;

import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import tagger.core.ListInteger;

public class TrainingDataSSTSparse implements TrainingData {

	static Logger logger = Logger.getLogger(TrainingDataSST.class);

	  /// Corpus name
	  public String name;
	  
	  
	  /// D ? array of sentences x words x features
	  private ListInteger[][] D;
	  
	  /// G  ? array of sentences x words tags (i.e. labels)
	  private int[][] G;  
	  
	  /// Examples sentences ids
	  private  String[] ID;
	  
	  /// to keep track of adding examples
	  private int lastEx=0;
	  
	  public TrainingDataSSTSparse(int size, String name) {
		  D = new ListInteger[size][];
		  G = new int[size][];
		  ID = new String[size];
		  this.name=name;
	  }
	  
	 
	  public TrainingDataSSTSparse(
			    List<List<List<Integer> > > ND,
			     List<List<Integer> > NG,
			     List<String> NID) { 
		   		ID = new String[NID.size()];
			 	int vjj=0;
					 for(String v : NID) {
						ID[vjj++]=v;
					 }	
					 constructor(ND,NG);	 
	  }
	  
	  public TrainingDataSSTSparse(List<List<List<Integer> > > ND,
			     List<List<Integer> > NG){
		  constructor(ND,NG);
	  }
	  
	  private void constructor(List<List<List<Integer> > > ND,
			     List<List<Integer> > NG) {
	 	// lets transform D and G
	 	D = new ListInteger[ND.size()][];
	 	 int ji=0;
	 	 for(List<List<Integer>> e : ND) {
	 		 int j=0;
	 		ListInteger[] x = new ListInteger[e.size()];
	 		 for(List<Integer> v : e) {
	 			 int k=0;
	 			ListInteger anArray = new ListInteger(v.size());
	 			int p=0;
	 			 for( Integer h: v ) {
	 				 anArray.set(p,h.intValue());++p;
	 			 }
	 			 x[j++]=anArray;
	 		 }
	 		D[ji++]=x;
	 	 }
	 	 
	 	G = new int [NG.size()][];
	 	int jj=0;
			 for(List<Integer> v : NG) {
				 int k=0;
				 int [] anArray = new int [v.size()];
				 for( Integer h: v ) {
					 anArray[k++]=h.intValue();
				 }
				 G[jj++]=anArray;
			 }
			 
			
	  }
	  
	  public TrainingData[] splitTrain(int propor) {
		 
		  
		  int totalSize = ID.length; 
		  int sizeTrain = 0 ;
		  int sizeDev=0;
		  int[] dec= new int[totalSize]; 
		  Random rnd= new Random();
		  for(int i=0;i<D.length;++i) {
			  if(rnd.nextInt(99)>propor) { dec[i]=0; ++sizeTrain;}
			  else {  dec[i]=1; ++sizeDev;}
		  }
		  
		  
		  TrainingDataSSTSparse[] res =  new TrainingDataSSTSparse[2];
		  res[0] = new TrainingDataSSTSparse(sizeTrain,name+"_train");
		  res[1] = new TrainingDataSSTSparse(sizeDev,name+"_dev");
			  
		  int cur=0;
		  int ncur[]={0,0};
		  
		  for(int i=0;i<D.length;++i) {
			  if(dec[i]==0) { cur=0; }
			  else {  cur=1; }
		     res[cur].D[ncur[cur]]=D[i];
		     res[cur].ID[ncur[cur]]=ID[i];
		     res[cur].G[ncur[cur]]=G[i];
		     ncur[cur]++;
		  }  
		  
		  logger.info("training:"+ncur[0]);
		  logger.info("dev:"+ncur[1]);
		  
		  return res;
	  }

	
	public TrainingInstance getTrainingExample(int i) {
		return null;
	}

	
	
	public void addTrainingExample(TrainingExampleSparse te) {			
		ID[lastEx]=te.id;D[lastEx]=te.W;G[lastEx]=te.T;
		lastEx++;
	}
	
	
	public String getName() {
		return name;
	}

	
	public void setName(String dname) {
		name=dname;
		
	}

	
	public int length() {
		return D.length;
	}
	

	@Override
	public TrainingExampleSparse getTrainingExampleSparse(int i) {
		return new  TrainingExampleSparse(ID[i],D[i],G[i]);
	}

}


