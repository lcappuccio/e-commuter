package org.systemexception.ecommuter.model;

/**
 * @author leo
 * @date 01/03/15 17:15
 */
public class Territory {

	private String country, postalCode, placeName, administrativeAreaLevel2, administrativeAreaLevel1;

	public Territory() {}

	public Territory(String country, String postalCode, String placeName, String administrativeAreaLevel1,
	                 String administrativeAreaLevel2) {
		this.country = country;
		this.postalCode = postalCode;
		this.placeName = placeName;
		this.administrativeAreaLevel1 = administrativeAreaLevel1;
		this.administrativeAreaLevel2 = administrativeAreaLevel2;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public String getAdministrativeAreaLevel2() {
		return administrativeAreaLevel2;
	}

	public void setAdministrativeAreaLevel2(String administrativeAreaLevel2) {
		this.administrativeAreaLevel2 = administrativeAreaLevel2;
	}

	public String getAdministrativeAreaLevel1() {
		return administrativeAreaLevel1;
	}

	public void setAdministrativeAreaLevel1(String administrativeAreaLevel1) {
		this.administrativeAreaLevel1 = administrativeAreaLevel1;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Territory territory = (Territory) o;

		if (country != null ? !country.equals(territory.country) : territory.country != null) return false;
		if (postalCode != null ? !postalCode.equals(territory.postalCode) : territory.postalCode != null) return false;
		if (placeName != null ? !placeName.equals(territory.placeName) : territory.placeName != null) return false;
		if (administrativeAreaLevel2 != null ? !administrativeAreaLevel2.equals(territory.administrativeAreaLevel2) :
				territory.administrativeAreaLevel2 != null)
			return false;
		return administrativeAreaLevel1 != null ? administrativeAreaLevel1.equals(territory.administrativeAreaLevel1)
				: territory.administrativeAreaLevel1 == null;

	}

	@Override
	public int hashCode() {
		int result = country != null ? country.hashCode() : 0;
		result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
		result = 31 * result + (placeName != null ? placeName.hashCode() : 0);
		result = 31 * result + (administrativeAreaLevel2 != null ? administrativeAreaLevel2.hashCode() : 0);
		result = 31 * result + (administrativeAreaLevel1 != null ? administrativeAreaLevel1.hashCode() : 0);
		return result;
	}
}
