package org.systemexception.ecommuter.model;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.systemexception.ecommuter.enums.Constants;
import org.systemexception.ecommuter.enums.CsvHeaders;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.pojo.CsvParser;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.List;

/**
 * @author leo
 * @date 01/03/15 18:25
 */
public class Territories {

	private static final Logger LOGGER = LoggerFactory.getLogger(Territories.class);

	private final HashSet<Territory> territories;

	public Territories() {
		this.territories = new HashSet<>();
	}

	public Territories(File territoriesFile) throws CsvParserException {

		territories = new HashSet<>();

		LOGGER.info("readCsvTerritories" + Constants.LOG_OBJECT_SEPARATOR + territoriesFile.getName());
		CsvParser csvParser = new CsvParser(territoriesFile);
		List<CSVRecord> csvRecords = csvParser.readCsvContents();
		for (CSVRecord csvRecord : csvRecords) {
			String country = csvRecord.get(CsvHeaders.COUNTRY);
			String postalCode = csvRecord.get(CsvHeaders.POSTAL_CODE);
			String placeName = csvRecord.get(CsvHeaders.PLACE_NAME);
			Territory territory = new Territory(country, postalCode, placeName);
			territories.add(territory);
		}
		LOGGER.info("finishCsvTerritories" + Constants.LOG_OBJECT_SEPARATOR + territoriesFile.getName());

	}

	public HashSet<Territory> getTerritories() {
		return territories;
	}

	private void isValidTerritory(final Territory territory) {
		if (territories.contains(territory)) {
			String errorMessage = "isValidTerritory"  + Constants.LOG_OBJECT_SEPARATOR + territory.getCountry() +
					Constants.LOG_ITEM_SEPARATOR + territory.getPostalCode();
			LOGGER.error(errorMessage);
			throw new InvalidParameterException(errorMessage);
		}
	}

	public void addTerritory(final Territory territory) {
		isValidTerritory(territory);
		territories.add(territory);
	}
}
