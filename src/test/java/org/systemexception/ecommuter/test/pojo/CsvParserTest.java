package org.systemexception.ecommuter.test.pojo;

import org.apache.commons.csv.CSVRecord;
import org.junit.BeforeClass;
import org.junit.Test;
import org.systemexception.ecommuter.enums.CsvHeaders;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.pojo.CsvParser;
import org.systemexception.ecommuter.test.End2End;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

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
		URL myTestURL = ClassLoader.getSystemResource(DATABASE_TEST_CSV_FILE);
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
		List<CSVRecord> records = sut.readCsvContents();
		for (CSVRecord territory : records) {
			if (territory.get(CsvHeaders.PLACE_NAME.toString()).toLowerCase(Locale.getDefault()).equals("luino")) {
				assertTrue(territory.get(CsvHeaders.POSTAL_CODE.toString()).equals(End2End.LOCATION_LUINO_POSTCODE));
				assertTrue(territory.get(CsvHeaders.LATITUDE.toString()).equals("46.0019"));
				assertTrue(territory.get(CsvHeaders.LONGITUDE.toString()).equals("8.7451"));
			}
		}
	}

}