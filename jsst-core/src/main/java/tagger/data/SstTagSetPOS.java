package tagger.data;

import tagger.core.SttTagger.tagmode;
import tagger.utils.FileDescription;

/**
 * A Tagset for PoS
 *  
 * @author jordi
 *
 */
public class SstTagSetPOS extends SstTagSet {
	
	public SstTagSetPOS(FileDescription tagset, boolean addUnk) throws Exception {
	    super();
		init(tagset,addUnk);
		mode=tagmode.POS;
	}

	public SstTagSetPOS(FileDescription tagset) throws Exception {
		super();
		init(tagset,false);
		mode=tagmode.POS;
	}
}
