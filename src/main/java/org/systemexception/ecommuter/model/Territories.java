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

	public Territories(final File territoriesFile) throws CsvParserException {

		territories = new HashSet<>();

		LOGGER.info("readCsvTerritories" + Constants.LOG_OBJECT_SEPARATOR + territoriesFile.getName());
		final CsvParser csvParser = new CsvParser(territoriesFile);
		final List<CSVRecord> csvRecords = csvParser.readCsvContents();
		for (final CSVRecord csvRecord : csvRecords) {
			final String country = csvRecord.get(CsvHeaders.COUNTRY);
			final String postalCode = csvRecord.get(CsvHeaders.POSTAL_CODE);
			final String placeName = csvRecord.get(CsvHeaders.PLACE_NAME);
			final Territory territory = new Territory(country, postalCode, placeName);
			territories.add(territory);
		}
		LOGGER.info("finishCsvTerritories" + Constants.LOG_OBJECT_SEPARATOR + territoriesFile.getName());
	}

	public HashSet<Territory> getTerritories() {
		return territories;
	}

	void addTerritory(final Territory territory) {
		isValidTerritory(territory);
		territories.add(territory);
	}

	private void isValidTerritory(final Territory territory) {
		if (territories.contains(territory)) {
			final String errorMessage = "isValidTerritory"  + Constants.LOG_OBJECT_SEPARATOR + territory.getCountry() +
					Constants.LOG_ITEM_SEPARATOR + territory.getPostalCode();
			LOGGER.error(errorMessage);
			throw new InvalidParameterException(errorMessage);
		}
	}
}
