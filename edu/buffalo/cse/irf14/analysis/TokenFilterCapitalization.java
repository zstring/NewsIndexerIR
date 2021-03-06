/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author avinav sharan and himanshu sharma
 *
 */
public class TokenFilterCapitalization extends TokenFilter {
//	private static Pattern pattCap;
	private static Matcher matCap;
	static {
//		pattCap = Pattern.compile(".*\\w*(\\.|\\?)$");
		matCap = Pattern.compile(".*\\w*(\\.|\\?)$").matcher("");
	}

	public TokenFilterCapitalization(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * @see edu.buffalo.cse.irf14.analysis.Analyzer#increment()
	 * Rule 1(for capital words (isCap): If AVINAV, maintain only if other words in sentence are not capital.
	 * Rule 2(for Camel Case words(first letter words) (isCamel)): If Avinav, Sharan then maintain only if it is not the first word of the sentence.
	 * Rule 3(for capital letter at inappropriate places(isSpecialCap)): If iOS, BTech, sofTech, sofTEch, 
	 * SofTEch appears any where in the sentence maintain it.
	 * ##only one of the isCap/isSpecialCap/isCamel can be true at a time.###
	 * 
	 * 0. Take token String in characters (because of the special capital thing, 
	 *    toUpper and charAt(0) could have been used for other two cases.)
	 * 1. If first letter is not in lower, isCap = true.
	 * 2. If first letter is capital -> isCamel = True, isCap = True
	 * 3. If any other letter is capital -> isSpecialCap = True
	 * 4. If any letter is NOT capital -> isCap = False
	 * 5. So: IF isCap = true : rest are to be considered false,
	 *        IF isSpecialCap = true : isCamel to be considered false.(using else if)
	 * 
	 */

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		//Added condition for single token stream to handle
		//Query term value to handle camel case structure
		if (stream.getTokenList().size() == 1)
			return false;
		if (stream.hasNext()) {
			Token token;
			token = stream.next();
			if (token != null) {
				String tkString = token.toString();
				if (tkString != null && !tkString.isEmpty()
						&& !tkString.equals(tkString.toLowerCase())) {
					char[] tkChar = token.getTermBuffer();
					boolean isCamel = true, isCap = false, isSpecialCap = false;
					if (!(tkChar[0] >= 'a' && tkChar[0] <= 'z')) {
						isCap = true;
					}
					// isCamel Should be false if the token begins with a special char
					if(!(tkChar[0] >= 'A' && tkChar[0] <= 'Z')) {
						isCamel = false;
					}
					for (int i =1; i < tkChar.length; i++) {
						if (tkChar[i] >= 'A' && tkChar[i] <= 'Z') {
							isSpecialCap = true;
						}
						// isCap should not be false if special character occurs
						else if (tkChar[i] >= 'a' && tkChar[i] <= 'z'){
							isCap = false;
							//not breaking the loop(by checking isSpecialCap) intentionally 
						}
					}
					//If all the words are capital
					if (isCap) {
						List<Token> line = getTokenLine();
						boolean flagUpper = true;
						for (int i = 0; i < line.size(); i++) {
							String str = line.get(i).toString();
							if(!str.equals(str.toUpperCase())) {
								//tkString = tkString.toLowerCase();
								flagUpper = false;
								break;
							}
						}
						if (flagUpper) {
							for (int i = 0; i < line.size(); i++) {
								Token tk = line.get(i);
								String tkStr = tk.toString();
								tk.setTermText(tkStr.toLowerCase());
							}
						}
					}
					else if (isSpecialCap) {
						//do nothing
					}
					else if (isCamel) {
						int index = stream.getCurrentIndex();
						String prev = "", prevSub = "";
						//The previous token would already be merged with older tokens
						if (stream.hasPreviousAt(index)) {
							if (stream.getTokenAt(index - 1) != null) {
								prev = stream.getTokenAt(index - 1).toString();
								int endWord = prev.length();
								endWord = prev.indexOf(" ");
								if (endWord >= 1) {
									prevSub = prev.substring(1,endWord);	
								}
								else {
									prevSub = prev.substring(1);
								}
							}
							//if (prev.matches(".*\\w*(\\.|\\?)$")) {
//							if (pattCap.matcher(prev).matches()) {
							if (matCap.reset(prev).matches()) {
								tkString = tkString.toLowerCase();
							}
							//							 will not work if more than two words are merged.
//							else if (prev.matches("(\\s?[A-Z][^A-Z]*(\\s|$))")) {
								else if ((prev.charAt(0) >= 'A' && prev.charAt(0) <='Z') 
										&& prevSub.equals(prevSub.toLowerCase())) {
									tkString = prev + " " + tkString;
									stream.removeAt(index - 1);
									index = index - 1;
								}
							}
							else {
								tkString = tkString.toLowerCase();
							}
						token.setTermText(tkString);
						}
					}
				}
			}
			return stream.hasNext();
		}

		/* If previous/next token ends with . or ? and previous characters are words, then it means a punctuation mark
		 * i.e.: <time. . time? ? B-52.> are all valid punctuation. but <avinav...  is-*!? a+.> not valid punctuation.   
		 * 
		 */
		public List<Token> getTokenLine() {
			int currIndex = stream.getCurrentIndex();
			List<Token> tokenLine = new ArrayList<Token>();
			if (stream.getCurrent() != null) 
			{
				tokenLine.add(stream.getCurrent());
			}
			String prev = "",next = "";
			int i = currIndex, lineThreshold = 0;
			while (stream.hasPreviousAt(i)&&(lineThreshold++ < 10)) {
				Token tk = stream.getTokenAt(i-1);
				if (tk != null) {
					prev = tk.toString();
				}
				//if (prev.matches(".*\\w*(\\.|\\?)$")) {
//				if (pattCap.matcher(prev).matches()) {
				if (matCap.reset(prev).matches()) {
					break;
				}
				else {
					tokenLine.add(0,tk);
				}
				i -= 1;
			}
			i = currIndex;
			lineThreshold = 0;
			while (stream.hasNextAt(i)&&(lineThreshold++ < 10)) {
				Token tk = stream.getTokenAt(i+1);
				if ( tk != null ) {
					next = tk.toString();
				}
				//if (next.matches(".*\\w*(\\.|\\?)$")) {
//				if (pattCap.matcher(next).matches()) {
				if (matCap.reset(prev).matches()) {
					tokenLine.add(tk);
					break;
				}
				else {
					tokenLine.add(tk);
				}
				i += 1;
			}
			return tokenLine;
		}
	}