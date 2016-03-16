package tagger.data;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import tagger.utils.FileDescription;


public class RawInputFile {
	static Logger logger = Logger.getLogger(RawInputFile.class);
	
	  int i=0;
	  boolean eod=false;
	  boolean eof = false;
	  StringBuffer para;
	  BufferedReader reader;
	  
	  
	  public void close() throws IOException { reader.close();}
	  
      public RawInputFile(FileDescription file) throws Exception {
    	 //@JAB BFS reader=  Utils.getBufferedReader(file.path,file.encoding);  
    	 reader=  file.open();  
    	 constructor();
      }
      
      public RawInputFile(BufferedReader preader) throws Exception {
    	  reader=preader; constructor();
      }
      
      private void constructor() throws Exception {  	 
    	  String line = reader.readLine();
    	  if(line==null) throw new Exception("inputrow empty header");
    	  while (line!=null && !line.startsWith("%%#"))
				{line = reader.readLine();logger.info("Skip till %%# "+line);}
    	  while (line!=null && line.startsWith("%%#"))
				{line = reader.readLine();logger.info("Skip till !%%# "+line);}
    	  if(line==null) {eod=true;eof=true;}
    	  else {
    		  internalGetParagraph();
    	  }
      }
      
      public boolean eod() {return eod;}
      
      public boolean eof() {return eof;}
      
      private void internalGetParagraph() throws IOException {
    	  eod=false;
    	  para = new StringBuffer();
    	  String line=reader.readLine();
    	  logger.info("par asked at "+line);
    	  while (line!=null && line.startsWith("%%#")) line = reader.readLine();
    	  while (line!=null && !line.startsWith("%%#")) { para.append(line);line = reader.readLine();}
    	  while (line!=null && line.startsWith("%%#")) line = reader.readLine();
    	  if(line==null) {eod=true;eof=true;}
    	  else { eod= line.startsWith("%%#DOC");}
    	  
    	  status();
      }
      
      public void status() {
    	  logger.info("Par:"+para);
    	  logger.info("EOD:"+eod);
    	  logger.info("EOF:"+eof);
      }
      
	  public String getParagraph() throws IOException { 
		  String res=para.toString();
		  internalGetParagraph();
		  return res;
	  }

	public void nextDocument() {
		if(!eof) eod=false;
	}
	  
	
}
