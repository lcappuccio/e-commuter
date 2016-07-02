package org.systemexception.ecommuter.api;

/**
 * @author leo
 * @date 02/07/16 23:07
 */
public interface DatabaseApi {

	/**
	 * Sets up the database on the specified folder
	 *
	 * @param dbFolder the database storage folder
	 */
	void initialSetup(String dbFolder);

	/**
	 * Reads all lines from a csv file and creates all nodes
	 *
	 * @param fileName the csv file containing the structure
	 */
	void addTerritories(String fileName);
}
