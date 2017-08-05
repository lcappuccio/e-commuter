package org.systemexception.ecommuter.test.services;

import org.junit.*;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.systemexception.ecommuter.Application;
import org.systemexception.ecommuter.services.DatabaseApi;
import org.systemexception.ecommuter.services.LocationApi;
import org.systemexception.ecommuter.services.StorageApi;
import org.systemexception.ecommuter.exceptions.TerritoriesException;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.model.Persons;
import org.systemexception.ecommuter.services.StorageImpl;
import org.systemexception.ecommuter.test.End2End;
import org.systemexception.ecommuter.test.pojo.CsvParserTest;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * @author leo
 * @date 03/07/16 02:12
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
public class DatabaseImplTest {

	private static final String DATABASE_FOLDER = End2End.TARGET_FOLDER + File.separator + End2End.TEST_DATABASE_FOLDER;
	private static final String PERSON_LAST_NAME = "NEWLASTNAME";
	private static final String PERSON_NAME = "NEWNAME";
	@Autowired
	private DatabaseApi sut;
	@Autowired
	private LocationApi locationService;
	private Person person;
	private Address addressFromGeo;
	private final String updatedName = "UpdatedName", updatedSurname = "UpdatedSurname";

	@BeforeClass
	public static void setSut() throws IOException {
		StorageApi storageApi = new StorageImpl(DATABASE_FOLDER);
		storageApi.removeFolder(DATABASE_FOLDER);
	}

	@Before
	public void setUp() throws Exception {
		URL myTestURL = ClassLoader.getSystemResource(CsvParserTest.DATABASE_TEST_CSV_FILE);
		File myFile = new File(myTestURL.toURI());
		sut.addTerritories(myFile);
		addressFromGeo = locationService.geoToAddress(45.4641776, 9.1899885);
		String personId = UUID.randomUUID().toString();
		person = new Person(personId, PERSON_NAME, PERSON_LAST_NAME, addressFromGeo, addressFromGeo);
		person.setHomeAddress(addressFromGeo);
		person.setWorkAddress(addressFromGeo);
		sut.addPerson(person);
	}

	@After
	public void tearDown() {
		sut.deletePerson(person);
	}

	@Test(expected = ConstraintViolationException.class)
	public void add_person_twice() throws TerritoriesException {
		sut.addPerson(person);
	}

	@Test
	public void find_person_lives_in() {
		Persons personsLivesIn = sut.findPersonsLivesIn(person.getHomeAddress().getTerritory().getCountry(),
				person.getHomeAddress().getTerritory().getPostalCode());

		assertEquals(personsLivesIn.getPersons().size(), 1);
		assertEquals(personsLivesIn.getPersons().iterator().next(), person);
	}

	@Test
	public void find_person_works_in() {
		Persons personsLivesIn = sut.findPersonsWorksIn(person.getHomeAddress().getTerritory().getCountry(),
				person.getHomeAddress().getTerritory().getPostalCode());

		assertEquals(personsLivesIn.getPersons().size(), 1);
		assertEquals(personsLivesIn.getPersons().iterator().next(), person);
	}

	@Test
	public void find_person_nonexisting_node() {
		Persons nullPersons = sut.findPersonsLivesIn("XXXX","XXXX");

		assertTrue(0 == nullPersons.getPersons().size());
	}

	@Test(expected = TerritoriesException.class)
	public void add_person_nonexisting_node() throws TerritoriesException {
		Address missingAddress = person.getHomeAddress();
		missingAddress.getTerritory().setPostalCode("XXXX");
		person.setHomeAddress(missingAddress);
		sut.addPerson(person);
	}

	@Test(expected = TerritoriesException.class)
	public void add_person_bad_country_good_postalcode() throws TerritoriesException {
		Address missingAddress = person.getHomeAddress();
		missingAddress.getTerritory().setCountry("XXX");
		sut.addPerson(person);
	}

	@Test
	public void update_person() {
		Person personBeforeUpdate = sut.findPersonsLivesIn(addressFromGeo.getTerritory().getCountry(),
				addressFromGeo.getTerritory().getPostalCode()).getPersons().iterator().next();
		Person personBuffer = new Person(personBeforeUpdate.getId(), personBeforeUpdate.getName(),
				personBeforeUpdate.getLastname(), personBeforeUpdate.getHomeAddress(),
				personBeforeUpdate.getWorkAddress());
		personBuffer.setName(updatedName);
		personBuffer.setLastname(updatedSurname);
		Person personAfterUpdate = sut.updatePerson(personBuffer);

		assertEquals(personBeforeUpdate.getId(), personAfterUpdate.getId());
		assertNotEquals(personBeforeUpdate, personAfterUpdate);
	}

	@Test
	public void update_person_bad_id() {
		Person personBeforeUpdate = sut.findPersonsLivesIn(addressFromGeo.getTerritory().getCountry(),
				addressFromGeo.getTerritory().getPostalCode()).getPersons().iterator().next();
		Person personBuffer = new Person(personBeforeUpdate.getId(), personBeforeUpdate.getName(),
				personBeforeUpdate.getLastname(), personBeforeUpdate.getHomeAddress(),
				personBeforeUpdate.getWorkAddress());
		personBuffer.setId("BAD_ID");
		personBuffer.setName(updatedName);
		personBuffer.setLastname(updatedSurname);
		Person personAfterUpdate = sut.updatePerson(personBuffer);

		assertNotEquals(personBeforeUpdate, personAfterUpdate);
	}

	@Test
	public void find_person_by_name() {
		Persons personsByLastname = sut.findPersonsByLastname(PERSON_LAST_NAME);

		assertFalse(personsByLastname.getPersons().isEmpty());
		assertEquals(1, personsByLastname.getPersons().size());
		assertEquals(PERSON_LAST_NAME, personsByLastname.getPersons().iterator().next().getLastname());
		assertEquals(person, personsByLastname.getPersons().iterator().next());
	}

	@Test
	public void find_person_by_name_after_update() {
		Person personBeforeUpdate = sut.findPersonsLivesIn(addressFromGeo.getTerritory().getCountry(),
				addressFromGeo.getTerritory().getPostalCode()).getPersons().iterator().next();
		Person personBuffer = new Person(personBeforeUpdate.getId(), personBeforeUpdate.getName(),
				personBeforeUpdate.getLastname(), personBeforeUpdate.getHomeAddress(),
				personBeforeUpdate.getWorkAddress());
		String lastnameUpdate = updatedSurname + "UPDATE";
		personBuffer.setLastname(lastnameUpdate);
		Person personAfterUpdate = sut.updatePerson(personBuffer);
		Persons personsByLastname = sut.findPersonsByLastname(lastnameUpdate);

		assertEquals(personBeforeUpdate.getId(), personAfterUpdate.getId());
		assertNotEquals(personBeforeUpdate, personAfterUpdate);
		assertFalse(personsByLastname.getPersons().isEmpty());
		assertEquals(1, personsByLastname.getPersons().size());
		assertEquals(lastnameUpdate, personsByLastname.getPersons().iterator().next().getLastname());
	}
}