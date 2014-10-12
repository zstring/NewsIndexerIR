package edu.buffalo.cse.irf14.query;

import java.util.Map;

import edu.buffalo.cse.irf14.index.Posting;

public class AndOperator implements Expression {
	private Expression leftOperand;
	private Expression rightOperand;
	public AndOperator(Expression left, Expression right) {
		this.leftOperand = left;
		this.rightOperand = right;
	}

	@Override
	public Map<String, Posting> interpret()  {
		// TODO Auto-generated method stub
		Map<String, Posting> leftMap = leftOperand.interpret();
		Map<String, Posting> rightMap = rightOperand.interpret();
		leftMap.re
		
		return null;
	}

	@Override
	public String toSudoString() {
		// TODO Auto-generated method stub
		return leftOperand.toString() + " AND " + rightOperand.toString();
	}

	@Override
	public String toString() {
		return toSudoString();
	}

}
