package edu.buffalo.cse.irf14.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.Posting;

public class Term implements Expression {
	private String name;
	public Term(String name) {
		this.name = new String(name);
	}

	@Override
	public Map<String, Posting> interpret(HashMap<IndexType, IndexReader> reader) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toSudoString() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public String toString() {
		return toSudoString();
	}

	@Override
	public Map<Integer, Double> getQVector(
			HashMap<IndexType, IndexReader> reader) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Double> getQueryTermFreq(
			HashMap<IndexType, IndexReader> reader) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression expandWildCard(HashMap<IndexType, SortedMap<String, Integer>> hm,
			HashMap<IndexType, SortedMap<String, Integer>> hmRev,
			Map<String, List<String>> expandResult) {
		// TODO Auto-generated method stub
		return null;
		
	}
	
}
