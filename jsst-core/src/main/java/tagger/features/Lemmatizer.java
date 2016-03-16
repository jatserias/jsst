package tagger.features;

import java.util.List;


/*
 * An interface to provide lemmas
 */
public interface Lemmatizer {

	/// try to lemmatize without PoS
	public  String get_lemma(String wordFrom);
	
	/// Returns the lemma given the wordForm and its Part of Speech
	public  String get_lemma(String wordFrom,String pos);
	
	//@TODO put this aggregated functions somewhere else
	//public  List<String> getLemmasVector(final String[] L, final String[] P);
	
	
	//public static  String[] getLemmas(final String[] L, final String[] P) { return null;}
	
	
}
