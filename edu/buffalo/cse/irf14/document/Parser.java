/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nikhillo
 * Class that parses a given file into a Document
 */
public class Parser {
	/** Static method to parse the given file into the Document object.
	 * @param filename : The fully qualified filename to be parsed
	 * @return The parsed and fully loaded Document object
	 * @throws ParserException In case any error occurs during parsing
	 */
	public static Document parse(String filename) throws ParserException {
		// TODO YOU MUST IMPLEMENT THIS
		Document d = new Document();
			int lastIndex = filename.lastIndexOf(File.separator)
					, flag = 0;
			String categoryPath = filename.substring(0, lastIndex);
			String fileIdWithExtn = filename.substring(lastIndex + 1);
			String fileId = fileIdWithExtn.replaceFirst("[.][^.]+$"
					, "");
			String category = categoryPath.substring(categoryPath.lastIndexOf
					(File.separator) + 1);
			d.setField(FieldNames.FILEID, fileId);
			d.setField(FieldNames.CATEGORY, category);
			String line;
			StringBuilder content = new StringBuilder();
			//Had to use it because Document.SetField() accepts a String[]
			String[] strAuthors = null;				
			List<String> authors = new ArrayList<String>();   
			try {
				FileReader reader = new FileReader(filename);
				BufferedReader buff = new BufferedReader(reader);
			// Had to use it because lists were easily modified.

			while ((line = buff.readLine()) != null) {
				line = line.trim();
				if (!line.equals("")) {
					//Parse the first line as titile.
					if (flag == 0) {
						d.setField(FieldNames.TITLE,line);
						flag = 1;
					}
					//If the second line has <AUTHOR> tag, call extractAuthor function to parse auhtors and organization.
					else if(flag == 1 && line.contains("<AUTHOR>")) {
						authors = extractAuthor(line);
						strAuthors = authors.subList(1, authors.size()).toArray(new String[authors.size() - 1]);

						if(authors.get(0).equals("n")) {
							d.setField(FieldNames.AUTHOR, strAuthors);
						}
						else {
							d.setField(FieldNames.AUTHOR, Arrays.copyOfRange(strAuthors, 1, strAuthors.length - 1));
							d.setField(FieldNames.AUTHORORG, strAuthors[strAuthors.length - 1]);
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
						content.append("\n" + line);
					}
				}
			}
			d.setField(FieldNames.CONTENT, content.toString());	
			buff.close();
		} catch (Exception e) {
			System.out.println("Error Occurred " + e.getMessage());
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
		try {
		String months = "(-)|((jan)|(feb)|(mar)|(apr)|(may)|(jun)|"
				+ "(jul)|(aug)|(sep)|(oct)|(nov)|(dec))";
		Matcher mat = Pattern.compile(months,
				Pattern.CASE_INSENSITIVE).matcher(line);
		int hyphenIndex = -1, monthIndex = -1;
		String place = "", date = "", content = "";
		
			while (mat.find()) {
				if ("-".equals(mat.group())) {
					hyphenIndex = mat.start();
				}
				else if (mat.group() != null) {
					monthIndex = mat.start();
				}
			}
			if (hyphenIndex == -1) {
				content = line;
			}
			else if (monthIndex == -1) {
				place = line.substring(0, hyphenIndex).trim();
				content = line.substring(hyphenIndex).trim();
			}
			else if (monthIndex == 0) {
				date = line.substring(0, hyphenIndex).trim();
				content = line.substring(hyphenIndex).trim();
			}
			else 
			{
				place = line.substring(0, monthIndex).trim();
				if (place.endsWith(",")) {
					place = place.substring(0, place.length() - 1);
				}
				date = line.substring(monthIndex, hyphenIndex).trim();
				content = line.substring(hyphenIndex).trim();
			}
			extract.add(place);
			extract.add(date);
			extract.add(content);
		} catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
		return extract;
	}

	/**
	 * Function to extract parse line with tag <AUTHOR> </AUTHOR>
	 * @param line
	 * @return
	 */
	public static List<String> extractAuthor(final String line) {
		List <String> authors = new ArrayList<String>();
		try {
			int authorStartIndex = 0, authorEndIndex = 0, orgStartIndex = 0;
			String orgName = null;
			//if first element = n, there is no organization name in the list
			authors.add("n");
			String regex = " (by) | (and) |,|</author>";
			Matcher mat = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(line);
			while(mat.find()) {
				if(mat.group().equalsIgnoreCase(" by ")) {
					authorStartIndex = mat.end(); 
				} 
				if(mat.group().equalsIgnoreCase(" and ")) {
					authorEndIndex = mat.start();
					authors.add(line.substring(authorStartIndex, authorEndIndex).trim());
					authorStartIndex = authorEndIndex + 4;
				}
				if(mat.group().equals(",")) {
					authorEndIndex = mat.start();
					authors.add(line.substring(authorStartIndex, authorEndIndex).trim());
					orgStartIndex = authorEndIndex + 1;
				}
				if(mat.group().equalsIgnoreCase("</AUTHOR>")) {
					if (orgStartIndex != 0) {
						orgName = line.substring(orgStartIndex, mat.start()).trim();
					}
					else {
						authors.add(line.substring(authorStartIndex,mat.start()).trim());
					}
				}
			}
			if (orgName != null) {
				//Setting it as y so that calling function know that the last 
				//element is organization name.
				authors.set(0, "y");
				authors.add(orgName);
			}
		} catch (Exception e)
		{
			System.out.println(" Error ");
		}
		return authors;
	}
}
