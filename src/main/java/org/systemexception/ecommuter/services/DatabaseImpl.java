package org.systemexception.ecommuter.services;

import org.apache.commons.csv.CSVRecord;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.neo4j.graphdb.schema.ConstraintCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.systemexception.ecommuter.api.DatabaseApi;
import org.systemexception.ecommuter.enums.Constants;
import org.systemexception.ecommuter.enums.CsvHeaders;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.exceptions.TerritoriesException;
import org.systemexception.ecommuter.model.*;
import org.systemexception.ecommuter.pojo.CsvParser;
import org.systemexception.ecommuter.pojo.PersonJsonParser;

import javax.annotation.PreDestroy;
import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.systemexception.ecommuter.enums.DatabaseConfiguration.*;

/**
 * @author leo
 * @date 02/07/16 23:23
 */
public class DatabaseImpl implements DatabaseApi {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final GraphDatabaseService graphDb;
	private final Index<Node> indexPostalCode, indexPersonId;
	private final RelationshipType livesInRelation = RelationshipType.withName(LIVES_IN.toString());
	private final RelationshipType worksInRelation = RelationshipType.withName(WORKS_IN.toString());
	private final RelationshipIndex indexLivesIn, indexWorksIn;
	private Territories territories;

	public DatabaseImpl(final String dbFolder) {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File(dbFolder));
		ConstraintCreator constraintCreator;
		IndexManager indexManager = graphDb.index();
		try (Transaction tx = graphDb.beginTx()) {
			indexPostalCode = indexManager.forNodes(POSTAL_CODE.toString());
			indexPersonId = indexManager.forNodes(PERSON_ID.toString());
			constraintCreator = graphDb.schema().constraintFor(Label.label(PERSON_ID.toString()))
					.assertPropertyIsUnique(PERSON_ID.toString());
			indexLivesIn = indexManager.forRelationships(LIVES_IN.toString());
			indexWorksIn = indexManager.forRelationships(WORKS_IN.toString());
			tx.success();
		}
		try (Transaction tx = graphDb.beginTx()) {
			constraintCreator.create();
			logger.info("DatabaseImpl" + Constants.LOG_OBJECT_SEPARATOR + dbFolder);
			tx.success();
		}
	}

	@Override
	public void addTerritories(final File territoriesFile) throws CsvParserException, TerritoriesException {
		readCsvTerritories(territoriesFile);
		logger.info("addTerritories" + Constants.LOG_OBJECT_SEPARATOR + territoriesFile.getName());
		// Create all nodes
		try (Transaction tx = graphDb.beginTx()) {
			for (Territory territory : territories.getTerritories()) {
				Node territoryNode = graphDb.createNode();
				territoryNode.setProperty(POSTAL_CODE.toString(), territory.getPostalCode());
				territoryNode.setProperty(PLACE_NAME.toString(), territory.getPlaceName());
				indexPostalCode.add(territoryNode, POSTAL_CODE.toString(), territory.getPostalCode());
				logger.info("addedTerritory" + Constants.LOG_OBJECT_SEPARATOR + territory.getPostalCode() +
						Constants.LOG_ITEM_SEPARATOR + territory.getPlaceName());
			}
			tx.success();
		}
		logger.info("loadedTerritories" + Constants.LOG_OBJECT_SEPARATOR + territoriesFile.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Person addPerson(final Person person) throws TerritoriesException {
		Address homeAddress = person.getHomeAddress();
		Address workAddress = person.getWorkAddress();
		logger.info("addPerson" + Constants.LOG_OBJECT_SEPARATOR + person.getId() + Constants.LOG_ITEM_SEPARATOR +
				person.getName() + Constants.LOG_ITEM_SEPARATOR + person.getSurname() + Constants.LOG_ITEM_SEPARATOR +
				"lives in " + person.getHomeAddress().getPostalCode() + Constants.LOG_ITEM_SEPARATOR +
				"works in " + person.getWorkAddress().getPostalCode());
		// Get nodes for addresses
		try (Transaction tx = graphDb.beginTx()) {
			Optional<Node> homeNode = getNodeByPostalCode(homeAddress.getPostalCode());
			Optional<Node> workNode = getNodeByPostalCode(workAddress.getPostalCode());
			if (homeNode.isPresent() && workNode.isPresent()) {
				Node personNode = graphDb.createNode();
				personNode.setProperty(PERSON_DATA.toString(), PersonJsonParser.fromPerson(person).toString());
				personNode.setProperty(PERSON_ID.toString(), person.getId());
				try {
					personNode.addLabel(Label.label(PERSON_ID.toString()));
				} catch (ConstraintViolationException ex) {
					String message = ex.getMessage();
					tx.failure();
					logger.error("addPerson" + Constants.LOG_OBJECT_SEPARATOR + person.getId() + Constants
							.LOG_ITEM_SEPARATOR + person.getName() + Constants.LOG_ITEM_SEPARATOR +
							person.getSurname() + Constants.LOG_ITEM_SEPARATOR + message);
					throw new InvalidParameterException();
				}
				indexPersonId.add(personNode, PERSON_ID.toString(), person.getId());
				// Add LIVES_IN edge
				Relationship livesIn = personNode.createRelationshipTo(homeNode.get(), livesInRelation);
				indexLivesIn.add(livesIn, LIVES_IN.toString(), homeAddress.getPostalCode());
				// Add WORKS_IN edge
				Relationship worksIn = personNode.createRelationshipTo(workNode.get(), worksInRelation);
				indexWorksIn.add(worksIn, WORKS_IN.toString(), workAddress.getPostalCode());
				logger.info("addedPerson" + Constants.LOG_OBJECT_SEPARATOR + person.getId() +
						Constants.LOG_ITEM_SEPARATOR + person.getName() + Constants.LOG_ITEM_SEPARATOR +
						person.getSurname() + Constants.LOG_ITEM_SEPARATOR + "lives in " +
						person.getHomeAddress().getFormattedAddress() + Constants.LOG_ITEM_SEPARATOR + "works in " +
						person.getWorkAddress().getFormattedAddress());
			} else {
				String errorMessage = "Non existing territory";
				logger.error("addedPerson" + Constants.LOG_OBJECT_SEPARATOR + person.getId() +
						Constants.LOG_ITEM_SEPARATOR + person.getName() + Constants.LOG_ITEM_SEPARATOR +
						person.getSurname() + Constants.LOG_ITEM_SEPARATOR + errorMessage);
				throw new TerritoriesException(errorMessage);
			}
			tx.success();
		}
		return person;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Person updatePerson(Person person) {
		logger.info("updatePerson" + Constants.LOG_OBJECT_SEPARATOR + person.getId() + Constants.LOG_ITEM_SEPARATOR +
				person.getName() + Constants.LOG_ITEM_SEPARATOR + person.getSurname());
		try (Transaction tx = graphDb.beginTx()) {
			IndexHits<Node> nodes = indexPersonId.get(PERSON_ID.toString(), person.getId());
			Node personNode = nodes.getSingle();
			if (personNode != null) {
				personNode.setProperty(PERSON_DATA.toString(), PersonJsonParser.fromPerson(person).toString());
				tx.success();
			} else {
				logger.info("updatedPersonNotFound" + Constants.LOG_OBJECT_SEPARATOR + person.getId() +
						Constants.LOG_ITEM_SEPARATOR + person.getName() + Constants.LOG_ITEM_SEPARATOR +
						person.getSurname());
				tx.terminate();
			}
		}
		logger.info("updatedPerson" + Constants.LOG_OBJECT_SEPARATOR + person.getId() + Constants.LOG_ITEM_SEPARATOR +
				person.getName() + Constants.LOG_ITEM_SEPARATOR + person.getSurname());
		return person;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePerson(Person person) {
		logger.info("deletePerson" + Constants.LOG_OBJECT_SEPARATOR + person.getId() + Constants.LOG_ITEM_SEPARATOR +
				person.getName() + Constants.LOG_ITEM_SEPARATOR + person.getSurname());
		try (Transaction tx = graphDb.beginTx()) {
			IndexHits<Node> nodes = indexPersonId.get(PERSON_ID.toString(), person.getId());
			Node personNode = nodes.getSingle();
			while (personNode.getRelationships().iterator().hasNext()) {
				personNode.getRelationships().iterator().next().delete();
			}
			personNode.delete();
			tx.success();
		}
		logger.info("deletedPerson" + Constants.LOG_OBJECT_SEPARATOR + person.getId() + Constants.LOG_ITEM_SEPARATOR +
				person.getName() + Constants.LOG_ITEM_SEPARATOR + person.getSurname());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Persons findPersonsLivesIn(final String postalCode) {
		logger.info("findPersonsLivingIn" + Constants.LOG_OBJECT_SEPARATOR + postalCode);
		return getPersons(postalCode, livesInRelation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Persons findPersonsWorksIn(final String postalCode) {
		logger.info("findPersonsWorkingIn" + Constants.LOG_OBJECT_SEPARATOR + postalCode);
		return getPersons(postalCode, worksInRelation);
	}

	/**
	 * Returns a node given the postalCode
	 *
	 * @param postalCode
	 * @return an optional with the node of the postal code
	 */
	// TODO LC This can return more than one node
	private Optional<Node> getNodeByPostalCode(final String postalCode) {
		logger.info("getNodeByPostalCode" + Constants.LOG_OBJECT_SEPARATOR + postalCode);
		Iterator<Node> nodeIterator = indexPostalCode.get(POSTAL_CODE.toString(), postalCode).iterator();
		if (nodeIterator.hasNext()) {
			return Optional.of(nodeIterator.next());
		} else {
			logger.info("getNodeByPostalCode" + Constants.LOG_OBJECT_SEPARATOR + postalCode + " does not exist");
			return Optional.empty();
		}
	}

	/**
	 * Returns Persons for the given postal code and territory relation
	 *
	 * @param postalCode
	 * @param relationshipType is the relationship type to seek after (works in, lives in)
	 * @return the list of persons with the given relationship to this node
	 */
	private Persons getPersons(String postalCode, RelationshipType relationshipType) {
		logger.info("getPersonsByPostalCodeRelation" + Constants.LOG_OBJECT_SEPARATOR + relationshipType.toString() +
				Constants.LOG_ITEM_SEPARATOR + postalCode);
		List<Node> foundNode = new ArrayList<>();
		Persons foundPersons = new Persons();
		try (Transaction tx = graphDb.beginTx()) {
			Optional<Node> nodeByPostalCode = getNodeByPostalCode(postalCode);
			if (nodeByPostalCode.isPresent()) {
				for (Relationship relationship : nodeByPostalCode.get().getRelationships(relationshipType)) {
					foundNode.add(relationship.getStartNode());
				}
				for (Node node : foundNode) {
					String personJson = node.getProperty(PERSON_DATA.toString()).toString();
					if (!foundPersons.getPersons().contains(personJson)) {
						logger.info("getPersonsByPostalCodeRelation" + Constants.LOG_OBJECT_SEPARATOR +
								relationshipType.toString() + Constants.LOG_ITEM_SEPARATOR + postalCode);
						foundPersons.addPerson(PersonJsonParser.fromString(personJson));
					}
				}
			}
			tx.success();
		}
		return foundPersons;
	}

	private void readCsvTerritories(final File territoriesFile) throws CsvParserException, TerritoriesException {
		logger.info("readCsvTerritories" + Constants.LOG_OBJECT_SEPARATOR + territoriesFile.getName());
		CsvParser csvParser = new CsvParser(territoriesFile);
		List<CSVRecord> csvRecords = csvParser.readCsvContents();
		territories = new Territories();
		for (CSVRecord csvRecord : csvRecords) {
			String postalCode = csvRecord.get(CsvHeaders.POSTAL_CODE.toString());
			String placeName = csvRecord.get(CsvHeaders.PLACE_NAME.toString());
			Territory territory = new Territory(postalCode, placeName);
			territories.addTerritory(territory);
		}
		logger.info("finishCsvTerritories" + Constants.LOG_OBJECT_SEPARATOR + territoriesFile.getName());
	}

	@PreDestroy
	private void close() {
		logger.info("closeDatabase");
		graphDb.shutdown();
	}

}
