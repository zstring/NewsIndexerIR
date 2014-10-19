package edu.buffalo.cse.irf14.Scorer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.buffalo.cse.irf14.SearchRunner.ScoringModel;
import edu.buffalo.cse.irf14.index.Posting;

public class ScorerClass {

	public TreeMap<String, Double> rankResult (Map<String, Posting> unRankedResult,
			HashMap<String, HashMap<Integer, Double>> docVector, Map<Integer, Double> queryTermFreq,
			Map<Integer, Double> queryVector, double avgLen, ScoringModel model) {
		
		HashMap<String, Double> rankedResult = new HashMap<String, Double>();
		Set<Integer> termKeys = queryVector.keySet();
		
		if(ScoringModel.OKAPI.equals(model)) {
			double k1 = 1.2, k3 = 2, b = 0.75;
			Double termFreq = 0.0, idf = 0.0;
			double val = 0.0, score = 0.0;
			for (String docId : unRankedResult.keySet()) {
				val = 0.0;
				score = 0.0;
				Map<Integer, Double> docV = docVector.get(docId);
				Posting post = unRankedResult.get(docId);
				//Extra weight for having title term document
				int termTitleId = 1;
				if (post.getType()) {
					termTitleId = post.getTermId(); 
				}
				for (Integer termId : termKeys) {
					double titleWt = 1;
					if (termId == termTitleId) {
						titleWt = 4;
					}
					idf = queryVector.get(termId) == null ? 0 : queryVector.get(termId);
					termFreq = docV.get(termId) == null ? 0 : docV.get(termId) * titleWt;
					val = idf * ((k1 + 1) * termFreq) / (k1 * ((1 - b) + b * (docV.size() / avgLen)) + termFreq);
					if (termKeys.size() >= 10) val *= ((k3 + 1) * queryTermFreq.get(termId)) / (k3 + queryTermFreq.get(termId));
					score += val;
				}
				rankedResult.put(docId, score);
			}
		}
		else {			
			for (String docId : unRankedResult.keySet()) {
				Double sum = 0.0;
				Posting post = unRankedResult.get(docId);
				//Extra weight for having title term document
				int termTitleId = 1;
				if (post.getType()) {
					termTitleId = post.getTermId(); 
				}
				Map<Integer, Double> docV = docVector.get(docId);
				for (Integer termId : termKeys) {
					double titleWt = 1;
					if (termId == termTitleId) {
						titleWt = 4;
					}
					sum += docV.get(termId) == null ? 0 : docV.get(termId) * queryVector.get(termId) * titleWt;
				}
				rankedResult.put(docId, sum);
			}
		}
		TreeMap<String, Double> sortedRankedRes = 
				new TreeMap<String, Double>(new SortByValueComp(rankedResult));
		for (Integer termId : termKeys) {
			System.out.println("TermID: " + termId + " Weight: " + queryVector.get(termId));
		}
		sortedRankedRes.putAll(rankedResult);
		for (String doc : sortedRankedRes.keySet()) {
			double val = sortedRankedRes.get(doc);
			if (val > 1) {
				sortedRankedRes.put(doc, 1.0);
			}
		}
		return sortedRankedRes;
	}
	/*
	 * Comaparator for TreeMap
	 * Sorting the keys based on values
	 */
	public class SortByValueComp implements Comparator<String> {
		Map<String, Double> map;
		public SortByValueComp(Map<String, Double> map) {
			this.map = map;
		}
		@Override
		public int compare(String a, String b) {
			//Intentionally comparaing two String objects
			//to check weather they are same object or 
			//not if same then return 0 else compare the value 
			//of another string to first one
			Double aValue = map.get(a);
			Double bValue = map.get(b);
			int res = aValue.compareTo(bValue);
			return res != 0 ? -res : a.compareTo(b);
		}
	}
}
