package tagger.utils;

import java.util.ArrayList;
import java.util.List;

import tagger.features.Lemmatizer;

public class WordUtils {

	public static String[] getLemmas(String[] L, String[] P, Lemmatizer M) {
		
			int _L_ = L.length;
			String[] WM = new String[_L_];

			for (int i = 0; i < _L_; ++i){
				WM[i]=(M.get_lemma(L[i], P[i]));
			}
			return WM;
		}
		
	
	public static  List<String> getLemmasVector(String[] L, String[] P,Lemmatizer M) {
			int _L_ = L.length;
			List<String> WM = new ArrayList<String>(_L_);

			for (int i = 0; i < _L_; ++i){
				WM.add(M.get_lemma(L[i], P[i]));
			}
			return WM;
		}
	
	
	 //FAKE function @TODO change the callers
    public static String my_tolower(String s) {
    	return s.toLowerCase();
    }
		
}
