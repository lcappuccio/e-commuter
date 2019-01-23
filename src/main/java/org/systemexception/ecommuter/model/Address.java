package org.systemexception.ecommuter.model;

/**
 * @author leo
 * @date 01/07/16 23:06
 */
public class Address {

	private String streetNumber, route, formattedAddress;
	private double latitude, longitude;
	private Territory territory;

	public String getFormattedAddress() {
		return formattedAddress;
	}

	public void setFormattedAddress(final String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(final String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(final String route) {
		this.route = route;
	}

	public Territory getTerritory() {
		return territory;
	}

	public void setTerritory(final Territory territory) {
		this.territory = territory;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Address address = (Address) o;

		if (Double.compare(address.latitude, latitude) != 0) return false;
		if (Double.compare(address.longitude, longitude) != 0) return false;
		if (streetNumber != null ? !streetNumber.equals(address.streetNumber) : address.streetNumber != null)
			return false;
		if (route != null ? !route.equals(address.route) : address.route != null) return false;
		if (formattedAddress != null ? !formattedAddress.equals(address.formattedAddress) : address.formattedAddress
				!= null)
			return false;
		return territory != null ? territory.equals(address.territory) : address.territory == null;

	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = streetNumber != null ? streetNumber.hashCode() : 0;
		result = 31 * result + (route != null ? route.hashCode() : 0);
		result = 31 * result + (formattedAddress != null ? formattedAddress.hashCode() : 0);
		temp = Double.doubleToLongBits(latitude);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		result = 31 * result + (territory != null ? territory.hashCode() : 0);
		return result;
	}
}
