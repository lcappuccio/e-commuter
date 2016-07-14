package org.systemexception.ecommuter.services;

import org.apache.commons.csv.CSVRecord;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.systemexception.ecommuter.api.DatabaseApi;
import org.systemexception.ecommuter.api.LoggerApi;
import org.systemexception.ecommuter.enums.CsvHeaders;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.exceptions.TerritoriesException;
import org.systemexception.ecommuter.model.*;
import org.systemexception.ecommuter.pojo.CsvParser;
import org.systemexception.ecommuter.pojo.LoggerService;
import org.systemexception.ecommuter.pojo.PersonJsonParser;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.PreDestroy;
import java.io.File;
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

	private final LoggerApi logger = LoggerService.getFor(this.getClass());
	private final GraphDatabaseService graphDb;
	private final Index<Node> indexPostalCode, indexPerson;
	private final RelationshipType livesInRelation = RelationshipType.withName(LIVES_IN.toString());
	private final RelationshipType worksInRelation = RelationshipType.withName(WORKS_IN.toString());
	private final RelationshipIndex indexLivesIn, indexWorksIn;
	private Territories territories;

	public DatabaseImpl(final String dbFolder) {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File(dbFolder));
		IndexManager indexManager = graphDb.index();
		try (Transaction tx = graphDb.beginTx()) {
			indexPostalCode = indexManager.forNodes(POSTAL_CODE.toString());
			indexPerson = indexManager.forNodes(PERSON.toString());
			indexLivesIn = indexManager.forRelationships(LIVES_IN.toString());
			indexWorksIn = indexManager.forRelationships(WORKS_IN.toString());
			tx.success();
		}
	}
	
	@Override
	public void addTerritories(final File territoriesFile) throws CsvParserException, TerritoriesException {
		readCsvTerritories(territoriesFile);
		logger.addTerritories(territoriesFile.getName());
		// Create all nodes
		try (Transaction tx = graphDb.beginTx()) {
			for (Territory territory : territories.getTerritories()) {
				Node territoryNode = graphDb.createNode();
				territoryNode.setProperty(POSTAL_CODE.toString(), territory.getPostalCode());
				territoryNode.setProperty(PLACE_NAME.toString(), territory.getPlaceName());
				indexPostalCode.add(territoryNode, POSTAL_CODE.toString(), territory.getPostalCode());
				logger.addedTerritory(territory);
			}
			tx.success();
		}
		logger.loadedTerritories(territoriesFile);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Person addPerson(final Person person) throws TerritoriesException {
		Address homeAddress = person.getHomeAddress();
		Address workAddress = person.getWorkAddress();
		logger.addPerson(person);
		// Get vertices for addresses
		try (Transaction tx = graphDb.beginTx()) {
			Optional<Node> homeNode = getNodeByPostalCode(homeAddress.getPostalCode());
			Optional<Node> workNode = getNodeByPostalCode(workAddress.getPostalCode());
			if (homeNode.isPresent() && workNode.isPresent()) {
				Node personNode = graphDb.createNode();
				personNode.setProperty(PERSON_DATA.toString(), PersonJsonParser.fromPerson(person).toString());
				indexPerson.add(personNode, PERSON.toString(), person);
				// Add LIVES_IN edge
				logger.addPersonRelation(person, homeAddress, LIVES_IN.toString());
				Relationship livesIn = personNode.createRelationshipTo(homeNode.get(), livesInRelation);
				indexLivesIn.add(livesIn, LIVES_IN.toString(), homeAddress.getPostalCode());
				// Add WORKS_IN edge
				logger.addPersonRelation(person, workAddress, WORKS_IN.toString());
				Relationship worksIn = personNode.createRelationshipTo(workNode.get(), worksInRelation);
				indexWorksIn.add(worksIn, WORKS_IN.toString(), workAddress.getPostalCode());
				logger.addedPerson(person);
			} else {
				logger.addedNotPerson(person);
				throw new TerritoriesException("Non existing territory");
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
		logger.updatePerson(person);
		logger.updatedPerson(person);
		// TODO LC implement
		throw new NotImplementedException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePerson(Person person) {
		logger.deletePerson(person);
		try (Transaction tx = graphDb.beginTx()) {
			IndexHits<Node> nodes = indexPerson.get(PERSON.toString(), person);
			Node personNode = nodes.getSingle();
			while (personNode.getRelationships().iterator().hasNext()) {
				personNode.getRelationships().iterator().next().delete();
			}
			personNode.delete();
			tx.success();
		}
		logger.deletedPerson(person);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Persons findPersonsLivesIn(final String postalCode) {
		logger.findPersonsLivingIn(postalCode);
		return getPersons(postalCode, livesInRelation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Persons findPersonsWorksIn(final String postalCode) {
		logger.findPersonsWorkingIn(postalCode);
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
		logger.getNodeByPostalCode(postalCode);
		Iterator<Node> nodeIterator = indexPostalCode.get(POSTAL_CODE.toString(), postalCode).iterator();
		if (nodeIterator.hasNext()) {
			return Optional.of(nodeIterator.next());
		} else {
			logger.getNodeByPostalCodeNotExists(postalCode);
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
		logger.getPersonsByPostalCodeRelation(postalCode, relationshipType.toString());
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
						logger.foundPersonByPostalCodeRelation(postalCode, relationshipType.toString());
						foundPersons.addPerson(PersonJsonParser.fromString(personJson));
					}
				}
			}
			tx.success();
		}
		return foundPersons;
	}

	private void readCsvTerritories(final File territoriesFile) throws CsvParserException, TerritoriesException {
		logger.readCsvTerritories(territoriesFile);
		CsvParser csvParser = new CsvParser(territoriesFile);
		List<CSVRecord> csvRecords = csvParser.readCsvContents();
		territories = new Territories();
		for (CSVRecord csvRecord : csvRecords) {
			String postalCode = csvRecord.get(CsvHeaders.POSTAL_CODE.toString());
			String placeName = csvRecord.get(CsvHeaders.PLACE_NAME.toString());
			Territory territory = new Territory(postalCode, placeName);
			territories.addTerritory(territory);
		}
		logger.finishCsvTerritories(territoriesFile);
	}

	@PreDestroy
	private void close() {
		logger.closeDatabase();
		graphDb.shutdown();
	}

}
