package org.systemexception.ecommuter.pojo;

import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.systemexception.ecommuter.End2EndTest;
import org.systemexception.ecommuter.enums.CsvHeaders;
import org.systemexception.ecommuter.exceptions.CsvParserException;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author leo
 * @date 02/07/16 21:59
 */
public class CsvParserTest {

	private CsvParser sut;
	public static final String DATABASE_TEST_CSV_FILE = "geodata_SMALL.csv";
	private static File resourceFile;

	@BeforeAll
	static void setUp() throws URISyntaxException {
		final URL myTestURL = ClassLoader.getSystemResource(DATABASE_TEST_CSV_FILE);
		resourceFile = new File(myTestURL.toURI());
	}

	@Test
	void open_test_territories_file() {
        assertTrue(resourceFile.exists());
	}

	@Test
	void throw_exception_for_nonexisting_file(){
        assertThrows(CsvParserException.class,
                () -> {
                    new CsvParser(new File("nonexistingfile.txt"));
                });
    };

	@Test
	void reads_territories_test_file() throws CsvParserException {
		sut = new CsvParser(new File(resourceFile.getAbsolutePath()));
		assertTrue(sut.readCsvContents().size() > 0);
	}

	@Test
	void parse_correctly_luino_record() throws CsvParserException {
		sut = new CsvParser(new File(resourceFile.getAbsolutePath()));
		final List<CSVRecord> records = sut.readCsvContents();
		for (final CSVRecord territory : records) {
			if (territory.get(CsvHeaders.PLACE_NAME.name()).toLowerCase(Locale.getDefault()).equals("luino")) {
				assertEquals(End2EndTest.LOCATION_LUINO_POSTCODE, territory.get(CsvHeaders.POSTAL_CODE.name()));
				assertEquals("46.0019", territory.get(CsvHeaders.LATITUDE.name()));
				assertEquals("8.7451", territory.get(CsvHeaders.LONGITUDE.name()));
			}
		}
	}

}