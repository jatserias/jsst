package tagger.features;



import it.unimi.dsi.bits.TransformationStrategies;
import it.unimi.dsi.sux4j.mph.Hashes;


public class FeatureEncoderMurMurHash implements FeatureEncoder{
     long size= Long.MAX_VALUE;
     long seed = Math.round(Math.random());
    	
     public FeatureEncoderMurMurHash(long seed) {
    	 this.seed=seed;
     }
     
	 public  long encode(String feature) {
		return  Math.abs(Hashes.murmur3(TransformationStrategies.utf16().toBitVector(feature),seed)) % size ;
	 }
	 
	public  static void  main(String args[]) {
		 String [] tests = {"hola=1","hola=2", "ehola=3"};
		 
		 //use the same seed if you need to have the same encoding 
		 FeatureEncoderMurMurHash fe = new FeatureEncoderMurMurHash(1065402021);
		 for(String test: tests) {
			 System.err.println(test+":"+fe.encode(test));
		 }
	 }

	@Override
	public long esize() {
		
		return -1;
	}

	
	 
}
