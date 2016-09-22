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

	public String getCountry() {
		return country;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Territory territory = (Territory) o;

		if (country != null ? !country.equals(territory.country) : territory.country != null) return false;
		if (postalCode != null ? !postalCode.equals(territory.postalCode) : territory.postalCode != null) return false;
		return placeName != null ? placeName.equals(territory.placeName) : territory.placeName == null;

	}

	@Override
	public int hashCode() {
		int result = country != null ? country.hashCode() : 0;
		result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
		result = 31 * result + (placeName != null ? placeName.hashCode() : 0);
		return result;
	}
}
