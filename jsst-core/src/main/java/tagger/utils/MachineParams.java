package tagger.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;


public class MachineParams {
	static Logger logger = Logger.getLogger(MachineParams.class);
	Properties tempProp;
	MachineParams(String file) {
		
	try{	 
	 tempProp = new Properties();
	 tempProp.load(new FileInputStream(file));
	 } catch (IOException e) {
		 logger.error("Could not load parameter file <"+file+">");
	 }
	}
	
	String get(String key)  throws Exception{
		String res = (String) tempProp.get(key);
		if(res==null) throw new Exception("Error getting config propertie for "+key);
		return res;
	}
}
