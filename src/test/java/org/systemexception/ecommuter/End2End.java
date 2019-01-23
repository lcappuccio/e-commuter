package org.systemexception.ecommuter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.model.Persons;
import org.systemexception.ecommuter.pojo.CsvParserTest;
import org.systemexception.ecommuter.services.DatabaseApi;
import org.systemexception.ecommuter.services.LocationApi;
import org.systemexception.ecommuter.services.StorageApi;
import org.systemexception.ecommuter.services.StorageImpl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * @author leo
 * @date 03/07/16 20:59
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
public class End2End {

	public static final String TARGET_FOLDER = "target", TEST_DATABASE_FOLDER = "test_database";
	private final static String DATABASE_FOLDER = TARGET_FOLDER + File.separator + TEST_DATABASE_FOLDER;
	public static final String PERSON_NAME_A = "TEST_NAME_A", PERSON_SURNAME_A = "TEST_SURNAME_A",
			PERSON_NAME_B = "TEST_NAME_B", PERSON_SURNAME_B = "TEST_SURNAME_B",
			PERSON_NAME_C = "TEST_NAME_C", PERSON_SURNAME_C = "TEST_SURNAME_C";
	public static final String LOCATION_LUINO_POSTCODE = "21016", LOCATION_ITALY = "IT";
	@Autowired
	private DatabaseApi databaseApi;
	@Autowired
	private LocationApi locationService;
	@Autowired
	private StorageApi storageApi;

	@Before
	public void setUp() throws CsvParserException, URISyntaxException, IOException {
		storageApi = new StorageImpl(TARGET_FOLDER);
		storageApi.removeFolder(DATABASE_FOLDER);
		final URL myTestURL = ClassLoader.getSystemResource(CsvParserTest.DATABASE_TEST_CSV_FILE);
		final File myFile = new File(myTestURL.toURI());
		databaseApi.addTerritories(myFile);
	}

	@Test
	public void api_key_is_not_null() {
		assertNotNull(Application.API_KEY);
	}

	@Test
	public void end2End() throws Exception {
		final Person personA = new Person();
		final Address addressWorkA = locationService.geoToAddress(46.003509, 8.742917);
		final Address addressHomeA = locationService.geoToAddress(46.000490, 8.738347);
		personA.setId(UUID.randomUUID().toString());
		personA.setName(PERSON_NAME_A);
		personA.setLastname(PERSON_SURNAME_A);
		personA.setHomeAddress(addressHomeA);
		personA.setWorkAddress(addressWorkA);

		final Person personB = new Person();
		final Address addressWorkB = locationService.geoToAddress(46.002834, 8.742499);
		final Address addressHomeB = locationService.geoToAddress(45.999950, 8.740594);
		personB.setId(UUID.randomUUID().toString());
		personB.setName(PERSON_NAME_B);
		personB.setLastname(PERSON_SURNAME_B);
		personB.setHomeAddress(addressHomeB);
		personB.setWorkAddress(addressWorkB);

		final Person personC = new Person();
		final Address addressWorkC = locationService.geoToAddress(45.996015, 8.732703);
		final Address addressHomeC = locationService.geoToAddress(45.999659, 8.737842);
		personC.setId(UUID.randomUUID().toString());
		personC.setName(PERSON_NAME_C);
		personC.setLastname(PERSON_SURNAME_C);
		personC.setHomeAddress(addressHomeC);
		personC.setWorkAddress(addressWorkC);

		databaseApi.addPerson(personA);
		databaseApi.addPerson(personB);
		databaseApi.addPerson(personC);

		final Persons nearbyPersons = locationService.findNearbyPersons(personA, databaseApi.findPersonsLivesIn(
				LOCATION_ITALY, LOCATION_LUINO_POSTCODE), 0.5);
		assertEquals(1, nearbyPersons.getPersons().size());
		assertEquals(nearbyPersons.getPersons().iterator().next(), personB);
	}
}
