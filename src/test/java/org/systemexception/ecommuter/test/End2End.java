package org.systemexception.ecommuter.test;

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

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author leo
 * @date 03/07/16 20:59
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
public class End2End {

	@Autowired
	private DatabaseApi databaseApi;
	@Autowired
	private LocationApi locationService;

	@Before
	public void setUp() throws CsvParserException, TerritoriesException, URISyntaxException, LocationException {
		URL myTestURL = ClassLoader.getSystemResource("it_data_SMALL.csv");
		File myFile = new File(myTestURL.toURI());
		databaseApi.addTerritories(myFile);
	}

	@Test
	public void api_key_is_not_null() {
		assertNotNull(Application.apiKey);
	}

	@Test
	public void end2End() throws LocationException {
		Person personA = new Person();
		Address addressWorkA = locationService.geoToAddress(46.003509, 8.742917);
		Address addressHomeA = locationService.geoToAddress(46.000490, 8.738347);
		personA.setName("TEST_NAME_A");
		personA.setSurname("TEST_SURNAME_A");
		personA.setHomeAddress(addressHomeA);
		personA.setWorkAddress(addressWorkA);

		Person personB = new Person();
		Address addressWorkB = locationService.geoToAddress(46.002834, 8.742499);
		Address addressHomeB = locationService.geoToAddress(45.999950, 8.740594);
		personB.setName("TEST_NAME_B");
		personB.setSurname("TEST_SURNAME_B");
		personB.setHomeAddress(addressHomeB);
		personB.setWorkAddress(addressWorkB);

		Person personC = new Person();
		Address addressWorkC = locationService.geoToAddress(45.996015, 8.732703);
		Address addressHomeC = locationService.geoToAddress(45.999659, 8.737842);
		personC.setName("TEST_NAME_C");
		personC.setSurname("TEST_SURNAME_C");
		personC.setHomeAddress(addressHomeC);
		personC.setWorkAddress(addressWorkC);

		databaseApi.addPerson(personA);
		databaseApi.addPerson(personB);
		databaseApi.addPerson(personC);

		Persons nearbyPersons =
				locationService.findNearbyPersons(personA, databaseApi.findPersonsLivesIn("21016"), 0.5);

		assertTrue(nearbyPersons.getPersons().size() == 1);
		assertTrue(nearbyPersons.getPersons().get(0).equals(personB));
	}
}
