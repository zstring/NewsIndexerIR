package edu.buffalo.cse.irf14.Scorer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.buffalo.cse.irf14.SearchRunner.ScoringModel;
import edu.buffalo.cse.irf14.index.Posting;

public class ScorerClass {

	public void rankResult (Map<String, Posting> unRankedResult,
			HashMap<String, HashMap<Integer, Double>> docVector,
			Map<Integer, Double> queryVector, ScoringModel model) {
		Set<Integer> termKeys = queryVector.keySet();
		for (String docId : unRankedResult.keySet()) {
			Double sum = 0.0;
			Map<Integer, Double> docV = docVector.get(docId);
			for (Integer termId : termKeys) {
				sum += docV.get(termId) * queryVector.get(termId);
			}
		}
	}
}
