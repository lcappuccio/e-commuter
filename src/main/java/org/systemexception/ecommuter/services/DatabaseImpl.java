package org.systemexception.ecommuter.services;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.neo4j.graphdb.schema.ConstraintDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.systemexception.ecommuter.enums.Constants;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.exceptions.TerritoriesException;
import org.systemexception.ecommuter.model.*;
import org.systemexception.ecommuter.pojo.PersonJsonParser;
import org.systemexception.ecommuter.pojo.UserDataSantizer;

import javax.annotation.PreDestroy;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;

import static org.systemexception.ecommuter.enums.DatabaseConfiguration.*;

/**
 * @author leo
 * @date 02/07/16 23:23
 */
public class DatabaseImpl implements DatabaseApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseImpl.class);

	private final GraphDatabaseService graphDb;
	private final RelationshipType livesInRelation = RelationshipType.withName(LIVES_IN.toString());
	private final RelationshipType worksInRelation = RelationshipType.withName(WORKS_IN.toString());
	private final Label constraintPersonIdLabel = Label.label(PERSON_ID.toString());
    private final Label postalCodeLabel = Label.label(POSTAL_CODE.toString());

	private Index<Node> indexPostalCode;
    private Index<Node> indexPersonId;
    private Index<Node> indexPersonLastName;
	private RelationshipIndex indexLivesIn;
    private RelationshipIndex indexWorksIn;

	public DatabaseImpl(final String dbFolder) {

		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File(dbFolder));
		final IndexManager indexManager = graphDb.index();
		createIndexes(indexManager);
		createSchema();
		LOGGER.info("DatabaseImpl{}{}", Constants.LOG_OBJECT_SEPARATOR, dbFolder);
	}

	@Override
	public void addTerritories(final File territoriesFile) throws CsvParserException {

		final Territories territories = new Territories(territoriesFile);
		LOGGER.info("addTerritories{}{}", Constants.LOG_OBJECT_SEPARATOR, territoriesFile.getName());

		// Create all nodes
		try (final Transaction tx = graphDb.beginTx()) {
			for (Territory territory : territories.getTerritories()) {
				final Node territoryNode = graphDb.createNode();
				territoryNode.setProperty(COUNTRY.toString(), territory.getCountry());
				territoryNode.setProperty(POSTAL_CODE.toString(), territory.getPostalCode());
				territoryNode.setProperty(PLACE_NAME.toString(), territory.getPlaceName());
				indexPostalCode.add(territoryNode, POSTAL_CODE.toString(), territory.getPostalCode());
				LOGGER.info("addedTerritory{}{}{}{}{}{}",
                        Constants.LOG_OBJECT_SEPARATOR,
                        territory.getCountry(),
						Constants.LOG_ITEM_SEPARATOR,
                        territory.getPostalCode(),
                        Constants.LOG_ITEM_SEPARATOR,
						territory.getPlaceName());
			}
			tx.success();
		}

		LOGGER.info("addedTerritories{}{}", Constants.LOG_OBJECT_SEPARATOR, territoriesFile.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Person addPerson(final Person person) throws TerritoriesException {

		final Address homeAddress = person.getHomeAddress();
		final Address workAddress = person.getWorkAddress();
        final String personIdSanitized = UserDataSantizer.returnAsSafe(person.getId());
        LOGGER.info("addPerson{}{}", Constants.LOG_OBJECT_SEPARATOR, personIdSanitized);
		final Optional<Node> homeNode = getNodeByPostalCode(homeAddress.getTerritory().getCountry(),
				homeAddress.getTerritory().getPostalCode());
		final Optional<Node> workNode = getNodeByPostalCode(workAddress.getTerritory().getCountry(),
				workAddress.getTerritory().getPostalCode());

		if (homeNode.isPresent() && workNode.isPresent()) {
			insertPerson(person, homeNode.get(), workNode.get());
		} else {
			final String errorMessage = "Non existing territory";
			LOGGER.error("addPerson{}{}", Constants.LOG_OBJECT_SEPARATOR, personIdSanitized);
			throw new TerritoriesException(errorMessage);
		}

		return person;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Person updatePerson(final Person person) {

        final String personIdSanitized = UserDataSantizer.returnAsSafe(person.getId());
        LOGGER.info("updatePerson{}{}", Constants.LOG_OBJECT_SEPARATOR, personIdSanitized);

		try (Transaction tx = graphDb.beginTx()) {
			final IndexHits<Node> nodes = indexPersonId.get(PERSON_ID.toString(), person.getId());
			final Node personNode = nodes.getSingle();
			if (personNode != null) {
				updateLastnameIndex(person, personNode);
				personNode.setProperty(PERSON_DATA.toString(), PersonJsonParser.fromPerson(person).toString());
				tx.success();
			} else {
				LOGGER.info("updatePersonNotFound{}{}", Constants.LOG_OBJECT_SEPARATOR, personIdSanitized);
				tx.success();
			}
		}

		LOGGER.info("updatedPerson{}{}", Constants.LOG_OBJECT_SEPARATOR, personIdSanitized);
		return person;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePerson(final Person person) {

        final String personIdSanitized = UserDataSantizer.returnAsSafe(person.getId());
        LOGGER.info("deletePerson{}{}", Constants.LOG_OBJECT_SEPARATOR, personIdSanitized);

		try (final Transaction tx = graphDb.beginTx()) {
			final IndexHits<Node> nodes = indexPersonId.get(PERSON_ID.toString(), personIdSanitized);
			final Node personNode = nodes.getSingle();
			while (personNode.getRelationships().iterator().hasNext()) {
				personNode.getRelationships().iterator().next().delete();
				indexPersonLastName.remove(personNode, personIdSanitized);
			}
			personNode.delete();
			tx.success();
		}

		LOGGER.info("deletedPerson{}{}", Constants.LOG_OBJECT_SEPARATOR, personIdSanitized);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Persons findPersonsLivesIn(final String country, final String postalCode) {

        final String countrySanitized = UserDataSantizer.returnAsSafe(country);
        final String postalCodeSanitized = UserDataSantizer.returnAsSafe(postalCode);
		LOGGER.info("findPersonsLivingIn{}{}{}{}",
                Constants.LOG_OBJECT_SEPARATOR,
                countrySanitized,
                Constants.LOG_ITEM_SEPARATOR,
				postalCodeSanitized);

		return getPersons(country, postalCode, livesInRelation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Persons findPersonsWorksIn(final String country, final String postalCode) {

        final String countrySanitized = UserDataSantizer.returnAsSafe(country);
        final String postalCodeSanitized = UserDataSantizer.returnAsSafe(postalCode);
		LOGGER.info("findPersonsWorkingIn{}{}{}{}",
                Constants.LOG_OBJECT_SEPARATOR,
                countrySanitized,
                Constants.LOG_ITEM_SEPARATOR,
				postalCodeSanitized);

		return getPersons(country, postalCode, worksInRelation);
	}

	/**
	 * {@inheritDoc}
	 */
	public Persons findPersonsByLastname(final String lastname) {

        final String lastNameSanitized = UserDataSantizer.returnAsSafe(lastname);
		final Persons persons = new Persons();
		LOGGER.info("findPersonsByLastname{}{}", Constants.LOG_OBJECT_SEPARATOR, lastNameSanitized);

		try (final Transaction tx = graphDb.beginTx()) {
			final Iterator<Node> nodeIterator = indexPersonLastName.get(PERSON_LAST_NAME.toString(), lastNameSanitized).iterator();
			tx.success();
			while (nodeIterator.hasNext()) {
				final Node node = nodeIterator.next();
				final String personJsonString = node.getProperty(PERSON_DATA.toString()).toString();
				final Person person = PersonJsonParser.fromString(personJsonString);
				persons.addPerson(person);
			}
		}
		return persons;
	}

	/**
	 * Returns a node given the postalCode
	 *
	 * @param country
	 * @param postalCode
	 * @return an optional with the node of the postal code, IMPORTANT, there can be more than one location with same
     * postal code
	 */
	private Optional<Node> getNodeByPostalCode(final String country, final String postalCode) {

        String safeCountry = UserDataSantizer.returnAsSafe(country);
        String safePostalCode = UserDataSantizer.returnAsSafe(postalCode);
        LOGGER.info("getNodeByPostalCode{}{}{}{}",
                Constants.LOG_OBJECT_SEPARATOR,
                safeCountry,
                Constants.LOG_ITEM_SEPARATOR,
                safePostalCode);

		try (final Transaction tx = graphDb.beginTx()) {
			final Iterator<Node> nodeIterator = indexPostalCode.get(POSTAL_CODE.toString(), safePostalCode).iterator();
			tx.success();
			if (nodeIterator.hasNext()) {
				final Node node = nodeIterator.next();
				final String nodeCountry = node.getProperty(COUNTRY.toString()).toString();
				if (safeCountry.equals(nodeCountry)) {
					return Optional.of(node);
				} else {
					LOGGER.info("getNodeByPostalCode{}{}{}{} does not exist",
                            Constants.LOG_OBJECT_SEPARATOR,
                            safeCountry,
                            Constants.LOG_ITEM_SEPARATOR,
                            safePostalCode);
					return Optional.empty();
				}
			} else {
				LOGGER.error("getNodeByPostalCode{}{}{}{} node missing",
                        Constants.LOG_OBJECT_SEPARATOR,
                        safeCountry,
						Constants.LOG_ITEM_SEPARATOR,
                        safePostalCode);
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
	private synchronized void insertPerson(final Person person, final Node homeNode, final Node workNode) {

		try (final Transaction tx = graphDb.beginTx()) {
            String safePersonId = UserDataSantizer.returnAsSafe(person.getId());
            final Node personNode = graphDb.createNode();
			personNode.setProperty(PERSON_DATA.toString(), PersonJsonParser.fromPerson(person).toString());
			personNode.setProperty(PERSON_ID.toString(), person.getId());
			try {
				personNode.addLabel(constraintPersonIdLabel);
			} catch (ConstraintViolationException ex) {
				final String errorMessage = ex.getMessage();
				tx.failure();
				LOGGER.error("{}{}{}", safePersonId, Constants.LOG_ITEM_SEPARATOR, errorMessage);
				throw new ConstraintViolationException(errorMessage);
			}
			indexPersonId.add(personNode, PERSON_ID.toString(), person.getId());
			indexPersonLastName.add(personNode, PERSON_LAST_NAME.toString(), person.getLastname());
			// Add LIVES_IN edge
			final Relationship livesIn = personNode.createRelationshipTo(homeNode, livesInRelation);
			indexLivesIn.add(livesIn, LIVES_IN.toString(), homeNode.getProperty(POSTAL_CODE.toString()));
			// Add WORKS_IN edge
			final Relationship worksIn = personNode.createRelationshipTo(workNode, worksInRelation);
			indexWorksIn.add(worksIn, WORKS_IN.toString(), workNode.getProperty(POSTAL_CODE.toString()));
			LOGGER.info("addedPerson{}{}", Constants.LOG_OBJECT_SEPARATOR, safePersonId);
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
	private Persons getPersons(final String country, final String postalCode, final RelationshipType relationshipType) {

        String safeCountry = UserDataSantizer.returnAsSafe(country);
        String safePostalCode = UserDataSantizer.returnAsSafe(postalCode);
        LOGGER.info("getPersonsByPostalCodeRelation{}{}{}{}{}{}",
                Constants.LOG_OBJECT_SEPARATOR,
                safeCountry,
                Constants.LOG_ITEM_SEPARATOR,
                safePostalCode);
		final HashSet<Node> foundNodes = new HashSet<>();
		final Persons foundPersons = new Persons();

		try (final Transaction tx = graphDb.beginTx()) {
			final Optional<Node> nodeByPostalCode = getNodeByPostalCode(country, postalCode);
			if (nodeByPostalCode.isPresent()) {
				for (Relationship relationship : nodeByPostalCode.get().getRelationships(relationshipType)) {
					foundNodes.add(relationship.getStartNode());
				}
				for (final Node node : foundNodes) {
					final String personJsonString = node.getProperty(PERSON_DATA.toString()).toString();
					final Person person = PersonJsonParser.fromString(personJsonString);
					if (!foundPersons.getPersons().contains(person)) {
						foundPersons.addPerson(person);
					}
				}
			}
			tx.success();
		}

		return foundPersons;
	}

	/**
	 * Creates indexes with the given IndexManager
	 *
	 * @param indexManager
	 * @return
	 */
	private void createIndexes(final IndexManager indexManager) {

		try (final Transaction tx = graphDb.beginTx()) {
			indexPostalCode = indexManager.forNodes(POSTAL_CODE.toString());
			indexPersonId = indexManager.forNodes(PERSON_ID.toString());
			indexPersonLastName = indexManager.forNodes(PERSON_LAST_NAME.toString());
			indexLivesIn = indexManager.forRelationships(LIVES_IN.toString());
			indexWorksIn = indexManager.forRelationships(WORKS_IN.toString());
			tx.success();
		}
	}

	/**
	 * Creates the database schema and constraints
	 */
	private void createSchema() {

		try (final Transaction tx = graphDb.beginTx()) {
			final Iterator<ConstraintDefinition> constraintDefinitionIterator =
					graphDb.schema().getConstraints(constraintPersonIdLabel).iterator();
			if (!constraintDefinitionIterator.hasNext()) {
				graphDb.schema().constraintFor(constraintPersonIdLabel).assertPropertyIsUnique(PERSON_ID.toString())
						.create();
				graphDb.schema().constraintFor(postalCodeLabel).assertPropertyIsUnique(POSTAL_CODE.toString())
						.create();
				LOGGER.info("DatabaseImpl{}Constraint {}", Constants.LOG_OBJECT_SEPARATOR, PERSON_ID);
			}
			tx.success();
		}
	}

	/**
	 * Update indexPersonLastName if updating the surname
	 *
	 * @param person     the person received to update
	 * @param personNode the person in the database
	 */
	private void updateLastnameIndex(final Person person, final Node personNode) {

		final String personAsJson = personNode.getProperty(PERSON_DATA.toString()).toString();
		final Person personFromJson = PersonJsonParser.fromString(personAsJson);

		if (!personFromJson.getLastname().equals(person.getLastname())) {
			indexPersonLastName.remove(personNode);
			indexPersonLastName.add(personNode, PERSON_LAST_NAME.toString(), person.getLastname());
		}
	}

	@PreDestroy
	private void close() {
		LOGGER.info("closeDatabase");
		graphDb.shutdown();
	}

}
