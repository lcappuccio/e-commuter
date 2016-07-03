package org.systemexception.ecommuter.model;

/**
 * @author leo
 * @date 01/03/15 17:15
 */
public class Territory {

	private final String postalCode, placeName;

	public Territory(String postalCode, String placeName) {
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
