package tagger.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

import tagger.utils.Utils;

/**
 * 
 * online Traininig Data with it a mixed of corpus with different weights
 * 
 * 
 * @TODO online only supports one line FORMATS  
 * 
 * @author jordi
 *
 */
public class TrainingDataSSTMixedonline extends  TrainingDataSSTonline{

	int positions[];
	String[] filenames;
	Double[] weights;
	Vector<Vector<Long>> mmap;
	RandomAccessFile mrf[];
	

	public TrainingDataSSTMixedonline(String configCorpusFile,TrainingFormatReader formatReader) throws IOException {
		super();
		
		// read the file list an weight form config file
		// per line corpusfile <tab> weight
		BufferedReader fin=Utils.getBufferedReader(configCorpusFile,"UTF-8");
		
	String input;
	Vector<String> files = new Vector<String>();
	Vector<Double> rweights = new Vector<Double>();
	while ((input=fin.readLine())!=null){
    try {
     String [] buff= input.split("[\t ]");
     String file = buff[0];
     Double weight = Double.parseDouble(buff[1]);
     files.add(file);
     rweights.add(weight);
    }catch(Exception e){
    	logger.error("Format error on line:"+input);
    	logger.error(e);
    	
    }
    }
    filenames=files.toArray(new String[0]);
    weights = rweights.toArray(new Double[0]);
		init(formatReader);
	}
	
	public TrainingDataSSTMixedonline(String[] filenames, Double[] weights,TrainingFormatReader formatReader) throws IOException {
		super();
		this.filenames=filenames;
		this.weights=weights;
		init(formatReader);
	}
	
	private void init(TrainingFormatReader formatReader) throws IOException {
		positions=new int[filenames.length];
		
		
		// fullfill map and length
		this.formatReader=formatReader;
		
		mmap= new Vector<Vector<Long>>(filenames.length);
		mrf = new RandomAccessFile[filenames.length];
		int nf=0;
		length=0;
		// create an index
		for(String filename:filenames) {
	    Vector<Long> tmap = new Vector<Long>();
		RandomAccessFile reader=  new RandomAccessFile(new File(filename),"r"); 
		long offset=0;
		tmap.add(offset);
		String line;
		
		//@TODO call a reader.readExample() to support more than line formats
		while((line = reader.readLine())!=null) {
			//@TO BUILD SAME FEATURES THAN INMEM METHOD 
			formatReader.readExample(line);
			offset=reader.getFilePointer();
			tmap.add(offset);
			++length;
		}
		logger.info("Corpus :"+filenames[nf]+" got examples "+length);
		
		mmap.add(tmap);
		positions[nf]=length;
		reader.close();
		
		//@TODO something different delaing with encoding using RandomAccess or InputStreamFile
		mrf[nf] = new RandomAccessFile(new File(filename),"r");
		++nf;
		
		}
		
		logger.info("Total number of examples:"+length);
	}
	
	public int whichFile(int iexemple){
		int i=0; 
		while(i<positions.length && iexemple>=positions[i]) ++i; 
		return i;
	}
	
	@Override
	public TrainingInstance getTrainingExample(int i) throws IOException {
		TrainingInstance e=null;
		int nf=whichFile(i);
		int ni;
		if(nf>=1) ni = i - positions[nf-1];
		else ni=i;
		try {		
	    	mrf[nf].seek(mmap.get(nf).get(ni));
		}catch(Exception xe){
			logger.error("Something got wrong accessing example: "+i+" pos:"+ni +" in file "+nf);
			logger.error(xe);		
			System.exit(-1);
		}
		try{
		String buff = mrf[nf].readLine();
		 e = formatReader.readExample(buff);
	    }catch(Exception x) {
	    	logger.error("Something got wrong reading example: "+i+" pos:"+ni +" in file "+nf);
	    	logger.error(x);		
			System.exit(-1);
	    }
	    
		return e;
	}

	@Override
	public int length() {
		return length;
	}
}
