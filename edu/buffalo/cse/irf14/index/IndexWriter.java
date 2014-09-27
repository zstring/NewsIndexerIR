/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
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
		biAuthor.addDocument(d);
		biCategory.addDocument(d);
		biPlace.addDocument(d);
		biTerm.addDocument(d);
	}

	/**
	 * Method that indicates that all open resources must be closed
	 * and cleaned and that the entire indexing operation has been completed.
	 * @throws IndexerException : In case any error occurs
	 */
	public void close() throws IndexerException {
		//TODO
		String[] fileNames = {"AUTHOR", "CATEGORY", "PLACE", "TERM"};
		Integer[][] termIndexKeys = new Integer[4][]; 
		biAuthor.setDocNum();
		biCategory.setDocNum();
		biPlace.setDocNum();
		biTerm.setDocNum();
		termIndexKeys[0] = biAuthor.getTermKeys().toArray(new Integer[0]);
		termIndexKeys[1] = biCategory.getTermKeys().toArray(new Integer[0]);
		termIndexKeys[2] = biPlace.getTermKeys().toArray(new Integer[0]);
		termIndexKeys[3] = biTerm.getTermKeys().toArray(new Integer[0]);
		Arrays.sort(termIndexKeys[0], biAuthor.new SortByFreq());
		Arrays.sort(termIndexKeys[1], biCategory.new SortByFreq());
		Arrays.sort(termIndexKeys[2], biPlace.new SortByFreq());
		Arrays.sort(termIndexKeys[3], biTerm.new SortByFreq());
		FileOutputStream fo;
		try {
			fo = new FileOutputStream(indexDir + java.io.File.separator + fileNames[0]);
			GZIPOutputStream gzipOut = new GZIPOutputStream(fo);
			ObjectOutputStream oos = new ObjectOutputStream(gzipOut);
			oos.writeObject(biAuthor);
			oos.writeObject(termIndexKeys[0]);
			oos.flush();
			oos.close();
			fo.close();

			fo = new FileOutputStream(indexDir + java.io.File.separator + fileNames[1]);
			gzipOut = new GZIPOutputStream(fo);
			oos = new ObjectOutputStream(gzipOut);
			oos.writeObject(biCategory);
			oos.writeObject(termIndexKeys[1]);
			oos.flush();
			oos.close();
			fo.close();

			fo = new FileOutputStream(indexDir + java.io.File.separator + fileNames[2]);
			gzipOut = new GZIPOutputStream(fo);
			oos = new ObjectOutputStream(gzipOut);
			oos.writeObject(biPlace);
			oos.writeObject(termIndexKeys[2]);
			oos.flush();
			oos.close();
			fo.close();

			fo = new FileOutputStream(indexDir + java.io.File.separator + fileNames[3]);
			gzipOut = new GZIPOutputStream(fo);
			oos = new ObjectOutputStream(gzipOut);
			oos.writeObject(biTerm);
			oos.writeObject(termIndexKeys[3]);
			oos.flush();
			oos.close();
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
