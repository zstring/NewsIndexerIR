package edu.buffalo.cse.irf14.query;
import java.util.Map;
import edu.buffalo.cse.irf14.index.Posting;

public class OrOperator implements Expression {
	private Expression leftOperand;
	private Expression rightOperand;
	
	public OrOperator(Expression left, Expression right) {
		this.leftOperand = left;
		this.rightOperand = right;
	}

	@Override
	public Map<String, Posting> interpret(String s) {
		// TODO Auto-generated method stub
		return null;
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
}
