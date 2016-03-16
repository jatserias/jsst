package tagger.utils;

import it.unimi.dsi.io.FastBufferedReader;
import it.unimi.dsi.io.LineIterator;
import it.unimi.dsi.lang.MutableString;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;



public class CreateGazzetterFromList {

	
	/**
	 * reads a plain list of turn int a GAZ file (word_word... LABEL_#tokens 
	 * 
	 * PERFIRST
	 * PERLAST
	 * CTRYs_AND_TRTRYs
	 * 
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void main (String[] args) throws UnsupportedEncodingException, FileNotFoundException {
		String input[]= {"perfirst","perlast","locations","organizations"};
		String label[]= {"PERFIRST", "PERLAST", "CTRYs_AND_TRTRYs", "ORG"};
		
		for(int f=0;f<input.length;++f) {
			PrintStream out = new PrintStream("de."+input[f]+".txt"); 
		LineIterator lineIterator = new LineIterator( new FastBufferedReader( new InputStreamReader( new FileInputStream( "/Users/jordi/Desktop/NEGerman/GAZ/"+input[f]+".de.txt" ),"UTF-8")));
		while(lineIterator.hasNext()) {
			MutableString ms = lineIterator.next();
			int ipos = ms.indexOf(' ')+1;
			
			// count the number of tokens
			// Should use the same tokenizer than on the text
			int i= ms.indexOf(' ',ipos);
			
			int nt=1;
			while(i>=0) {
				//System.err.print("Char>"+ms.charAt(i));
				ms = ms.charAt(i, '_');
				//System.err.println("< changed to "+ms.charAt(i));
				i= ms.indexOf(' ',i+1);
				++nt;
			}
					
			ms.append('\t');
			ms.append(label[f]);
			ms.append('_');
			ms.append(nt);
			out.println(ms.substring(ipos).toString());
				
			
		}
		
		out.close();
		}
	}
}
