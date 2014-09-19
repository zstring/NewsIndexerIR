package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenFilterSpecialChars extends TokenFilter {

	public TokenFilterSpecialChars(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if (stream.hasNext()) {
			Token token = stream.next();
			String tkString = token.toString();
			//String specChar = "[^.a-zA-Z0-9- ]";
			//String specChar = "[~!@#$%^&*()_/\\+<>|=]";
			//char[] specChar = {'~','!','@','#','$','%','^','&','*'};
			String specChar = "[^\\.\\sa-zA-Z0-9@-]";
			Matcher matcher = Pattern.compile("(.*[a-zA-Z])(-)([a-zA-Z].*)").matcher(tkString);
			if (matcher.matches()) {
				tkString = matcher.group(1)+matcher.group(3);
			}
			tkString = tkString.replaceAll(specChar, "");
			if (tkString.matches(".*\\w@\\w.*")) {
				int posAt = tkString.indexOf('@');
				String splitString = tkString.substring(posAt+1);
				tkString = tkString.substring(0,posAt);
				Token newToken = new Token();
				if (!"".equals(splitString)) {
					newToken.setTermText(splitString);
					stream.insertAt(stream.getCurrentIndex()+1, newToken);
					//intentionally not moved the stream to next index so
					//that it does not interfere with other filters in chain
				}
				
			}
			else if (tkString.matches("@")){
				tkString = tkString.replaceAll("@", "");	
			}
			
			if (!tkString.equals("")) {
				token.setTermText(tkString);
			}
			else {
				stream.remove();
			}
			return true;
		}
		return false;
	}
}
