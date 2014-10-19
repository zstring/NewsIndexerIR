/**
 * 
 */
package edu.buffalo.cse.irf14.query;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.Posting;

/**
 * @author avinav and himanshu
 *
 */
public class ExpressionParser implements Expression {
	private Expression expression;
	Matcher matcherIndex = Pattern.compile("(Author|Category|Term|Place)(:)",
			Pattern.CASE_INSENSITIVE).matcher("");
	Matcher matcherSpChar = Pattern.compile("(\\()|(\\))|(\")").matcher("");
	Matcher matcherOp = Pattern.compile("AND|OR|NOT").matcher("");
	Matcher mat = Pattern.compile("author|category|place|term",Pattern.CASE_INSENSITIVE).matcher("");
	
	public Expression getExpression() {
		return expression;
	}
	
	public void expressionParser(String userQuery, String defaultOperator) throws QueryParserException {
		if (userQuery == null || userQuery.isEmpty()) {
			throw new QueryParserException("Query Null");
		}
		
		/* Preprocessing: 1. Replacing ":" from "Index:" to " "
		 * 2. Adding white space between "\"", "(" and ")"  
		 * 3. Add brackets in case of two or more repetitive terms
		 * 
		 */
		matcherIndex.reset(userQuery);
		StringBuffer sb = new StringBuffer();
		while(matcherIndex.find()) {
			matcherIndex.appendReplacement(sb, matcherIndex.group(1) + " ");
		}
		matcherIndex.appendTail(sb);
		userQuery = sb.toString();

		matcherSpChar.reset(userQuery);
		StringBuffer sb1 = new StringBuffer();
		while(matcherSpChar.find()) {
			matcherSpChar.appendReplacement(sb1, " " + matcherSpChar.group(0) + " ");
		}
		matcherSpChar.appendTail(sb1);
		userQuery = sb1.toString();
		
		Tokenizer tkr = new Tokenizer(" ");
		TokenStream tokenStream = null;
		try {
			tokenStream = tkr.consume(userQuery);
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (tokenStream == null) {
			throw new QueryParserException("Query Parser Exception: tokenStream empty");
		}
	
		int in = 0;
		//{notTerm, Term, Term}
		boolean[] openBrackPatt = {true, false, false};
		boolean isTerm = false, nonTermExist = false;
		int[] openBrackPos = {0, 0, 0};
		while (tokenStream.hasNext()) {
			String tokenString = tokenStream.next().toString();
			if (mat.reset(tokenString).matches()) {
				if (tokenStream.hasNext()) tokenString = tokenStream.next().toString();
				else break;
			}
			in = tokenStream.getCurrentIndex();
			isTerm = isTerm(tokenString, tokenStream, true);
			if(!isTerm) nonTermExist = true;
			if (openBrackPatt[0]) {
				if (openBrackPatt[1]) {
					if(openBrackPatt[2] = isTerm) {
						openBrackPos[2] = in;
						if (!isTerm || nonTermExist) {
							Token tk = new Token();
							tk.setTermText("(");
							tokenStream.insertAt(openBrackPos[1], tk);
							tokenStream.next();
							openBrackPatt[0] = false; openBrackPatt[1] = false;
							openBrackPatt[2] = false;
						}
					}
					else {
						openBrackPatt[0] = true; openBrackPatt[1] = false;
					}
				}
				else {
					if (openBrackPatt[1] = isTerm) {
						openBrackPos[1] = in;
					}
					else { 
						openBrackPatt[0] = true; 
					}
				}
			}
			else {
				openBrackPatt[0] = !isTerm;
				openBrackPos[0] = in;
			}
		}
		tokenStream.next();
		// Term,Term,NonTerm
		openBrackPatt[0] = true;openBrackPatt[1] = false; openBrackPatt[2] = false;
		isTerm = false; nonTermExist = false;
		openBrackPos[0] = tokenStream.getCurrentIndex(); openBrackPos[1] = 0; openBrackPos[2] = 0;
		while (tokenStream.hasPrevious()) {
			String tokenString = tokenStream.previous().toString();
			if (mat.reset(tokenString).matches()) {
				if (tokenStream.hasPrevious()) tokenString = tokenStream.previous().toString();
				else break;
			}
			in = tokenStream.getCurrentIndex();
			isTerm = isTerm(tokenString, tokenStream ,false);
			if(!isTerm) nonTermExist = true;
			if (openBrackPatt[0]) {
				if (openBrackPatt[1]) {
					if(openBrackPatt[2] = isTerm) {
						openBrackPos[2] = in;
						if (!isTerm || nonTermExist) {
							Token tk = new Token();
							tk.setTermText(")");
							tokenStream.insertAt(openBrackPos[1], tk);
							tokenStream.next();
							openBrackPatt[0] = false; openBrackPatt[1] = false;
							openBrackPatt[2] = false;
						}
					}
					else {
						openBrackPatt[0] = true; openBrackPatt[1] = false;
					}
				}
				else {
					if (openBrackPatt[1] = isTerm) {
						openBrackPos[1] = in;
					}
					else { 
						openBrackPatt[0] = true; 
					}
				}
			}
			else {
				openBrackPatt[0] = !isTerm;
				openBrackPos[0] = in;
			}
		}
		tokenStream.reset();
		
		// Preprocessing End
		 
		
		Stack<Expression> operator = new Stack<Expression>();
		Stack<Expression> operand = new Stack<Expression>();
		boolean dQuotesOn = false,dQuotesOff = false, defaultIndex = true, 
				superDefaultIndex = false, isDefaultOperator = false, notOperator = false;
		//String index = "";
		
		
		IndexType index = IndexType.TERM;
		StringBuilder quotedString = new StringBuilder("\"");
		while (tokenStream.hasNext()) {
			String token = tokenStream.next().toString();
			if (defaultIndex) {
				index = IndexType.TERM;
			}
			if ("\"".equals(token)) {
				dQuotesOn = !dQuotesOn;
				if (dQuotesOff) {
					Expression qTerm = new QTerm(quotedString.toString().trim()+"\"", index);
					if(notOperator) {
						operand.push(new NotOperator(qTerm));
						notOperator = false;
					}
					else {
						operand.push(qTerm);
					}
					quotedString = new StringBuilder("\"");
					if(!superDefaultIndex) {
						defaultIndex = true;
					}
					if (isDefaultOperator) {
						operator.push(new Term("OR"));
					}
					isDefaultOperator = true;
				}
				dQuotesOff = !dQuotesOff;
			}
			else if(dQuotesOn) {
				quotedString.append(token+" ");
			}
			else if ("AUTHOR".equalsIgnoreCase(token)){
				index = IndexType.AUTHOR;
				defaultIndex = false;
				isDefaultOperator = false;
			}
			else if ("TERM".equalsIgnoreCase(token)) {
				index = IndexType.TERM;
				defaultIndex = false;
				isDefaultOperator = false;
			}
			else if ("CATEGORY".equalsIgnoreCase(token)) {
				index = IndexType.CATEGORY;
				defaultIndex = false;
				isDefaultOperator = false;
			}
			else if ("PLACE".equalsIgnoreCase(token)) {
				index = IndexType.PLACE;
				defaultIndex = false;
				isDefaultOperator = false;
			}
			else if(")".equals(token)) {
				operator.push(new Term(token));
				ObjectifyBracket(operator, operand);
				superDefaultIndex = false;
				defaultIndex = true;
				
				isDefaultOperator = false;
			}
			else if ("AND".equals(token) || "OR".equals(token)||
					"NOT".equals(token) || "(".equals(token)) {
				if ("(".equals(token)) {
					superDefaultIndex = true;
				}
				if("NOT".equals(token)) {
					operator.push(new Term("AND"));
					notOperator = true;
				}
				else {
					Expression term = new Term(token);
					operator.push(term);
				}
				isDefaultOperator = false;
			}
			else {
				Expression qterm = new QTerm(token,index);
				
				if(notOperator) {
					operand.push(new NotOperator(qterm));
					notOperator = false;
				}
				else {
					operand.push(qterm);
				}
				/* if defaultIndex is true, default index, "term" is used
				 * superDefaultIndex is true when it sees and (
				 * defaultIndex should not be made true
				 */
				if(!superDefaultIndex) {
					defaultIndex = true;
				}
				// if two continuous terms occur isDefaultOperator index is true
				
				if (isDefaultOperator) {
					operator.push(new Term("OR"));
				}
				isDefaultOperator = true;
			}
		}
		ObjectifyEnd(operator, operand);
		this.expression = operand.pop();
	}
	
	public void ObjectifyBracket(Stack<Expression> operator,Stack<Expression> operand) throws QueryParserException {
		// Removing the ) that was pushed for calling this method
		if (operator.isEmpty()){
			throw new QueryParserException("Operator stack underflow!");
		}
		Expression popped = operator.pop();
		/* It can only encounter AND,NOT,OR or ( 
		 * It cannot encounter ) because it is the first ) itself.
		 */
		while (true) {
			if (operator.isEmpty()) throw new QueryParserException("Operator stack underflow!");  
			popped = operator.pop();
			Objectify(operator, operand, popped);
			if ("(".equals(popped.toString())) {
				break;
			}
		}
	}
	
	public void ObjectifyEnd(Stack<Expression> operator,Stack<Expression> operand) throws QueryParserException {
//		if (operator.isEmpty()) {
//			throw new Exception("Operator Stack underflow while ending");
//		}
		Expression popped;
		while (!operator.isEmpty()) {
			popped = operator.pop();
			Objectify(operator, operand, popped);
		}
	}
	/* It can only encounter AND,NOT,OR or ( 
	 * It cannot encounter ) because it is the first ) itself.
	 */
	public void Objectify(Stack<Expression> operator,Stack<Expression> operand, Expression popped) throws QueryParserException {
		String poppedText = popped.toString();
		if ("AND".equals(poppedText)) {
			if (operand.size() >= 2) {
				Expression rightOperand = operand.pop();
				Expression leftOperand = operand.pop();
				Expression andOp = new AndOperator(leftOperand,rightOperand);
				operand.push(andOp);
			}
			else {
				throw new QueryParserException("Operand Stack Underflow for AND operator");
			}
		}
		else if ("OR".equals(poppedText)) {
			if (operand.size() >=2) {
				Expression rightOperand = operand.pop();
				Expression leftOperand = operand.pop();
				Expression orOp = new OrOperator(leftOperand,rightOperand);
				operand.push(orOp);
			}
			else {
				throw new QueryParserException("Operand Stack Underflow for OR operator");
			}
		}
		else if("NOT".equals(poppedText)) {
			if (operand.size() >= 1) {
				Expression op = operand.pop();
				Expression notOp = new NotOperator(op);
				operand.push(notOp);
			}
			else {
				throw new QueryParserException("Operand Stack Underflow for NOT operator");
			}
		}
		else if("(".equals(poppedText)) {
			if (operand.size() >= 1) {
				Expression encaps = operand.pop();
				Expression opBrack = new QBracket(encaps);
				operand.push(opBrack);
			}
			else {
				throw new QueryParserException("Operand Stack Underflow for Brackets");
			}
		}
	}
	
	public boolean isTerm(String term, TokenStream tokenStream, boolean next) {
		if (term == null) {
			return false;
		}
		matcherOp.reset(term);
		if (matcherOp.matches() || "(".equals(term) || ")".equals(term)) {
			return false;
		}
		if("\"".equals(term)) {
			if (next) {
				while (tokenStream.hasNext()) {
					term = tokenStream.next().toString();
					if ("\"".equals(term)) {
						return true;
					}
				}
			}
			else {
				while (tokenStream.hasPrevious()) {
					term = tokenStream.previous().toString();
					if ("\"".equals(term)) {
						return true;
					}
				}
			}
		}
		return true;
	}

	@Override
	public Map<String, Posting> interpret(HashMap<IndexType, IndexReader> reader) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toSudoString() {
		// TODO Auto-generated method stub
		return "{ " + expression.toString() + " }";
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

}
