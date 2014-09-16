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
			char[] tkChar = token.getTermBuffer();
			tkString.trim().replaceAll("[.!?]*$", "");
			tkString.trim().replaceAll("'s?$", "");
			String input = tkString;
			int indexGrp2 = 0, indexGrp1 =0;
			Matcher matcher = Pattern.compile("([a-zA-Z])+(-)+[a-zA-Z]+").matcher(tkString);
			while(matcher.find()) {
				indexGrp1 = matcher.end(1);
				indexGrp2 = matcher.end(2);
				input= 	input.substring(0, indexGrp1) + " " + input.substring(indexGrp2);
				matcher.reset(input);
			}
			/*	int count = 0;
			for (int i = tkChar.length - 1; i >= 0; i++) {
				if (count == 0 && (tkChar[i] == '\'' || tkChar[i] == '?' || tkChar[i] == '!' || tkChar[i] == '.')) {
					tkChar[i] = ' ';
				}
				else if (count == 1 && tkChar[i] == '\'' && (tkChar[i+1] == 's'||tkChar[i+1] == 'S')) {
					tkChar[i] = ' ';
					tkChar[i+1] = ' ';
				}
				else if (tkChar[i] == '-') {
					if (tkChar[i-1] >= 'a') {
						
					}
				}
				else {
					count += 1; 
				}
			*/		
		}
		return false;
	}
}
