package org.systemexception.ecommuter.enums;

/**
 * @author leo
 * @date 02/07/16 23:34
 */
public enum DatabaseConfiguration {

	NEO_INDEX_PARAMETER("exact"),
	// TERRITORIES
	VERTEX_TERRITORY_CLASS("class:Territory"),
	POSTAL_CODE("postalCode"),
	PLACE_NAME("placeName"),
	// PERSON
	VERTEX_PERSON_CLASS("class:Person"),
	NAME("name"),
	SURNAME("surname"),
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
