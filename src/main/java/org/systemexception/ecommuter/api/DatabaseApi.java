package org.systemexception.ecommuter.api;

import com.tinkerpop.blueprints.Vertex;
import org.springframework.stereotype.Service;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.exceptions.TerritoriesException;

import java.io.IOException;

/**
 * @author leo
 * @date 02/07/16 23:07
 */
@Service
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
	 * Exports the database
	 * WARNING: Export doesn't lock your database, but browses it. This means that concurrent operation can be
	 * executed during the export
	 *
	 * @param exportFileName the file name of the export
	 */
	void exportDatabase(String exportFileName) throws IOException;

	/**
	 * Drops the database
	 */
	void drop() throws IOException;

}
