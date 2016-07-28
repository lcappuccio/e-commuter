package org.systemexception.ecommuter.enums;

/**
 * @author leo
 * @date 02/07/16 23:34
 */
public enum DatabaseConfiguration {

	// TERRITORIES
	POSTAL_CODE("postalCode"),
	PLACE_NAME("placeName"),
	// PERSON
	PERSON_ID("id"),
	PERSON_DATA("personData"),
	// EDGE
	LIVES_IN("livesIn"),
	WORKS_IN("worksIn");

	private final String databaseConfiguration;

	DatabaseConfiguration(String databaseConfiguration) {
		this.databaseConfiguration = databaseConfiguration;
	}

	@Override
	public String toString() {
		return databaseConfiguration;
	}
}
