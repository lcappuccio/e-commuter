package org.systemexception.ecommuter.api;

/**
 * @author leo
 * @date 02/07/16 23:07
 */
public interface DatabaseApi {

	/**
	 * Reads all lines from a csv file and creates all nodes
	 *
	 * @param fileName the csv file containing the structure
	 */
	void addTerritories(String fileName);
}
