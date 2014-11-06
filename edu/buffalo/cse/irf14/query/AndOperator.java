package edu.buffalo.cse.irf14.query;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

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
			//Updating the Title Term Type for 
			// Query Weight
			Set<String> keys = new HashSet<String>(rightMap.keySet());
			keys.addAll(new HashSet<String>(leftMap.keySet()));
			for (String docId : keys) {
				Posting postRight = rightMap.get(docId);
				Posting postLeft = leftMap.get(docId);
				if (postRight == null || postLeft == null) {
					leftMap.remove(docId);
				}
				else if (postRight.getType()) {
						leftMap.put(docId, postRight);
				}
			}
//			leftMap.keySet().retainAll(rightMap.keySet());	
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
		if (leftMap == null) {
			return rightMap;
		}
		if (rightMap == null) {
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

	@Override
	public Map<Integer, Double> getQueryTermFreq(
			HashMap<IndexType, IndexReader> reader) {
		// TODO Auto-generated method stub
		Map<Integer, Double> leftMap = leftOperand.getQueryTermFreq(reader);
		Map<Integer, Double> rightMap = rightOperand.getQueryTermFreq(reader);
		if (leftMap == null) {
			return rightMap;
		}
		if (rightMap == null) {
			return leftMap;
		}
		for (Integer termId : leftMap.keySet() ) {
			Double oldtermFreq = rightMap.get(termId);
			if ( oldtermFreq != null ) {
				rightMap.put(termId, oldtermFreq + leftMap.get(termId));
			}
			else {
				rightMap.put(termId, leftMap.get(termId));
			}
		}
		return rightMap;
	}

	@Override
	public Expression expandWildCard(HashMap<IndexType, SortedMap<String, Integer>> hm,
			HashMap<IndexType, SortedMap<String, Integer>> hmRev,
			Map<String, List<String>> expandResult) {
		// TODO Auto-generated method stub
		this.leftOperand = this.leftOperand.expandWildCard(hm, hmRev, expandResult);
		this.rightOperand = this.rightOperand.expandWildCard(hm, hmRev, expandResult);
		return this;
		
	}

}
