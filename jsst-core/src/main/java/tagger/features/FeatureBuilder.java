package tagger.features;



import java.io.IOException;
import java.util.List;

import tagger.data.Instance;
/**
 * An API for feature builders
 * @TODO solve the [] vector duality on checkConsistency 
 * @TODO split encoding and extraction of features
 * @TODO split extract features as the may need / or not word/lemma etc
 * 
 * @TODO extract and encode should be two different functions?
 * @TODO have a more general frame passing a TagDocument to extract features
 * 
 * @author jordi
 *
 */
public interface FeatureBuilder {
 
/// Calculate String Features from a List of Word/ Pos / Lemmas (not all information neede)
 public void  extractFeatures(final Instance instance, FeatureVector[] fv);

public void setUseLemma(boolean useLemaFeature);

public int tagfile(String inputFile, String outputFile, String encoding,
		String encoding2, boolean hasPoS, boolean hasLemma, boolean hasSlabel) throws IOException;
 
}
