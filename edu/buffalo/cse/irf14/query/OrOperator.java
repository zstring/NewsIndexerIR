package edu.buffalo.cse.irf14.query;
import java.util.HashMap;
import java.util.Map;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.Posting;

public class OrOperator implements Expression {
	private Expression leftOperand;
	private Expression rightOperand;
	
	public OrOperator(Expression left, Expression right) {
		this.leftOperand = left;
		this.rightOperand = right;
	}

	@Override
	public Map<String, Posting> interpret(HashMap<IndexType, IndexReader> reader) {
		// TODO Auto-generated method stub
		Map<String, Posting> leftMap = this.leftOperand.interpret(reader);
		Map<String, Posting> rightMap = this.rightOperand.interpret(reader);
		leftMap.putAll(rightMap);
		return leftMap;
	}

	@Override
	public String toSudoString() {
		// TODO Auto-generated method stub
		return leftOperand.toString() + " OR " + rightOperand.toString();
	}

	@Override
	public String toString() {
		return toSudoString();
	}

	@Override
	public Map<Integer, Double> getQVector(
			HashMap<IndexType, IndexReader> reader) {
		// TODO Auto-generated method stub
		Map<Integer, Double> leftMap = leftOperand.getQVector(reader);
		Map<Integer, Double> rightMap = rightOperand.getQVector(reader);
		if (leftMap.isEmpty() || leftMap == null) {
			return rightMap;
		}
		if (rightMap.isEmpty() || rightMap == null) {
			return leftMap;
		}
		for (Integer termId : leftMap.keySet() ) {
			Double oldIdf = rightMap.get(termId);
			if ( oldIdf != null) {
				rightMap.put(termId,oldIdf + leftMap.get(termId));
			}
			else {
				rightMap.put(termId, leftMap.get(termId));
			}
		}
		return rightMap;
	}
}
