package tagger.utils;

import java.util.*;


import tagger.features.Fs;
/**
 * 
 * ArrayList Double / Integer
 * 
 * @author jordi
 *
 * @param <T>
 */
/**
 * 
 * A class for testing different List implementations: ArrayList / Array 
 * 
 */
public class ClassChooser{
  
    
	/* USe Vector **/
	
	public static List<Double> createListDouble(int n) { return new Vector<Double>(n);}
	public static List<Integer> createListInteger(int n) { return new Vector<Integer>(n);}
	public static List<Long> createListLong(int n) { return new Vector<Long>(n);}
	public static List<Long> createListLong() { return new Vector<Long>();}
	
	public static List<String> createListString() {return new Vector<String>();}
	public static List<String> createListString(int n) {return new Vector<String>(n);}

	public static List<List<String>> createListListString() {
		return (List<List<String>>) (List<?>)  new Vector<Vector<String>>();
	}
	public static List<List<Integer>> createListListInteger() {
		return (List<List<Integer>>) (List<?>)  new Vector<Vector<Integer>>();
	}
	public static List<HashMap<Integer, Fs>> createHashMapIntegerFs() {
		return new Vector<HashMap<Integer,Fs> >();
	}

	public static List<HashSet<Integer>> ListHashSetInteger() {
		return new Vector<HashSet<Integer>>();
	}
	public static List<Integer> createListInteger() {
		return new Vector<Integer>();
	}
	public static List<List<List<Integer>>> createListListListInteger() {
		return (List<List<List<Integer>>>) (List<?>)  new Vector<Vector<Vector<Integer>>>();
	}
	
	/**
	public static List<Double> createListDouble(int n) { return new ArrayList<Double>(n);}
	public static List<Integer> createListInteger(int n) { return new ArrayList<Integer>(n);}
	public static List<Long> createListLong(int n) { return new ArrayList<Long>(n);}
	public static List<Long> createListLong() { return new ArrayList<Long>();}
	
	public static List<String> createListString() {return new ArrayList<String>();}
	public static List<String> createListString(int n) {return new ArrayList<String>(n);}

	public static List<List<String>> createListListString() {
		return (List<List<String>>) (List<?>)  new ArrayList<ArrayList<String>>();
	}
	public static List<List<Integer>> createListListInteger() {
		return (List<List<Integer>>) (List<?>)  new ArrayList<ArrayList<Integer>>();
	}
	public static List<HashMap<Integer, Fs>> createHashMapIntegerFs() {
		return new ArrayList<HashMap<Integer,Fs> >();
	}

	public static List<HashSet<Integer>> ListHashSetInteger() {
		return new ArrayList<HashSet<Integer>>();
	}
	public static List<Integer> createListInteger() {
		return new ArrayList<Integer>();
	}
	public static List<List<List<Integer>>> createVectorVectorVectorInteger() {
		return (List<List<List<Integer>>>) (List<?>)  new ArrayList<ArrayList<ArrayList<Integer>>>();
	}
	
	**/
	
	
}
