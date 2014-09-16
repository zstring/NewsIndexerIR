package edu.buffalo.cse.irf14.analysis;

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
			  String specChar = "[^.a-zA-Z0-9- ]";
			//String specChar = "[~!@#$%^&*()_/\\+<>|=]";
			//char[] specChar = {'~','!','@','#','$','%','^','&','*'};
			tkString = tkString.replaceAll(specChar, "");
			if (!tkString.equals("")) {
				token.setTermText(tkString);
			}
			else {
				stream.remove();
			}
		}
		return stream.hasNext();
	}
}
