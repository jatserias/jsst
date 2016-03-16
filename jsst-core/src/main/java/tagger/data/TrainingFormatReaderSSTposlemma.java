package tagger.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import tagger.core.SttTagger;
import tagger.features.FeatureBuilder;
import tagger.features.FeatureEncoder;
import tagger.features.FeatureVector;

/**
 * 
 * @author jordi
 *
 */
public class TrainingFormatReaderSSTposlemma implements TrainingFormatReader{
		
		BufferedReader  ifile;
					
					//@TODO find out how to get this options correctly 
					boolean secondorder=false; 
					boolean updateFeatureBuilder=true;
					
					
			static Logger logger = Logger.getLogger(TrainingFormatReaderSSTposlemma.class);
					
			/// Feature Builder
			public FeatureBuilder fb;
			
			/// Feature Builder
			public FeatureEncoder fe;
			///tagset
			public SstTagSet tagset;
			
			
			/**
			 * 
			 * A format Reader for examples in SST  format
			 * 
			 * @param tagset: the tagset
			 * @param fb: the feature builder
			 */
			public TrainingFormatReaderSSTposlemma(BufferedReader ifile, SstTagSet tagset,FeatureBuilder fb,FeatureEncoder fe) {
				this.ifile=ifile;
				this.tagset=tagset;
				this.fb=fb;
				this.fe=fe;
			}
			
			
			//@TODO check that is used consistenly withput a buffer
			public TrainingFormatReaderSSTposlemma(SstTagSet tagset,FeatureBuilder fb,FeatureEncoder fe) {
				this.tagset=tagset;
				this.fb=fb;
				this.fe=fe;
			}
			
			public TrainingInstance readExample(BufferedReader buff ) throws IOException  {
				return readExample(buff.readLine());
			}
			
			/**
			 *DEPRECATED
			public TrainingExample readExample() throws IOException  {
				return readExample(ifile.readLine());
			}
			**/
			
			/* (non-Javadoc)
			 * @see tagger.data.TrainingFormatReader#readExample(java.lang.String)
			 */
			public TrainingInstance readExample(String buff )  {
				String fields[] =	buff.split("[\t]");

				String id = fields[0];
				
				//pos
				String[] P = new String[fields.length-1];
				
				// word
				String[] W = new String[fields.length-1];
				
				// lemma empty
				String[]  LM = new String[0];
				
				int[] NL = new int[fields.length-1];;
				try {
				if (id != ""){
					
					//logger.info("sentence id ="+ID);

					
					for(int tj=1;tj<fields.length;tj++){
						String tokens[]= fields[tj].split("[ ]");
						W[tj-1]=tokens[0];
						P[tj-1]=tokens[1];
						// LABELS 
						NL[tj-1]=tagset.getLabelId(tokens[2]);
					}
						//CALCULATE W  P LM  from input
						
						FeatureVector[] senfeat = new FeatureVector[W.length];
						fb.extractFeatures(new InstanceRaw(W, P, LM,null),senfeat);
						
						
						int[][] NS = SttTagger.encode(senfeat, false,fe);
						
						//CREATE true NERC labels??
						
				
					//Convert	
					return new TrainingInstance(id, NS, NL);
					}
				} catch (Exception e) {
				
					logger.warn("Unknown error processing the example. Discarting example:"+buff);
					e.printStackTrace();
					return null;
				}
				logger.warn("Empty Example!!");
				return null;
			}


//			@Override
//			public TrainingExample next() throws IOException {
//				// TODO Auto-generated method stub
//				return null;
//			}

			


}
