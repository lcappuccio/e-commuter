package org.systemexception.ecommuter.enums;

/**
 * @author leo
 * @date 06/03/15 21:13
 */
public enum CsvHeaders {

	POSTAL_CODE("POSTAL_CODE"),
	PLACE_NAME("PLACE_NAME"),
	LATITUDE("LATITUDE"),
	LONGITUDE("LONGITUDE");

	private final String csvHeader;

	CsvHeaders(String csvHeader) {
		this.csvHeader = csvHeader;
	}

	@Override
	public String toString() {
		return csvHeader;
	}
}
