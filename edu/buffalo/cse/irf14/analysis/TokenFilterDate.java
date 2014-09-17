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
			Token token = stream.next();
			String tkString = token.toString();
			String[] tTime = checkAndExtractTime(tkString);
			String retVal = tTime[0];
			String retTime = tTime[1];
			if (!retVal.equals("0")) {
				token.setTermText(retTime);
				if (retVal.equals("2")) {
					stream.removeAt(stream.getCurrentIndex() + 1);
				}
				return stream.hasNext();
			}
//			String month = checkAndExtractDate(tkString);
//			if (!month.equals("")) {
//				validateAndProcessNeighborTokens(month);
//			}
//			int tkNum = -1;
//			try {
//				tkNum = Integer.parseInt(tkString);
//			} catch (NumberFormatException e) {
//			}
//			if (tkNum >= 1000 && tkNum <= 3000) {
//				
////				String m = prevTokens[2].toString();
////				String d = prevTokens[3].toString();
////				String fetchedMonthDate = checkAndExtractDate(d);
////				token.setTermText(tkString + fetchedMonthDate[1]);
//			}
		}
		return stream.hasNext();
	}
	
	/**
	 * 
	 * @param str
	 * @return
	 */
	private String[] checkAndExtractTime(String str) {
		String retTime[] = new String[2];
		String endString = "";
		//False identify that there is no AM/PM attached to time string
		//and look for next tokens to identify AM/PM;
		retTime[0] = "0";
		String timeRegex = "(\\d{1,2})(:(\\d{1,2}))?(:(\\d{1,2}))?\\s?([aApP][mM].{0,5})?";
		Matcher mat = Pattern.compile(timeRegex).matcher(str);
		String finalTime = "";
		if (mat.matches()) {
			retTime[0] = "1";
			int hh = 0, mm = 0, ss = 0;
			if (mat.group(1) != null)
				hh = Integer.parseInt(mat.group(1));
			if (mat.group(2) != null)
				mm = Integer.parseInt(mat.group(3));
			if (mat.group(4) != null)
				ss = Integer.parseInt(mat.group(5));
			if (mat.group(6) != null) {
				String timeZone = mat.group(6).toLowerCase();
				if (timeZone.contains("pm")) {
					hh = hh + 12; //for PM evening time;
				}
				// Do nothing for AM time
			}
			else {
				
				Token nextToken  = stream.getTokenAt(stream.getCurrentIndex() + 1);
				String nextStr = "";
				if (nextToken != null) {
					nextStr = nextToken.getTermText();
				}
				Matcher match = Pattern.compile("(am|pm)[^\\w^\\d]?", Pattern.CASE_INSENSITIVE).matcher(nextStr);
				if (match.matches()) {
					retTime[0] = "2";
					String val = match.group(1).toLowerCase();
					if (val.equals("pm")) { 
						hh = hh + 12;//for PM evening time;
						endString = nextStr.replace("pm", "");
					}
					else 
						endString = nextStr.replace("am", "");
				}
				if (nextStr.contains("am")) {
					retTime[0] = "2";
				}
				else if (nextStr.contains("pm")) {
					
					retTime[0] = "2";
				}
			}
			if (hh < 10) finalTime += "0" + hh;
			else finalTime += hh;
			if (mm < 10) finalTime += ":0" + mm;
			else finalTime += ":" + mm;
			if (ss < 10) finalTime += ":0" + ss;
			else finalTime += ":" + ss;
			retTime[1] = finalTime + endString;
		}
		return retTime;
	}

	private String checkAndExtractMonth(String m) {
		String retVal = "";
		boolean flag = false;
		String monthRegex = "(jan)|(feb)|(mar)|(apr)|(may)|(jun)|"
				+ "(jul)|(aug)|(sep)|(oct)|(nov)|(dec)";
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
			retVal = monthIndex;
		}
		return retVal;
	}

	private String checkAndExtractDate(String d) {
		String retVal = "";
		Boolean flag = false;
		String digitRegex = "\\d+";
		String dateIndex = "01";
		Matcher mat = Pattern.compile(digitRegex).matcher(d);
		if (mat.matches()) {
			dateIndex = d;
			flag = true;
			if (dateIndex.length() == 1) {
				dateIndex = "0" + dateIndex;
			}
		}
//		if (flag) {
//			retVal[0] = Boolean.TRUE.toString();
//		}
//		else {
//			retVal[0] = Boolean.FALSE.toString();
//		}
		retVal = dateIndex;
		return retVal;
	}
	
	private void validateAndProcessNeighborTokens(String month) {
		Token[] tks = stream.getPrevTokens();
		int prev = 0, next = 0;
		boolean flag = true;
		for (int i = 0; i < 5; i++) {
			if (tks[4 - i] != null && flag) {
				String date = checkAndExtractDate(tks[4 - i].toString());
				if (!date.equals("")) {
					
				}
			}
			if (tks[5 + i] != null && flag) {
				
			}
			if (true) {
				flag = false;
			}
		}
	}
}
