package edu.buffalo.cse.irf14.query;

public class OrOperator implements Expression {
	private Expression leftOperand;
	private Expression rightOperand;
	
	public OrOperator(Expression left, Expression right) {
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
