/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author avinav sharan and himanshu sharma
 *
 */
public class TokenFilterSymbol extends TokenFilter {
	//	private static Pattern pattSym, pattPunc, pattSingle;
	private static Matcher matSym, matPunc, matSingle, matIsWord;
	static {
		matSym = Pattern.compile("(-|^)([a-zA-Z])+(-)+([a-zA-Z])+(-|$)").matcher("");
		matPunc = Pattern.compile("(^| )['\"-]*|([-'\"\\.!\\?,]*|'s)($| )").matcher("");
		matSingle = Pattern.compile("'").matcher("");
		matIsWord = Pattern.compile("[\\w]*").matcher("");
	}
	public TokenFilterSymbol(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if (stream.hasNext()) {
			Token token;
			token = stream.next();
			if (token != null && !token.isDate() && !token.isTime()) {
				String tkString = token.toString();
				if (tkString != null && !matIsWord.reset(tkString).matches()) {
					// Word Contractions
					if (contractions.containsKey(tkString.toLowerCase())) {
						boolean upper = false;
						StringBuilder builder = new StringBuilder(contractions.get(tkString.toLowerCase()));
						String tkStringUpper = tkString.toUpperCase();
						if (tkString.equals(tkStringUpper)) {
							upper = true;
						}
						else if (tkString.charAt(0) >= 'A' && tkString.charAt(0) <= 'Z'){
							builder.setCharAt(0, (char) (builder.charAt(0) - 32));
						}
						tkString = builder.toString();
						if (upper) tkString = tkStringUpper;
					}

					// Start or End with ' or " or -
					// End with Punctuation
					//				tkString = pattPunc.matcher(tkString).replaceAll(" ");
					//				tkString = pattSingle.matcher(tkString).replaceAll("");
					tkString = matPunc.reset(tkString).replaceAll(" ");
					if (tkString.contains("'")) {
						tkString = matSingle.reset(tkString).replaceAll("");
					}
					tkString = tkString.trim();

					// Hyphens
					String input = tkString; 
					int indexGrp2 = 0, indexGrp1 =0;
					//				Matcher matcher = pattSym.matcher(tkString);
					if (tkString.contains("-")){
						Matcher matcher = matSym.reset(tkString);
						while(matcher.find()) {
							indexGrp1 = matcher.end(2);
							indexGrp2 = matcher.end(3);
							input= 	input.substring(0, indexGrp1) + " " + input.substring(indexGrp2);
							matcher.reset(input);
						}
					}
					tkString = input;
					if ("".equals(tkString)) {
						stream.remove();
					} else {
						token.setTermText(tkString);
					}
				}
			}
			else if (token.isDate() || token.isTime()){
				String str = token.getTermText();
				char c = str.charAt(str.length() - 1);
				if (!Character.isDigit(c)) {
					token.setTermText(str.substring(0, str.length() - 1));
				}
			}
		}
		return stream.hasNext();
	}


	private static final Map<String, String> contractions;
	static {
		Map<String,String> tempMap = new HashMap<String, String>();
		tempMap.put("ain't",	"am not");
		tempMap.put("aren't",	"are not");
		tempMap.put("can't",	"cannot");
		tempMap.put("could've",	"could have");
		tempMap.put("couldn't",	"could not");
		tempMap.put("couldn't've",	"could not have");
		tempMap.put("didn't",	"did not");
		tempMap.put("doesn't",	"does not");
		tempMap.put("don't",	"do not");
		tempMap.put("hadn't",	"had not");
		tempMap.put("hadn't've",	"had not have");
		tempMap.put("hasn't",	"has not");
		tempMap.put("haven't",	"have not");
		tempMap.put("he'd",	"he had");
		tempMap.put("he'd've",	"he would have");
		tempMap.put("he'll",	"he shall");
		tempMap.put("he's",	"he has");
		tempMap.put("how'd",	"how did");
		tempMap.put("how'll",	"how will");
		tempMap.put("how's",	"how has ");
		tempMap.put("i'd",	"i had ");
		tempMap.put("i'd've",	"i would have");
		tempMap.put("i'll",	"i shall ");
		tempMap.put("i'm",	"i am");
		tempMap.put("i've",	"i have");
		tempMap.put("isn't",	"is not");
		tempMap.put("it'd",	"it had");
		tempMap.put("it'd've",	"it would have");
		tempMap.put("it'll",	"it shall");
		tempMap.put("it's",	"it has");
		tempMap.put("let's",	"let us");
		tempMap.put("ma'am",	"madam");
		tempMap.put("mightn't",	"might not");
		tempMap.put("mightn't've",	"might not have");
		tempMap.put("might've",	"might have");
		tempMap.put("mustn't",	"must not");
		tempMap.put("must've",	"must have");
		tempMap.put("needn't",	"need not");
		tempMap.put("not've",	"not have");
		tempMap.put("o'clock",	"of the clock");
		tempMap.put("shan't",	"shall not");
		tempMap.put("she'd",	"she had");
		tempMap.put("she'd've",	"she would have");
		tempMap.put("she'll",	"she will");
		tempMap.put("she's",	"she has ");
		tempMap.put("should've",	"should have");
		tempMap.put("shouldn't",	"should not");
		tempMap.put("shouldn't've",	"should not have");
		tempMap.put("that's",	"that has");
		tempMap.put("there'd",	"there had");
		tempMap.put("there'd've",	"there would have");
		tempMap.put("there're",	"there are");
		tempMap.put("there's",	"there has");
		tempMap.put("they'd",	"they would");
		tempMap.put("they'd've",	"they would have");
		tempMap.put("they'll",	"they shall ");
		tempMap.put("they're",	"they are");
		tempMap.put("they've",	"they have");
		tempMap.put("wasn't",	"was not");
		tempMap.put("we'd",	"we had ");
		tempMap.put("we'd've",	"we would have");
		tempMap.put("we'll",	"we will");
		tempMap.put("we're",	"we are");
		tempMap.put("we've",	"we have");
		tempMap.put("weren't",	"were not");
		tempMap.put("what'll",	"what shall");
		tempMap.put("what're",	"what are");
		tempMap.put("what's",	"what has ");
		tempMap.put("what've",	"what have");
		tempMap.put("when's",	"when has");
		tempMap.put("where'd",	"where did");
		tempMap.put("where's",	"where has");
		tempMap.put("where've",	"where have");
		tempMap.put("who'd",	"who would");
		tempMap.put("who'll",	"who shall");
		tempMap.put("who're",	"who are");
		tempMap.put("who's",	"who has");
		tempMap.put("who've",	"who have");
		tempMap.put("why'll",	"why will");
		tempMap.put("why're",	"why are");
		tempMap.put("why's",	"why has");
		tempMap.put("won't",	"will not");
		tempMap.put("would've",	"would have");
		tempMap.put("wouldn't",	"would not");
		tempMap.put("wouldn't've",	"would not have");
		tempMap.put("y'all",	"you all");
		tempMap.put("y'all'd've",	"you all should have");
		tempMap.put("you'd",	"you had");
		tempMap.put("you'd've",	"you would have");
		tempMap.put("you'll",	"you shall");
		tempMap.put("you're",	"you are");
		tempMap.put("you've",	"you have");
		tempMap.put("'em",	"them");
		contractions = Collections.unmodifiableMap(tempMap);
	}
}
