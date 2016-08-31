package org.systemexception.ecommuter.test.services;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.systemexception.ecommuter.Application;
import org.systemexception.ecommuter.api.DatabaseApi;
import org.systemexception.ecommuter.api.LocationApi;
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

	private final static String DATABASE_FOLDER = End2End.TARGET_FOLER + File.separator + End2End.TEST_DATABASE_FOLDER;
	@Autowired
	private DatabaseApi sut;
	@Autowired
	private LocationApi locationService;
	private Person person;
	private Address addressFromGeo;
	private final String updatedName = "UpdatedName", updatedSurname = "UpdatedSurname";

	@BeforeClass
	public static void setSut() throws IOException {
		StorageImpl.removeFolder(DATABASE_FOLDER);
	}

	@Before
	public void setUp() throws Exception {
		URL myTestURL = ClassLoader.getSystemResource(CsvParserTest.DATABASE_TEST_CSV_FILE);
		File myFile = new File(myTestURL.toURI());
		sut.addTerritories(myFile);
		addressFromGeo = locationService.geoToAddress(45.4641776, 9.1899885);
		String personId = UUID.randomUUID().toString();
		person = new Person(personId, End2End.PERSON_NAME_A, End2End.PERSON_SURNAME_C, addressFromGeo, addressFromGeo);
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
		Persons personsLivesIn = sut.findPersonsLivesIn(person.getHomeAddress().getCountry(),
				person.getHomeAddress().getPostalCode());

		assertEquals(personsLivesIn.getPersons().size(), 1);
		assertEquals(personsLivesIn.getPersons().get(0), person);
	}

	@Test
	public void find_person_works_in() {
		Persons personsLivesIn = sut.findPersonsWorksIn(person.getHomeAddress().getCountry(),
				person.getHomeAddress().getPostalCode());

		assertEquals(personsLivesIn.getPersons().size(), 1);
		assertEquals(personsLivesIn.getPersons().get(0), person);
	}

	@Test
	public void find_person_nonexisting_node() {
		Persons nullPersons = sut.findPersonsLivesIn("XXXX","XXXX");

		assertTrue(0 == nullPersons.getPersons().size());
	}

	@Test(expected = TerritoriesException.class)
	public void add_person_nonexisting_node() throws TerritoriesException {
		Address missingAddress = person.getHomeAddress();
		missingAddress.setPostalCode("XXXX");
		person.setHomeAddress(missingAddress);
		sut.addPerson(person);
	}

	@Test(expected = TerritoriesException.class)
	public void add_person_bad_country_good_postalcode() throws TerritoriesException {
		Address missingAddress = person.getHomeAddress();
		missingAddress.setCountry("XXX");
		sut.addPerson(person);
	}

	@Test
	public void update_person() {
		Person personBeforeUpdate = sut.findPersonsLivesIn(addressFromGeo.getCountry(),
				addressFromGeo.getPostalCode()).getPersons().get(0);
		Person personBuffer = new Person(personBeforeUpdate.getId(), personBeforeUpdate.getName(),
				personBeforeUpdate.getSurname(), personBeforeUpdate.getHomeAddress(),
				personBeforeUpdate.getWorkAddress());
		personBuffer.setName(updatedName);
		personBuffer.setSurname(updatedSurname);
		Person personAfterUpdate = sut.updatePerson(personBuffer);

		assertEquals(personBeforeUpdate.getId(), personAfterUpdate.getId());
		assertNotEquals(personBeforeUpdate, personAfterUpdate);
	}

	@Test
	public void update_person_bad_id() {
		Person personBeforeUpdate = sut.findPersonsLivesIn(addressFromGeo.getCountry(),
				addressFromGeo.getPostalCode()).getPersons().get(0);
		Person personBuffer = new Person(personBeforeUpdate.getId(), personBeforeUpdate.getName(),
				personBeforeUpdate.getSurname(), personBeforeUpdate.getHomeAddress(),
				personBeforeUpdate.getWorkAddress());
		personBuffer.setId("BAD_ID");
		personBuffer.setName(updatedName);
		personBuffer.setSurname(updatedSurname);
		Person personAfterUpdate = sut.updatePerson(personBuffer);

		assertNotEquals(personBeforeUpdate, personAfterUpdate);
	}
}