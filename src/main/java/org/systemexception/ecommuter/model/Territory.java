package org.systemexception.ecommuter.model;

/**
 * @author leo
 * @date 01/03/15 17:15
 */
public class Territory {

	private final String country, postalCode, placeName;

	public Territory(String country, String postalCode, String placeName) {
		this.country = country;
		this.postalCode = postalCode;
		this.placeName = placeName;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getPlaceName() {
		return placeName;
	}
}
