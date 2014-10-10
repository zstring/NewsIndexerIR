package edu.buffalo.cse.irf14.query;

public class Term implements Expression {
	private String name;
	public Term(String name) {
		this.name = new String(name);
	}

	@Override
	public int interpret(String s) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

}
