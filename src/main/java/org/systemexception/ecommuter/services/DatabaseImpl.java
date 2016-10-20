package org.systemexception.ecommuter.services;

import org.apache.commons.csv.CSVRecord;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.neo4j.graphdb.schema.ConstraintDefinition;
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
import java.util.HashSet;
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
	private Index<Node> indexPostalCode, indexPersonId;
	private RelationshipIndex indexLivesIn, indexWorksIn;
	private final RelationshipType livesInRelation = RelationshipType.withName(LIVES_IN.toString());
	private final RelationshipType worksInRelation = RelationshipType.withName(WORKS_IN.toString());
	private final Label constraintPersonIdLabel = Label.label(PERSON_ID.toString()),
			postalCodeLabel = Label.label(POSTAL_CODE.toString());
	private Territories territories;

	public DatabaseImpl(final String dbFolder) {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File(dbFolder));
		IndexManager indexManager = graphDb.index();
		createIndexes(indexManager);
		createSchema();
		logger.info("DatabaseImpl" + Constants.LOG_OBJECT_SEPARATOR + dbFolder);
	}

	@Override
	public void addTerritories(final File territoriesFile) throws CsvParserException {
		readCsvTerritories(territoriesFile);
		logger.info("addTerritories" + Constants.LOG_OBJECT_SEPARATOR + territoriesFile.getName());
		// Create all nodes
		try (Transaction tx = graphDb.beginTx()) {
			for (Territory territory : territories.getTerritories()) {
				Node territoryNode = graphDb.createNode();
				territoryNode.setProperty(COUNTRY.toString(), territory.getCountry());
				territoryNode.setProperty(POSTAL_CODE.toString(), territory.getPostalCode());
				territoryNode.setProperty(PLACE_NAME.toString(), territory.getPlaceName());
				indexPostalCode.add(territoryNode, POSTAL_CODE.toString(), territory.getPostalCode());
				logger.info("addedTerritory" + Constants.LOG_OBJECT_SEPARATOR + territory.getCountry() +
						Constants.LOG_ITEM_SEPARATOR + territory.getPostalCode() + Constants.LOG_ITEM_SEPARATOR +
						territory.getPlaceName());
			}
			tx.success();
		}
		logger.info("addedTerritories" + Constants.LOG_OBJECT_SEPARATOR + territoriesFile.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Person addPerson(final Person person) throws TerritoriesException {
		Address homeAddress = person.getHomeAddress();
		Address workAddress = person.getWorkAddress();
		logger.info("addPerson" + Constants.LOG_OBJECT_SEPARATOR + person.getId() + Constants.LOG_ITEM_SEPARATOR +
				"lives in " + person.getHomeAddress().getCountry() + Constants.LOG_ITEM_SEPARATOR +
				person.getHomeAddress().getPostalCode() + Constants.LOG_ITEM_SEPARATOR +
				"works in " + person.getWorkAddress().getCountry() + Constants.LOG_ITEM_SEPARATOR +
				person.getWorkAddress().getPostalCode());
		Optional<Node> homeNode = getNodeByPostalCode(homeAddress.getCountry(), homeAddress.getPostalCode());
		Optional<Node> workNode = getNodeByPostalCode(workAddress.getCountry(), workAddress.getPostalCode());
		if (homeNode.isPresent() && workNode.isPresent()) {
			insertPerson(person, homeNode.get(), workNode.get());
		} else {
			String errorMessage = "Non existing territory";
			logger.error("addPerson" + Constants.LOG_OBJECT_SEPARATOR + person.getId() +
					Constants.LOG_ITEM_SEPARATOR + errorMessage);
			throw new TerritoriesException(errorMessage);
		}
		return person;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Person updatePerson(Person person) {
		logger.info("updatePerson" + Constants.LOG_OBJECT_SEPARATOR + person.getId());
		try (Transaction tx = graphDb.beginTx()) {
			IndexHits<Node> nodes = indexPersonId.get(PERSON_ID.toString(), person.getId());
			Node personNode = nodes.getSingle();
			if (personNode != null) {
				personNode.setProperty(PERSON_DATA.toString(), PersonJsonParser.fromPerson(person).toString());
				tx.success();
			} else {
				logger.info("updatedPersonNotFound" + Constants.LOG_OBJECT_SEPARATOR + person.getId());
				tx.terminate();
			}
		}
		logger.info("updatedPerson" + Constants.LOG_OBJECT_SEPARATOR + person.getId());
		return person;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePerson(Person person) {
		logger.info("deletePerson" + Constants.LOG_OBJECT_SEPARATOR + person.getId());
		try (Transaction tx = graphDb.beginTx()) {
			IndexHits<Node> nodes = indexPersonId.get(PERSON_ID.toString(), person.getId());
			Node personNode = nodes.getSingle();
			while (personNode.getRelationships().iterator().hasNext()) {
				personNode.getRelationships().iterator().next().delete();
			}
			personNode.delete();
			tx.success();
		}
		logger.info("deletedPerson" + Constants.LOG_OBJECT_SEPARATOR + person.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Persons findPersonsLivesIn(final String country, final String postalCode) {
		logger.info("findPersonsLivingIn" + Constants.LOG_OBJECT_SEPARATOR + country + Constants.LOG_ITEM_SEPARATOR +
				postalCode);
		return getPersons(country, postalCode, livesInRelation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Persons findPersonsWorksIn(final String country, final String postalCode) {
		logger.info("findPersonsWorkingIn" + Constants.LOG_OBJECT_SEPARATOR + country + Constants.LOG_ITEM_SEPARATOR +
				postalCode);
		return getPersons(country, postalCode, worksInRelation);
	}

	/**
	 * Returns a node given the postalCode
	 *
	 * @param country
	 * @param postalCode
	 * @return an optional with the node of the postal code
	 */
	// TODO LC This could be more than one node!
	private Optional<Node> getNodeByPostalCode(final String country, final String postalCode) {
		logger.info("getNodeByPostalCode" + Constants.LOG_OBJECT_SEPARATOR + country + Constants.LOG_ITEM_SEPARATOR +
				postalCode);
		try (Transaction tx = graphDb.beginTx()) {
			Iterator<Node> nodeIterator = indexPostalCode.get(POSTAL_CODE.toString(), postalCode).iterator();
			tx.success();
			if (nodeIterator.hasNext()) {
				Node node = nodeIterator.next();
				String nodeCountry = node.getProperty(COUNTRY.toString()).toString();
				if (country.equals(nodeCountry)) {
					return Optional.of(node);
				} else {
					logger.info("getNodeByPostalCode" + Constants.LOG_OBJECT_SEPARATOR + country +
							Constants.LOG_ITEM_SEPARATOR + postalCode + " does not exist");
					return Optional.empty();
				}
			} else {
				logger.error("getNodeByPostalCode" + Constants.LOG_OBJECT_SEPARATOR + country +
						Constants.LOG_ITEM_SEPARATOR + postalCode + " node missing");
				return Optional.empty();
			}
		}
	}

	/**
	 * Insert person to the database
	 *
	 * @param person
	 * @param homeNode
	 * @param workNode
	 */
	private void insertPerson(final Person person, final Node homeNode, final Node workNode) {
		try (Transaction tx = graphDb.beginTx()) {
			Node personNode = graphDb.createNode();
			personNode.setProperty(PERSON_DATA.toString(), PersonJsonParser.fromPerson(person).toString());
			personNode.setProperty(PERSON_ID.toString(), person.getId());
			try {
				personNode.addLabel(constraintPersonIdLabel);
			} catch (ConstraintViolationException ex) {
				String errorMessage = ex.getMessage();
				tx.failure();
				logger.error(person.getId() + Constants.LOG_ITEM_SEPARATOR + Constants.LOG_ITEM_SEPARATOR +
						errorMessage);
				throw new ConstraintViolationException(errorMessage);
			}
			indexPersonId.add(personNode, PERSON_ID.toString(), person.getId());
			// Add LIVES_IN edge
			Relationship livesIn = personNode.createRelationshipTo(homeNode, livesInRelation);
			indexLivesIn.add(livesIn, LIVES_IN.toString(), homeNode.getProperty(POSTAL_CODE.toString()));
			// Add WORKS_IN edge
			Relationship worksIn = personNode.createRelationshipTo(workNode, worksInRelation);
			indexWorksIn.add(worksIn, WORKS_IN.toString(), workNode.getProperty(POSTAL_CODE.toString()));
			logger.info("addedPerson" + Constants.LOG_OBJECT_SEPARATOR + person.getId());
			tx.success();
		}
	}

	/**
	 * Returns Persons for the given postal code and territory relation
	 *
	 * @param country
	 * @param postalCode
	 * @param relationshipType is the relationship type to seek after (works in, lives in)
	 * @return the list of persons with the given relationship to this node
	 */
	private Persons getPersons(String country, String postalCode, RelationshipType relationshipType) {
		logger.info("getPersonsByPostalCodeRelation" + Constants.LOG_OBJECT_SEPARATOR + relationshipType.toString() +
				Constants.LOG_ITEM_SEPARATOR + country + Constants.LOG_ITEM_SEPARATOR + postalCode);
		HashSet<Node> foundNodes = new HashSet<>();
		Persons foundPersons = new Persons();
		try (Transaction tx = graphDb.beginTx()) {
			Optional<Node> nodeByPostalCode = getNodeByPostalCode(country, postalCode);
			if (nodeByPostalCode.isPresent()) {
				for (Relationship relationship : nodeByPostalCode.get().getRelationships(relationshipType)) {
					foundNodes.add(relationship.getStartNode());
				}
				for (Node node : foundNodes) {
					String personJson = node.getProperty(PERSON_DATA.toString()).toString();
					if (!foundPersons.getPersons().contains(personJson)) {
						foundPersons.addPerson(PersonJsonParser.fromString(personJson));
					}
				}
			}
			tx.success();
		}
		return foundPersons;
	}

	private void readCsvTerritories(final File territoriesFile) throws CsvParserException {
		logger.info("readCsvTerritories" + Constants.LOG_OBJECT_SEPARATOR + territoriesFile.getName());
		CsvParser csvParser = new CsvParser(territoriesFile);
		List<CSVRecord> csvRecords = csvParser.readCsvContents();
		territories = new Territories();
		for (CSVRecord csvRecord : csvRecords) {
			String country = csvRecord.get(CsvHeaders.COUNTRY);
			String postalCode = csvRecord.get(CsvHeaders.POSTAL_CODE);
			String placeName = csvRecord.get(CsvHeaders.PLACE_NAME);
			Territory territory = new Territory(country, postalCode, placeName);
			territories.addTerritory(territory);
		}
		logger.info("finishCsvTerritories" + Constants.LOG_OBJECT_SEPARATOR + territoriesFile.getName());
	}

	/**
	 * Creates indexes and returns a ConstraintCreator
	 *
	 * @param indexManager
	 * @return
	 */
	private void createIndexes(IndexManager indexManager) {
		try (Transaction tx = graphDb.beginTx()) {
			indexPostalCode = indexManager.forNodes(POSTAL_CODE.toString());
			indexPersonId = indexManager.forNodes(PERSON_ID.toString());
			indexLivesIn = indexManager.forRelationships(LIVES_IN.toString());
			indexWorksIn = indexManager.forRelationships(WORKS_IN.toString());
			tx.success();
		}
	}

	/**
	 * Creates the database schema
	 */
	private void createSchema() {
		try (Transaction tx = graphDb.beginTx()) {
			Iterator<ConstraintDefinition> constraintDefinitionIterator =
					graphDb.schema().getConstraints(constraintPersonIdLabel).iterator();
			if (!constraintDefinitionIterator.hasNext()) {
				graphDb.schema().constraintFor(constraintPersonIdLabel).assertPropertyIsUnique(PERSON_ID.toString())
						.create();
				graphDb.schema().constraintFor(postalCodeLabel).assertPropertyIsUnique(POSTAL_CODE.toString())
						.create();
				logger.info("DatabaseImpl" + Constants.LOG_OBJECT_SEPARATOR + "Constraint " + PERSON_ID.toString());
			}
			tx.success();
		}
	}

	@PreDestroy
	private void close() {
		logger.info("closeDatabase");
		graphDb.shutdown();
	}

}
