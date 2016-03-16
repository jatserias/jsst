package tagger.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;


import org.apache.log4j.Logger;
import tagger.features.FeatureBuilder;
import tagger.features.FeatureEncoder;
import tagger.utils.ClassChooser;

/**
 * 
 * Reads training string feature file
 * 
 * @author jordi
 *
 */
public class TrainingFormatReaderSSTfeatures  implements TrainingFormatReader{
	
	BufferedReader  ifile;
				
				//@TODO find out how to get this options correctly 
				boolean secondorder=false; 
				boolean updateFeatureBuilder=true;
				
				
		static Logger logger = Logger.getLogger(TrainingFormatReaderSSTfeatures.class);
				
		/// Feature Builder
		public FeatureBuilder fb;
		
		/// Feature Encoder
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
		public TrainingFormatReaderSSTfeatures(BufferedReader ifile, SstTagSet tagset,FeatureBuilder fb,FeatureEncoder fe) {
			this.ifile=ifile;
			this.tagset=tagset;
			this.fb=fb;
			this.fe=fe;
		}
		
		
		//@TODO check that is used consistenly withput a buffer
		public TrainingFormatReaderSSTfeatures(SstTagSet tagset,FeatureBuilder fb, FeatureEncoder fe) {
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

			try {
			if (id != ""){
				List<Integer> L = ClassChooser.createListInteger(); //new Vector<Integer>();
				List<List<Integer>> S = ClassChooser.createListListInteger(); //new Vector<Vector<Integer> >();
				
				//logger.info("sentence id ="+ID);


				for(int tj=1;tj<fields.length;tj++){
					List<String> O = ClassChooser.createListString(); // new Vector<String>();
					List<Integer> O_int = ClassChooser.createListInteger(); //new Vector<Integer>();

					String tokens[]= fields[tj].split("[ ]");
					//String f = fields[0];
					//O.add(f);
					//logger,info("READ:"+fields[tj]);
					int ti=0;
					while (ti<tokens.length){
						String f=tokens[ti++];
						O.add(f);
					}

					int _O_ = O.size();
					if (_O_ < 2){
						logger.error("\ntoo few info in id: "+id);
					}
					int i;
					for (i=0; i < _O_-1; ++i){
						//@jab elementAt(i);
						String a = O.get(i);
						
						/// THIS CODE IS REPETEAD
						Long v = fe.encode(a); //FSIS_update_hmap(a,updateFeatureBuilder);
						
						if(v!=null) O_int.add(v.intValue());
						else {logger.error("Feature "+a+" not encoded");}
						
						//@JAB todo looks like reimplemented in FeatureBuilder
						if (secondorder){
							for (int j = i+1; j < _O_-1; ++j){
								//String b = O.elementAt(j);
								String b = O.get(j);
						
								//JAB CHECK is the same in C++ a<b
								if (a.compareTo(b)<0) {
								    v = fe.encode(a+"-"+b); //FSIS_update_hmap(a+"-"+b,updateFeatureBuilder);
									if(v!=null) O_int.add(v.intValue());
									else {logger.info("Feature "+a+"-"+b+" not encoded");}
									
								}
								else {
									v = fe.encode(b+"-"+a);//FSIS_update_hmap(b+"-"+a,updateFeatureBuilder);
									if(v!=null) O_int.add(v.intValue());
									else {logger.info("Feature "+b+"-"+a+" not encoded");}
									
								}
							}
						}
					}
					S.add(O_int);
					
					//@JAB check Adding the last label (Training) if we use FSIS will not be in the right order
					// calling tagset to check the annotation is consistent with the tagset
					
					//	L.add(tagset.LSIS.update_hmap(O.elementAt(i),false));
					L.add(tagset.LSIS.update_hmap(O.get(i),false));
				}
				///System.err.println("Reader encoder size:"+fe.esize());
				//Convert
				return new TrainingInstance(id, S, L);
			}
			} catch (Exception e) {
			
				logger.warn("Unknown error processing the example. Discarting example:"+buff);
				e.printStackTrace();
				return null;
			}
			logger.warn("Empty Example!!");
			return null;
		}

		

		public TrainingInstance next() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}


}
