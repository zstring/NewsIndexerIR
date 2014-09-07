/**
 * 
 */
package edu.buffalo.cse.irf14;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.Parser;
import edu.buffalo.cse.irf14.document.ParserException;
import edu.buffalo.cse.irf14.index.IndexWriter;
import edu.buffalo.cse.irf14.index.IndexerException;

/**
 * @author nikhillo
 *
 */
public class Runner {

	/**
	 * 
	 */
	public Runner() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String ipDir = args[0];
		String indexDir = args[1];
		//more? idk!
		
		File ipDirectory = new File(ipDir);
		String[] catDirectories = ipDirectory.list();
		
		String[] files;
		File dir;
		
		Document d = null;
		IndexWriter writer = new IndexWriter(indexDir);
		String fileAddress = "/home/inspire/Dropbox/UB/JavaWorkspace/newsindexer/news_training/testing/parsedResult.txt";
		try {
			double start_time = System.currentTimeMillis();
			double end_time = 0;
			int fileCount = 0;
			FileWriter fileWriter = new FileWriter(fileAddress);
			BufferedWriter bf = new BufferedWriter(fileWriter);
			for (String cat : catDirectories) {
				dir = new File(ipDir+ File.separator+ cat);
				files = dir.list();
				
				if (files == null)
					continue;
				
				for (String f : files) {
					try {
						d = Parser.parse(dir.getAbsolutePath() + File.separator +f);
						writer.addDocument(d);
						bf.write(fileCount++ + ". ##########################################\n");
						bf.write(d.toString());
						
					} catch (ParserException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
			}
			end_time = System.currentTimeMillis();
			System.out.println("Program Time: "
					+ (end_time - start_time));
			writer.close();
			bf.write("Program Time: " + (end_time - start_time));
			bf.close();
			fileWriter.close();
		} catch (IndexerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
