package org.systemexception.ecommuter.services;

import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONWriter;
import org.apache.commons.csv.CSVRecord;
import org.neo4j.index.impl.lucene.LowerCaseKeywordAnalyzer;
import org.neo4j.kernel.impl.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.systemexception.ecommuter.api.DatabaseApi;
import org.systemexception.ecommuter.enums.Constants;
import org.systemexception.ecommuter.enums.CsvHeaders;
import org.systemexception.ecommuter.enums.DatabaseConfiguration;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.exceptions.PersonsException;
import org.systemexception.ecommuter.exceptions.TerritoriesException;
import org.systemexception.ecommuter.model.*;
import org.systemexception.ecommuter.pojo.CsvParser;
import org.systemexception.ecommuter.pojo.PersonJsonParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * @author leo
 * @date 02/07/16 23:23
 */
public class DatabaseImpl implements DatabaseApi {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String dbFolder;
	private Neo4jGraph graph;
	private Index<Vertex> indexPostalCode, indexPlaceName;
	private Territories territories;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialSetup(final String dbFolder) {
		this.dbFolder = dbFolder;
		graph = new Neo4jGraph(dbFolder);
		indexPostalCode = graph.createIndex(DatabaseConfiguration.POSTAL_CODE.toString(), Vertex.class,
				new Parameter(DatabaseConfiguration.NEO_INDEX_PARAMETER.toString(),
						LowerCaseKeywordAnalyzer.class.getName()));
		indexPlaceName = graph.createIndex(DatabaseConfiguration.PLACE_NAME.toString(), Vertex.class,
				new Parameter(DatabaseConfiguration.NEO_INDEX_PARAMETER.toString(),
						LowerCaseKeywordAnalyzer.class.getName()));
	}

	@Override
	public void addTerritories(final String fileName) throws CsvParserException, TerritoriesException {
		readCsvTerritories(fileName);
		logger.info("AddTerritories: " + fileName);
		// Create all nodes
		for (Territory territory : territories.getTerritories()) {
			Vertex territoryVertex = graph.addVertex(DatabaseConfiguration.VERTEX_TERRITORY_CLASS.toString());
			territoryVertex.setProperty(DatabaseConfiguration.POSTAL_CODE.toString(), territory.getPostalCode());
			territoryVertex.setProperty(DatabaseConfiguration.PLACE_NAME.toString(), territory.getPlaceName());
			indexPostalCode.put(DatabaseConfiguration.POSTAL_CODE.toString(), territory.getPostalCode(),
					territoryVertex);
			indexPlaceName.put(DatabaseConfiguration.PLACE_NAME.toString(), territory.getPlaceName(), territoryVertex);
			logger.info("AddTerritories territory: " + territory.getPostalCode() + Constants.LOG_SEPARATOR +
					territory.getPlaceName());
		}
		logger.info("Loaded " + fileName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// TODO LC This can return more than one vertex
	public Optional<Vertex> getVertexByPostalCode(final String postalCode) {
		logger.info("GetVertexByPostalCode: " + postalCode);
		Iterator<Vertex> vertexIterator = indexPostalCode.get(DatabaseConfiguration.POSTAL_CODE.toString(),
				postalCode).iterator();
		if (vertexIterator.hasNext()) {
			return Optional.of(vertexIterator.next());
		} else {
			logger.info("GetVertexByPostalCode: " + postalCode + " does not exist");
			return Optional.empty();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// TODO LC This can return more than one vertex
	public Optional<Vertex> getVertexByPlaceName(final String placeName) {
		logger.info("GetVertexByPlaceName: " + placeName);
		Iterator<Vertex> vertexIterator = indexPlaceName.get(DatabaseConfiguration.PLACE_NAME.toString(),
				placeName).iterator();
		if (vertexIterator.hasNext()) {
			return Optional.of(vertexIterator.next());
		} else {
			logger.info("GetVertexByPlaceName: " + placeName + " does not exist");
			return Optional.empty();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPerson(final Person person) {
		Address homeAddress = person.getHomeAddress();
		Address workAddress = person.getWorkAddress();
		logger.info("AddPerson adding: " + person.getName() + Constants.LOG_SEPARATOR + person.getSurname() +
				Constants.LOG_SEPARATOR + "lives in " + homeAddress.getPostalCode() + Constants.LOG_SEPARATOR +
				"works in " + workAddress.getPostalCode());
		// Get vertices for addresses
		// TODO LC Strategy for non existing vertices. Now will throw java.util.NoSuchElementException
		Vertex homeVertex = getVertexByPostalCode(homeAddress.getPostalCode()).get();
		Vertex workVertex = getVertexByPostalCode(workAddress.getPostalCode()).get();
		Vertex personVertex = graph.addVertex(DatabaseConfiguration.VERTEX_PERSON_CLASS.toString());
		personVertex.setProperty(DatabaseConfiguration.PERSON_DATA.toString(),
				PersonJsonParser.fromPerson(person).toString());
		// Add LIVES_IN edge
		logger.info("AddPerson edge: " + DatabaseConfiguration.LIVES_IN.toString() + Constants.LOG_SEPARATOR +
				homeAddress.getPostalCode());
		Edge livesInEdge = graph.addEdge(null, personVertex, homeVertex, DatabaseConfiguration.LIVES_IN.toString());
		livesInEdge.setProperty(DatabaseConfiguration.EDGE_TYPE.toString(), DatabaseConfiguration.LIVES_IN.toString());
		// Add WORKS_IN edge
		logger.info("AddPerson edge: " + DatabaseConfiguration.WORKS_IN.toString() + Constants.LOG_SEPARATOR +
				workAddress.getPostalCode());
		Edge worksInEdge = graph.addEdge(null, personVertex, workVertex, DatabaseConfiguration.WORKS_IN.toString());
		worksInEdge.setProperty(DatabaseConfiguration.EDGE_TYPE.toString(), DatabaseConfiguration.WORKS_IN.toString());
		logger.info("AddPerson added: " + person.getName() + Constants.LOG_SEPARATOR + person.getSurname() +
				Constants.LOG_SEPARATOR + "lives in " + homeAddress.getPostalCode() + Constants.LOG_SEPARATOR +
				"works in " + workAddress.getPostalCode());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Persons findPersonsLivesIn(final String postalCode) throws PersonsException {
		List<Vertex> foundVertex = new ArrayList<>();
		Vertex vertexByPostalCode = getVertexByPostalCode(postalCode).get();
		for (Edge edge : vertexByPostalCode.getEdges(Direction.IN, DatabaseConfiguration.LIVES_IN.toString())) {
			foundVertex.add(edge.getVertex(Direction.OUT));
		}
		Persons foundPersons = new Persons();
		for (Vertex vertex : foundVertex) {
			String personJson = vertex.getProperty(DatabaseConfiguration.PERSON_DATA.toString());
			if (!foundPersons.getPersons().contains(personJson)) {
				foundPersons.addPerson(PersonJsonParser.fromString(personJson));
			}
		}
		return foundPersons;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Persons findPersonsWorksIn(final String postalCode) throws PersonsException {
		List<Vertex> foundVertex = new ArrayList<>();
		Vertex vertexByPostalCode = getVertexByPostalCode(postalCode).get();
		for (Edge edge : vertexByPostalCode.getEdges(Direction.IN, DatabaseConfiguration.WORKS_IN.toString())) {
			foundVertex.add(edge.getVertex(Direction.OUT));
		}
		Persons foundPersons = new Persons();
		for (Vertex vertex : foundVertex) {
			String personJson = vertex.getProperty(DatabaseConfiguration.PERSON_DATA.toString());
			if (!foundPersons.getPersons().contains(personJson)) {
				foundPersons.addPerson(PersonJsonParser.fromString(personJson));
			}
		}
		return foundPersons;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exportDatabase(String exportFileName) throws IOException {
		OutputStream outStream = new FileOutputStream(exportFileName);
		GraphSONWriter.outputGraph(graph, outStream);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void drop() throws IOException {
		graph.shutdown();
		FileUtils.deleteRecursively(new File(dbFolder));
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
