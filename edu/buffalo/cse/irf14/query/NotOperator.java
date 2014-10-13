package edu.buffalo.cse.irf14.query;

import java.util.HashMap;
import java.util.Map;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.Posting;

public class NotOperator implements Expression {
	private Expression operand;
	
	public NotOperator (Expression operand) {
		this.operand = operand;
	}

	@Override
	public Map<String, Posting> interpret(HashMap<IndexType, IndexReader> reader) {
		// TODO Auto-generated method stub
		return operand.interpret(reader);
	}

	@Override
	public String toSudoString() {
		// TODO Auto-generated method stub
		return "<" + operand.toString() + ">";
	}

	@Override
	public String toString() {
		return toSudoString();
	}

}
