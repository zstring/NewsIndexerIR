/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author inspire
 *
 */
public class TokenFilterDate extends TokenFilter {
	/**
	 * default constructor takes stream parameter and
	 * call the super constructor to initialize tokenstream object
	 * @param stream
	 */
	public TokenFilterDate(TokenStream stream) {
		super(stream);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.irf14.analysis.Analyzer#increment()
	 */
	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if (stream.hasNext()) {
			Token token = stream.getCurrent();
			String tkString = token.toString();
			int tkNum = -1;
			try {
				tkNum = Integer.parseInt(tkString);
			} catch (NumberFormatException e) {
			}
			if (tkNum >= 1000 && tkNum <= 3000) {
				
				Token[] prevTokens = new Token[2];//stream.getPrevFiveToken();
				String m = prevTokens[3].toString();
				String d = prevTokens[4].toString();
				String[] fetchedMonthDate = fetchMonthDate(m, d);
				if (!fetchedMonthDate[0].equals(Boolean.TRUE.toString())) {
					fetchedMonthDate = fetchMonthDate(d, m);
				}
				token.setTermText(tkString + fetchedMonthDate[1]);
			}
		}
		return stream.hasNext();
	}

	private String[] fetchMonthDate(String m, String d) {
		String[] retVal = new String[2];
		Boolean flag = false;
		String monthRegex = "(jan)|(feb)|(mar)|(apr)|(may)|(jun)|"
				+ "(jul)|(aug)|(sep)|(oct)|(nov)|(dec)";
		String digitRegex = "\\d+";
		String dateIndex = "01";
		String monthIndex = "01";
		Matcher mat = Pattern.compile(monthRegex,
				Pattern.CASE_INSENSITIVE).matcher(m);
		if (mat.find()) {
			for (int i = 1; i <= 12; i++) {
				if (mat.group(i) != null) {
					monthIndex = i + "";
					flag = true;
					if (monthIndex.length() == 1) {
						monthIndex = "0" + monthIndex;
					}
					break;
				}
			}
			mat = Pattern.compile(digitRegex).matcher(d);
			if (mat.matches()) {
				dateIndex = d;
				flag = true;
				if (dateIndex.length() == 1) {
					dateIndex = "0" + dateIndex;
				}
			}
		}
		if (flag) {
			retVal[0] = Boolean.TRUE.toString();
		}
		else {
			retVal[0] = Boolean.FALSE.toString();
		}
		retVal[1] = monthIndex + dateIndex;
		return retVal;
	}
}
