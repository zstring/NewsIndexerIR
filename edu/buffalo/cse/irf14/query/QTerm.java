package edu.buffalo.cse.irf14.query;
import java.util.Map;
import edu.buffalo.cse.irf14.index.Posting;

public class QTerm extends QIndexType implements Expression {
	private String term;
	private String index;
	public QTerm(String term, String index) {
		this.term = term;
		this.index = index;
	}
	
	@Override
	public Map<String, Posting> interpret() {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public String toSudoString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return toSudoString();
	}
}
