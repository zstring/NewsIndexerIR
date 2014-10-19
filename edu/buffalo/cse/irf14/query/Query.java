package edu.buffalo.cse.irf14.query;

import java.util.HashMap;
import java.util.Map;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.Posting;

/**
 * Class that represents a parsed query
 * @author nikhillo
 *
 */
public class Query {
	/**
	 * Method to convert given parsed query into string
	 */
	Expression queryTerm;
	public Query(Expression term) {
		this.queryTerm = term;
	}
	public String toString() {
		//TODO: YOU MUST IMPLEMENT THIS
		return queryTerm.toString();
	}
	
	public Map<String, Posting> execute(HashMap<IndexType, IndexReader> reader) {
		// TODO Auto-generated method stub
		Map<String, Posting> ret = queryTerm.interpret(reader);
//		for (String doc : ret.keySet()){
//			System.out.println(" DocName : " + doc + " " + ret.get(doc).toString());
//		}
		return ret;
	}

	public Map<Integer, Double> getVector(HashMap<IndexType, IndexReader> reader) {
		// TODO Auto-generated method stub
		Map<Integer, Double> map = queryTerm.getQVector(reader);
		return map;
	}

	private Map<Integer, Double> getUnitVector (Map<Integer, Double> map) {
		Double sum = 0.0;
		for (Integer termId : map.keySet()) {
			sum += Math.pow(map.get(termId), 2);
		}
		sum = Math.sqrt(sum);
		for (Integer termId : map.keySet()) {
			Double val = map.get(termId) / sum;
			map.put(termId, val);
		}
		return map;
	}
}
