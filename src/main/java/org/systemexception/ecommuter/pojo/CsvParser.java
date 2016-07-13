package org.systemexception.ecommuter.pojo;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.systemexception.ecommuter.api.LoggerApi;
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

	private final LoggerApi logger = LoggerService.getFor(this.getClass());
	private List<CSVRecord> records;

	public CsvParser(File csvFile) throws CsvParserException {
		String[] headerMapping = new String[]{
				CsvHeaders.COUNTRY.toString(), CsvHeaders.POSTAL_CODE.toString(), CsvHeaders.PLACE_NAME.toString(),
				CsvHeaders.ADMIN_NAME1.toString(), CsvHeaders.ADMIN_CODE1.toString(),
				CsvHeaders.ADMIN_NAME2.toString(), CsvHeaders.ADMIN_CODE2.toString(),
				CsvHeaders.ADMIN_NAME3.toString(), CsvHeaders.ADMIN_CODE3.toString(),
				CsvHeaders.LATITUDE.toString(), CsvHeaders.LONGITUDE.toString(), CsvHeaders.ACCURACY.toString()};
		CSVFormat csvFormat = CSVFormat.RFC4180.withHeader(headerMapping).withSkipHeaderRecord(true);
		try {
			URL csvUrl = csvFile.toURI().toURL();
			Reader csvReader = new InputStreamReader(csvUrl.openStream(), "UTF-8");
			CSVParser csvParser = new CSVParser(csvReader, csvFormat);
			records = csvParser.getRecords();
			logger.csvLoaded(csvFile);
		} catch (IOException ex) {
			String errorMessage = ex.getMessage();
			logger.csvLoadError(csvFile, errorMessage);
			throw new CsvParserException(errorMessage);
		}
	}

	public List readCsvContents() {
		return records;
	}
}
