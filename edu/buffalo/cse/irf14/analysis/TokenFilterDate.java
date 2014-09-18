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
			if (token != null) {
				String tkString = token.toString();
				if (tkString != null && !tkString.equals("")) {
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
					String[] retMonth = checkAndExtractMonth(tkString);
					if (retMonth[0].equals("1")) {
						token.setTermText(retMonth[1]);
					}
				}
			}
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
		String timeRegex = "(\\d{1,2})(:(\\d{1,2}))(:(\\d{1,2}))?\\s?([aApP][mM].{0,5})?";
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
					endString = timeZone.replace("pm", "");
				}
				else if (timeZone.contains("am")) {
					endString = timeZone.replace("am", "");
					//Do nothing for AM time
				}
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
					else {
						endString = nextStr.replace("am", "");
					}
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

	private String[] checkAndExtractMonth(String m) {
		String[] retMonth = new String[2];
		String [] dateAndYear = null;
		boolean flag = false;
		retMonth[0] = "0";
		String monthRegex = "(jan)|(feb)|(mar)|(apr)|(may)|(jun)|"
				+ "(jul)|(aug)|(sep)|(oct)|(nov)|(dec)";
		String monthIndex = "01";
		Matcher mat = Pattern.compile(monthRegex,
				Pattern.CASE_INSENSITIVE).matcher(m);
		if (mat.find()) {
			retMonth[0] = "1";
			for (int i = 1; i <= 12; i++) {
				if (mat.group(i) != null) {
					monthIndex = i + "";
					flag = true;
					if (monthIndex.length() == 1) {
						monthIndex = "0" + monthIndex;
					}
					Token[] tokenList = stream.getPrevTokens();
					//dateAndYear contains Date and Year value at
					//0 and 1 position respectively
					dateAndYear = processTokensForDate(tokenList);
					break;
				}
			}
			//Structure of dateAndYear Variable 
			//dateAndYear[0] = last special char of date such as "." ","
			//dateAndYear[1] = date value such as "1", "2";
			//dateAndYear[2] = last special char of year such as "." ","
			//dateAndYear[3] = year value
			if (dateAndYear != null) {
				if (!"".equals(dateAndYear[3])) {
				retMonth[1] = dateAndYear[3] + monthIndex;
				}
				else {
					retMonth[1] = "1900" + monthIndex;
				}
				if (!"".equals(dateAndYear[1])) {
					retMonth[1] += dateAndYear[1];
				}
				else {
					retMonth[1] += "01";
				}
				retMonth[1] += dateAndYear[2];
			}
		}
		return retMonth;
	}

	/**
	 * process all the tokens with respect to date 
	 * for merging all related tokens such as time, year and Date
	 * @param tokenList
	 */
	private String[] processTokensForDate(Token[] tokenList) {
		// TODO Auto-generated method stub
		String[] dateAndYear = new String[4];
		dateAndYear[0] = "01";
		dateAndYear[1] = "1900";
		boolean dateFlag = false, yearFlag = false, timeFlag = false;
		String[] retDate = null, retYear = null, retTime = null;
		for (int i = 0; i < 5 && (!dateFlag || !yearFlag); i++) {
			Token tPrev = tokenList[4 - i];
			Token tNext = tokenList[i + 6];
			String tPrevStr = "", tNextStr = "";
			if (tPrev != null) tPrevStr = tPrev.toString();
			if (tNext != null) tNextStr = tNext.toString();
			//Step1 check for Date (01-30)
			if (tPrevStr != null && !"".equals(tPrevStr)) {
				if (!dateFlag) {
					retDate = checkAndExtractDate(tPrevStr);
					if (retDate != null) {
					//if (!"".equals(retDate)) {
						dateFlag = true;
						stream.removeAt(stream.getCurrentIndex() - i - 1);
					}
				}
				if (!yearFlag) {
					retYear = checkAndExtractYear(tPrevStr);
					if (retYear != null) {
					//if (!"".equals(retYear)) {
						yearFlag = true;
						stream.removeAt(stream.getCurrentIndex() - i - 1);
					}
				}
				if (!timeFlag) {
//					/retTime = checkAndExtractTime(tPrevStr);
				}
			}
			
			if (tNextStr != null && !"".equals(tNextStr)) {
				if (!dateFlag) {
					retDate = checkAndExtractDate(tNextStr);
					if (retDate != null) {
					//if (!"".equals(retDate)) {
						dateFlag = true;
						stream.removeAt(stream.getCurrentIndex() + i + 1);
					}
				}
				if (!yearFlag) {
					retYear = checkAndExtractYear(tNextStr);
					if (retYear != null) {
					//if (!"".equals(retYear)) {
						yearFlag = true;
						stream.removeAt(stream.getCurrentIndex() + i + 1);
					}
				}
			}
		}
		if (dateFlag && yearFlag) {
			dateAndYear[0] = retDate[0];
			dateAndYear[1] = retDate[1];
			dateAndYear[2] = retYear[0];
			dateAndYear[3] = retYear[1];
		}
		else if (dateFlag) {
			dateAndYear[0] = retDate[0];
			dateAndYear[1] = retDate[1];
			dateAndYear[2] = "";
			dateAndYear[3] = "";
		}
		else if (yearFlag) {
			dateAndYear[0] = "";
			dateAndYear[1] = "";
			dateAndYear[2] = retYear[0];
			dateAndYear[3] = retYear[1];
		}
		return dateAndYear;
	}

	/**
	 * 
	 * @param tPrevStr
	 * @return
	 */
	private String[] checkAndExtractYear(String d) {
		// TODO Auto-generated method stub
		String[] retVal = null;
		String digitRegex = "\\d+";
		String yearIndex = "1900";
		String lastChar = "";
		Matcher mat = Pattern.compile(digitRegex).matcher(d);
		if (mat.find()) {
			int beginIndex = mat.start();
			int endIndex = mat.end();
			int len = endIndex - beginIndex + 1;
			if (d.length() <= len) {
				String yearStr = d.substring(beginIndex, endIndex);
				int yearVal = Integer.parseInt(yearStr);
				//Checking if the integer val of date is valid or not	
				if (yearVal < 1000 || yearVal > 3000)
					return retVal;
				yearIndex = yearStr;
				if (d.length() == len) {
					lastChar = d.charAt(d.length() - 1) +  "";
				}
				retVal = new String[2];
				retVal[0] = lastChar;
				retVal[1] = yearIndex;
			}
		}
		//this character contains the value for date such
		//1999, or 2000. then output will be
		//retVal = {",", 1999};
		//retVal[0] = {".", 2000};
		return retVal;
	}

	private String[] checkAndExtractDate(String d) {
		String[] retVal = null;
		String digitRegex = "\\d+";
		String dateIndex = "01";
		String lastChar = "";
		Matcher mat = Pattern.compile(digitRegex).matcher(d);
		if (mat.find()) {
			int beginIndex = mat.start();
			int endIndex = mat.end();
			int len = endIndex - beginIndex + 1;
			if (d.length() <= len) {
				String dateStr = d.substring(beginIndex, endIndex);
				int dateVal = Integer.parseInt(dateStr);
				//Checking if the integer val of date is valid or not
				if (dateVal < 1 || dateVal > 31)
					return retVal;
				dateIndex = dateStr;
				if (dateIndex.length() == 1) {
					dateIndex = "0" + dateIndex;
				}
				if (d.length() == len) {
					lastChar = d.charAt(d.length() - 1) + "";
				}
				retVal = new String[2];
				retVal[0] = lastChar;
				retVal[1] = dateIndex;
			}
		}
		//this character contains the value for date such
		//19, or 20. then output will be
		//retVal = {",", 19};
		//retVal[0] = {".", 20};
		return retVal;
	}
}
