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
		if (rightMap == null) {
			return leftMap;
		}
		
		if (rightOperand instanceof NotOperator) {
			if (leftMap == null) {
				return null;
			}
			leftMap.keySet().removeAll(rightMap.keySet());
		}
		else {
			if (leftMap == null) {
				return rightMap;
			}
			leftMap.keySet().retainAll(rightMap.keySet());	
		}
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
