package org.systemexception.ecommuter.pojo;

import org.apache.commons.csv.CSVRecord;
import org.junit.BeforeClass;
import org.junit.Test;
import org.systemexception.ecommuter.enums.CsvHeaders;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.End2End;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author leo
 * @date 02/07/16 21:59
 */
public class CsvParserTest {

	private CsvParser sut;
	public static final String DATABASE_TEST_CSV_FILE = "geodata_SMALL.csv";
	private static File resourceFile;

	@BeforeClass
	public static void setUp() throws URISyntaxException {
		final URL myTestURL = ClassLoader.getSystemResource(DATABASE_TEST_CSV_FILE);
		resourceFile = new File(myTestURL.toURI());
	}

	@Test
	public void open_test_territories_file() {
		assertTrue(resourceFile.exists());
	}

	@Test(expected = CsvParserException.class)
	public void throw_exception_for_nonexisting_file() throws CsvParserException {
		sut = new CsvParser(new File("nonexistingfile.txt"));
	}

	@Test
	public void reads_territories_test_file() throws CsvParserException {
		sut = new CsvParser(new File(resourceFile.getAbsolutePath()));
		assertTrue(sut.readCsvContents().size() > 0);
	}

	@Test
	public void parse_correctly_luino_record() throws CsvParserException {
		sut = new CsvParser(new File(resourceFile.getAbsolutePath()));
		final List<CSVRecord> records = sut.readCsvContents();
		for (final CSVRecord territory : records) {
			if (territory.get(CsvHeaders.PLACE_NAME.name()).toLowerCase(Locale.getDefault()).equals("luino")) {
				assertEquals(territory.get(CsvHeaders.POSTAL_CODE.name()), End2End.LOCATION_LUINO_POSTCODE);
				assertEquals("46.0019", territory.get(CsvHeaders.LATITUDE.name()));
				assertEquals("8.7451", territory.get(CsvHeaders.LONGITUDE.name()));
			}
		}
	}

}