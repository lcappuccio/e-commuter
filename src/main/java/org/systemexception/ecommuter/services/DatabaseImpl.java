package org.systemexception.ecommuter.services;

import org.apache.commons.csv.CSVRecord;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.systemexception.ecommuter.api.DatabaseApi;
import org.systemexception.ecommuter.enums.Constants;
import org.systemexception.ecommuter.enums.CsvHeaders;
import org.systemexception.ecommuter.enums.DatabaseConfiguration;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.exceptions.TerritoriesException;
import org.systemexception.ecommuter.model.*;
import org.systemexception.ecommuter.pojo.CsvParser;
import org.systemexception.ecommuter.pojo.PersonJsonParser;

import javax.annotation.PreDestroy;
import java.io.File;
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
	private GraphDatabaseService graphDb;
	private Index<Node> indexPostalCode, indexPlaceName, indexPerson;
	private RelationshipType livesInRelation = RelationshipType.withName(DatabaseConfiguration.LIVES_IN.toString()),
			worksInRelation = RelationshipType.withName(DatabaseConfiguration.WORKS_IN.toString());
	private RelationshipIndex indexLivesIn, indexWorksIn;
	private Territories territories;

	public DatabaseImpl(final String dbFolder) {
		this.dbFolder = dbFolder;
		initialSetup();
	}

	private void initialSetup() {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File(dbFolder));
		IndexManager indexManager = graphDb.index();
		try (Transaction tx = graphDb.beginTx()) {
			indexPostalCode = indexManager.forNodes(DatabaseConfiguration.POSTAL_CODE.toString());
			indexPlaceName = indexManager.forNodes(DatabaseConfiguration.PLACE_NAME.toString());
			indexPerson = indexManager.forNodes(DatabaseConfiguration.PERSON.toString());
			indexLivesIn = indexManager.forRelationships(DatabaseConfiguration.LIVES_IN.toString());
			indexWorksIn = indexManager.forRelationships(DatabaseConfiguration.WORKS_IN.toString());
			tx.success();
		}
	}

	@Override
	public void addTerritories(final String fileName) throws CsvParserException, TerritoriesException {
		readCsvTerritories(fileName);
		logger.info("AddTerritories: " + fileName);
		// Create all nodes
		for (Territory territory : territories.getTerritories()) {
			try (Transaction tx = graphDb.beginTx()) {
				Node territoryNode = graphDb.createNode();
				territoryNode.setProperty(DatabaseConfiguration.POSTAL_CODE.toString(), territory.getPostalCode());
				territoryNode.setProperty(DatabaseConfiguration.PLACE_NAME.toString(), territory.getPlaceName());
				indexPostalCode.add(territoryNode, DatabaseConfiguration.POSTAL_CODE.toString(),
						territory.getPostalCode());
				indexPlaceName.add(territoryNode, DatabaseConfiguration.PLACE_NAME.toString(),
						territory.getPlaceName());
				tx.success();
			}

			logger.info("AddTerritories territory: " + territory.getPostalCode() + Constants.LOG_SEPARATOR +
					territory.getPlaceName());
		}
		logger.info("Loaded " + fileName);
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
		try (Transaction tx = graphDb.beginTx()) {
			IndexHits<Node> foundPersons = indexPerson.get(DatabaseConfiguration.PERSON.toString(), person);
			if (foundPersons.size() > 0) {
				logger.info("AddPerson: " + person.getName() + Constants.LOG_SEPARATOR + person.getSurname() + " with" +
						" same home and work address already exists");
				return;
			}
			Node homeNode = getNodeByPostalCode(homeAddress.getPostalCode()).get();
			Node workNode = getNodeByPostalCode(workAddress.getPostalCode()).get();
			Node personNode = graphDb.createNode();
			personNode.setProperty(DatabaseConfiguration.PERSON_DATA.toString(),
					PersonJsonParser.fromPerson(person).toString());
			indexPerson.add(personNode, DatabaseConfiguration.PERSON.toString(), person);
			// Add LIVES_IN edge
			logger.info("AddPerson edge: " + DatabaseConfiguration.LIVES_IN.toString() + Constants.LOG_SEPARATOR +
					homeAddress.getPostalCode());
			Relationship livesIn = personNode.createRelationshipTo(homeNode, livesInRelation);
			indexLivesIn.add(livesIn, DatabaseConfiguration.LIVES_IN.toString(),
					DatabaseConfiguration.LIVES_IN.toString());
			// Add WORKS_IN edge
			logger.info("AddPerson edge: " + DatabaseConfiguration.WORKS_IN.toString() + Constants.LOG_SEPARATOR +
					workAddress.getPostalCode());
			Relationship worksIn = personNode.createRelationshipTo(workNode, worksInRelation);
			indexWorksIn.add(worksIn, DatabaseConfiguration.WORKS_IN.toString(),
					DatabaseConfiguration.WORKS_IN.toString());
			logger.info("AddPerson added: " + person.getName() + Constants.LOG_SEPARATOR + person.getSurname() +
					Constants.LOG_SEPARATOR + "lives in " + homeAddress.getPostalCode() + Constants.LOG_SEPARATOR +
					"works in " + workAddress.getPostalCode());
			tx.success();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePerson(Person person) {
		try (Transaction tx = graphDb.beginTx()) {
			tx.success();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Persons findPersonsLivesIn(final String postalCode) {
		List<Node> foundNode = new ArrayList<>();
		Persons foundPersons = new Persons();
		try (Transaction tx = graphDb.beginTx()) {
			Node nodeByPostalCode = getNodeByPostalCode(postalCode).get();
			for (Relationship relationship : nodeByPostalCode.getRelationships(livesInRelation)) {
				foundNode.add(relationship.getStartNode());
			}
			for (Node node : foundNode) {
				String personJson = node.getProperty(DatabaseConfiguration.PERSON_DATA.toString()).toString();
				if (!foundPersons.getPersons().contains(personJson)) {
					foundPersons.addPerson(PersonJsonParser.fromString(personJson));
				}
			}
			tx.success();
		}
		return foundPersons;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Persons findPersonsWorksIn(final String postalCode) {
		List<Node> foundNode = new ArrayList<>();
		Persons foundPersons = new Persons();
		try (Transaction tx = graphDb.beginTx()) {
			Node nodeByPostalCode = getNodeByPostalCode(postalCode).get();
			for (Relationship relationship : nodeByPostalCode.getRelationships(worksInRelation)) {
				foundNode.add(relationship.getStartNode());
			}
			for (Node node : foundNode) {
				String personJson = node.getProperty(DatabaseConfiguration.PERSON_DATA.toString()).toString();
				if (!foundPersons.getPersons().contains(personJson)) {
					foundPersons.addPerson(PersonJsonParser.fromString(personJson));
				}
			}
			tx.success();
		}
		return foundPersons;
	}



	/**
	 * Returns the vertex given the postalCode
	 *
	 * @param postalCode
	 * @return
	 */
	// TODO LC This can return more than one vertex
	private Optional<Node> getNodeByPostalCode(final String postalCode) {
		logger.info("GetVertexByPostalCode: " + postalCode);
		Iterator<Node> nodeIterator = indexPostalCode.get(DatabaseConfiguration.POSTAL_CODE.toString(),
				postalCode).iterator();
		if (nodeIterator.hasNext()) {
			return Optional.of(nodeIterator.next());
		} else {
			logger.info("GetVertexByPostalCode: " + postalCode + " does not exist");
			return Optional.empty();
		}
	}

	/**
	 * Returns the vertex given the placeName
	 *
	 * @param placeName
	 * @return
	 */
	// TODO LC This can return more than one vertex
	private Optional<Node> getNodeByPlaceName(final String placeName) {
		logger.info("GetVertexByPlaceName: " + placeName);
		try (Transaction tx = graphDb.beginTx()) {
			Iterator<Node> nodeIterator = indexPlaceName.get(DatabaseConfiguration.PLACE_NAME.toString(),
					placeName).iterator();
			tx.success();
			if (nodeIterator.hasNext()) {
				return Optional.of(nodeIterator.next());
			} else {
				logger.info("GetVertexByPlaceName: " + placeName + " does not exist");
				return Optional.empty();
			}
		}
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

	@PreDestroy
	private void close() {
		logger.info("Close: started shutdown");
		graphDb.shutdown();
		logger.info("Close: " + dbFolder);
	}

}
