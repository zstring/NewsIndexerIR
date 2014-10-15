package edu.buffalo.cse.irf14.Scorer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.buffalo.cse.irf14.SearchRunner.ScoringModel;
import edu.buffalo.cse.irf14.index.Posting;

public class ScorerClass {

	public HashMap<String, Double> rankResult (Map<String, Posting> unRankedResult,
			HashMap<String, HashMap<Integer, Double>> docVector,
			Map<Integer, Double> queryVector, ScoringModel model) {
		HashMap<String, Double> rankedResult = new HashMap<String, Double>();
		Set<Integer> termKeys = queryVector.keySet();
		for (String docId : unRankedResult.keySet()) {
			Double sum = 0.0;
			Map<Integer, Double> docV = docVector.get(docId);
			for (Integer termId : termKeys) {
				sum += docV.get(termId) == null ? 0 : docV.get(termId) * queryVector.get(termId);
			}
			rankedResult.put(docId, sum);
		}
		return rankedResult;
	}

}
