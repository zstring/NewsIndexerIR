/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * @author avinav and himanshu
 *
 */
public class TokenFilterNumber extends TokenFilter{
	public TokenFilterNumber(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		/* 1. Does token contains AM/PM/BC./AD/month name
		 * 2. Does next token contains AM/P.M./B.C./AD
		 * 3. Does previous token contains month name
		 * 4. if 1,2,3 fails. Is number 4 digit and between 1000 and 3000
		 * 5. if 1,2,3,4 fails: remove number digits only. if empty string remains, remove token. 
		 */
		if (stream.hasNext()) {
			Token token = stream.next();
			String tkString = token.toString();
			if (tkString.matches("")) {
				
			}
		}
			
		
		
		return false;
	}

}
