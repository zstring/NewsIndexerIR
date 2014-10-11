package edu.buffalo.cse.irf14.query;

public class TestExpressionParser {
	public static void main(String args[]) {
		ExpressionParser expParser = new ExpressionParser();
		
		try {
			Expression expression = expParser.expressionParser("Category:war and author:dutt not place:baghdad and prisoners detainees rebels", "OR");
			System.out.println(expression.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
