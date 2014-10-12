package edu.buffalo.cse.irf14.query;

import java.util.Map;
import edu.buffalo.cse.irf14.index.Posting;

public class NotOperator implements Expression {
	private Expression operand;
	
	public NotOperator (Expression operand) {
		this.operand = operand;
	}

	@Override
	public Map<String, Posting> interpret(String s) {
		// TODO Auto-generated method stub
		return null;
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
