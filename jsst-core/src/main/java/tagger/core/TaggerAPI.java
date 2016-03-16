package tagger.core;

public interface TaggerAPI {

	public String[]  tagSequence(final String[] vs,final String [] fvpos, final String [] fvlemma);
    
}
