/**
 * 
 */
package edu.buffalo.cse.irf14.query;

/**
 * @author nikhillo
 * Static parser that converts raw text to Query objects
 */
public class QueryParser {
	/**
	 * MEthod to parse the given user query into a Query object
	 * @param userQuery : The query to parse
	 * @param defaultOperator : The default operator to use, one amongst (AND|OR)
	 * @return Query object if successfully parsed, null otherwise
	 */
	public static Query parse(String userQuery, String defaultOperator) throws QueryParserException{
		//TODO: YOU MUST IMPLEMENT THIS METHOD
		ExpressionParser expressionParser = new ExpressionParser();
		expressionParser.expressionParser(userQuery, defaultOperator);
		Query query = new Query(expressionParser.getExpression());
		return query;
	}
}
