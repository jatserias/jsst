package tagger.features;

import java.util.Random;

/*
 * 
 * 
 */
public class Fs {
	//@TODO jordi find a better solution to setup the random generator	
	public static Random generator = new Random();
	
	/// Weight associated with F
	public	  double alpha;     
	/// Numerator of the average F-value                       
	public	  double avg_num;   
	/// Iteration number of last update                       
	public	  int lst_upd;     
	///
	public	  double upd_val;

	public	 double avg_val( int it, boolean use_avg ){
		if (!use_avg)
		     return alpha;
		  return  (avg_num+(alpha*(it-lst_upd)))/(double)it;
	}
	public	  void update( int it, boolean doupdate ) {
		if (upd_val != 0 && doupdate){
		    avg_num += alpha*(it-lst_upd);
		    lst_upd = it;
		    alpha += upd_val;
		  }
		  upd_val = 0;		
	}
	public	 void equate( Fs c ) {
		alpha = c.alpha;
	 avg_num = c.avg_num;
	 lst_upd = c.lst_upd;
	 upd_val = c.upd_val;}
	
	public	  void rand_alpha() { 
	  //double r = rand()/double(RAND_MAX), p = rand()/double(RAND_MAX);
		double r = (double) generator.nextDouble(); //@AJAB ??generator.nextGaussian();
		double p = (double) generator.nextDouble();
		if (p > 0.5)
		    alpha = r;
		  else alpha = r*-1;
   }

	public static Fs parseFs(String readLine) {
		String buff [] = readLine.split("[ \t]");
		try {
		Fs res =  new Fs(Double.parseDouble(buff[0]),Double.parseDouble(buff[1]),Integer.parseInt(buff[2]),Double.parseDouble(buff[3]));
		return res;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Error parsing FS input:"+buff);
			throw new RuntimeException("Error parsing FS input:"+buff);
		}
		
	}
	
	
	public Fs() { alpha= 0; avg_num=0; lst_upd=0;upd_val=0;}
	
	public Fs(double alpha,
			double avg_num, 
			 int lst_upd,
			double upd_val) {
		this.alpha = alpha;
		this.avg_num=avg_num;
		this.lst_upd=lst_upd;
		this.upd_val=upd_val;
	}
	
	
	public String toString(){
		return "alpha "+String.format("%.2f",alpha)+" avg "+String.format("%.2f",avg_num)+" lst "+lst_upd+" upd "+String.format("%.2f",upd_val);
	}
	
	 
	};