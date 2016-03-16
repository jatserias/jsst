package tagger.data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import tagger.core.PS_HMM;
import tagger.core.PS_HMM_Sparse;
import tagger.features.FeatureDecoder;
import tagger.features.Fs;

/**
 * 
 * A reader for SST models
 * 
 * @author jordi
 *
 */
public class ModelFormatReader {
	
	static Logger logger = Logger.getLogger(ModelFormatReader.class);
	
	
	/**
	 * 
	 * @TODO why we need the tagset (it should be contained in the model)
	 * 
	 * 
	 * @param fin
	 * @return
	 * @throws IOException
	 */
	static public PS_HMM jabLoadModel(final ModelDescription m) throws IOException{
		 //@JAB jabLoad_XIS(modelname+".F");
		  logger.info(".");
		  PS_HMM p = new PS_HMM(m.tagset);
		  return jabLoadModel(p, m);
	}
	
	//TODO SPARSE ADAPT
	public static PS_HMM_Sparse jabLoadModel(PS_HMM_Sparse m,
			ModelDescription md) throws IOException {
		 //@JAB jabLoad_XIS(modelname+".F");
		  logger.info(".");
		 
		 
		  BufferedReader fin = md.open();
		  
		  //char strbuf[1000];
		  int _phis_, _phif_, _phib_, _phiw_;
		  
		  // @JAB Wondering why are we re-sizing everything (k = tagset.size()) 
		  //in >> k >> it >> _phis_ >> word_weight;  
		  // m.set_k(Integer.parseInt(fin.readLine()));
		  
		  ///Size in file must correspond to tagset size!
		  //assert(Integer.parseInt(fin.readLine())==m.k);
		  
		  fin.readLine();
		  
		  m.it= Integer.parseInt(fin.readLine());
		  _phis_ = Integer.parseInt(fin.readLine());
		  
		  m.word_weight = Double.parseDouble(fin.readLine().split("[\t]")[0]); 
		  
		  //in.getline(strbuf,1000);
		  m.phi_s = new ArrayList<Fs>(_phis_);
		  for (int i = 0; i < _phis_; ++i){
		    //Fs fs;
		    //in >> fs.alpha >> fs.avg_num >> fs.lst_upd >> fs.upd_val;
		    Fs fs = Fs.parseFs(fin.readLine());
		    m.phi_s.add(fs);
		  }
		  
		  
		  
		//  in >> _phif_;
		  _phif_ = Integer.parseInt(fin.readLine().split("[ \t]")[0]);
		  m.phi_f = new ArrayList<Fs>(_phif_);
		  
		//  in.getline(strbuf,1000);
		  logger.info(".");
		  for (int i = 0; i < _phif_; ++i){
		    //Fs fs;
		    //in >> fs.alpha >> fs.avg_num >> fs.lst_upd >> fs.upd_val;
			Fs fs = Fs.parseFs(fin.readLine());
		    m.phi_f.add(fs);
		  }
		  //in >> _phib_;
		  _phib_= Integer.parseInt(fin.readLine().split("[ \t]")[0]);
		  //in.getline(strbuf,1000);
		  logger.info(".");
		  String buff[];
		  int bp;
		  m.phi_b= new Fs[_phib_][];
		  for (int i = 0; i < _phif_; ++i){
		    int _i_;
		    //in >> _i_; 
		   
		    buff= fin.readLine().split("[\t ]");
		    bp=0;  
			  
		    _i_ =Integer.parseInt(buff[bp++]);
		    Fs[] Fi = new Fs[_i_];
		    for (int j = 0; j < _i_; ++j){
		      //Fs fs;
		      //in >> fs.alpha >> fs.avg_num >> fs.lst_upd >> fs.upd_val;
		      Fs fs = new Fs(Double.parseDouble(buff[bp++]), Double.parseDouble(buff[bp++]),Integer.parseInt(buff[bp++]),Double.parseDouble(buff[bp++]));
		  	
		      Fi[j]=fs;
		    }
		    m.phi_b[i]=Fi;
		  }
		  //in >> _phiw_;
		  _phiw_  = Integer.parseInt(fin.readLine().split("[ \t]")[0]);
		  //in.getline(strbuf,1000);
		  logger.info(".");
		
		  for (int i = 0; i < _phiw_; ++i){
		   
		    buff= fin.readLine().split("[\t ]");
		    bp=0;  
		  
		    int _i_ =Integer.parseInt(buff[bp++]);
		    HashMap<Integer,Fs> Fi= new HashMap<Integer,Fs>();
		    //while (in.peek() == 9){
		    while(bp<buff.length) {
		      Fs fs;
		      int fid;
		      //in >> fid >> fs.alpha >> fs.avg_num >> fs.lst_upd >> fs.upd_val;
		      fid=Integer.parseInt(buff[bp++]);
		      fs = new Fs(Double.parseDouble(buff[bp++]), Double.parseDouble(buff[bp++]),Integer.parseInt(buff[bp++]),Double.parseDouble(buff[bp++]));
		      Fi.put(fid,fs);
		    }
		    m.phi_w.add(Fi);
		  }
		  String check;
		  check=fin.readLine();
		  if (!check.startsWith("ENDOFMODEL")){
			  logger.error("\n\tcheck error:"+check);
		      throw new IOException("Format error on laoding model ");
		  }
		  else logger.info("\tOK");
		  
		return m;
		
	}
	
	static public PS_HMM jabLoadModel(PS_HMM m, final ModelDescription md) throws IOException{
		 //@JAB jabLoad_XIS(modelname+".F");
		  logger.info(".");
		 
		 
		  BufferedReader fin = md.open();
		  
		  //char strbuf[1000];
		  int _phis_, _phif_, _phib_, _phiw_;
		  
		  // @JAB Wondering why are we re-sizing everything (k = tagset.size()) 
		  //in >> k >> it >> _phis_ >> word_weight;  
		  // m.set_k(Integer.parseInt(fin.readLine()));
		  
		  ///Size in file must correspond to tagset size!
		  //assert(Integer.parseInt(fin.readLine())==m.k);
		  
		  fin.readLine();
		  
		  m.it= Integer.parseInt(fin.readLine());
		  _phis_ = Integer.parseInt(fin.readLine());
		  
		  m.word_weight = Double.parseDouble(fin.readLine().split("[\t]")[0]); 
		  
		  //in.getline(strbuf,1000);
		  m.phi_s = new ArrayList<Fs>(_phis_);
		  for (int i = 0; i < _phis_; ++i){
		    //Fs fs;
		    //in >> fs.alpha >> fs.avg_num >> fs.lst_upd >> fs.upd_val;
		    Fs fs = Fs.parseFs(fin.readLine());
		    m.phi_s.add(fs);
		  }
		  
		  
		  
		//  in >> _phif_;
		  _phif_ = Integer.parseInt(fin.readLine().split("[ \t]")[0]);
		  m.phi_f = new ArrayList<Fs>(_phif_);
		  
		//  in.getline(strbuf,1000);
		  logger.info(".");
		  for (int i = 0; i < _phif_; ++i){
		    //Fs fs;
		    //in >> fs.alpha >> fs.avg_num >> fs.lst_upd >> fs.upd_val;
			Fs fs = Fs.parseFs(fin.readLine());
		    m.phi_f.add(fs);
		  }
		  //in >> _phib_;
		  _phib_= Integer.parseInt(fin.readLine().split("[ \t]")[0]);
		  //in.getline(strbuf,1000);
		  logger.info(".");
		  String buff[];
		  int bp;
		  m.phi_b= new Fs[_phib_][];
		  for (int i = 0; i < _phif_; ++i){
		    int _i_;
		    //in >> _i_; 
		   
		    buff= fin.readLine().split("[\t ]");
		    bp=0;  
			  
		    _i_ =Integer.parseInt(buff[bp++]);
		    Fs[] Fi = new Fs[_i_];
		    for (int j = 0; j < _i_; ++j){
		      //Fs fs;
		      //in >> fs.alpha >> fs.avg_num >> fs.lst_upd >> fs.upd_val;
		      Fs fs = new Fs(Double.parseDouble(buff[bp++]), Double.parseDouble(buff[bp++]),Integer.parseInt(buff[bp++]),Double.parseDouble(buff[bp++]));
		  	
		      Fi[j]=fs;
		    }
		    m.phi_b[i]=Fi;
		  }
		  //in >> _phiw_;
		  _phiw_  = Integer.parseInt(fin.readLine().split("[ \t]")[0]);
		  //in.getline(strbuf,1000);
		  logger.info(".");
		
		  for (int i = 0; i < _phiw_; ++i){
		   
		    buff= fin.readLine().split("[\t ]");
		    bp=0;  
		  
		    int _i_ =Integer.parseInt(buff[bp++]);
		    HashMap<Integer,Fs> Fi= new HashMap<Integer,Fs>();
		    //while (in.peek() == 9){
		    while(bp<buff.length) {
		      Fs fs;
		      int fid;
		      //in >> fid >> fs.alpha >> fs.avg_num >> fs.lst_upd >> fs.upd_val;
		      fid=Integer.parseInt(buff[bp++]);
		      fs = new Fs(Double.parseDouble(buff[bp++]), Double.parseDouble(buff[bp++]),Integer.parseInt(buff[bp++]),Double.parseDouble(buff[bp++]));
		      Fi.put(fid,fs);
		    }
		    m.phi_w.add(Fi);
		  }
		  String check;
		  check=fin.readLine();
		  if (!check.startsWith("ENDOFMODEL")){
			  logger.error("\n\tcheck error:"+check);
		      throw new IOException("Format error on laoding model ");
		  }
		  else logger.info("\tOK");
		  
		return m;
		}
	
	
	
	/**
	 * 
	 * @TODO This is saving the Featuresshould be in Features?
	 * 
	 * @param fname
	 * @param fb
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	static  void print_XIS_XSI(String fname, final FeatureDecoder fdec) throws FileNotFoundException, UnsupportedEncodingException {
		 PrintStream out = new PrintStream(fname,PS_HMM.SAVEMODEL_ENCODING);	 
		  for (int i=0;i < fdec.size();++i)  
		   out.println(i +"\t"+ fdec.decode(i));
	     out.close();
		}
	
	/**
	 * Write the model
	 * @param modelname
	 * @param fdec
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void SaveModel(PS_HMM m,String modelname, FeatureDecoder fdec) throws FileNotFoundException, UnsupportedEncodingException {
		 logger.info( "\nSaveModel(" + modelname + ")");
		 // @JAB remove MODEL_PATH 10/2010
		 PrintStream  out = new PrintStream(modelname +PS_HMM.PS_HMM_MODEL_FILENAME_EXTENSION,PS_HMM.SAVEMODEL_ENCODING);
		 print_XIS_XSI(modelname+PS_HMM.FEATURE_MODEL_FILENAME_EXTENSION,fdec);
		 
		 // k is the size of the tagset (UNK should not be considered !?)
		  out.println( m.k  + "\n" + m.it +"\n" + m.phi_s.size() + "\n" + m.word_weight  + "\tphi_s\talpha\tavg_num\tlst_upd\tupd_val");
		  for (Fs f: m.phi_s)
		    out.println( f.alpha + " " + f.avg_num + " " + f.lst_upd + " " + f.upd_val);
		  out.println( m.phi_f.size() + "\tphi_f");
		  for (Fs f: m.phi_f)
		    out.println( f.alpha + " " + f.avg_num + " " + f.lst_upd+ " " + f.upd_val);
		  out.println( m.phi_b.length + "\t" + "\tphi_b");
		  for (int i = 0; i < m.phi_b.length; ++i){
		    int _i_ = m.phi_b[i].length;
		    out.print(_i_);
		    for (int j = 0; j < _i_; ++j)
		      out.print( "\t" + m.phi_b[i][j].alpha + " " + m.phi_b[i][j].avg_num+ " " + m.phi_b[i][j].lst_upd + " " + m.phi_b[i][j].upd_val);
		    out.println();
		  }
		  out.println( m.phi_w.size() + "\tphi_w");
		  for (int i = 0; i < m.phi_w.size(); ++i){
		    out.print( i );
		    for (Integer e : m.phi_w.get(i).keySet()) {
		    	Fs fs =  m.phi_w.get(i).get(e);
		    	 out.print( "\t" + e + " " + fs.alpha + " " + fs.avg_num   + " " + fs.lst_upd + " " + fs.upd_val);
		    	}
		    out.println();
		  }
		  out.println( "ENDOFMODEL");
		  out.close();
		}

	


	public static void SaveModel(PS_HMM_Sparse m,String modelname, FeatureDecoder fb) throws FileNotFoundException, UnsupportedEncodingException {
		 logger.info( "\nSaveModel(" + modelname + ")");
		 // @JAB remove MODEL_PATH 10/2010
		 PrintStream  out = new PrintStream(modelname +PS_HMM_Sparse.PS_HMM_MODEL_FILENAME_EXTENSION,PS_HMM_Sparse.SAVEMODEL_ENCODING);
		 print_XIS_XSI(modelname+PS_HMM_Sparse.FEATURE_MODEL_FILENAME_EXTENSION,fb);
		 
		 // k is the size of the tagset (UNK should not be considered !?)
		  out.println( m.k  + "\n" + m.it +"\n" + m.phi_s.size() + "\n" + m.word_weight  + "\tphi_s\talpha\tavg_num\tlst_upd\tupd_val");
		  for (Fs f: m.phi_s)
		    out.println( f.alpha + " " + f.avg_num + " " + f.lst_upd + " " + f.upd_val);
		  out.println( m.phi_f.size() + "\tphi_f");
		  for (Fs f: m.phi_f)
		    out.println( f.alpha + " " + f.avg_num + " " + f.lst_upd+ " " + f.upd_val);
		  out.println( m.phi_b.length + "\t" + "\tphi_b");
		  for (int i = 0; i < m.phi_b.length; ++i){
		    int _i_ = m.phi_b[i].length;
		    out.print(_i_);
		    for (int j = 0; j < _i_; ++j)
		      out.print( "\t" + m.phi_b[i][j].alpha + " " + m.phi_b[i][j].avg_num+ " " + m.phi_b[i][j].lst_upd + " " + m.phi_b[i][j].upd_val);
		    out.println();
		  }
		  out.println( m.phi_w.size() + "\tphi_w");
		  for (int i = 0; i < m.phi_w.size(); ++i){
		    out.print( i );
		    for (Integer e : m.phi_w.get(i).keySet()) {
		    	Fs fs =  m.phi_w.get(i).get(e);
		    	 out.print( "\t" + e + " " + fs.alpha + " " + fs.avg_num   + " " + fs.lst_upd + " " + fs.upd_val);
		    	}
		    out.println();
		  }
		  out.println( "ENDOFMODEL");
		  out.close();
		}
	

		
	 
}
