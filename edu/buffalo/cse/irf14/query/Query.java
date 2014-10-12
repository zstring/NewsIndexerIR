package edu.buffalo.cse.irf14.query;

import java.util.HashMap;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;

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
	
	public void execute(HashMap<IndexType, IndexReader> reader) {
		// TODO Auto-generated method stub
		queryTerm.interpret(reader);
	}
}
