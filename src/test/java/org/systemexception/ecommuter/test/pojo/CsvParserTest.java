package org.systemexception.ecommuter.test.pojo;

import org.apache.commons.csv.CSVRecord;
import org.junit.BeforeClass;
import org.junit.Test;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.pojo.CsvParser;

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
	private static final String testFile = "it_data_SMALL.csv";
	private static File resourceFile;

	@BeforeClass
	public static void setUp() throws URISyntaxException {
		URL myTestURL = ClassLoader.getSystemResource(testFile);
		resourceFile = new File(myTestURL.toURI());
	}

	@Test
	public void open_test_territories_file() {
		assertTrue(resourceFile.exists());
	}

	@Test(expected = CsvParserException.class)
	public void throw_exception_for_nonexisting_file() throws CsvParserException {
		sut = new CsvParser("nonexistingfile.txt");
	}

	@Test
	public void reads_territories_test_file() throws CsvParserException {
		sut = new CsvParser(resourceFile.getAbsolutePath());
		assertTrue(sut.readCsvContents().size() > 0);
	}

	@Test
	public void parse_correctly_luino_record() throws CsvParserException {
		sut = new CsvParser(resourceFile.getAbsolutePath());
		List<CSVRecord> records = sut.readCsvContents();
		for (CSVRecord territory : records) {
			if (territory.get("PLACE_NAME").toLowerCase(Locale.getDefault()).equals("luino")) {
				assertTrue(territory.get("POSTAL_CODE").equals("21016"));
				assertTrue(territory.get("LATITUDE").equals("46.0019"));
				assertTrue(territory.get("LONGITUDE").equals("8.7451"));
			}
		}
	}

}