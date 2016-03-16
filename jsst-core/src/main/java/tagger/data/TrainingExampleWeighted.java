package tagger.data;

import java.util.List;

/**
 * An example with an associated weight
 * 
 * @author jordi
 *
 */
public class TrainingExampleWeighted extends TrainingInstance {
	double weight;

	public TrainingExampleWeighted(String id, List<List<Integer>> pw,
			List<Integer> pt, double weight) {
		super(id, pw, pt);
		this.weight=weight;
	}

	
	
	@Override
	public double getWeight() {
		return weight;
	}
}
