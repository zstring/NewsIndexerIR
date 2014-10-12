package edu.buffalo.cse.irf14.query;

import java.util.HashMap;
import java.util.Map;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.Posting;

public class AndOperator implements Expression {
	private Expression leftOperand;
	private Expression rightOperand;
	public AndOperator(Expression left, Expression right) {
		this.leftOperand = left;
		this.rightOperand = right;
	}

	@Override
	public Map<String, Posting> interpret(HashMap<IndexType, IndexReader> reader)  {
		// TODO Auto-generated method stub
		Map<String, Posting> leftMap = leftOperand.interpret(reader);
		Map<String, Posting> rightMap = rightOperand.interpret(reader);
		leftMap.keySet().retainAll(rightMap.keySet());
		return leftMap;
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
