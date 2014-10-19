package edu.buffalo.cse.irf14.query.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.buffalo.cse.irf14.query.Expression;
import edu.buffalo.cse.irf14.query.ExpressionParser;

public class ExpressionParserTest {
	private ExpressionParser expParser;
	private String[] input = {"hello", "hello world", "\"hello world\"", "orange AND yellow",
			"(black OR blue) AND bruises", "Author:rushdie NOT jihad", 
			"Category:War AND Author:Dutt AND Place:Baghdad AND prisoners detainees rebels",
			"(Love NOT War) AND Category:(movies NOT crime)" ,"author: rushdie NOT \"jihad hello\""};
	private String[] output = {"{ Term:hello }",
			"{ Term:hello OR Term:world }",
			"{ Term:\"hello world\" }",
			"{ Term:orange AND Term:yellow }",
			"{ [ Term:black OR Term:blue ] AND Term:bruises }",
			"{ Author:rushdie AND <Term:jihad> }",
			"{ Category:War AND Author:Dutt AND Place:Baghdad AND [ Term:prisoners OR Term:detainees OR Term:rebels ] }",
			"{ [ Term:Love AND <Term:War> ] AND [ Category:movies AND <Category:crime> ] }",
			"{ Author:rushdie AND <Term:\"jihad hello\"> }"};
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testMain() {
		expParser = new ExpressionParser();
		try {
			for (int i = 0; i < input.length; i++) {
				expParser.expressionParser(input[i], "OR");
				assertEquals( output[i],expParser.toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("failed");
		}
	}

}
