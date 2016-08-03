package org.systemexception.ecommuter.test.services;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.systemexception.ecommuter.Application;
import org.systemexception.ecommuter.api.DatabaseApi;
import org.systemexception.ecommuter.api.LocationApi;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.exceptions.LocationException;
import org.systemexception.ecommuter.exceptions.TerritoriesException;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.model.Persons;
import org.systemexception.ecommuter.services.StorageImpl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * @author leo
 * @date 03/07/16 02:12
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
public class DatabaseImplTest {

	private final static String DATABASE_FOLDER = "target" + File.separator + "test_database";
	@Autowired
	private DatabaseApi sut;
	@Autowired
	private LocationApi locationService;
	private Person person;
	private String personId;
	private Address addressFromGeo;

	@BeforeClass
	public static void setSut() throws IOException {
		StorageImpl.removeFolder(DATABASE_FOLDER);
	}

	@Before
	public void setUp() throws CsvParserException, TerritoriesException, URISyntaxException, LocationException {
		URL myTestURL = ClassLoader.getSystemResource("it_data_SMALL.csv");
		File myFile = new File(myTestURL.toURI());
		sut.addTerritories(myFile);
		addressFromGeo = locationService.geoToAddress(45.4641776, 9.1899885);
		personId = UUID.randomUUID().toString();
		person = new Person(personId, "TEST_NAME", "TEST_SURNAME", addressFromGeo, addressFromGeo);
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
		Persons personsLivesIn = sut.findPersonsLivesIn(person.getHomeAddress().getPostalCode());

		assertEquals(personsLivesIn.getPersons().size(), 1);
		assertEquals(personsLivesIn.getPersons().get(0), person);
	}

	@Test
	public void find_person_works_in() {
		Persons personsLivesIn = sut.findPersonsWorksIn(person.getHomeAddress().getPostalCode());

		assertEquals(personsLivesIn.getPersons().size(), 1);
		assertEquals(personsLivesIn.getPersons().get(0), person);
	}

	@Test
	public void find_person_nonexisting_node() {
		Persons nullPersons = sut.findPersonsLivesIn("XXXX");

		assertTrue(0 == nullPersons.getPersons().size());
	}

	@Test(expected = TerritoriesException.class)
	public void add_person_nonexisting_node() throws TerritoriesException {
		Address missingAddress = person.getHomeAddress();
		missingAddress.setPostalCode("XXXX");
		person.setHomeAddress(missingAddress);
		sut.addPerson(person);
	}

	@Test
	public void update_person() {
		Person personBeforeUpdate = sut.findPersonsLivesIn(addressFromGeo.getPostalCode()).getPersons().get(0);
		Person personBuffer = new Person(personBeforeUpdate.getId(), personBeforeUpdate.getName(),
				personBeforeUpdate.getSurname(), personBeforeUpdate.getHomeAddress(),
				personBeforeUpdate.getWorkAddress());
		personBuffer.setName("UpdatedName");
		personBuffer.setSurname("UpdatedSurname");
		Person personAfterUpdate = sut.updatePerson(personBuffer);

		assertEquals(personBeforeUpdate.getId(), personAfterUpdate.getId());
		assertNotEquals(personBeforeUpdate, personAfterUpdate);
	}

	@Test
	public void update_person_bad_id() {
		Person personBeforeUpdate = sut.findPersonsLivesIn(addressFromGeo.getPostalCode()).getPersons().get(0);
		Person personBuffer = new Person(personBeforeUpdate.getId(), personBeforeUpdate.getName(),
				personBeforeUpdate.getSurname(), personBeforeUpdate.getHomeAddress(),
				personBeforeUpdate.getWorkAddress());
		personBuffer.setId("BAD_ID");
		personBuffer.setName("UpdatedName");
		personBuffer.setSurname("UpdatedSurname");
		Person personAfterUpdate = sut.updatePerson(personBuffer);

		assertNotEquals(personBeforeUpdate, personAfterUpdate);
	}
}