package org.systemexception.ecommuter.pojo;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.systemexception.ecommuter.enums.Constants;
import org.systemexception.ecommuter.enums.CsvHeaders;
import org.systemexception.ecommuter.exceptions.CsvParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.List;

/**
 * @author leo
 * @date 01/03/15 14:59
 */
public class CsvParser {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<CSVRecord> records;

	public CsvParser(File csvFile) throws CsvParserException {
		String[] headerMapping = new String[]{
				CsvHeaders.COUNTRY.name(), CsvHeaders.POSTAL_CODE.name(), CsvHeaders.PLACE_NAME.name(),
				CsvHeaders.ADMIN_NAME1.name(), CsvHeaders.ADMIN_CODE1.name(),
				CsvHeaders.ADMIN_NAME2.name(), CsvHeaders.ADMIN_CODE2.name(),
				CsvHeaders.ADMIN_NAME3.name(), CsvHeaders.ADMIN_CODE3.name(),
				CsvHeaders.LATITUDE.name(), CsvHeaders.LONGITUDE.name(), CsvHeaders.ACCURACY.name()};
		CSVFormat csvFormat = CSVFormat.RFC4180.withHeader(headerMapping).withSkipHeaderRecord(true);
		try {
			URL csvUrl = csvFile.toURI().toURL();
			Reader csvReader = new InputStreamReader(csvUrl.openStream(), "UTF-8");
			CSVParser csvParser = new CSVParser(csvReader, csvFormat);
			records = csvParser.getRecords();
			logger.info("loadedCsv" + Constants.LOG_OBJECT_SEPARATOR + csvFile.getName());
		} catch (IOException ex) {
			String errorMessage = ex.getMessage();
			logger.error(csvFile.getName() + Constants.LOG_ITEM_SEPARATOR + errorMessage);
			throw new CsvParserException(errorMessage);
		}
	}

	public List readCsvContents() {
		return records;
	}
}
