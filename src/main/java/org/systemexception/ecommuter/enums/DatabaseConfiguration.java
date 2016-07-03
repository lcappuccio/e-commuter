package org.systemexception.ecommuter.enums;

/**
 * @author leo
 * @date 02/07/16 23:34
 */
public enum DatabaseConfiguration {

	NEO_INDEX_PARAMETER("exact"),
	VERTEX_TERRITORY_CLASS("class:Territory"),
	VERTEX_INDEX("vertexIndex"),
	POSTAL_CODE("postalCode"),
	PLACE_NAME("placeName");

	private final String databaseConfiguration;

	DatabaseConfiguration(String databaseConfiguration) {
		this.databaseConfiguration = databaseConfiguration;
	}

	@Override
	public String toString() {
		return databaseConfiguration;
	}
}
