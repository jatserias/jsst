package tagger.extra;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;



/**
 * A class to map Catalan parole tags to attributes (for CONLL format)
 * 
 * http://www.lsi.upc.edu/~nlp/tools/parole-sp.html
 * 
 * @author jordi
 *
 */
public class TagsetParole {
	HashMap<features,String> shorts;
	
	public HashMap<pos, features[]> mapfeatures;
	final static int posLength=2;
	
	public TagsetParole() {
		 mapfeatures= new HashMap<pos, features[]>();
		 shorts = new HashMap<features,String>();
		 shorts.put(features.gender,"gen");
		 shorts.put(features.person,"per");
		 shorts.put(features.mod,"mod");
		 shorts.put(features.number,"num");
		 shorts.put(features.tense,"ten");
		 shorts.put(features.spfor,"for");
		 shorts.put(features.pos,"pos");
		 shorts.put(features.cas,"cas");
		 shorts.put(features.pol,"pol");
		 shorts.put(features.function,"fun");
		 
		 HashMap<features,Class<? extends Enum>>  maptypes= new HashMap<features,Class<? extends Enum> >();
		 maptypes.put(features.gender, gender.class);
		 maptypes.get(features.gender).getEnumConstants();
}
	

	
	public enum tense implements CTest { none('0'), 
		conditional('c'), //c, //conditional, 
		future('f'), // f, //future 
		imperfect('i'), //imperfect, i, //imperfect, 
		past('s'), //s, //past
		present('p') //p //present 
		;
		 char id;
		 tense(char id){
		this.id=id;
	}
		 
		@Override
		public Character getChar() {
			return id;
		}
	}
	
	
	
	enum mod implements CTest {
			
		none('0'),
		gerund('g'), //" => "mod=g",
	    imperative('m'), // " => "mod=m",
	    indicative('i'), // , // => "mod=i", 
	    infinitive('n'), // => "mod=n",
	    pastparticiple('p'), //  => "mod=p",
	    subjunctive('s')// => "mod=s",
	    ;
		
		
	    char id;
	    mod(char id){
			this.id=id;
		}
	    
	    @Override
		public Character getChar() {
			return id;
		}
	}
	
	
	
			
	enum cas implements CTest {
		nominative('n'),//" => "cas=n",
		oblique('o'),//" => "cas=o",
		accusative('a'),//" =>  "cas=a",
		dative('d')//" => "cas=d",
		;
		
		 char id;
		 cas(char id){
				this.id=id;
			}
		
		 @Override
			public Character getChar() {
				return id;
			} 
	}
	 
	 public enum number implements CTest{ 
		 none('0'), singular('s'), plural('p'), common('0')
	 ;
	 char id;
	 number(char id){
			this.id=id;
		}
	
	 @Override
		public Character getChar() {
			return id;
		} 
	 
	 @Override
	   public String toString() {
		 return this.getClass().getName()+"."+super.toString();
	  }
	 }
	 
	 public enum gender implements CTest{ none('0'), masculine('m'), femenine('f'), neutral('n'), common('c')	 
		 ;
	 char id;
	 gender(char id){
			this.id=id;
		}
	
	 @Override
		public Character getChar() {
			return id;
		} 
	 }
	 public enum person implements CTest{ none('0'), first('1'), second('2'), third('3')
		 ;
	 ;
	 char id;
	 person(char id){
			this.id=id;
		}
	
	 @Override
		public Character getChar() {
			return id;
		} }
	 
	 public enum none implements CTest{ none('0')
		
	 ;
	 char id;
	 none(char id){
			this.id=id;
		}
	
	 @Override
		public Character getChar() {
			return id;
		} 
	 
	 @Override
	   public String toString() {
		 return this.getClass().getName()+"."+super.toString();
	  }
	 }
	 /**
	  * 
	  * features in a parole label
	  * 
	  * @author jordi
	  *
	  */
	 public enum features { 
	 	 none(TagsetParole.none.class), 
		 number(TagsetParole.number.class), 
		 pol(TagsetParole.none.class), 
		 cas(TagsetParole.cas.class), 
		 person(TagsetParole.person.class),  
		 pos(TagsetParole.none.class), 
		 gender(TagsetParole.gender.class),   
		 mod(TagsetParole.mod.class),
		 tense(TagsetParole.tense.class) 
		 ,spfor(TagsetParole.none.class) 
		 ,function(TagsetParole.none.class)
		 
		 ;
		 
		 Class<? extends CTest> c;
		 CharMapEnum map;
		 
	   features(Class<? extends CTest> c) {
		 this.c=c;	 
		 map= new CharMapEnum((Enum) c.getEnumConstants()[0]);
	    }  
	   
	    Enum getName(char c) {
	    	//System.err.println("look up:"+c+" in "+map.getClass());
	    	//Map.Entry<Character,CTest> e : m.entrySet() ) {
//	    	Set<Entry<Character,CTest>> k = map.map.entrySet();
//	    	for(Entry<Character,CTest> v: k) {
//	    		System.err.println(v.getKey()+" -> "+v.getValue());
//	    	}
//	    	System.err.println();
	    	return map.getValue(c);
	    }
	 }
	
	 

	 
	 /**
	  * two digit pos
	  * 
	  * @author jordi
	  *
	  */
	 public enum pos {
		 ao,
		 aq,
		 cc,
		 cs,
		 da,
		 dd,
		 de,
		 di,
		 dn,
		 dp,
		 dr,
		 dt,
		 fa,
		 fc,
		 fd,
		 fe,
		 fg,
		 fh,
		 fi,
		 fp,
		 fs,
		 fl,
		 fx,
		 ft,
		 fz,
		 nc,
		 np,
		 p0,
		 pd,
		 pi,
		 pn,
		 pp,
		 pr,
		 pt,
		 px,
		 rg,
		 rn,
		 sp,
		 va,
		 vm,
		 vs,
		 zm,
		 zp,
		 zu,
		 i, //?
		 w, //?
		 z, //?
	 }
	 
	 public static String mainPos(String tag) {
		 if(tag.charAt(0)=='.') return pos.z.name();
		 return tag.length()>posLength ? tag.substring(0,posLength): tag;
	 }
	 
	 /**
	  * This use to map parole to CONLL features
	  * @param utag
	  * @return
	  */
	 public String tag2Features(String utag) {
		 StringBuffer res = new StringBuffer();
		 String tag = utag.toLowerCase();
		 try {
		 pos fpos = pos.valueOf( mainPos(tag));
		 features[] posfeat= mapfeatures.get(fpos);
		 
		          
		if(posfeat!=null) {
		 char[] mf= new char[features.values().length];
		 for(int i=0;i<posfeat.length;++i) {
			 features f = posfeat[i];
			 if(f!=null //old features.none 
					 && tag.charAt(posLength+i)!='0') {
				mf[f.ordinal()] = tag.charAt(posLength+i);
			 }
		 }
		 
		 for(features fa: features.values()) {
			 char c = mf[fa.ordinal()];
			 if(c!='\u0000') {
			  if(res.length()>0) res.append("|");
			   res.append(shorts.get(fa)+"="+c);
			 }
		 }
		}
		} catch(Exception e) {
			e.printStackTrace();
		}
		 
		 if(res.length()==0) {return "_";}
		 return res.toString();
	 }
	 
	 /**
	  * loads a map form a file
	  * @param dname
	  */
	 void loadMap(BufferedReader fin) {
			try {String buff;
				while((buff=fin.readLine())!=null) {	
					String[] fields= buff.split("\t");
					
					int fl =  fields.length-posLength;
					if(fl<0) fl=0;
					
					features featpos[] = new features[fl];
					for(int i=posLength;i<fields.length;++i)  {
						featpos[i-posLength]=features.valueOf(fields[i]);
					}
					
					mapfeatures.put(pos.valueOf(fields[0]), featpos);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}		
	 }
	 
	 public void loadMap(String dname) {
		 try {
			BufferedReader fin = new BufferedReader(new FileReader(dname));
			loadMap(fin);
		 } catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	 }
	 
	 void loadMap(InputStream in) {		 
			BufferedReader fin = new BufferedReader(new InputStreamReader(in));
		 loadMap(fin);
	 }
	 
	 
	 TagParole createTag(String tag) {
		 return new TagParole(tag,this);
	 }
	 
	 public class TagParole {
		 
		 char[] mf;
		 
		 TagParole(String tag, TagsetParole tagset) {
		 String ipos= tag.toLowerCase();
		
		 TagsetParole.pos fpos = pos.valueOf( mainPos(ipos));
		 features[] posfeat= tagset.mapfeatures.get(fpos);
		 if(posfeat!=null) {
		   mf= new char[features.values().length];
		  for(int i=0;i<posfeat.length;++i) {
			 features f = posfeat[i];
			 if(f!=null && ipos.length()>posLength+i) {
				// System.err.println("Setting "+f+" mf["+f.ordinal()+"] to "+ipos.charAt(posLength+i));
				mf[f.ordinal()] = ipos.charAt(posLength+i);
			 }
		  }
		 }
		 } 
		 
		 Enum get(features f) {
			// System.err.println("Accesing char at "+f.ordinal()+" "+mf[f.ordinal()]);
			 return f.getName(mf[f.ordinal()]);
		 }
	 }
	 /**
	  * parole mapping ASPECT
	  * 
	  * habría estado cantando: VAIC3S0 NCMS000/VAP00SM VMG0000
	  * habría cantado :  VAIC3S0 VMP00SM
	  * estaría cantado: VAIC3S0 VMG0000
	  * cantaría: VMIC3S0
	  * 
	  * @param args
	  * @throws IOException
	  */
	 public static void main(String [] args) throws IOException {
		 
	/**
	 * 
	 * buld a map from PAROLE-ANCORA to Event TIMEML attributes
	 * 
	 * mood=conditional
	 * 
	 */
		 
		 
		 TagsetParole  tagset = new TagsetParole();
		 tagset.loadMap("src/test/resources/parole.txt");
		 String[] tags = {"VMP00SM", "NCMS000"};
		 for(String tag : tags) {
		 System.err.println("PAROLE:"+tag); 
		 TagParole t = tagset.createTag(tag);
		 
		 switch(tag.charAt(0)){
		 
		 case 'v':
		 case 'V':
			 
		 switch((tense) t.get(features.tense)) {
		 case conditional:
			 System.err.println("pos=VERB\tvform=NONE\tmood=CONDITIONAL\n");
		 
		 }
		 
		 switch((mod) t.get(features.mod)) {
		 case imperative:
			 System.err.println("pos=VERB\tvform=NONE\tmood=IMPERATIVE\n");
			 break;
		 case infinitive:
			 System.err.println("pos=VERB\tvform=NONE\tmood=NONE\n");
			 break;
		 case gerund :
			 System.err.println("pos=VERB\tvform=GERUNDIVE\tmood=NONE\n");
			 break;
		 case pastparticiple:
			 System.err.println("pos=VERB\tvform=PARTICIPLE\tmood=NONE\n");
			 break;
		 case indicative:
			 System.err.println("pos=VERB\tvform=NONE\tmood=INDIVATIVE\n");
			 break;
		 case subjunctive:
			 System.err.println("pos=VERB\tvform=NONE\tmood=SUBJUNTIVE\n");
			 break;
		 }
		 
		 break;
		 
		 case 'n':
		 case 'N':
			 System.err.println("pos=NOUN\tvform=NONE\ttense=NONE\taspect=NONE\tmood=NONE\n");
		 
		 break;
			 
		 case 'a':
		 case 'A':	 
		 
			 System.err.println("pos=ADJECTIVE\tvform=NONE\ttense=NONE\taspect=NONE\tmood=NONE\n");
			 
			 break;
			 
		 case 'P':
			 System.err.println("pos=PREPOSITION\tvform=NONE\ttense=NONE\taspect=NONE\tmood=NONE\n");
			 
		      break;
		      
		 default :
			 System.err.println("pos=OTHER\tvform=NONE\ttense=NONE\taspect=NONE\tmood=NONE\n");
			 
		 }
		 
		 
		
//		 System.err.println(t.get(features.tense));
//		 System.err.println(t.get(features.mod));
//		 System.err.println(t.get(features.person));
//		 System.err.println(t.get(features.number));
		 System.err.println("DONE");
		 
		 }
		 // CONLL extrange upper lower case conversion (fix)
		 // if(pos.charAt(0)=='f' || pos.length()==1) {pos = fields[2].charAt(0)+pos.substring(1,pos.length());}
//		 TagsetParole.pos fpos = pos.valueOf( mainPos(ipos));
//		 features[] posfeat= tagset.mapfeatures.get(fpos);
//		 if(posfeat!=null) {
//			 char[] mf= new char[features.values().length];
//			 for(int i=0;i<posfeat.length;++i) {
//				 features f = posfeat[i];
//				 if(f!=null //features.none
//						 && ipos.charAt(posLength+i)!='0') {
//					mf[f.ordinal()] = ipos.charAt(posLength+i);
//				 }
//				 
//			 }
//			 // Need to check if feature is available
//			// System.err.println(hmod.get(""+mf[features.mod.ordinal()]));
//		 }
		 
	 }
	 
	 /**
	  * 
	  * reads a multitag file and converts it to CONLL format
	  * uses the map in file  parole.txt
	  * 
	  * @param args 0: input file (stdin if ommited)
	  * @param args 1: output file (stdout if ommited)
	  * @throws IOException
	  */
	 public static void oldmain(String [] args) throws IOException {
		 TagsetParole  tagset = new TagsetParole();
		 tagset.loadMap("src/main/resources/parole.txt");
		 String buff;
		 InputStreamReader infile;
		 PrintStream outfile;
		 
		 if(args.length >0) infile =new InputStreamReader( new FileInputStream(args[0]));
		 else infile=new InputStreamReader(System.in);
		 
		 if(args.length >1) outfile =new PrintStream(args[1]);
		 else outfile=new PrintStream(System.out);
		 
		 int ntoken=1;
		 BufferedReader fin = new BufferedReader(infile);
		 while((buff=fin.readLine())!=null) {	
			 if(buff.startsWith("%%#")) { 
				 if(buff.charAt(3)=='S') {outfile.println(); ntoken=1;}
				 outfile.println(buff);
			 }
			 else {
			 //split with tabular second  field is POS
			 String fields[] = buff.split("[ \t]");
			 String pos = fields[2].toLowerCase();
			 // CONLL extrange upper lower case conversion (fix)
			 if(pos.charAt(0)=='f' || pos.length()==1) {pos = fields[2].charAt(0)+pos.substring(1,pos.length());}
			 
			 outfile.print(ntoken+"\t"+fields[0]+"\t"+fields[1]+"\t"+pos.charAt(0)+"\t"+mainPos(pos)+"\t"+tagset.tag2Features(pos));
			 outfile.println();
			 ntoken++;
			 }
		 }
		 outfile.close();
	 }

//	public static String getFeature(features f, String ipos) {
//		 if(f!=features.none && ipos.charAt(posLength+f.ordinal())!='0') { 
//			 ipos.charAt(f.ordinal()); 
//		 }
//		 return null;
//		 
//	}
}
