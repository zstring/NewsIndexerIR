package edu.buffalo.cse.irf14.query;

import java.util.Map;

import edu.buffalo.cse.irf14.index.Posting;

public class Term implements Expression {
	private String name;
	public Term(String name) {
		this.name = new String(name);
	}

	@Override
	public Map<String, Posting> interpret() {
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
}
