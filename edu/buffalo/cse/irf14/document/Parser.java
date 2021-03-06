/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.irf14.analysis.TokenStream;



/**
 * @author nikhillo
 * Class that parses a given file into a Document
 */
public class Parser {
	private static Matcher matMonth;
	private static Matcher matAuthor;
	static {
		String months = "(jan)|(feb)|(mar)|(apr)|(may)|(jun)|"
				+ "(jul)|(aug)|(sep)|(oct)|(nov)|(dec)";
		matMonth = Pattern.compile(months, Pattern.CASE_INSENSITIVE).matcher("");
		String regex = " (by) | (and) |,|</author>";
		matAuthor = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher("");
	}
	/** Static method to parse the given file into the Document object.
	 * @param filename : The fully qualified filename to be parsed
	 * @return The parsed and fully loaded Document object
	 * @throws ParserException In case any error occurs during parsing
	 */
	public static Document parse(String filename) throws ParserException {
		// TODO YOU MUST IMPLEMENT THIS
		if (filename == null || filename.isEmpty()) {
			throw new ParserException();
		}
		Document d = new Document();
		
		FileReader reader;
		BufferedReader buff;
		try {
			reader = new FileReader(filename);
			buff = new BufferedReader(reader);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
			throw new ParserException();
		}
		
		try {
			int lastIndex = filename.lastIndexOf(File.separator)
					, flag = 0;
			String categoryPath, fileIdWithExtn, fileId, category;
			if (lastIndex == -1) {
				categoryPath = "";
				fileIdWithExtn = filename;
				category = "";
			} else {
				categoryPath = filename.substring(0, lastIndex);
				fileIdWithExtn = filename.substring(lastIndex + 1);
				category = categoryPath.substring(categoryPath.lastIndexOf
						(File.separator) + 1);
			}
			fileId = fileIdWithExtn.replaceFirst("[.][^.]+$", "");
			d.setField(FieldNames.FILEID, fileId);
			d.setField(FieldNames.CATEGORY, category);
			String line;
			StringBuilder content = new StringBuilder();
			//Had to use it because Document.SetField() accepts a String[]
//			String[] strAuthors = null;
			String strAuthors = "";
			List<String> authors = new ArrayList<String>();
			// Had to use it because lists were easily modified.
			while ((line = buff.readLine()) != null) {
				line = line.trim();
				if (!line.isEmpty()) {
					//Parse the first line as titile.
					if (flag == 0) {
						d.setField(FieldNames.TITLE,line);
						flag = 1;
					}
					//If the second line has <AUTHOR> tag, call extractAuthor function to parse auhtors and organization.
					else if(flag == 1 && line.contains("<AUTHOR>")) {
						authors = extractAuthor(line);
						if (authors.size() > 1) {
//							strAuthors = authors.subList(1, authors.size()).toArray(new String[authors.size() - 1]);
							strAuthors = authors.get(1);
							if(authors.get(0).equals("n")) {
								d.setField(FieldNames.AUTHOR, strAuthors);
							}
							else {
//								d.setField(FieldNames.AUTHOR, Arrays.copyOfRange(strAuthors, 0, strAuthors.length - 1));
//								d.setField(FieldNames.AUTHORORG, strAuthors[strAuthors.length - 1]);
								d.setField(FieldNames.AUTHOR, strAuthors);
								d.setField(FieldNames.AUTHORORG, authors.get(2));
							}
						}
						flag = 2;
					}
					else if (flag == 1 || flag == 2) {
						//To Implement: Parse line with place,date and content.
						List<String> placeDate = extractPlaceAndDate(line);
						d.setField(FieldNames.PLACE, placeDate.get(0));
						d.setField(FieldNames.NEWSDATE, placeDate.get(1));
						content.append(placeDate.get(2));
						flag = 3;
					}
					else {
						content.append(" " + line);
					}
				}
			}
			d.setField(FieldNames.CONTENT, content.toString());	
			buff.close();
		} catch (Exception e) {
			System.out.println("Error Occurred in Parser" + e.getMessage());

		}
		return d;
	}

	/**
	 * This method extract the Place, date and remaining 
	 * content of the line.
	 * @param line a String line that to be parsed
	 * @return the extracted place, date and content from that line
	 */
	public static List<String> extractPlaceAndDate(String line) {
		List<String> extract = new ArrayList<String>();
		String place = "", date = "", content = "";
		try {
			if (line.contains("-")) {
				content = line.substring(line.indexOf("-")+1);
				line = line.substring(0,line.indexOf("-"));
				Matcher mat = matMonth.reset(line);
				int monthIndex = -1;
				if (mat.find()) {
					monthIndex = mat.start();
				}
				if (monthIndex == -1) {
					place = line.trim();
				}
				else {
					place = line.substring(0, monthIndex).trim();
					if (place.endsWith(",")) {
						place = place.substring(0, place.length() - 1);
					}
					date = line.substring(monthIndex).trim();
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		extract.add(place);
		extract.add(date);
		extract.add(content);
		return extract;
	}

	 /** Function to extract parse line with tag <AUTHOR> </AUTHOR>
	 * @param line
	 * @return
	 */
	public static List<String> extractAuthor(final String line) {
		List <String> authors = new ArrayList<String>();
		String author = "", authorOrg = "";
		try {
			int authorStartIndex = 0, authorEndIndex = 0, orgStartIndex = 0;
			String orgName = null;
			//if first element = n, there is no organization name in the list
			authors.add("n");
			Matcher mat = matAuthor.reset(line);
			while (mat.find()) {
				if (mat.group().equalsIgnoreCase(" by ")) {
					authorStartIndex = mat.end();
					}
				else if (mat.group().equalsIgnoreCase(" and ")) {
					authorEndIndex = mat.start();
//					authors.add(line.substring(authorStartIndex, authorEndIndex).trim());
					author = author + line.substring(authorStartIndex, authorEndIndex).trim() + ", ";
					authorStartIndex = authorEndIndex + 4;
				}
				else if (mat.group().equals(",")) {
					authorEndIndex = mat.start();
//					authors.add(line.substring(authorStartIndex, authorEndIndex).trim());
					author += line.substring(authorStartIndex, authorEndIndex).trim() + ", ";
					orgStartIndex = authorEndIndex + 1;
				}
				else if (mat.group().equalsIgnoreCase("</AUTHOR>")) {
					if (orgStartIndex != 0) {
						orgName = line.substring(orgStartIndex, mat.start()).trim();
					}
					else {
//						authors.add(line.substring(authorStartIndex,mat.start()).trim());
						author += line.substring(authorStartIndex,mat.start()).trim() + " ";
					}
				}
			}
			authors.add(author.trim());
			if (orgName != null) {
				//Setting it as y so that calling function know that the last
				//element is organization name.
				authors.set(0, "y");
				authors.add(orgName);
			}
		} catch (Exception e) {
			System.out.println(" Error while extracting author "
					+ "during parser" + e.getMessage());
		}
		return authors;
	}
}
