package org.systemexception.ecommuter.test.services;

import com.tinkerpop.blueprints.Vertex;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.systemexception.ecommuter.api.DatabaseApi;
import org.systemexception.ecommuter.enums.DatabaseConfiguration;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.exceptions.TerritoriesException;
import org.systemexception.ecommuter.services.DatabaseImpl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author leo
 * @date 03/07/16 02:12
 */
public class DatabaseImplTest {

	private DatabaseApi sut;
	private final static String dbFileName = "target/database_italy", exportFileName = "target/database_export.csv";
	private File exportFile;

	@Before
	public void setUp() throws CsvParserException, TerritoriesException, URISyntaxException {
		URL myTestURL = ClassLoader.getSystemResource("it_data_SMALL.csv");
		File myFile = new File(myTestURL.toURI());
		sut = new DatabaseImpl();
		sut.initialSetup(dbFileName);
		sut.addTerritories(myFile.getAbsolutePath());
		exportFile = new File(exportFileName);
	}

	@After
	public void tearDown() throws IOException {
		sut.drop();
	}

	@Test
	public void get_vertex_by_postal_code() {
		Vertex vertexByPostalCode = sut.getVertexByPostalCode("21016");
		String postalCode = vertexByPostalCode.getProperty(DatabaseConfiguration.POSTAL_CODE.toString());
		String placeName = vertexByPostalCode.getProperty(DatabaseConfiguration.PLACE_NAME.toString());

		assertEquals("21016", postalCode);
		assertEquals("Luino", placeName);
	}

	@Test
	public void get_vertex_by_place_name() {
		Vertex vertexByPostalCode = sut.getVertexByPlaceName("Luino");
		String postalCode = vertexByPostalCode.getProperty(DatabaseConfiguration.POSTAL_CODE.toString());
		String placeName = vertexByPostalCode.getProperty(DatabaseConfiguration.PLACE_NAME.toString());

		assertEquals("21016", postalCode);
		assertEquals("Luino", placeName);
	}

	@Test
	public void export_the_database() throws IOException {
		sut.exportDatabase(exportFileName);

		assertTrue(exportFile.exists());
	}
}