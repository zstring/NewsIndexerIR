/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nikhillo
 * Class that converts a given string into a {@link TokenStream} instance
 */
public class Tokenizer {
	
	String dmeter;
	/**
	 * Default constructor. Assumes tokens are whitespace delimited
	 */
	public Tokenizer() {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		dmeter = " ";
	}
	
	/**
	 * Overloaded constructor. Creates the tokenizer with the given delimiter
	 * @param delim : The delimiter to be used
	 */
	public Tokenizer(String delim) {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		dmeter = delim;
	}
	
	/**
	 * Method to convert the given string into a TokenStream instance.
	 * This must only break it into tokens and initialize the stream.
	 * No other processing must be performed. Also the number of tokens
	 * would be determined by the string and the delimiter.
	 * So if the string were "hello world" with a whitespace delimited
	 * tokenizer, you would get two tokens in the stream. But for the same
	 * text used with lets say "~" as a delimiter would return just one
	 * token in the stream.
	 * @param str : The string to be consumed
	 * @return : The converted TokenStream as defined above
	 * @throws TokenizerException : In case any exception occurs during
	 * tokenization
	 */
	public TokenStream consume(String str) throws TokenizerException {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		if (str == null || str.isEmpty()) {
			// WHAT IF ONLY spaces are present in str??
			throw new TokenizerException();
		}
		TokenStream tokenStream = null;
		try {
			List<Token> tokenList = new ArrayList<Token>();
			// Have to check if new tk object need to be created inside for
			String[] words = str.split(dmeter);
			for (int i = 0; i < words.length; i++) {
				String word = words[i].trim();
				if (!word.isEmpty()) {
					// passing the ith position to store the location 
					// in a document Positional Index
					Token tk = new Token(i); 
					tk.setTermText(word);
					tokenList.add(tk);
				}
			}
			if (!tokenList.isEmpty()) {
				tokenStream = new TokenStream();
				tokenStream.setTokenList(tokenList);
			}
		} catch (Exception e) {
//			throw new TokenizerException();
			e.printStackTrace();
		}
		return tokenStream;
	}
}
