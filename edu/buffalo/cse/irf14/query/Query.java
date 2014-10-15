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
		for (String doc : ret.keySet()){
			System.out.println(" DocName : " + doc + " " + ret.get(doc).toString());
		}
		System.out.println("TOTAL COUNT " + ret.keySet().size());
		return ret;
	}

	public Map<Integer, Double> getVector(HashMap<IndexType, IndexReader> reader) {
		// TODO Auto-generated method stub
		return queryTerm.getQVector(reader);
	}
}
