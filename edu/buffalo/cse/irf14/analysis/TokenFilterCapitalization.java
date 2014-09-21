/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.List;

/**
 * @author avinav sharan and himanshu sharma
 *
 */
public class TokenFilterCapitalization extends TokenFilter {

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
		if (stream.hasNext()) {
			Token token = stream.next();
			String tkString = token.toString();
			char[] tkChar = token.getTermBuffer();
			boolean isCamel = true, isCap = false, isSpecialCap = false;
			if (!(tkChar[0] >= 'a' && tkChar[0] <= 'z')) {
				isCap = true;
			}
			// isCamel Should be false if the token begins with a special char
			if(!(tkChar[0] >= 'A' && tkChar[0] <= 'Z')) {
				isCamel = false;
			}
			
			for (int i =1; i<tkChar.length; i++) {
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
				List<String> line = getTokenLine(stream);
				for (int i = 0; i <= line.size(); i++) {
					if(!line.equals(line.get(i).toUpperCase())) {
						tkString = tkString.toLowerCase();
						break;
					}
				}
			}
			else if (isSpecialCap) {
				//do nothing
			}
			else if (isCamel) {
				int index = stream.getCurrentIndex();
				String prev =""; 
				//The previous token would already be merged with older tokens
				if (stream.hasPrevious()) {
					prev = stream.previous().toString();
					if (prev.matches(".*\\w*(.|?)$")) {
						tkString = tkString.toLowerCase();
						stream.setCurrentIndex(index);
					}
					// will not work if more than two words are merged.
					else if ((prev.charAt(0) >= 'A' && prev.charAt(0) <='Z') && prev.substring(1).equals(prev.substring(1).toLowerCase())) {
						tkString = prev + " " + tkString;
						stream.remove();
						index = index - 1;
						}
					else {
						stream.setCurrentIndex(index);
					}
				}
			}
			token.setTermText(tkString);
			return true;
		}
		return false;
	}
	
	/* If previous/next token ends with . or ? and previous characters are words, then it means a punctuation mark
	 * i.e.: <time. . time? ? B-52.> are all valid punctuation. but <avinav...  is-*!? a+.> not valid punctuation.   
	 * 
	 */
	public List<String> getTokenLine(TokenStream tkStream) {
		int currIndex = stream.getCurrentIndex();
		List<String> tokenLine = new ArrayList<String>();
		tokenLine.add(stream.getCurrent().toString());
		String prev = "",next = "";
		while (stream.hasPrevious()) {
			prev = stream.previous().toString();
			if (prev.matches(".*\\w*(.|?)$")) {
				break;
			}
			else {
				tokenLine.add(0,prev);
			}
		}
		while (stream.hasNext()) {
			next = stream.next().toString();
			if (next.matches(".*\\w*(.|?)$")) {
				tokenLine.add(next);
				break;
			}
			else {
				tokenLine.add(next);
			}
		}
		stream.setCurrentIndex(currIndex);
		return tokenLine;
	}

}
