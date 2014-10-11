package edu.buffalo.cse.irf14.query;

public class QTerm extends QIndexType implements Expression {
	private String term;
	private String index;
	public QTerm(String term, String index) {
		this.term = term;
		this.index = index;
	}
	
	@Override
	public int interpret(String s) {
		// TODO Auto-generated method stub
		return 0;
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
