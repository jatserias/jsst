package tagger.extra;

import java.util.HashMap;

import tagger.extra.TagsetParole.tense;


public class CharMapEnum <T extends Enum & CTest>{
	 
	 T e;
	 HashMap<Character,T> map; 
	 
	public CharMapEnum(T e) {
		 this.e=e;	 
		 map = new HashMap<Character,T>();
		 for(T v:getEnumConstats()) {
			 map.put(v.getChar(),v);
		 }
		 
	 }
	 
	public  T[] getEnumConstats() { 			 
	   T[] x=(T[]) this.e.getClass().getEnumConstants();	 
	  return x;
	 }
	 
	public  T getValue(char c){
		 return map.get(c);
	 }
	
	public static void main(String args[]) {
		 CharMapEnum<tense> tensemap = new CharMapEnum(tense.none);
		 
		 System.err.println(tensemap.getValue('c'));
		 
	}
}