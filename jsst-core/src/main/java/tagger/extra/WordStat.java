package tagger.extra;

import java.util.Collection;

public class WordStat {
	 public static String SER="ser";
	 public static String ESTAR="estar";
	 public static String HABER="haber";
	 
	public  WordStat(String parole, String lemma) {
		   mpos=parole.charAt(0);
		   pos =  mpos=='v' && parole.length()>4 ? parole.substring(2,4) : "xx";
		   ser=(lemma.compareTo(SER)==0);
		   estar=(lemma.compareTo(ESTAR)==0);
		   haber=(lemma.compareTo(HABER)==0);
	 }
	 char mpos;
	 String pos;
	 boolean ser;
	 boolean estar;
	 boolean haber;
	 
	 public String toString() {
		 return pos+(haber ? "(HABER)" : (ser ? "(SER)" : (estar ? "(ESTAR)" : "")));
	 }
	 
	 public static void main(String[] args) {
		String[] tags={"vsp","vg0","np"};
		String[] lemmas={"haber", "cantado", "mucho"};
		String[] tokens={"haber", "cantado", "mucho"};
		 Collection<EventFeatures> res = TimeML.displayEvent("GOLD", tokens, tags, lemmas);
	 }
}
