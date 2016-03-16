package tagger.extra;

public class EventFeatures {

	public boolean isEvent;
	/// Event class 
	public tagger.extra.TimeML.EventClass eclass;
	public TimeMLatt att;
	public String word;
	public String trace;
	
	public EventFeatures(String trace,String word, TimeMLatt xx, boolean isEvent, tagger.extra.TimeML.EventClass eclass){
		this.trace=trace;
		this.att=xx;
		this.word=word;
		this.isEvent=isEvent;
		this.eclass=eclass;
	}
	
	public String toString() {
		return trace+"\t"+(isEvent ? "" : "NO") + "EVEN "+word+"\t"+att;
	}

	public boolean sameWord(EventFeatures ev) {		
//		String dword =ev.word.toLowerCase();
//		boolean res =dword.startsWith(word+"_") || dword.contains(" "+word+"_");
//		System.out.println(dword+" <-> "+word+" "+res);
		
		boolean res = (ev.word.compareToIgnoreCase(word)==0);
		//System.out.println(ev.word+" <-> "+word+" "+res);
		return res;
	}

	public static String diffSignature(EventFeatures ev, EventFeatures eev) {
		
		return TimeMLatt.diffSignature(ev.att,  eev.att);
	}
}
