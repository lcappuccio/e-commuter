package org.systemexception.ecommuter.test;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.systemexception.ecommuter.Application;
import org.systemexception.ecommuter.api.DatabaseApi;
import org.systemexception.ecommuter.api.LocationApi;
import org.systemexception.ecommuter.api.StorageApi;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.exceptions.TerritoriesException;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.model.Persons;
import org.systemexception.ecommuter.services.StorageImpl;
import org.systemexception.ecommuter.test.pojo.CsvParserTest;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author leo
 * @date 03/07/16 20:59
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
public class End2End {

	public static final String TARGET_FOLER = "target", TEST_DATABASE_FOLDER = "test_database";
	private final static String DATABASE_FOLDER = TARGET_FOLER + File.separator + TEST_DATABASE_FOLDER;
	public static final String PERSON_NAME_A = "TEST_NAME_A", PERSON_SURNAME_A = "TEST_SURNAME_A",
			PERSON_NAME_B = "TEST_NAME_B", PERSON_SURNAME_B = "TEST_SURNAME_B",
			PERSON_NAME_C = "TEST_NAME_C", PERSON_SURNAME_C = "TEST_SURNAME_AC";
	public static final String LOCATION_LUINO_POSTCODE = "21016", LOCATION_ITALY = "IT";
	@Autowired
	private DatabaseApi databaseApi;
	@Autowired
	private LocationApi locationService;
	private static StorageApi storageApi;

	@BeforeClass
	public static void setSut() throws IOException {
		storageApi = new StorageImpl(TARGET_FOLER);
		storageApi.removeFolder(DATABASE_FOLDER);
	}

	@Before
	public void setUp() throws CsvParserException, TerritoriesException, URISyntaxException {
		URL myTestURL = ClassLoader.getSystemResource(CsvParserTest.DATABASE_TEST_CSV_FILE);
		File myFile = new File(myTestURL.toURI());
		databaseApi.addTerritories(myFile);
	}

	@Test
	public void api_key_is_not_null() {
		assertNotNull(Application.apiKey);
	}

	@Test
	public void end2End() throws Exception {
		Person personA = new Person();
		Address addressWorkA = locationService.geoToAddress(46.003509, 8.742917);
		Address addressHomeA = locationService.geoToAddress(46.000490, 8.738347);
		personA.setId(UUID.randomUUID().toString());
		personA.setName(PERSON_NAME_A);
		personA.setSurname(PERSON_SURNAME_A);
		personA.setHomeAddress(addressHomeA);
		personA.setWorkAddress(addressWorkA);

		Person personB = new Person();
		Address addressWorkB = locationService.geoToAddress(46.002834, 8.742499);
		Address addressHomeB = locationService.geoToAddress(45.999950, 8.740594);
		personB.setId(UUID.randomUUID().toString());
		personB.setName(PERSON_NAME_B);
		personB.setSurname(PERSON_SURNAME_B);
		personB.setHomeAddress(addressHomeB);
		personB.setWorkAddress(addressWorkB);

		Person personC = new Person();
		Address addressWorkC = locationService.geoToAddress(45.996015, 8.732703);
		Address addressHomeC = locationService.geoToAddress(45.999659, 8.737842);
		personC.setId(UUID.randomUUID().toString());
		personC.setName(PERSON_NAME_C);
		personC.setSurname(PERSON_SURNAME_C);
		personC.setHomeAddress(addressHomeC);
		personC.setWorkAddress(addressWorkC);

		databaseApi.addPerson(personA);
		databaseApi.addPerson(personB);
		databaseApi.addPerson(personC);

		Persons nearbyPersons = locationService.findNearbyPersons(personA, databaseApi.findPersonsLivesIn(
				LOCATION_ITALY, LOCATION_LUINO_POSTCODE), 0.5);
		assertTrue(nearbyPersons.getPersons().size() == 1);
		assertTrue(nearbyPersons.getPersons().get(0).equals(personB));
	}
}
