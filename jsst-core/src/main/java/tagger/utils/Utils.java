package tagger.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import tagger.features.Fs;

public class Utils {

	public static final String GZIP_FILENAME_EXTENSION = ".gz";
	
	//get a BufferedReader from a fly and gunzip it on the fly if needed
	public static BufferedReader getBufferedReader(String filename, String encoding) throws FileNotFoundException, IOException {
	if(filename.endsWith(GZIP_FILENAME_EXTENSION))
		return new BufferedReader(new InputStreamReader((InputStream) new GZIPInputStream( new FileInputStream(filename)),encoding));
	else 
		return new BufferedReader(new InputStreamReader((InputStream) new FileInputStream(filename),encoding));
	}
	
	
	 public static void arraydump(Writer wr, double[][] delta, String s) throws IOException {
		//dumping delta psi
		 for(int i=0;i<delta.length;++i)
		 	for(int j=0;j<delta[i].length;++j) 
		 		wr.append(s+"["+i+","+j+"]="+delta[i][j]+"\n");
	 }
	 
	 public static void arraydump(Writer wr,int[][] delta, String s) throws IOException {
			//dumping delta psi
			 for(int i=0;i<delta.length;++i)
			 	for(int j=0;j<delta[i].length;++j) 
			 		wr.append(s+"["+i+","+j+"]="+delta[i][j]+"\n");
		 }
	 
	 public static void arraydump(int[][] delta, String s,String out) {
	        try {
		    FileWriter fw = new FileWriter(out);	    
			//dumping delta psi
			 for(int i=0;i<delta.length;++i)
			 	for(int j=0;j<delta[i].length;++j) 
			 		 fw.write(s+"["+i+","+j+"]="+delta[i][j]+"\n");
			 fw.close();
	        }
	        catch(Exception e) {
	        	e.printStackTrace();
	        }
		 }
	 public static void arraydump(int[][][] delta, String s,String out) {
	        try {
		    FileWriter fw = new FileWriter(out);	    
			//dumping delta psi
			 for(int i=0;i<delta.length;++i)
			 	for(int j=0;j<delta[i].length;++j) 
			 		for(int k=0;k<delta[i][j].length;++k) 
			 		 fw.write(s+"["+i+","+j+","+k+"]="+delta[i][j][k]+"\n");
			 fw.close();
	        }
	        catch(Exception e) {
	        	e.printStackTrace();
	        }
		 }
	 
	 public static void arraydump(Vector<Vector< Vector<Integer> > > delta, String s,String out) {
	        try {
		    FileWriter fw = new FileWriter(out);	    
			//dumping delta psi
			 for(int i=0;i<delta.size();++i)
			 	for(int j=0;j<delta.get(i).size();++j) 
			 		for(int k=0;k<delta.get(i).get(j).size();++k) 
			 		 fw.write(s+"["+i+","+j+","+k+"]="+delta.get(i).get(j).get(k)+"\n");
			 fw.close();
	        }
	        catch(Exception e) {
	        	e.printStackTrace();
	        }
		 }
	 public static void arraydump(Writer wr, Fs[][] delta, String s) throws IOException {
			//dumping delta psi
			 for(int i=0;i<delta.length;++i)
			 	for(int j=0;j<delta[i].length;++j) 
			 		 wr.append(s+"["+i+","+j+"]="+delta[i][j]+"\n");
		 }
	 

		/**
		 * Remove incorrect unicode characters (substituted by 0x20)
		 *
		 *       Char        ::=        #x9 | #xA | #xD | [#x20-#xD7FF] | 
		[#xE000-#xFFFD] | [#x10000-#x10FFFF]     /*
		 *   (any Unicode character, excluding the surrogate blocks, FFFE, and 
		FFFF. )
		 *
		 * Those chars ranges are taken from 
		http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char
		 *
		 * @param s string to check/clean
		 * @return
		 */
	    public static String removeUTF8Invalid(String s) {

	        StringBuffer res = new StringBuffer();                
	        int codePoint;                                       
	        
	        for(int i=0;i<s.length();i = i + Character.charCount(codePoint)) {
	            codePoint = s.codePointAt(i);                       
	            if (((codePoint == 0x9) || 
	            		(codePoint == 0xA) ||   
	            		(codePoint == 0xD) ||
	                    ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
	                    ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
	                    ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)))) {
	            }
	            else {
	                //substitute invalid character with 0x20
	                codePoint=0x20;
	            }
	            res.append(Character.toChars(codePoint));        
	        }
	        return res.toString();
	    }
	    
	    /**
	     * Test for removing invalid UTf-8 characters
	     * 
	     * @param args
	     * @throws IOException
	     */
	    public static void main(String[] args) throws IOException {
	    	
	    	BufferedReader in = getBufferedReader(args[0],"ISO-8859-1");//"utf-8");
	    	PrintStream out = new PrintStream(args[1],"UTF-8");
	    	String buff;
	    	while((buff=in.readLine())!=null) {
	    		out.println(removeUTF8Invalid(buff));
	    	}
	    }
}
