package tagger.features;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;



import tagger.data.Instance;
import tagger.utils.ClassChooser;
import tagger.utils.FileDescription;
import tagger.utils.Utils;
import tagger.utils.WordUtils;

/**
 * 
 * A class to provide feature base on a gazetter (list of names)
 * 
 * the constructor needs a file containing the names of the files to load
 * 
 * POS is needed (we may want to add a PoS Feature adder?)
 * 
 * @TODO provide the files encoding
 * @TODO Gazetter needs the PoS to lemmatize (!? we should think how to fix dependencies)
 * 
 * Features generated from Gazetter
 * 
 * TRIG
 * RX
 * LX
 * B-
 * I-
 * 
 * Uses a morph cache to lemmatize
 * 
 * 
 * Example of Gazzetter input file: Word <tab> Feature <tab> Feature <tab> Feature
 * 
 * In Multitoken entries, tokens are joined with '_' 
 * 
 * 100     FWNSS1=adj.all  BIFWNSS1=adj.all        NWNSS1_adj=1
 * 1000    FWNSS1=adj.all  BIFWNSS1=adj.all        NWNSS1_adj=1
 * 1000th  FWNSS1=adj.all  BIFWNSS1=adj.all        NWNSS1_adj=1
 * 
 * Multitoken Example:
 * 
 * all_right       FWNSS2=adj.all  BIFWNSS2=adj.all        NWNSS2_adj=1
 *
 * TRIGGER examples:
 * 
 * abulense        TRIG_GENT_1
 * abulenses       TRIG_GENT_1
 * academic        TRIG_PER_1
 * 
 * 
 * 
 * 
 * @TODO reduce memory fingerprint using TFILE
 * 
 * @author jordi
 *
 */
public class FeatureBuilderGazetter extends FeatureBuilderBasic {
	private static final String LINE_FILE_SPLITTER = "[\t]";
	private static final String TOKEN_SEPARATOR = "_";
	private static final String I_PREFIX = "I-";
	private static final String B_PREFIX = "B-";
	private static final String GAZ_ENCODING = "UTF-8";
	
	/// prefix to identify TRIGger features
	private static final String FEATURE_PREFIX_TRIGGER = "TRIG";
	
	/// the prefix for the generated features
	private static final String FEATURE_PREFIX_LX = "LX-";
	/// the prefix for the generated features
	private static final String FEATURE_PREFIX_RX = "RX-";
	
	
	
	
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FeatureBuilderGazetter.class);

	/// A decoder int -> String for the list of "features" associated to the gazzetter 
	FeatureEncoderDecoder fd;
	
	/// LEMMA => Encoded Features @TODO optimize this mapping
	public Map<String,int[]> gazetter;
	
	/// Ready to tag? whenever we have loaded the gazzetters or not 
	boolean ready;
	
	/// Maximum span
	int maxspan;

	/// Morphological Cache (needed for lemmatizing)
	Lemmatizer M;
	
	
	public FeatureBuilderGazetter(FeatureEncoderDecoder fe,String basename, String fname,int p_maxspan,Lemmatizer mcache) throws Exception {
		super();
		M =mcache;fd=fe;
		init(basename,fname,p_maxspan);
	}
	

	public FeatureBuilderGazetter(FeatureEncoderDecoder fe,int p_maxspan,Lemmatizer mcache, FileDescription[] gazzetters) throws Exception {
		super();
		M =mcache;fd=fe;
		Ginit(p_maxspan);
		for(FileDescription gaz:gazzetters) addGazzetter(gaz,fe);
	}
	
	void Ginit(int p_maxspan) {
		ready=false; 
		maxspan=p_maxspan; 
		gazetter = new HashMap<String,int[]>(); 
	}

	




	/**
	 * Load a gazetter from the file named fname
	 * 
	 * @param fname
	 * @return
	 * @throws IOException
	 */
	private List<String> load_GAZ_files(String fname, String encoding) throws IOException{
		List<String> GAZ = new ArrayList<String>(); 
		BufferedReader gazFilenames = Utils.getBufferedReader(fname , encoding);
		String gazFile;
		while((gazFile=gazFilenames.readLine())!=null) {
			
			GAZ.add(gazFile);
			log.info("Adding GAZ file "+gazFile+" #"+GAZ.size());
		}

		log.info("\t|G("+fname+")| = "+GAZ.size());
		return GAZ;
	}


	private void addGazzetter(final FileDescription gaz,FeatureEncoder fe) throws FileNotFoundException, IOException {
		log.warn("loading "+gaz.buildFilename());
		BufferedReader fgaz = gaz.open();
		
		String line;
		
		
		while ( (line=fgaz.readLine())!=null){
			String [] buff= line.split(LINE_FILE_SPLITTER);
			int last=buff.length; 
			int[] tags = new int[last-1];
			for(int j=1;j<last;++j){		  
				tags[j-1]=((int) fe.encode(buff[j])) ; 
			}

			
			//System.err.println(tags.length+" LINE:"+line);	
			// store the list of features associated to the entry
			if (tags.length>0)
				update_G(buff[0].toLowerCase(),tags);
			
			//tags.clear();
			
		}
	}
	
	private void init(String basename, String fname, int n) throws IOException { 
		Ginit(n);
		if (!ready){
			log.info("\n\tgaz_server::init("+fname+","+n+")");
			maxspan = n;
			
			for (String cname :load_GAZ_files(fname,GAZ_ENCODING)) {
				addGazzetter(new FileDescription(basename+cname,GAZ_ENCODING,false),fd);			
			}
			
			log.info("\t|G| = "+gazetter.size());
			ready = true;
		}
	}


/**
 * 
 * concatenates int list
 * 
 * @param w
 * @param tags
 */
	void update_G(String w, final int[] tags) {
		int[] elem = gazetter.get(w);

		if(elem==null)  {
			gazetter.put(w, tags);
	     }
		else 
		{		
			int[] s= new int[elem.length+tags.length];
			System.arraycopy(elem, 0, s, 0, elem.length);
			System.arraycopy(tags, 0, s, elem.length, tags.length);
			gazetter.put(w, s);		
		}
	}




	

	@Override
	public    void extractFeatures(final Instance instance,  FeatureVector[] fv) {
			final String[] W= instance.get("WORD");
			String[] P =instance.get("POS");
			String[] L =instance.get("LEMMA");
			String S[] = instance.get("SLABEL");
		//@TODO fix jab WM lemma list is not returned?
		
			String[] LOW = new String[W.length];
		    for(int i=0;i<W.length;++i) { LOW[i]=W[i].toLowerCase();}
		    
		if(L.length<W.length) {
			L = WordUtils.getLemmas(LOW,P,M);
		}

		super.extractFeatures(instance,fv);
       
		
		InternalExtract_feats(fd,LOW, P, L, S,fv);//FSIS
		
	}

	
	/**
	 * Lemmas must be provided!
	 * 
	 * calculate new feats
	 * @param fd FeatureDecoder ... need to "decode" the list of ints the gazzetter returns
	 * @param L wordforms
	 * @param P Pos
	 * @param O features already extracted
	 * @param WM lemmas
	 */
	private void InternalExtract_feats( 
		final FeatureDecoder fd,
		final String[] L, 
		final String[] P,
		final String[] WM,
		final String[] S,
		FeatureVector[] fv) {
		
		int _L_ = L.length;
			for (int i = 0; i < _L_; ++i){
			 for (int j = 0; j < maxspan; ++j)
				if (i-j >= 0){
					int lx = i-j;
					//String pos = P[lx];
					StringBuffer w = new StringBuffer(L[lx]);
					StringBuffer wm = new StringBuffer(WM[lx]);
					
					//StringBuffer wm = new StringBuffer(? WM[lx].toLowerCase() : "");

					++lx;
					while (lx <= i){
						w.append(TOKEN_SEPARATOR).append(L[lx]);
						wm.append(TOKEN_SEPARATOR).append(WM[lx]); //??.toLowerCase());
						++lx;
					}

					if (w.length()-j+1 > 2){


						int[] G_i= gazetter.get(w.toString());

						if (G_i==null) {
							//log.warn(w+" not found in Gazzetters");
							G_i= gazetter.get(wm.toString());
						} //else log.warn(w+" FOUND in  Gazzetters");
					
						
						if (G_i!=null){
						  // A gazetter returns a list of integers (are the encoded strings??)
							for (int r = 0; r < G_i.length; ++r){
								int left = i-j;
								
								int G_i_r_id=G_i[r];
								
								/// Get the String label back from the  encoding
								String G_i_r_Label = fd.decode(G_i_r_id);
								
								boolean isTrigger = G_i_r_Label.startsWith(FEATURE_PREFIX_TRIGGER);
								
								if (isTrigger && left-1 >= 0) fv[left-1].add(FEATURE_PREFIX_RX+G_i_r_Label);
								
								fv[left++].add(B_PREFIX+G_i_r_Label);
								
								while (left <= i) fv[left++].add(I_PREFIX+G_i_r_Label);
								
								if (isTrigger && left < _L_) fv[left].add(FEATURE_PREFIX_LX+G_i_r_Label);
							}
						}
						//else log.warn(wm+" neither found in Gazzetters");
					}
				}
			 
			 if(S!=null && S.length>0) fv[i].add(SLABEL_FEAT+S[i]);
		}

			 
	}  


}
