package org.systemexception.ecommuter.test;

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

/**
 * @author leo
 * @date 03/07/16 02:12
 */
public class DatabaseImplTest {

	private DatabaseApi sut;
	private final static String dbFileName = "target/database_italy";

	@Before
	public void setUp() throws CsvParserException, TerritoriesException, URISyntaxException {
		URL myTestURL = ClassLoader.getSystemResource("it_data_SMALL.csv");
		File myFile = new File(myTestURL.toURI());
		sut = new DatabaseImpl();
		sut.initialSetup(dbFileName);
		sut.addTerritories(myFile.getAbsolutePath());
	}

	@After
	public void tearDown() throws IOException {
		sut.drop();
	}

	@Test
	public void database_is_loaded() {
		Vertex vertexByPostalCode = sut.getVertexByPostalCode("21016");
		String postalCode = vertexByPostalCode.getProperty(DatabaseConfiguration.POSTAL_CODE.toString());
		String placeName = vertexByPostalCode.getProperty(DatabaseConfiguration.PLACE_NAME.toString());

		assertEquals("21016", postalCode);
		assertEquals("Luino", placeName);
	}
}