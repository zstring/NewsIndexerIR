package edu.buffalo.cse.irf14.query;

public class TestExpressionParser {
	public static void main(String args[]) {
		ExpressionParser expParser = new ExpressionParser();
		
		try {
			expParser.expressionParser("author: rushdie not \"jihad hello\"", "OR");
			System.out.println(expParser.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
