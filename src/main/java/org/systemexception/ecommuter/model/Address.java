package org.systemexception.ecommuter.model;

/**
 * @author leo
 * @date 01/07/16 23:06
 */
public class Address {

	/**
	 * Following model in
	 * https://developers.google.com/maps/documentation/geocoding/start
	 */
	private String streetNumber, route, locality, administrativeAreaLevel2, administrativeAreaLevel1, country,
			postalCode;

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
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
}
