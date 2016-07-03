package org.systemexception.ecommuter.test.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * @author leo
 * @date 03/07/16 02:12
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
public class DatabaseImplTest {

	@Autowired
	private DatabaseApi sut;
	@Autowired
	private LocationApi locationService;
	private Person person;

	@Before
	public void setUp() throws CsvParserException, TerritoriesException, URISyntaxException, LocationException {
		URL myTestURL = ClassLoader.getSystemResource("it_data_SMALL.csv");
		File myFile = new File(myTestURL.toURI());
		sut.addTerritories(myFile);
		Address addressFromGeo = locationService.geoToAddress(45.4641776, 9.1899885);
		person = new Person("TEST_NAME", "TEST_SURNAME", addressFromGeo, addressFromGeo);
		person.setHomeAddress(addressFromGeo);
		person.setWorkAddress(addressFromGeo);
		sut.addPerson(person);
	}

	@After
	public void tearDown() {
		sut.deletePerson(person);
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
}