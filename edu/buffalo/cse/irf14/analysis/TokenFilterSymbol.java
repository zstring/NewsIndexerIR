/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author avinav sharan and himanshu sharma
 *
 */
public class TokenFilterSymbol extends TokenFilter {
	public TokenFilterSymbol(TokenStream stream) {
		super(stream);
	}



	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if (stream.hasNext()) {
			Token token = stream.next();
			String tkString = token.toString();
			//char[] tkChar = token.getTermBuffer();
			
			// Start or End with ' or " or -
			tkString = tkString.replaceAll("['\"-]*($|\\s)", " ");
			tkString = tkString.replaceAll("(^|\\s)['\"-]*", " ");
			
			// End with Punctuation
			tkString = tkString.replaceAll("[.!?]*($|\\s)", " ");
			
			// 's
			tkString = tkString.replaceAll("'s?($|\\s)", " ");
			tkString = tkString.trim();
			
			// Hyphens
			String input = tkString;
			int indexGrp2 = 0, indexGrp1 =0;
			Matcher matcher = Pattern.compile("(\\s|-|^)([a-zA-Z])+(-)+([a-zA-Z])+(-|$|\\s)").matcher(tkString);
			while(matcher.find()) {
				indexGrp1 = matcher.end(2);
				indexGrp2 = matcher.end(3);
				input= 	input.substring(0, indexGrp1) + " " + input.substring(indexGrp2);
				matcher.reset(input);
			}
			tkString = input;
			if ("".equals(tkString)) {
				stream.remove();
			} else {
				token.setTermText(tkString);
			}
			
			return true;
		}
		return false;
	}
}
