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
	private Pattern pattMonth;
	private Pattern pattTime;
	private Pattern pattYear;
	private Pattern pattAMPM;
	public TokenFilterDate(TokenStream stream) {
		super(stream);
		String monthRegex = "(january|jan)|(february|feb)|(march|mar)|"
				+ "(april|apr)|(may)|(june|jun)|(july|jul)|(august|aug)|"
				+ "(september|sep)|(october|oct)|(november|nov)|(december|dec)";
		pattMonth = Pattern.compile(monthRegex);
		String timeRegex = "(\\d{1,2})(:(\\d{1,2}))(:(\\d{1,2}))? ?([aApP][mM].{0,5})?";
		pattTime = Pattern.compile(timeRegex);
		String digitRegex = "\\d+";
		pattYear = Pattern.compile(digitRegex);
		String ampmRegex = "(am|pm)[^\\w^\\d]?";
		pattAMPM = Pattern.compile(ampmRegex);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.irf14.analysis.Analyzer#increment()
	 */
	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if (stream.hasNext() || this.isAnalyzer) {
			Token token;
			if (!this.isAnalyzer) {
				token = stream.next();
			}
			else {
				token = stream.getCurrent();
			}
			if (token != null) {
				String tkString = token.toString();
				if (tkString != null && !tkString.isEmpty()) {
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
						return stream.hasNext();
					}
					String[] retYear = checkAndExtractYear(tkString, "year");
					if (retYear != null) {
						token.setTermText(retYear[1] + retYear[0]);
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
		Matcher mat = pattTime.matcher(str);
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
				nextStr = nextStr.toLowerCase();
				//Matcher match = Pattern.compile("(am|pm)[^\\w^\\d]?", Pattern.CASE_INSENSITIVE).matcher(nextStr);
				Matcher match = pattAMPM.matcher(nextStr);
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
		String[] dateAndYear = null;
		boolean flag = false;
		retMonth[0] = "0";
		m = m.toLowerCase();
//		String monthRegex = "(january|jan)|(february|feb)|(march|mar)|"
//				+ "(april|apr)|(may)|(june|jun)|(july|jul)|(august|aug)|"
//				+ "(september|sep)|(october|oct)|(november|nov)|(december|dec)";
		String monthRegex = "(jan)";
		String monthIndex = "01";
		String monthChar = "";
//		Matcher mat = Pattern.compile(monthRegex).matcher(m);
		Matcher mat = pattMonth.matcher(m);
		if (mat.find()) {
			retMonth[0] = "1";
			for (int i = 1; i <= 12; i++) {
				if (mat.group(i) != null) {
					flag = true;
					if (i <= 9) {
						monthIndex = "0" + i;
					}
					monthChar = m.substring(mat.end());
					//monthIndex = String.format("%0"+ (2 - monthIndex.length()) +"d%s", 0, monthIndex);
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
			if (dateAndYear != null && dateAndYear[0] != null) {
				
				if (!"".equals(dateAndYear[3])) {
				retMonth[1] = dateAndYear[3] + monthIndex;
				}
				else {
					//return null;
					retMonth[1] = "1900" + monthIndex;
				}
				if (!"".equals(dateAndYear[1])) {
					retMonth[1] += dateAndYear[1];
				}
				else {
					retMonth[1] += "01";
				}
				if (!dateAndYear[2].isEmpty()) {
					retMonth[1] += dateAndYear[2];
				}
				else {
					retMonth[1] += monthChar;
				}
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
//		dateAndYear[0] = "01";
//		dateAndYear[1] = "1900";
		boolean dateFlag = false, yearFlag = false, timeFlag = false;
		boolean checkPrev = false;
		String[] retDate = null, retYear = null, retTime = null;
		for (int i = 0; i < 2 && (!dateFlag || !yearFlag); i++) {
			Token tPrev = tokenList[4 - i];
			Token tNext = tokenList[i + 6];
			String tPrevStr = "", tNextStr = "";
			checkPrev = false;
			if (tPrev != null) tPrevStr = tPrev.toString();
			if (tNext != null) tNextStr = tNext.toString();
			//Step1 check for Date (01-30)
			if (tPrevStr != null && !"".equals(tPrevStr)) {
				if (!dateFlag) {
					retDate = checkAndExtractDate(tPrevStr);
					if (retDate != null) {
					//if (!"".equals(retDate)) {
						dateFlag = true;
						checkPrev = true;
						stream.removeAt(stream.getCurrentIndex() - i - 1);
					}
				}
				if (!yearFlag && !checkPrev) {
					retYear = checkAndExtractYear(tPrevStr, "date");
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
			checkPrev = false;
			if (tNextStr != null && !"".equals(tNextStr)) {
				if (!dateFlag) {
					retDate = checkAndExtractDate(tNextStr);
					if (retDate != null) {
					//if (!"".equals(retDate)) {
						dateFlag = true;
						checkPrev = true;
						stream.removeAt(stream.getCurrentIndex() + i + 1);
					}
				}
				if (!yearFlag && !checkPrev) {
					retYear = checkAndExtractYear(tNextStr, "date");
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
	private String[] checkAndExtractYear(String d, String type) {
		// TODO Auto-generated method stub
		String[] retVal = null;
		String lastChar = "";
		Matcher mat = pattYear.matcher(d);
		if (mat.find()) {
			int beginIndex = mat.start();
			int endIndex = mat.end();
			int len = endIndex - beginIndex + 1;
			String yearStr = d.substring(beginIndex, endIndex);
			int yearVal = Integer.parseInt(yearStr);
			if (type.equals("date")) {
				if (d.length() <= len) {
					//Checking if the integer val of date is valid or not
					if (yearVal < 1000 || yearVal > 3000)
						return retVal;
					if (d.length() == len) {
						lastChar = d.charAt(d.length() - 1) +  "";
					}
					retVal = new String[2];
					retVal[0] = lastChar;
					retVal[1] = yearStr;
				}
			}
			else if (type.equals("year")) {
				String yearZone = d.substring(endIndex).toLowerCase();
				boolean flagYear = false;
				//flag for checking if we have to remove
				//next "AD" or "BC" token in the stream;
				boolean flagRemoveToken = false;
				//checking the length of remaining string yearZone
				//is less than three as it can have only have
				//"ad." or "bc," something like that at MAX
				if (yearZone.length() <= 3) {
					yearStr = String.format("%04d", yearVal);
//					yearStr = String.format("%0"+ (4 - yearStr.length())
//					+"d%s", 0, yearStr);
					if (!yearZone.contains("bc") && !yearZone.contains("ad")) {
						//else we have to check the next or prev token
						//get the respective "AD" "BC" if any.
						Token tNext = stream.getTokenAt(stream.getCurrentIndex() + 1);
						if (tNext != null)
							yearZone = tNext.toString() == null ? "" : tNext.toString().toLowerCase();
						flagRemoveToken = true;
					}
					
					if (yearZone.contains("bc")) {
						if (yearZone.length() == 3) {
							lastChar = yearZone.charAt(yearZone.length() - 1) + "";
						}
						//Added minus sign to for "BC";
						yearStr = "-" + yearStr;
						flagYear = true;
					}
					else if (yearZone.contains("ad")) {
						if (yearZone.length() == 3) {
							lastChar = yearZone.charAt(yearZone.length() - 1) + "";
						}
						flagYear = true;
					}
					//flagYear true means current token contains
					//the AD or BC with the year value combined
					//e.g. 1999AD or 500BC.
					if (flagYear) {
						retVal = new String[2];
						retVal[0] = lastChar;
						//Added default month and date
						retVal[1] = yearStr + "0101";
						if (flagRemoveToken) {
							stream.removeAt(stream.getCurrentIndex() + 1);
						}
					}
					else {
						//if there is only year part present in the text
						//then update to default values 
						if (d.length() <= len) {
							//Checking if the integer val of date is valid or not
							if (yearVal < 1000 || yearVal > 3000)
								return retVal;
							if (d.length() == len) {
								lastChar = d.charAt(d.length() - 1) +  "";
							}
							retVal = new String[2];
							retVal[0] = lastChar;
							retVal[1] = yearVal + "0101";
						}
					}
				}
				else {
					if (d.contains("-")) {
						String[] hypenYears = d.split("-");
						if (hypenYears.length == 2) {
							String finalYear = "";
							try {
								String firYearStr = hypenYears[0];
								Integer firstYear = Integer.parseInt(firYearStr);
								String secYearStr = hypenYears[1];
								String remainSecYearStr = "";
								Matcher mSec = pattYear.matcher(secYearStr);
								if (mSec.find()) {
									remainSecYearStr = secYearStr.substring(mSec.end());
									secYearStr = secYearStr.substring(mSec.start(), mSec.end());
								}
								else {
									//no matches for digit
									//so just return without processing
									return null;
								}
								int len1 = firYearStr.length();
								int len2 = secYearStr.length();
								secYearStr = firYearStr.substring(0, len1 - len2) + secYearStr;
								secYearStr += remainSecYearStr;
								String[] retSec = checkAndExtractYear(secYearStr, "year");
								if (retSec == null) {
									return null;
								}
								String[] retFirst = checkAndExtractYear(firYearStr, "year");
								if (retFirst == null) {
									return null;
								}
								retVal = new String[2];
								retVal[0] = retSec[0];
								retVal[1] = retFirst[1] + "-" + retSec[1];
							} catch (Exception e) {
								//System.out.println("Just the way program");
							}
						}
					}
				}
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
		String dateIndex = "01";
		String lastChar = "";
		//Matcher mat = Pattern.compile(digitRegex).matcher(d);
		Matcher mat = pattYear.matcher(d);
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
				dateIndex = String.format("%02d", dateVal);
//				dateIndex = String.format("%0"+ (2 - dateIndex.length()) +"d%s", 0, dateIndex);
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
