package tagger.learning;

import java.io.PrintStream;

public class Verbose_res {
	 public String L;
     public int nobjects, nanswers, nfullycorrect;
     public double R, P, F;
     public	 Verbose_res() {
    	 L = "";
    	 nobjects=0;nanswers=0;
    	 nfullycorrect=0;
    	 R=0;P=0;F=0;
     }
     public	 Verbose_res(String _L,int _no,int _na) {
    	 L=_L;
    	 nobjects=_no;nanswers=_na;nfullycorrect=0;
    	 R=0;P=0;F=0;
     }
     
	
	public void dump(PrintStream cout) {
		cout.println("L="+L);
	}
}
