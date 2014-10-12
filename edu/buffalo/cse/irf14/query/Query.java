package edu.buffalo.cse.irf14.query;

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
	
}
