package org.systemexception.ecommuter.services;

import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONWriter;
import org.apache.commons.csv.CSVRecord;
import org.neo4j.index.impl.lucene.LowerCaseKeywordAnalyzer;
import org.neo4j.kernel.impl.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.systemexception.ecommuter.api.DatabaseApi;
import org.systemexception.ecommuter.enums.CsvHeaders;
import org.systemexception.ecommuter.enums.DatabaseConfiguration;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.exceptions.TerritoriesException;
import org.systemexception.ecommuter.model.Territories;
import org.systemexception.ecommuter.model.Territory;
import org.systemexception.ecommuter.pojo.CsvParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

/**
 * @author leo
 * @date 02/07/16 23:23
 */
public class DatabaseImpl implements DatabaseApi {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String logSeparator = ", ";
	private String dbFolder;
	private Neo4jGraph graph;
	private Index<Vertex> index;
	private Territories territories;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialSetup(final String dbFolder) {
		this.dbFolder = dbFolder;
		graph = new Neo4jGraph(dbFolder);
		index = graph.createIndex(DatabaseConfiguration.VERTEX_INDEX.toString(), Vertex.class, new Parameter
				(DatabaseConfiguration.NEO_INDEX_PARAMETER.toString(), LowerCaseKeywordAnalyzer.class.getName()));
	}

	@Override
	public void addTerritories(final String fileName) throws CsvParserException, TerritoriesException {
		readCsvTerritories(fileName);
		logger.info("Loading " + fileName);
		// Create all nodes
		for (Territory territory : territories.getTerritories()) {
			Vertex territoryVertex = graph.addVertex(DatabaseConfiguration.VERTEX_TERRITORY_CLASS.toString());
			territoryVertex.setProperty(DatabaseConfiguration.POSTAL_CODE.toString(), territory.getPostalCode());
			territoryVertex.setProperty(DatabaseConfiguration.PLACE_NAME.toString(), territory.getPlaceName());
			index.put(DatabaseConfiguration.POSTAL_CODE.toString(), territory.getPostalCode(), territoryVertex);
			index.put(DatabaseConfiguration.PLACE_NAME.toString(), territory.getPlaceName(), territoryVertex);
			logger.info("Adding territory: " + territory.getPostalCode() + logSeparator + territory.getPlaceName());
		}
		logger.info("Loaded " + fileName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Vertex getVertexByPostalCode(final String postalCode) {
		Iterator<Vertex> vertexIterator = index.get(DatabaseConfiguration.POSTAL_CODE.toString(),
				postalCode).iterator();
		if (vertexIterator.hasNext()) {
			return vertexIterator.next();
		} else {
			logger.info(postalCode + logSeparator + " postal code does not exist");
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void drop() throws IOException {
		graph.shutdown();
		FileUtils.deleteRecursively(new File(dbFolder));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exportDatabase(String exportFileName) throws IOException {
		OutputStream outStream = new FileOutputStream(exportFileName);
		GraphSONWriter.outputGraph(graph, outStream);
	}

	private void readCsvTerritories(final String fileName) throws CsvParserException, TerritoriesException {
		logger.info("Start loading territories file");
		CsvParser csvParser = new CsvParser(fileName);
		List<CSVRecord> csvRecords = csvParser.readCsvContents();
		territories = new Territories();
		for (CSVRecord csvRecord : csvRecords) {
			String postalCode = csvRecord.get(CsvHeaders.POSTAL_CODE.toString());
			String placeName = csvRecord.get(CsvHeaders.PLACE_NAME.toString());
			Territory territory = new Territory(postalCode, placeName);
			territories.addTerritory(territory);
		}
		logger.info("Finished loading territories file");
	}
}
