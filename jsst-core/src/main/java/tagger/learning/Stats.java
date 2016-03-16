package tagger.learning;

//import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * 
 * computes some statistics
 * @author jordi
 *
 */
public class Stats {
	static Logger logger = Logger.getLogger(Stats.class);
	
	final static double log2(final double x) {
		return Math.log(x)/Math.log(2);
	}
	
	
	final static double sum_map(Map<String,Double> DATA){
	  double sum = 0;
	  for(Entry<String, Double> e : DATA.entrySet()) {
		  sum += (double)(e.getValue());
	  }
	  return sum;
	}

	// KL(P|Q) = sum_x P(x) log P(x)/Q(x)
	final static double KL(Map<String,Double> P,
			Map<String,Double> Q,
		 double NP,
		 double NQ,
		 double _W_,
		 double alpha){
	  double ans = 0;
	  if (NP == 0)
	    NP = sum_map(P);
	  if (NQ == 0)
	    NQ = sum_map(Q);
	  double denP = (double)(NP)+(_W_*alpha);
	  double denQ = (double)(NQ)+(_W_*alpha);
	  double p_unk = alpha/denP;
	  double q_unk = alpha/denQ;
	  double nsampledtypes = P.size();

	 
	  for (Entry<String,Double> e: P.entrySet()){
	    String x = e.getKey();
	    Double p_x = (e.getValue()+alpha)/denP;
	    Double q_x = q_unk;
	    Double d = Q.get(x); 
	    if (d != null )
	      q_x = (d+alpha)/denQ;
	    if (p_x != q_x)
	      ans += p_x*log2(p_x/q_x);
	  }

	 
	  for (Entry<String,Double> e: Q.entrySet()){
	    String x  = e.getKey();
	    Double d = P.get(x);
	    if (d==null){
	      nsampledtypes += 1;
	      Double q_x = (e.getValue()+alpha)/denQ; 
	      Double p_x = p_unk;
	      ans += p_x*log2(p_x/q_x);
	    }
	  }
	  ans += (_W_-nsampledtypes)*(p_unk*(log2(p_unk/q_unk)));
	  if (ans < 0)
	    ans = 0;
	  return ans;
	}

	////////////////////////////////////////////////////////////////////////
	// CHI(P,Q) = chi-square value between P and Q

	final static void make_O(
			Map<String,Double> P,
		    Map<String,Double> Q,
		    List<List<Double> > O){
	  //  log.info( + "\n\t\tmake_O(" + P_str + "," + Q_str + ")";
		//@JAB implementation
	  List<String> w_index = new Vector<String>();
	  Double smooth_val = 0.0;
	  
	  for (Entry<String,Double> e: P.entrySet()){
	    String p = e.getKey();
	    Double cp = (Double) e.getValue()+smooth_val, cq = smooth_val;
	    Double d = Q.get(p);
	    if (d==null)
	      cq = d;
	    //@JAB implementation
	    List<Double> row = new Vector<Double>();
	    row.add(cp);
	    row.add(cq);
	    O.add(row);
	    w_index.add(p);
	  }
	  
	
	  for (Entry<String,Double> e: Q.entrySet()){
	    String q = e.getKey();
	    Double cq = (Double) e.getValue()+smooth_val, cp = smooth_val;
	    Double d = P.get(q);
	    if (d==null){
	    	//@JAB implementation
	      List<Double> row= new Vector<Double>();
	      row.add(cp);
	      row.add(cq);
	      O.add(row);
	      w_index.add(q);
	    }
	  }
	
	  logger.info(  "\nW\n");
	  int _O_ = O.size();
	  for (int i = 0; i < _O_; ++i)
	    logger.info(  w_index.get(i) + "\t" + O.get(i).get(0) + "\t" + O.get(i).get(1));
	  //  getchar();
	
	}

	static void make_O_limit(Map<String,Double> P,
			  Map<String,Double> Q,
			  List<List<Double> > O,
			  Double minf){
	
	  logger.info("\n\t\tmake_O_limit(" + minf + ")");
	  //@JAB implementation
	  List<String> w_index = new Vector<String>();
	
	  
	  for (Entry<String,Double> e:P.entrySet()){
	    String p = e.getKey();
	    Double cp = e.getValue(), cq = 0.0;
	    Double d = Q.get(p);
	    if (d!=null)
	      cq = d;
	    if (cp >= minf && cq >= minf){
	    	//@JAB implementation
	      List<Double> row = new Vector<Double>();
	      row.add(cp);
	      row.add(cq);
	      O.add(row);
	
	      w_index.add(p);
	
	    }
	  }

	
	  logger.info( "\nO_limited\n");
	  int _O_ = O.size();
	  for (int i = 0; i < _O_; ++i)
	    logger.info( w_index.get(i) + "\t" + O.get(i).get(0) + "\t" + O.get(i).get(1));
	 
	
	}

	/**
	 * 
	 * @param O
	 * @param E
	 * @param v
	 * @param N not clear if we return the value
	 * @param ans_G2 not clear is we return the value
	 * @param deb
	 * @return
	 
	static double chi_from_table(Vector<Vector<Double> > O,
			     Vector<Vector<Double> > E,
			     int v,
			     Double N,
			     Double ans_G2,
			     boolean deb
			     )
	 */	
	
	static double chi_from_table(List<List<Double> > O,
		     List<List<Double> > E,	  
		     boolean deb
		     )
	 {
	  if (deb){
	    logger.info( "\nchi_from_table:" + "\nO:\n");
	  }
	  Double N = new Double(0);
	  double ans = 0;
	  double ans_G2 = new Double(0);
	//@JAB implementation
	  List<Double> Ei = new  Vector<Double>();
	  List<Double> Ej= new  Vector<Double>();
	  
	  int _i_ = O.size(), _j_ = O.get(0).size();
	  int v = (_i_-1)*(_j_-1);

	  for (int i = 0; i < _i_; ++i){
	    double sum_j = 0;
	  //@JAB implementation
	    List<Double> tmp = new  Vector<Double>();
	    for (int j = 0; j < _j_; ++j){
	      tmp.add(new Double(0));
	      sum_j += O.get(i).get(j);
	      N += O.get(i).get(j);
	    }
	    Ei.add(sum_j);
	    E.add(tmp);
	  }

	  for (int j = 0; j < _j_; ++j){
	    double sum_i = 0;
	    for (int i = 0; i < _i_; ++i)
	      sum_i += O.get(i).get(j);
	    Ej.add(sum_i);
	  }

	  for (int i = 0; i < _i_; ++i){
	    for (int j = 0; j < _j_; ++j){
	      E.get(i).set(j, (Ei.get(i)/N)*(Ej.get(j)/N)*N);
	      if (deb){
	    	  logger.info(  "\n" + O.get(i).get(0) + "\t" + O.get(i).get(1) + "\t" + Ei.get(i) + "\t" + Ej.get(j)
		     + "\nO_ij = " + O.get(i).get(j) + "\tE_ij = " + E.get(i).get(j));
	      }
	      
	      //      if (O[i][j] != E[i][j]){
	      double upd = Math.pow(O.get(i).get(j)-E.get(i).get(j),2)/E.get(i).get(j);
	      ans += upd;
	      if (O.get(i).get(j) > 0)
		ans_G2 += O.get(i).get(j)*Math.log(O.get(i).get(j)/E.get(i).get(j));
	      //      }
	    }
	  }
	  
	  ans_G2 *= 2;
	  if (deb){
	    logger.info( "\nv = " + v + "\nN = " + N + "\nE:\n");
	    for (int i = 0; i < _i_; ++i){
	      for (int j = 0; j < _j_; ++j)
		logger.info(  E.get(i).get(j) + "\t");
	      logger.info("\n");
	    }
	    logger.info(  "\nchi = " + ans + "\tG2 = " + ans_G2);
	  }
	  return ans;
	}

	static double CHI(Map<String,Double> P,Map<String,Double> Q,double minf){
	 //@JAB implementation
	  List<List<Double> > O = (List<List<Double> >)(List<?>)new Vector<Vector<Double> >();
	  List<List<Double> >  E= (List<List<Double> >)(List<?>)new Vector<Vector<Double> >();
	
	  if (minf == 0)
	    make_O(P,Q,O);
	  else 
	    make_O_limit(P,Q,O,minf);
	  
	  //@JAB we should change signature as can nto return _N or G2
	 //Double ans = chi_from_table(O,E,v,_N,G2,false);
	  Double ans = chi_from_table(O,E,false);
	  return ans;
	}

	//////////////////////////////////////////////////////////////////

	/**
	 * WARN: changing elemntAt to get
	 */
	static double variance(List<Double> X,double mu){
	  double ans = 0;
	  int _X_ = X.size();
	  for (int i = 0; i < _X_; ++i) {
		   double e = X.get(i)-mu;
		   ans += e * e;
	  }
	      //ans += (X.elementAt(i)-mu)*(X.elementAt(i)-mu);
	  ans = ans/ (double)(_X_-1);
	  return ans;
	}

	final public static double std_err(List<Double> X,Double mu){
	  return Math.sqrt(variance(X,mu));
	}

	/**
	 * WARN: changing elemntAt to get
	 * @param X
	 * @return
	 */
	public static double mean(List<Double> X){
	  double ans = 0;
	  int _X_ = X.size();
	  for (int i = 0; i < _X_; ++i)
	    ans += X.get(i); //X.elementAt(i);
	  ans = ans / (double) (_X_);
	  return ans;
	}

	/**
	 * ??
	 * @TODO @JAB check 
	 * @param X
	 * @param MAX
	 */
	static void Xmax(List<Double> X,List<Integer> MAX){
	  //int max_i = 0;
	  int _X_ = X.size();
	  double max_val = 0;
	  
	  for (int i = 0; i < _X_; ++i)
		  if (X.get(i) >= max_val){ //if (X.elementAt(i) >= max_val){
	     // max_i = i;
	      max_val = X.get(i);//X.elementAt(i);
	    }
	  
	  for (int i = 0; i < _X_; ++i)
		  if (X.get(i) == max_val)// if (X.elementAt(i) == max_val)
	      MAX.add(i);
	}



}
