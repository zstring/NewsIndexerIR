package edu.buffalo.cse.irf14.query;
import java.util.HashMap;
import java.util.Map;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.Posting;

public class QTerm extends QIndexType implements Expression {
	private String term;
	private IndexType index;
	public QTerm(String term, IndexType index) {
		this.term = term;
		this.index = index;
	}
	
	@Override
	public Map<String, Posting> interpret(HashMap<IndexType, IndexReader> reader) {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public String toSudoString() {
		// TODO Auto-generated method stub
		return index.toString() + ":" + term;
	}

	@Override
	public String toString() {
		return toSudoString();
	}
}
