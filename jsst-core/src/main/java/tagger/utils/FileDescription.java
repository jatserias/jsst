package tagger.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileDescription {
protected String encoding;
protected String path;
protected boolean compress;
protected boolean inJar;

/**
 * 
 * @TODO
 * Can we generate a File Description we can use files included in jars
 * is = FileUtils.class.getResourceAsStream(s);
 *    br = new BufferedReader(new InputStreamReader(is));
 * 
 * 
 * @param path
 * @param encoding
 * @param compress
 */
public FileDescription(String path, String encoding, boolean compress) {
	this.path=path;
	this.encoding=encoding;
	this.compress=compress;
}

public FileDescription(String path, String encoding, boolean compress, boolean inJar) {
	this.path=path;
	this.encoding=encoding;
	this.compress=compress;
	this.inJar=inJar;
}

public FileDescription(String path, String encoding) {
	this.path=path;
	this.encoding=encoding;
	this.compress=false;	
}

public String buildFilename() {
	String modelname = path;
   if(compress) modelname += Utils.GZIP_FILENAME_EXTENSION;
   return modelname;	

}

public String getName() {return path;}

public BufferedReader open() throws FileNotFoundException, IOException { 

	if(inJar) {
	   InputStream is = openStream(); //getClass().getResourceAsStream(buildFilename());
	  
	   return new BufferedReader(new InputStreamReader(is,encoding));
	}
	else
		return Utils.getBufferedReader(buildFilename(),encoding);
}

public InputStream openStream() throws IOException {
	InputStream is = null;
	try {
		is=getClass().getResourceAsStream(buildFilename()); 
	}
	catch(Exception e) {
		is=null;
		e.printStackTrace();
		throw new IOException("catch exception opening "+buildFilename()+" from inside jars");
	}
	if(is==null) {
		   throw new IOException("problems opening "+buildFilename()+" from inside jars");
	   }
	return is;
	
} 

}
