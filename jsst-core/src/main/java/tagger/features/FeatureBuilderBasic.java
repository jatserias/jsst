package tagger.features;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import org.apache.log4j.Logger;
import tagger.data.DataExampleRaw;
import tagger.data.Instance;
import tagger.data.InstanceRaw;
import tagger.utils.ClassChooser;
import tagger.utils.Utils;
import tagger.utils.WordUtils;


/**
 * @author jordi
 * Given a vector of words it generates an integer vector per word representing its features
 * 
 * Creating Features and encoding them may be done different classes
 * 
 * WARNINGS:
 * 
 *  - This featureBuilder is based on ASCII c functions (not UTF aware)
 *  - QUOTE NORMALIZATION is performed " '' `` ������ are translated into ``  (That is needed to use old C SST models)
 * 
 * 
 * Instead of returning an object, the Feature Builde will call the Feature representation with the FeatId and FeatureValue
 */
public class FeatureBuilderBasic implements FeatureBuilder {
	

	
	
	
	static final  String[] EMPTY_STRING_ARRAY = new String[0];
	 
	protected static final String SLABEL_FEAT = "slab=";
	
	private static final String POS_FEAT = "pos=";

	private static final String POS_P_1 = "pos+1=";

	private static final String POS_1 = "pos-1=";

	private static final String SB3_FEAT = "sb3=";

	private static final String SB_FEAT = "sb=";

	private static final String PR3_FEAT = "pr3=";

	private static final String PR_FEAT = "pr=";

	private static final String SH_FEAT = "sh=";

	private static final String LM_FEAT = "lm=";

	private static final String W_FEAT = "w=";

	private static final String SB_P_1 = "sb+1=";

	private static final String SH_P_1 = "sh+1=";

	private static final String LM_P_1 = "lm+1=";

	private static final String W_P_1 = "w+1=";

	private static final String SB_1 = "sb-1=";

	private static final String SH_1 = "sh-1=";

	private static final String LM_1 = "lm-1=";

	private static final String W_1 = "w-1=";

	private static final String KF = "KF";

	private static final String RP_END = "rp=end";

	private static final String RP_MID = "rp=mid";

	private static final String RP_BEGIN = "rp=begin";

	static Logger logger = Logger.getLogger(FeatureBuilderBasic.class);

	
   

	public FeatureOption defaultF;
    
  
    
	
	public FeatureBuilderBasic() throws Exception {
    	defaultF = new FeatureOption();
     }
	

  
    
  public  class FeatureOption {
    /// add specific real-valued feats to standard ones
    public boolean USE_R =false;           
    
    /// add constant feature for 
    public boolean USE_KF =false;         
    
    /// use word/lemma features
    public boolean USE_WORDS =true;      
    
    /// lowercase all words, generalized lowercase model   
    public  boolean LOWERCASE =false;       
    
    /// Use lemma attribute
    public   boolean USE_LEMMA =false;    
    
    /// USe/don't relative position features
    public boolean USE_RELPOS_FEATS =false; 
    
    /// Use morph_cache
    public  boolean USE_MORPH_CACHE =true;  
    
    /// Normalize all quotes to `
    public boolean NORMALIZE_QUOTES=true;
	
   }
    
    /// maximum length of gaz entries to use
    static final int maxspan_gaz = 10;

	

	
    
   
    
    /**
     * 
     * @TODO This function should be deprecated and made more robust (using codepoint)
     * 
     * Returns lower case and builds shade feature on sh 
     * @param s
     * @param sh
     * @return
     */
    public static String my_tolower_sh(final String s,StringBuilder sh) {
    	
    		 // StringBuilder ans = new StringBuilder();
    		    		 
    		  char old_char = '#';
    		  
    		  for(char c : s.toCharArray()) {

    		  
    		   // ans.append(java.lang.Character.toLowerCase(c));
    		   //Alternatively we may use  a switch with java.lang.Character.getType(c);
    			  
    		    char rg_c;
    		    if (java.lang.Character.isLetter(c)) //isalpha
    		      if (java.lang.Character.isUpperCase(c))
    		    	  rg_c = 'X';
    		      else 
    		    	  rg_c = 'x';
    		    else if (java.lang.Character.isDigit(c))
    		      rg_c = 'd';
    		    else rg_c = c;
    		    if (rg_c != old_char)
    		      sh.append(rg_c);

    		    old_char = rg_c;
    		  }
    		  
    		 
    		  return s.toLowerCase(); 
    		}
    
    //@TODO fix POS parameter!!
    /*
     * @W words
     * @P pos could be an empty string
     * @L could be an empty string
     */
 /* (non-Javadoc)
 * @see tagger.features.FeatureBuilder#extractFeatures(java.lang.String[], java.lang.String[], java.lang.String[])
 */
private   void extractFeatures(final String[] W, final String[] P,  final String[] L, final String[] S, FeatureVector[] fv) {
	extractFeatures(new InstanceRaw(W,L,P, S),fv);
}

/***
 * TODO getting named attributes that needs to be registered!
 */
public   void extractFeatures(final Instance instance, FeatureVector[] fv) {
	String W[]= instance.get("WORD");
	String P[]= instance.get("POS");
	String L[] = instance.get("LEMMA");
	String S[] = instance.get("SLABEL");			
	extractFeatures(W,P,L, S,defaultF,fv);
}

private  static  void  extractFeatures(final String[] W, final String[] P, final String[] L, final String[] S, final FeatureOption f, FeatureVector[] fv) {
   	 int n = W.length;
   	 int m = P.length;
   	  
	//@TODO jab fix parameters      
	boolean lowercase=false;
	
	//all this temp arrays are created all the time	
	// We do not really need keep n but just window -1, +1 (probably creating a n size string is costly)
	String[] LOW=new String[n];
	String[] SH = new String[n];
	String[] SB= new String[n];
	String[] SB3= new String[n];
	String[] PR= new String[n];
	String[] PR3= new String[n];
		//JAB
	String[] JAL= new String[n];
	


 
	for (int i=0;i<n;++i){
		//QUOTE NORMALIZATION
    String w_q = (f.NORMALIZE_QUOTES && ( W[i].compareTo("''")==0 || W[i].compareTo("������")==0)) ? "``" : W[i];  
       //Upper case normalization
	String w =  lowercase ?  WordUtils.my_tolower(w_q) : w_q;
   

   //TODO JORDI check that my_tolower not modifying bsh
   StringBuilder bsh=new StringBuilder();
   String  lemma = my_tolower_sh(w,bsh);
   SH[i]=bsh.toString(); // STring sh
   
    
   //recheck substring
    int last =lemma.length();
   if (last > 2){	
     SB[i] = lemma.substring(last-2); // sb
     PR[i] = lemma.substring(0,2); //pr
   
     if (last > 3){
    	 SB3[i] = lemma.substring(last-3); //sb3
    	 PR3[i] = lemma.substring(0,3); //pr3
     }
     else {
    	 SB3[i] = ""; //sb3
    	 PR3[i] = ""; //pr3 
     }
   }else {
	     SB[i] = ""; // sb
	     PR[i] = ""; //pr
	     SB3[i] = ""; //sb3
    	 PR3[i] = ""; //pr3 
   }
     
   LOW[i]=lemma;

   
   //@TODO check consistency USE_LEMMA and L[] not empty 
   if(f.USE_LEMMA) JAL[i]= L[i].toLowerCase();
 }
	
  
 for (int i = 0; i < n; ++i){
   //@JAB implementation
  
	//List<String> W_i = ClassChooser.createListString(); //new Vector<String>();
	
   FeatureVector featureVector = fv[i];
   if(featureVector==null) {throw new RuntimeException("Got a null fv[i] for i="+i);} 
   
if(f.USE_KF)	featureVector.add(KF);

   if(f.USE_RELPOS_FEATS){
   if (i==0)
     featureVector.add(RP_BEGIN);
   else if (i<n-1)
     featureVector.add(RP_MID);
   else 
     featureVector.add(RP_END);
   }

   if (i > 0){ 
     if(f.USE_WORDS)     featureVector.add(W_1+LOW[i-1]);
     if(f.USE_LEMMA)     featureVector.add(LM_1+JAL[i-1]);

     featureVector.add(SH_1+SH[i-1]);
     featureVector.add(SB_1+SB[i-1]);
   }
   
   if (i < n-1){
   	 if(LOW[i+1]==null) {throw new RuntimeException("Got a null LOW[i]");} 
     if(f.USE_WORDS)	  featureVector.add(W_P_1+LOW[i+1]);
     if(f.USE_LEMMA)      featureVector.add(LM_P_1+JAL[i+1]);

     featureVector.add(SH_P_1+SH[i+1]);
     featureVector.add(SB_P_1+SB[i+1]);
   }
   
   if(f.USE_WORDS)    featureVector.add(W_FEAT+LOW[i]);
   if(f.USE_LEMMA)	featureVector.add(LM_FEAT+JAL[i]);
   
   
   featureVector.add(SH_FEAT+SH[i]);
   featureVector.add(PR_FEAT+PR[i]);
   featureVector.add(PR3_FEAT+PR3[i]);
   featureVector.add(SB_FEAT+SB[i]);
   featureVector.add(SB3_FEAT+SB3[i]);
   if(S!=null && S.length>0)featureVector.add(SLABEL_FEAT+S[i]);
   
   //if POS available
   if (m>0) {
	   if (i > 0){
			   featureVector.add(POS_1+P[i-1]);
	   }
	   if (i < n-1){
			   featureVector.add(POS_P_1+P[i+1]);
	   }
	   
	     featureVector.add(POS_FEAT+P[i]);
	   }
   
   
 }
 
 
} 
    
 
	/**
	 * tags a file in SST format: boolean flags insdicates whether POS and LEMMA are in the input
	 * @TODO ongoing refactory
	 * 
	 * @param filein
	 * @param inputEncoding
	 * @param hasPos
	 * @param hasLemma
	 * @throws IOException
	 */
public void readMassi(String filein,String inputEncoding, boolean hasPos, boolean hasLemma, boolean hasSlabel) throws IOException {
    
	
	BufferedReader fin =	Utils.getBufferedReader(filein, inputEncoding);
	String buff;
	while((buff=fin.readLine())!=null) {
		String[] WB = buff.split("\t");
		String[] W =  new String[WB.length-1]; // first field is sentence id
		DataExampleRaw data = new DataExampleRaw(WB.length-1);
		
		//P is the POS vector
		String[] P = hasPos ? new String[WB.length-1] :EMPTY_STRING_ARRAY;
		//L lemma vector
		String[] L = hasLemma ? new String[WB.length-1]  : EMPTY_STRING_ARRAY;
		//S Slabelr
		String[] S = hasSlabel ? new String[WB.length-1]  : EMPTY_STRING_ARRAY;
		
		// First element is the ID?
		for(int i=1;i<WB.length;++i){
			String [] fields=WB[i].split(" ");
			W[i-1]=fields[0];
			if(hasPos) P[i-1]=fields[1];
			if(hasLemma) L[i-1]=fields[2];
			if(hasSlabel) S[i-1]=fields[3];
		}
		//String[] W = {"The", "cat", "eats", "fish"};
		
		int labelPos=1;
		if(hasPos) ++labelPos;
		if(hasPos) ++labelPos;
		
		FeatureVector[] features = new FeatureVector[W.length];
		 this.extractFeatures(W, P, L,S,features);
		
		//write a line
		int j=1;
		data.setId(WB[0]); //id
		for(FeatureVector feats: features) {
			data.addFeature(feats.get(0));
			for(int i=1;i<feats.size();++i)
				data.addFeature(feats.get(i));
			//add lema as feat
			if(hasLemma) data.addFeature("lema="+L[j]);		
				//add label ? :)
			   data.addLabel(W[j],P[j],L[j],WB[j].split(" ")[labelPos]);
			++j;
		}
	}
}

/**
 * 
 * Processes the examples from filein and writes their feature representation on fileout
 * 
 * @author jordi
 * @param filein
 * @param fileout
 * @param inputEncoding
 * @param ouputEncoding
 * @throws IOException
 */	
	
public int tagfile(String filein, String fileout, String inputEncoding, String outputEncoding, boolean hasPos, boolean hasLemma,boolean hasSlabel) throws IOException {
	int nwords=0;
	
	 PrintStream  out = new PrintStream(fileout, outputEncoding);
	// read a line
	 BufferedReader fin =	 Utils.getBufferedReader(filein, inputEncoding);
	//BufferedReader fin =		new BufferedReader(new InputStreamReader(new FileInputStream(filein), inputEncoding));
	String buff;
	while((buff=fin.readLine())!=null) {
		String[] WB = buff.split("\t");
		nwords+=WB.length-1;
		
		
		String[] W =  new String[WB.length-1]; // first field is sentence id
	
	//P is the POS vector
	String[] P = hasPos ? new String[WB.length-1] :new String[0];
	//L lemma vector
	String[] L = hasLemma ? new String[WB.length-1]  : new String[0];
		
	//TODO generalize this aprt
	String[] S = hasSlabel ?  new String[WB.length-1]  : new String[0];
	
	// First element is the ID?
	for(int i=1;i<WB.length;++i){
		String [] fields=WB[i].split(" ");
		int cpos=0;
		W[i-1]=fields[cpos++];
		if(hasPos) P[i-1]=fields[cpos++];
		if(hasLemma) L[i-1]=fields[cpos++];
		if(hasSlabel) S[i-1]=fields[cpos];
	}
	//String[] W = {"The", "cat", "eats", "fish"};
	
	int labelPos=1;
	if(hasLemma) ++labelPos;
	if(hasPos) ++labelPos;
	if(hasSlabel) ++labelPos;
	
	/**
	 * @TODO What if we have to calculate Pos and Lemma on the fly ??
	 */
	FeatureVector  features[] = new FeatureVector[W.length];
	for(int i=0;i<features.length;++i) features[i]=new FeatureVector();
	
	extractFeatures(W, P, L, S,features);
	
	
	
	//write a line
	int j=1;
	out.print(WB[0]); //id
	for(FeatureVector feats: features) {
		out.print("\t"+feats.get(0));
		for(int i=1;i<feats.size();++i)
			out.print(" "+feats.get(i));		
	    //add label ? :)
		out.print(" "+WB[j].split(" ")[labelPos]);
		++j;
	}
	
	out.println();
	}
	out.close();
	
	return nwords;
}

@Override
public void setUseLemma(boolean useLemaFeature) {
	// TODO Auto-generated method stub
	
}


	
}