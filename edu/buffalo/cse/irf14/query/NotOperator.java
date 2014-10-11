package edu.buffalo.cse.irf14.query;

public class NotOperator implements Expression {
	private Expression operand;
	
	public NotOperator (Expression operand) {
		this.operand = operand;
	}

	@Override
	public int interpret(String s) {
		// TODO Auto-generated method stub
		return 0;
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
