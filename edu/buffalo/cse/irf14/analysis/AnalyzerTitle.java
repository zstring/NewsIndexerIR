/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * @author avinav
 *
 */
public class AnalyzerTitle implements Analyzer{
	private TokenStream stream;
	private TokenFilter[] filter;
	
	public AnalyzerTitle(TokenStream stream) {
		// TODO Auto-generated constructor stub
		this.stream = stream;
		TokenFilterFactory tfFactory = TokenFilterFactory.getInstance();
		this.filter[0] = tfFactory.getFilterByType
				(TokenFilterType.CAPITALIZATION, stream);
		this.filter[0].setIsAnalyzer(true);
	}
	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if (stream.hasNext()) {
			stream.next();
			return filter[0].increment();
		}
		return false;
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return stream;
	}

}
