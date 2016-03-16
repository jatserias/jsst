package tagger.extra;

import tagger.extra.TimeML.Aspect;
import tagger.extra.TimeML.Mood;
import tagger.extra.TimeML.PartOfSpeech;
import tagger.extra.TimeML.TML_Tense;
import tagger.extra.TimeML.VerbForm;



public class TimeMLatt {



Enum[] res;

public TimeMLatt(PartOfSpeech  p, VerbForm  v, TML_Tense t, Aspect a ,Mood m) {
res = new Enum[Timemlfeat.values().length];
res[Timemlfeat.pos.ordinal()]=p;
res[Timemlfeat.vform.ordinal()]=v;
res[Timemlfeat.tense.ordinal()]=t;
res[Timemlfeat.aspect.ordinal()]=a;
res[Timemlfeat.mood.ordinal()]=m;
}

TimeMLatt(PartOfSpeech  p, VerbForm  v, TML_Tense t ,Mood m) {
res = new Enum[Timemlfeat.values().length];
res[Timemlfeat.pos.ordinal()]=p;
res[Timemlfeat.vform.ordinal()]=v;
res[Timemlfeat.tense.ordinal()]=t;
res[Timemlfeat.mood.ordinal()]=m;
}
public String toString(){
return toString('\t');
}

public String toString(char Sep){
StringBuffer sres = new StringBuffer();
for(Timemlfeat f :Timemlfeat.values()) {
	sres.append(f);
	sres.append('=');
	sres.append(res[f.ordinal()]);
	sres.append(Sep);
}
return sres.toString();
}
public Enum get(Timemlfeat att) {
	return res[att.ordinal()];
}

public static String diffSignature(TimeMLatt ev, TimeMLatt eev) {
	StringBuffer res = new StringBuffer();
	for(Timemlfeat f :Timemlfeat.values()) {
		if(ev.res[f.ordinal()]!=eev.res[f.ordinal()]) {
			res.append(f.name());
			res.append('[');
			res.append(ev.res[f.ordinal()].name());
			res.append("<>");
			res.append(eev.res[f.ordinal()].name());
			res.append(']');
		}
	}
	return res.toString();
}
}