package edu.buffalo.cse.irf14.query;

public class AndOperator implements Expression {
	private Expression leftOperand;
	private Expression rightOperand;
	public AndOperator(Expression left, Expression right) {
		this.leftOperand = left;
		this.rightOperand = right;
	}

	@Override
	public int interpret(String s) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toSudoString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return toSudoString();
	}

}
