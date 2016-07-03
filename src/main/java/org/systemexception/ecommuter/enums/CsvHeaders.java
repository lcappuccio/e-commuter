package org.systemexception.ecommuter.enums;

/**
 * @author leo
 * @date 06/03/15 21:13
 */
public enum CsvHeaders {

	COUNTRY("COUNTRY"),
	POSTAL_CODE("POSTAL_CODE"),
	PLACE_NAME("PLACE_NAME"),
	ADMIN_NAME1("ADMIN_NAME1"),
	ADMIN_CODE1("ADMIN_CODE1"),
	ADMIN_NAME2("ADMIN_NAME2"),
	ADMIN_CODE2("ADMIN_CODE2"),
	ADMIN_NAME3("ADMIN_NAME3"),
	ADMIN_CODE3("ADMIN_CODE3"),
	LATITUDE("LATITUDE"),
	LONGITUDE("LONGITUDE"),
	ACCURACY("ACCURACY");

	private final String csvHeader;

	CsvHeaders(String csvHeader) {
		this.csvHeader = csvHeader;
	}

	@Override
	public String toString() {
		return csvHeader;
	}
}
