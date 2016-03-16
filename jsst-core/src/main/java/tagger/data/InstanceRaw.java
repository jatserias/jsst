package tagger.data;

public class InstanceRaw implements Instance {
 String[] W;
 String[] L;
 String[] P;
 String[] S;
 
 public InstanceRaw(String[] W,String[] L,String[] P, String S[])  {
	 this.W=W;
	 this.L=L;
	 this.P=P;
	 this.S=S;
 }

@Override
public String[] get(String att) {
	if(att.compareTo("WORD")==0) return W;
	else if (att.compareTo("LEMMA")==0) return L;
	else if (att.compareTo("POS")==0) return P;
	else if (att.compareTo("SLABEL")==0) return S;
	throw new RuntimeException("Getting Unkown attribute:"+att);
}

@Override
public void set(String att, String[] T) {
	if(att.compareTo("WORD")==0) this.W=T;
	else if (att.compareTo("LEMMA")==0) this.L=T;
	else if (att.compareTo("POS")==0) this.P=T;
	else if (att.compareTo("SLABEL")==0) this.S=T;
	throw new RuntimeException("Seting Unkown attribute:"+att);
	
}
}
