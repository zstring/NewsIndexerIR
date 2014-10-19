/**
 * 
 */
package edu.buffalo.cse.irf14.query;
import java.util.HashMap;
import java.util.Map;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.Posting;

/**
 * @author avinav and himanshu
 *
 */
public class QBracket implements Expression {
	private Expression encapsulate;
	public QBracket(Expression encapsulate) {
		this.encapsulate = encapsulate;
	}

	@Override
	public Map<String, Posting> interpret(HashMap<IndexType, IndexReader> reader) {
		// TODO Auto-generated method stub
		return encapsulate.interpret(reader);
	}

	@Override
	public String toSudoString() {
		// TODO Auto-generated method stub
		return "[ " + encapsulate.toString() + " ]";
	}

	@Override
	public String toString() {
		return toSudoString();
	}

	@Override
	public Map<Integer, Double> getQVector(
			HashMap<IndexType, IndexReader> reader) {
		// TODO Auto-generated method stub
		return encapsulate.getQVector(reader);
	}

	@Override
	public Map<Integer, Double> getQueryTermFreq(
			HashMap<IndexType, IndexReader> reader) {
		// TODO Auto-generated method stub
		return encapsulate.getQueryTermFreq(reader);
	}
}
