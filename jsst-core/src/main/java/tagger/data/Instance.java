package tagger.data;

/**
 *
 *  representation of an instance
 *  
 *  TODO: String means non encoded features representation
 *  
 * @author jordi
 *
 */
public interface Instance {

	/// @return the feature named att for the sequence
	public String[] get(String att);

	/// sets feature named att to sequence
	public void set(String att, String[] sequence);

}
