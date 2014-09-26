/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo
 * Class responsible for writing indexes to disk
 */
public class IndexWriter {
	/**
	 * Default constructor
	 * @param indexDir : The root directory to be sued for indexing
	 */
	private String indexDir;
	private BaseIndexer biAuthor;
	private BaseIndexer biCategory;
	private BaseIndexer biPlace;
	private BaseIndexer biTerm;

	public IndexWriter(String indexDir) {
		//TODO : YOU MUST IMPLEMENT THIS
		this.indexDir = indexDir;
		this.biAuthor = new BaseIndexer(IndexType.AUTHOR);
		this.biCategory = new BaseIndexer(IndexType.CATEGORY);
		this.biPlace = new BaseIndexer(IndexType.PLACE);
		this.biTerm = new BaseIndexer(IndexType.TERM);
	}

	/**
	 * Method to add the given Document to the index
	 * This method should take care of reading the filed values, passing
	 * them through corresponding analyzers and then indexing the results
	 * for each indexable field within the document. 
	 * @param d : The Document to be added
	 * @throws IndexerException : In case any error occurs
	 */
	public void addDocument(Document d) throws IndexerException {
		//TODO : YOU MUST IMPLEMENT THIS
		biTerm.addDocument(d);
	}

	/**
	 * Method that indicates that all open resources must be closed
	 * and cleaned and that the entire indexing operation has been completed.
	 * @throws IndexerException : In case any error occurs
	 */
	public void close() throws IndexerException {
		//TODO
		Integer[] indexKeys = biTerm.getTermKeys().toArray(new Integer[0]);
		Arrays.sort(indexKeys, biTerm.new SortByFreq());
		FileOutputStream fo;
		try {
			fo = new FileOutputStream(indexDir + java.io.File.separator + "File");
			GZIPOutputStream gzipOut = new GZIPOutputStream(fo);
			ObjectOutputStream oos = new ObjectOutputStream(gzipOut);
			oos.writeObject(biTerm);
			oos.writeObject(indexKeys);
			oos.flush();
			oos.close();
//			fo = new FileOutputStream(indexDir + java.io.File.separator + "File");
//			ObjectOutputStream oos = new ObjectOutputStream(fo);
//			ObjectOutputStream oos = new ObjectOutputStream(fo);
//			oos.writeObject(biTerm);
//			oos.writeObject(indexKeys);
//			oos.close();
			fo.close();
			System.out.println("Writing Process Complete");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Exception in Writer File Not Found");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Exception in Writer IO Exception");
			e.printStackTrace();
		}
	}
}
