/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * @author avinav
 *
 */
public class TokenFilterStopWord extends TokenFilter{

	public TokenFilterStopWord(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub
	}
	
	private static final String stopWords;
	static {
		stopWords = ",a,about,above,after,again,against,all,am,an,and,any,are,aren't,as,at,"
				+ "be,because,been,before,being,below,between,both,but,by,can't,cannot,could,couldn't,"
				+ "did,didn't,do,does,doesn't,doing,don't,down,during,each,few,for,from,further,had,"
				+ "hadn't,has,hasn't,have,haven't,having,he,he'd,he'll,he's,her,here,here's,hers,"
				+ "herself,him,himself,his,how,how's,i,i'd,i'll,i'm,i've,if,in,into,is,isn't,it,"
				+ "it's,its,itself,let's,me,more,most,mustn't,my,myself,no,nor,not,of,off,on,"
				+ "once,only,or,other,ought,our,ours,this,";

	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if (stream.hasNext()||this.isAnalyzer) {
			Token token;
			if(!this.isAnalyzer) {
				token = stream.next();
			}
			else {
				token = stream.getCurrent();
			}
			String tkString = token.toString();
			if (stopWords.contains(","+tkString.toLowerCase()+",")) {
				stream.remove();
			}
			return true;
		}
		return false;
	}

}
