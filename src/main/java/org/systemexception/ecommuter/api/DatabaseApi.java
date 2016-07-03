package org.systemexception.ecommuter.api;

import com.tinkerpop.blueprints.Vertex;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.exceptions.TerritoriesException;

import java.io.IOException;

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
	void addTerritories(String fileName) throws CsvParserException, TerritoriesException;

	/**
	 * Returns the vertex given the nodeId
	 *
	 * @param postalCode the node id to retrieve
	 * @return a vertex object
	 */
	Vertex getVertexByPostalCode(String postalCode);

	/**
	 * Drops the database
	 */
	void drop() throws IOException;
}
