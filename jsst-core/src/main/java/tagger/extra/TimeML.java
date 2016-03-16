package tagger.extra;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import tagger.extra.TagsetParole.TagParole;
import tagger.extra.TagsetParole.features;
import tagger.extra.TagsetParole.mod;
import tagger.extra.TagsetParole.tense;


public class TimeML {
	
	public enum PartOfSpeech { 
		ADJECTIVE,
	NOUN,
	VERB,
	PREP,
	OTHER};
	
	
	public	enum VerbForm  {
		INFINITIVE,
	GERUNDIVE,
	PARTICIPLE,
	NONE}
	;
	
	VerbForm getVernForm() {
		return VerbForm.NONE;
	}
	
	public enum TML_Tense {
		FUTURE,
	PAST,
	PRESENT,
	NONE};
	
	public enum Aspect { 
		IMPERFECTIVE,
	PERFECTIVE,
	IMPERFECTIVE_PROGRESSIVE,
	PERFECTIVE_PROGRESSIVE,
	NONE};
	
	public enum Mood {
		INDICATIVE,
	SUBJUNCTIVE,
	CONDITIONAL,
	IMPERATIVE,
	NONE};
	
	public enum Polarity { NEG,
	POS};
	
	public enum EventClass {
		OCCURRENCE,
	PERCEPTION,
	REPORTING,
	ASPECTUAL,
	STATE,
	I_STATE,
	I_ACTION,
	NONE}
	

	public enum lemmasaux {
	 estar, haber
	}
	
	/**
	 * 
	 * load TimeML corpus
	 * 
	 * getTokens and run PosTagger
	 * 
	 * run the  chunker
	 * 
	 * generate sequence of candidate events
	 * 
	 * @param args
	 * @throws IOException
	 */
	 public static void main(String [] args) throws IOException { 
		 
		 String[] tags = {"VMP00SM", "NCMS000"};
		 String[] lemmas= {"haber", "teindo"};
		 
		 
	 }
	 
	 public static PrintStream perr = getPerr();
			 
	 public static PrintStream getPerr() {
		 try {
			return new PrintStream("perr.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return System.err;
	 }
	
	 static  HashMap<String, TimeMLatt> fmap = constructionMap();
	 /**
	  * (SPANISH)
	  * 
	  * A. Patrons de formes verbals compostes:
	  * 
	  * 	1. "estar" + GERUND	(e.g., "estaba/estaría cantando")
	  * 	2. "haber" + PARTICIPLE	(e.g., "había/hubieran cantado") 
	  * 	3. "haber" + "estar"-PARTICIPLE + GERUND  (e.g., "había estado cantando")
	  * 	
	  * B. Heurística per als atributs de: POS, VFORM, TENSE, ASPECT, MOOD:
	  * 
	  * Tots aquests atributs es representen al nucli semàntic de la forma composta,
	  * que és *sempre* el darrer element, i que doncs es tracta d'un verb en
	  * forma de gerundi o participi.
	  * 
	  *   1. Atribut que ja es prenen del nucli semàntic (darrer element):
	  *      - POS
	  * 
	  *   2. Atributs extrets directament del primer auxiliar:
	  *      - VFORM
	  *      - TENSE
	  *	     - MOOD
	  * 
	  *   3. Atribut que es calcula en base al tipus de "forma composta": 
	  *      - ASPECT
 	  *       El valor d'aquest atribut s'assigna com segueix:
	  * 
	  * 	1. Formes "estar" + GER: aspect="IMPERFECTIVE_PROGRESSIVE" 
	  * 	   Excepte el cas de "estuve cantando" (pretérito perfecto
	  * 	   simple), per al qual aspect="PERFECTIVE_PROGRESSIVE"
	  * 
	  * 	2. Formes "haber" + PART: aspect="PERFECTIVE"
	  * 
	  * 	3. Formes "haber" + "estar"PART + GER: aspect="PERFECTIVE_PROGRESSIVE"
	  * 
	  * @param tags
	  * @param lemmas
	  */
	 public static List<EventFeatures>  displayEvent(String jid,String[] tokens, String[] tags, String[] lemmas) {
		 ArrayList<EventFeatures> cres = new ArrayList<EventFeatures>();
		
			
		 //GO
		 
		 TagsetParole  tagset = new TagsetParole();
		 tagset.loadMap("/Users/jordi/yprojects/jsst-core/src/test/resources/parole.txt");
		 
		 
		 ArrayList<WordStat> seq= new ArrayList<WordStat>(); 
		 for(int i=0;i<tags.length;++i) {
			 //TODO lemma - tokens ??
			 seq.add(new WordStat(tags[i],tokens[i]));
		 }
		 // conver tags into seq
		
		 for(int i=0;i<seq.size();++i) {
			 WordStat s =seq.get(i);
			 switch(s.mpos) {
				 case 'v' :
				   
    			   TimeMLatt ca = fmap.get(s.pos);
    			   String pkey="";
    			   int ext=0;
    			   int cu=1;
    			   // try extending
    			   while((i+cu)<seq.size() && cu<4) {
    			   WordStat ss= seq.get(i+cu);

    			  // System.err.println(ss.mpos);
    			  // System.err.println(ss.pos.startsWith("p0") || ss.pos.startsWith("g0"));
    			  // System.err.println((s.estar || s.ser || s.haber));
    			   // posible candidate extension
    			   if(ss.mpos=='v' &&  (ss.pos.startsWith("p0") || ss.pos.startsWith("g0"))  && (s.estar || s.ser || s.haber)) {
    				   pkey = pkey+s.toString()+"+"+ss.pos;
    				   //System.err.println("Trying:"+pkey);
    				   TimeMLatt  np = fmap.get(pkey);
    				   if(np!=null) {ca=np; ext=cu;}
    			   }
    			   
    			   pkey = pkey+"+";
    			   cu++;

    			   }
    				
    			  
    			   if(ca==null) {
    				   perr.println("PROBLEM processing "+lemmas[i]+"_"+tags[i]);
    				   //System.exit(-1);
    			   }
    			   else {
    				   String vkey=tokens[i];
    			   //String vkey=lemmas[i]+"_"+tags[i];
    			   //for(int j=i+1;j<ext;++j) { vkey += " "+lemmas[j]+"_"+tags[j];}
    			   cres.add(candidateEvent(jid,vkey, ca));	
    			   
    			   i=i+ext; 
    			   }
    			  break;
    			  
				 case 'n':
					 cres.add(candidateEvent(jid,tokens[i],//lemmas[i]+"_"+tags[i],
							 new TimeMLatt(PartOfSpeech.NOUN, VerbForm.NONE, TML_Tense.NONE, Aspect.NONE,Mood.NONE)));
					 break;
				 case 'a':
					 cres.add(candidateEvent(jid,tokens[i], //lemmas[i]+"_"+tags[i],
							 new TimeMLatt(PartOfSpeech.ADJECTIVE, VerbForm.NONE, TML_Tense.NONE, Aspect.NONE,Mood.NONE)));
					 break;
				
    			 default: 
    				 cres.add(candidateEvent(jid,tokens[i], //lemmas[i]+"_"+tags[i],
							 new TimeMLatt(PartOfSpeech.OTHER, VerbForm.NONE, TML_Tense.NONE, Aspect.NONE,Mood.NONE)));
    				 break;
			 }
		
		 }
		 
		 return cres;
	 }

	private static HashMap<String, TimeMLatt> constructionMap() {
		HashMap<String,TimeMLatt> fmap= new HashMap<String,TimeMLatt>();
		 
		 //indicative
		 
		 //IMPERF
		 fmap.put("ip", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PRESENT, Aspect.IMPERFECTIVE,Mood.INDICATIVE));
		 fmap.put("ip(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PRESENT, Aspect.IMPERFECTIVE,Mood.INDICATIVE));
		
		 fmap.put("ii", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.IMPERFECTIVE,Mood.INDICATIVE));
		 fmap.put("ii(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.IMPERFECTIVE,Mood.INDICATIVE));
		 
		 fmap.put("if", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.FUTURE, Aspect.IMPERFECTIVE,Mood.INDICATIVE));
		 fmap.put("if(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.FUTURE, Aspect.IMPERFECTIVE,Mood.INDICATIVE));
		 
		 //PERF
		 fmap.put("ip(HABER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PRESENT, Aspect.PERFECTIVE,Mood.INDICATIVE));
		 fmap.put("ip(HABER)+p0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PRESENT, Aspect.PERFECTIVE,Mood.INDICATIVE));
		 
		 fmap.put("is", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.IMPERFECTIVE,Mood.INDICATIVE));
		 fmap.put("is(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.IMPERFECTIVE,Mood.INDICATIVE));
		 fmap.put("ii(HABER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.IMPERFECTIVE,Mood.INDICATIVE));
		 fmap.put("is(HABER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.IMPERFECTIVE,Mood.INDICATIVE));
		 fmap.put("ii(HABER)+p0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.IMPERFECTIVE,Mood.INDICATIVE));
		 fmap.put("is(HABER)+p0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.IMPERFECTIVE,Mood.INDICATIVE));
		 
		 
		 fmap.put("if(HABER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.FUTURE, Aspect.IMPERFECTIVE,Mood.INDICATIVE));
		 fmap.put("if(HABER)+p0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.FUTURE, Aspect.IMPERFECTIVE,Mood.INDICATIVE));
		 
		 //IMP_PROG
		 fmap.put("ip(ESTAR)+g0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PRESENT, Aspect.IMPERFECTIVE_PROGRESSIVE,Mood.INDICATIVE));
		 fmap.put("ip(ESTAR)+g0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PRESENT, Aspect.IMPERFECTIVE_PROGRESSIVE,Mood.INDICATIVE));
		 
		 fmap.put("ii(ESTAR)+g0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.IMPERFECTIVE_PROGRESSIVE,Mood.INDICATIVE));
		 fmap.put("ii(ESTAR)+g0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.IMPERFECTIVE_PROGRESSIVE,Mood.INDICATIVE));
		 
		 fmap.put("if(ESTAR)+g0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.FUTURE, Aspect.IMPERFECTIVE_PROGRESSIVE,Mood.INDICATIVE));
		 fmap.put("if(ESTAR)+g0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.FUTURE, Aspect.IMPERFECTIVE_PROGRESSIVE,Mood.INDICATIVE));
		 
		 // PERF PROG
		 
		 fmap.put("ip(HABER)+p0(ESTAR)+g0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PRESENT, Aspect.PERFECTIVE_PROGRESSIVE,Mood.INDICATIVE));
		 fmap.put("ip(HABER)+p0(ESTAR)+g0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PRESENT, Aspect.PERFECTIVE_PROGRESSIVE,Mood.INDICATIVE));
		 
		 
		 fmap.put("is(ESTAR)+g0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.PERFECTIVE_PROGRESSIVE,Mood.INDICATIVE));
		 fmap.put("is(ESTAR)+g0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.PERFECTIVE_PROGRESSIVE,Mood.INDICATIVE));
		 fmap.put("ii(HABER)+p0(ESTAR)+g0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.PERFECTIVE_PROGRESSIVE,Mood.INDICATIVE));
		 fmap.put("is(HABER)+p0(ESTAR)+g0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.PERFECTIVE_PROGRESSIVE,Mood.INDICATIVE));
		 fmap.put("ii(HABER)+p0(ESTAR)+g0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.PERFECTIVE_PROGRESSIVE,Mood.INDICATIVE));
		 fmap.put("is(HABER)+p0(ESTAR)+g0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.PERFECTIVE_PROGRESSIVE,Mood.INDICATIVE));
		 
		 fmap.put("if(HABER)+p0(ESTAR)+g0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.PERFECTIVE_PROGRESSIVE,Mood.INDICATIVE));
		 fmap.put("if(HABER)+p0(ESTAR)+g0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.PERFECTIVE_PROGRESSIVE,Mood.INDICATIVE));
		 
		 // SUBJUNTIVE
		 
		 // impr
		 fmap.put("sp", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PRESENT, Aspect.IMPERFECTIVE,Mood.SUBJUNCTIVE));
		 fmap.put("sp(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PRESENT, Aspect.IMPERFECTIVE,Mood.SUBJUNCTIVE));
		 
		 fmap.put("si", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.IMPERFECTIVE,Mood.SUBJUNCTIVE));
		 fmap.put("si(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.IMPERFECTIVE,Mood.SUBJUNCTIVE));
		 
		 fmap.put("sf", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.FUTURE, Aspect.IMPERFECTIVE,Mood.SUBJUNCTIVE));
		 fmap.put("sf(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.FUTURE, Aspect.IMPERFECTIVE,Mood.SUBJUNCTIVE));
		 
		 // perfective
		 fmap.put("sp(HABER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PRESENT, Aspect.PERFECTIVE,Mood.SUBJUNCTIVE));
		 fmap.put("sp(HABER)+p0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PRESENT, Aspect.PERFECTIVE,Mood.SUBJUNCTIVE));
		 
		 fmap.put("si(HABER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.PERFECTIVE,Mood.SUBJUNCTIVE));
		 fmap.put("si(HABER)+p0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.PERFECTIVE,Mood.SUBJUNCTIVE));
		 
		 // impr prog
		 fmap.put("sp(ESTAR)+g0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PRESENT, Aspect.IMPERFECTIVE_PROGRESSIVE,Mood.SUBJUNCTIVE));
		 fmap.put("sp(ESTAR)+g0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PRESENT, Aspect.IMPERFECTIVE_PROGRESSIVE,Mood.SUBJUNCTIVE));
		
		 fmap.put("si(ESTAR)+g0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.IMPERFECTIVE_PROGRESSIVE,Mood.SUBJUNCTIVE));
		 fmap.put("si(ESTAR)+g0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.IMPERFECTIVE_PROGRESSIVE,Mood.SUBJUNCTIVE));
		
		 // perf_ prog
		 fmap.put("sp(HABER)+po(ESTAR)+g0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PRESENT, Aspect.PERFECTIVE_PROGRESSIVE,Mood.SUBJUNCTIVE));
		 fmap.put("sp(HABER)+po(ESTAR)+g0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PRESENT, Aspect.PERFECTIVE_PROGRESSIVE,Mood.SUBJUNCTIVE));
			
		 fmap.put("si(HABER)+po(ESTAR)+g0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.PERFECTIVE_PROGRESSIVE,Mood.SUBJUNCTIVE));
		 fmap.put("si(HABER)+po(ESTAR)+g0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.PAST, Aspect.PERFECTIVE_PROGRESSIVE,Mood.SUBJUNCTIVE));
		
		 // CONDITINAL
		 fmap.put("cp", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.NONE, Aspect.IMPERFECTIVE,Mood.CONDITIONAL));
		 fmap.put("cp(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.NONE, Aspect.IMPERFECTIVE,Mood.CONDITIONAL));
			
		 fmap.put("cp(HABER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.NONE, Aspect.PERFECTIVE,Mood.CONDITIONAL));
		 fmap.put("cp(HABER)+p0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.NONE, Aspect.PERFECTIVE,Mood.CONDITIONAL));
			
		 fmap.put("cp(ESTAR)+g0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.NONE, Aspect.IMPERFECTIVE_PROGRESSIVE,Mood.CONDITIONAL));
		 fmap.put("cp(ESTAR)+g0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.NONE, Aspect.PERFECTIVE_PROGRESSIVE,Mood.CONDITIONAL));
		
		 fmap.put("cp(HABER)+po(ESTAR)+g0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.NONE, Aspect.PERFECTIVE_PROGRESSIVE,Mood.CONDITIONAL));
		 fmap.put("cp(HABER)+po(ESTAR)+g0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.NONE, Aspect.PERFECTIVE_PROGRESSIVE,Mood.CONDITIONAL));
			
		 // IMPERATIVE
		 fmap.put("mp", new TimeMLatt(PartOfSpeech.VERB, VerbForm.NONE, TML_Tense.NONE, Aspect.NONE,Mood.IMPERATIVE));
			
		 // INFINITIVES
		 fmap.put("n0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.INFINITIVE, TML_Tense.NONE, Aspect.IMPERFECTIVE,Mood.NONE));
		 fmap.put("n0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.INFINITIVE, TML_Tense.NONE, Aspect.IMPERFECTIVE,Mood.NONE));
				
		 fmap.put("n0(HABER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.INFINITIVE, TML_Tense.NONE, Aspect.PERFECTIVE,Mood.NONE));
		 fmap.put("n0(HABER)+p0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.INFINITIVE, TML_Tense.NONE, Aspect.PERFECTIVE,Mood.NONE));
			
		 
		 fmap.put("n0(ESTAR)+g0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.INFINITIVE, TML_Tense.NONE, Aspect.IMPERFECTIVE_PROGRESSIVE,Mood.NONE));
		 fmap.put("n0(ESTAR)+g0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.INFINITIVE, TML_Tense.NONE, Aspect.IMPERFECTIVE_PROGRESSIVE,Mood.NONE));
			
		 fmap.put("n0(HABER)+p0(ESTAR)+g0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.INFINITIVE, TML_Tense.NONE, Aspect.PERFECTIVE_PROGRESSIVE,Mood.NONE));
		 fmap.put("n0(HABER)+p0ESTAR)+g0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.INFINITIVE, TML_Tense.NONE, Aspect.PERFECTIVE_PROGRESSIVE,Mood.NONE));
			
		 // GERUNDS
		 fmap.put("g0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.GERUNDIVE, TML_Tense.NONE, Aspect.IMPERFECTIVE,Mood.NONE));
		 fmap.put("g0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.GERUNDIVE, TML_Tense.NONE, Aspect.IMPERFECTIVE,Mood.NONE));
				
		 fmap.put("g0(HABER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.GERUNDIVE, TML_Tense.NONE, Aspect.PERFECTIVE,Mood.NONE));
		 fmap.put("g0(HABER)+p0(SER)+p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.GERUNDIVE, TML_Tense.NONE, Aspect.PERFECTIVE,Mood.NONE));
			
		 // PARTICIPLE
		 fmap.put("p0", new TimeMLatt(PartOfSpeech.VERB, VerbForm.PARTICIPLE, TML_Tense.NONE, Aspect.NONE,Mood.NONE));
		return fmap;
	}
	 
	 
	 
	 
	 
	 @Deprecated
	 public static Collection<EventFeatures>  OLDdisplayEvent( String[] tags, String[] lemmas) {
		 ArrayList<EventFeatures> cres = new ArrayList<EventFeatures>();
		 
		 TagsetParole  tagset = new TagsetParole();
		 tagset.loadMap("/Users/jordi/yprojects/jsst-core/src/test/resources/parole.txt");
		 
		 TimeMLatt res;
		String jid="NOSE";
		 
		 
		 List<TimeMLatt> seq = new ArrayList<TimeMLatt>();
		 for(String tag : tags) {
			 seq.add(getTimeMLatt(tagset,tag));
		 }
		 
		 //System.err.println("Detecting events...");
		 for(int i=0;i<seq.size();++i) {
			 TimeMLatt cur = seq.get(i);
			 
			// System.err.println("TAGS:"+tags[i]);
			// System.err.println("CUR:"+cur);
	       /// Current  Candidate
			
			if(cur!=null) {
			PartOfSpeech mpos = (PartOfSpeech)cur.res[Timemlfeat.pos.ordinal()];
			if(mpos!=null)
			switch(mpos) {
			
			 // generate cadidate
			case VERB:
				//System.err.println("VERB CANDIDATE:"+cur);
			 //1) estar + GERUND
				if(i+1<seq.size() && lemmas[i].compareTo("estar")==0 && ((VerbForm)seq.get(i+1).res[Timemlfeat.vform.ordinal()]== VerbForm.GERUNDIVE)) {
					TimeMLatt chunk = new TimeMLatt(PartOfSpeech.VERB,
							(VerbForm) cur.get(Timemlfeat.vform),
							(TML_Tense) cur.get(Timemlfeat.tense),
							tags[i].startsWith("vmis") ? Aspect.IMPERFECTIVE_PROGRESSIVE : Aspect.PERFECTIVE_PROGRESSIVE,
							(Mood) cur.get(Timemlfeat.mood)
					);
					cres.add(candidateEvent(jid,lemmas[i]+"_"+tags[i]+" "+lemmas[i+1]+"_"+tags[i+1],chunk));
					i+=1;
				}
				else
			     //2 haber + PARTICIPLE		
					 if(i+1<seq.size() && lemmas[i].compareTo("haber")==0  && ((VerbForm) seq.get(i+1).get(Timemlfeat.vform)==VerbForm.PARTICIPLE)) {
						 TimeMLatt chunk = new TimeMLatt(PartOfSpeech.VERB,
									(VerbForm) cur.get(Timemlfeat.vform),
									(TML_Tense) cur.get(Timemlfeat.tense),
									Aspect.PERFECTIVE,
									(Mood) cur.get(Timemlfeat.mood)
							);
						 cres.add(candidateEvent(jid,lemmas[i]+"_"+tags[i]+" "+lemmas[i+1]+"_"+tags[i+1],chunk));
							i+=1;
					 }
				 else	 
				 //3) haber + estado + GERUND
				 if(i+2<seq.size() && lemmas[i].compareTo("haber")==0 && lemmas[i+1].compareTo("estado")==0 &&  ((VerbForm)seq.get(i+2).res[Timemlfeat.vform.ordinal()]==VerbForm.GERUNDIVE)) {
					 TimeMLatt chunk = new TimeMLatt(PartOfSpeech.VERB,
								(VerbForm) cur.get(Timemlfeat.vform),
								(TML_Tense) cur.get(Timemlfeat.tense),
								Aspect.PERFECTIVE_PROGRESSIVE,
								(Mood) cur.get(Timemlfeat.mood)
						);
						cres.add(candidateEvent(jid,lemmas[i]+"_"+tags[i]+" "+lemmas[i+1]+"_"+tags[i+1]+" "+lemmas[i+2]+"_"+tags[i+2], chunk));		
						i+=2;
			     }
				 // one word
				 else {
					 //??
					 // cur.res[timemlfeat.aspect.ordinal()] = tags[i].startsWith("vmis") ? Aspect.IMPERFECTIVE_PROGRESSIVE : Aspect.PERFECTIVE_PROGRESSIVE;
					 cres.add(candidateEvent(jid,lemmas[i]+"_"+tags[i],cur));
				  	 
				 }
				 
				break;
				
			case NOUN:
			case ADJECTIVE:
			 // generate cadidate
				
			
				cres.add(candidateEvent(jid, lemmas[i]+"_"+tags[i],cur));	break;
			default:
			}
		 }
			
			
		 }
		 return cres;
	 }
	 
	private static EventFeatures candidateEvent(String jid,String words, TimeMLatt cur) {
		//System.err.println("EVENT ["+words+"]:"+cur);	
		return new EventFeatures(jid,words,cur,false,EventClass.NONE);
	}


	/**
	 * 
	 * buld a map from PAROLE-ANCORA to Event TIMEML attributes
	 * 
	 * mood=conditional
	 * 
	 */
		 
		 
	 public static   TimeMLatt getTimeMLatt(TagsetParole  tagset,String tag) {
		if(tag==null) System.err.println("PROBLEMS we had a call with null pos tag");
		// System.err.println("PAROLE:"+tag); 
		 TagParole t = tagset.createTag(tag);
		 if(t==null) System.err.println("PROBLEMS with tag:"+tag);
		 TimeMLatt res=null;
		 switch(tag.charAt(0)){
		 
		 case 'v':
		 case 'V':
			 
			 //Further work on get the Tense
			 TML_Tense tt;
			 tense te = (tense)t.get(features.tense);
			 if(te==null) tt=TML_Tense.NONE; 
			 else
				 switch(te) {
				 case past: tt=TML_Tense.PAST; break;
				 case future: tt=TML_Tense.FUTURE; break;
				 case present: tt=TML_Tense.PRESENT; break;

				 case conditional:
					 //System.err.println("pos=VERB\tvform=NONE\tmood=CONDITIONAL\n");

					 //Further work on get the Tense
					 tt= TML_Tense.NONE;


					 res = new TimeMLatt(PartOfSpeech.VERB,VerbForm.NONE,TML_Tense.NONE, Aspect.IMPERFECTIVE,Mood.CONDITIONAL);   
					 break;
				 default: tt= TML_Tense.NONE;
				 }
			 
		//Aspect aspect= Aspect.NONE;
			 
		mod mo = (mod) t.get(features.mod);
		if(mo==null) {
			System.err.println("WARNING NULL MOD for tag:"+tag);
		}
		else{
		 switch(mo) {
		 case imperative:
			 //System.err.println("pos=VERB\tvform=NONE\tmood=IMPERATIVE\n");
			 //Further work on get the Tense
			  tt= TML_Tense.NONE;
			 
			 res = new TimeMLatt(PartOfSpeech.VERB,VerbForm.NONE,tt, Aspect.NONE,Mood.IMPERATIVE);   
			 break;
		 case infinitive:
			// System.err.println("pos=VERB\tvform=NONE\tmood=NONE\n");
			//Further work on get the Tense
			  tt= TML_Tense.NONE;
			 
			 res = new TimeMLatt(PartOfSpeech.VERB,VerbForm.INFINITIVE,tt, Aspect.IMPERFECTIVE,Mood.NONE);   
			 break;
		 case gerund :
			 //System.err.println("pos=VERB\tvform=GERUNDIVE\tmood=NONE\n");
			 
			 tt= TML_Tense.NONE;
			 
			 res = new TimeMLatt(PartOfSpeech.VERB,VerbForm.GERUNDIVE,tt, Aspect.IMPERFECTIVE,Mood.NONE);   
			 break;
		 case pastparticiple:
			 //System.err.println("pos=VERB\tvform=PARTICIPLE\tmood=NONE\n");
			 tt= TML_Tense.NONE;
			 
			 res = new TimeMLatt(PartOfSpeech.VERB,VerbForm.NONE,tt, Aspect.NONE, Mood.NONE);   
			 break;
		 case indicative:
			// System.err.println("pos=VERB\tvform=NONE\tmood=INDIVATIVE\n");
			 
			 //tt= TML_Tense.NONE;
			 
			 res = new TimeMLatt(PartOfSpeech.VERB,VerbForm.NONE,tt, Aspect.IMPERFECTIVE, Mood.INDICATIVE);   
			 break;
		 case subjunctive:
			 //System.err.println("pos=VERB\tvform=NONE\tmood=SUBJUNTIVE\n");
			 //tt= TML_Tense.NONE;
			 
			 res = new TimeMLatt(PartOfSpeech.VERB,VerbForm.NONE,tt, Aspect.IMPERFECTIVE, Mood.SUBJUNCTIVE);   
			 break;
		 }
		}
		 break;
		 
		 case 'n':
		 case 'N':
			 //System.err.println("pos=NOUN\tvform=NONE\ttense=NONE\taspect=NONE\tmood=NONE\n");
			 
			 res = new TimeMLatt(PartOfSpeech.NOUN,VerbForm.NONE, TML_Tense.NONE, Aspect.NONE, Mood.NONE);   
		 break;
			 
		 case 'a':
		 case 'A':	 
		 
			// System.err.println("pos=ADJECTIVE\tvform=NONE\ttense=NONE\taspect=NONE\tmood=NONE\n");
			 res = new TimeMLatt(PartOfSpeech.ADJECTIVE,VerbForm.NONE, TML_Tense.NONE, Aspect.NONE, Mood.NONE);   
			 break;
			 
		 case 'P':
			 //System.err.println("pos=PREPOSITION\tvform=NONE\ttense=NONE\taspect=NONE\tmood=NONE\n");
			 res = new TimeMLatt(PartOfSpeech.PREP,VerbForm.NONE, TML_Tense.NONE, Aspect.NONE,Mood.NONE);   
		      break;
		      
		 default :
			// System.err.println("pos=OTHER\tvform=NONE\ttense=NONE\taspect=NONE\tmood=NONE\n");
			 res = new TimeMLatt(PartOfSpeech.OTHER,VerbForm.NONE, TML_Tense.NONE, Aspect.NONE, Mood.NONE);   
		 }
		 
	
		 
		 
		 return res;
	 }
	 
}
