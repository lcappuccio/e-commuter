package org.systemexception.ecommuter.test.services;

import com.tinkerpop.blueprints.Vertex;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.systemexception.ecommuter.api.DatabaseApi;
import org.systemexception.ecommuter.api.LocationApi;
import org.systemexception.ecommuter.enums.DatabaseConfiguration;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.exceptions.LocationImplException;
import org.systemexception.ecommuter.exceptions.TerritoriesException;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.services.DatabaseImpl;
import org.systemexception.ecommuter.services.LocationImpl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author leo
 * @date 03/07/16 02:12
 */
public class DatabaseImplTest {

	private DatabaseApi sut;
	private final LocationApi locationService = new LocationImpl();
	private final static String dbFileName = "target/database_italy", exportFileName = "target/database_export.csv";
	private File exportFile;
	private Person person;

	@Before
	public void setUp() throws CsvParserException, TerritoriesException, URISyntaxException, LocationImplException {
		URL myTestURL = ClassLoader.getSystemResource("it_data_SMALL.csv");
		File myFile = new File(myTestURL.toURI());
		sut = new DatabaseImpl();
		sut.initialSetup(dbFileName);
		sut.addTerritories(myFile.getAbsolutePath());
		exportFile = new File(exportFileName);
		Address addressFromGeo = locationService.geoToAddress(45.4641776, 9.1899885);
		person = new Person("TEST_NAME", "TEST_SURNAME", addressFromGeo, addressFromGeo);
		person.setHomeAddress(addressFromGeo);
		person.setWorkAddress(addressFromGeo);
	}

	@After
	public void tearDown() throws IOException {
		sut.drop();
	}

	@Test
	public void get_vertex_by_postal_code() {
		Vertex vertexByPostalCode = sut.getVertexByPostalCode("21016").get();
		String postalCode = vertexByPostalCode.getProperty(DatabaseConfiguration.POSTAL_CODE.toString());
		String placeName = vertexByPostalCode.getProperty(DatabaseConfiguration.PLACE_NAME.toString());

		assertEquals("21016", postalCode);
		assertEquals("Luino", placeName);
	}

	@Test
	public void get_vertex_by_postal_code_empty() {
		Optional<Vertex> vertexByPostalCode = sut.getVertexByPostalCode("NON_EXISTING");

		assertTrue(vertexByPostalCode.equals(Optional.empty()));
	}

	@Test
	public void get_vertex_by_place_name() {
		Vertex vertexByPlaceName = sut.getVertexByPlaceName("Luino").get();
		String postalCode = vertexByPlaceName.getProperty(DatabaseConfiguration.POSTAL_CODE.toString());
		String placeName = vertexByPlaceName.getProperty(DatabaseConfiguration.PLACE_NAME.toString());

		assertEquals("21016", postalCode);
		assertEquals("Luino", placeName);
	}

	@Test
	public void get_vertex_by_place_name_empty() {
		Optional<Vertex> vertexByPlaceName = sut.getVertexByPlaceName("NON_EXISTING");

		assertTrue(vertexByPlaceName.equals(Optional.empty()));
	}

	@Test
	public void add_person() {
		sut.addPerson(person);
	}

	@Test
	public void export_the_database() throws IOException {
		sut.exportDatabase(exportFileName);

		assertTrue(exportFile.exists());
	}
}