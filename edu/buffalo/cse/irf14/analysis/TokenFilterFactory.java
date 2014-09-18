/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;


/**
 * Factory class for instantiating a given TokenFilter
 * @author nikhillo
 *
 */
public class TokenFilterFactory {
	/**
	 * Static member holds only one instance of
	 *TokenFilterFactory
	 */
	private static TokenFilterFactory tFactory = new TokenFilterFactory();
	/*
	 * This private constructor will prevent any other class from
	 *instantiating
	 */
	private void TokenFilterFactory() {
	}
	/**
	 * Static method to return an instance of the factory class.
	 * Usually factory classes are defined as singletons, i.e. 
	 * only one instance of the class exists at any instance.
	 * This is usually achieved by defining a private static instancefafa
	 * that is initialized by the "private" constructor.
	 * On the method being called, you return the static instance.
	 * This allows you to reuse expensive objects that you may create
	 * during instantiation
	 * @return An instance of the factory
	 */
	public static TokenFilterFactory getInstance() {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		return tFactory;
	}
	/**
	 * Returns a fully constructed {@link TokenFilter} instance
	 * for a given {@link TokenFilterType} type
	 * @param type: The {@link TokenFilterType} for which the 
	 * {@link TokenFilter} is requested
	 * @param stream: The TokenStream instance to be wrapped
	 * @return The built {@link TokenFilter} instance
	 */
	public TokenFilter getFilterByType(TokenFilterType type, TokenStream stream) {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		if ( TokenFilterType.SPECIALCHARS.equals(type)) {
			return new TokenFilterSpecialChars(stream);
		}
		else if (TokenFilterType.ACCENT.equals(type)) {
			
		}
		else if (TokenFilterType.CAPITALIZATION.equals(type)) {
			
		}
		else if (TokenFilterType.DATE.equals(type)) {
			return new TokenFilterDate(stream);
		}
		else if (TokenFilterType.NUMERIC.equals(type)) {
			
		}
		else if (TokenFilterType.STEMMER.equals(type)) {
			return new TokenFilterStemmer(stream);
		}
		else if (TokenFilterType.STOPWORD.equals(type)) {
			
		}
		else if (TokenFilterType.SYMBOL.equals(type)) {
			return new TokenFilterSymbol(stream);
		}
		return null;
	}
}
