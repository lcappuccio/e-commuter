package org.systemexception.ecommuter.model;

import java.util.Objects;

/**
 * @author leo
 * @date 01/03/15 17:15
 */
public class Territory {

	private String country, postalCode, placeName;

	public Territory() {}

	public Territory(String country, String postalCode, String placeName) {
		this.country = country;
		this.postalCode = postalCode;
		this.placeName = placeName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(final String country) {
		this.country = country;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(final String postalCode) {
		this.postalCode = postalCode;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(final String placeName) {
		this.placeName = placeName;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Territory territory = (Territory) o;

		if (!Objects.equals(country, territory.country)) return false;
		if (!Objects.equals(postalCode, territory.postalCode)) return false;
		return Objects.equals(placeName, territory.placeName);

	}

	@Override
	public int hashCode() {
		int result = country != null ? country.hashCode() : 0;
		result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
		result = 31 * result + (placeName != null ? placeName.hashCode() : 0);
		return result;
	}
}
