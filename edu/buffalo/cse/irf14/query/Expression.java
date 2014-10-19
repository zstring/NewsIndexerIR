package edu.buffalo.cse.irf14.query;

import java.util.HashMap;
import java.util.Map;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.Posting;

public interface Expression {

	/**
	 *
	 * @param reader 
	 * @param s
	 * @return
	 */
	public Map<String, Posting> interpret(HashMap<IndexType, IndexReader> reader);
	/**
	 *
	 * @return
	 */
	public String toSudoString();
	
	public Map<Integer, Double> getQVector(HashMap<IndexType, IndexReader> reader);
	public Map<Integer, Double> getQueryTermFreq(HashMap<IndexType, IndexReader> reader);
}
