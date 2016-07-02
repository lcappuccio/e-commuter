package org.systemexception.ecommuter.api;

import org.systemexception.ecommuter.model.Address;

/**
 * @author leo
 * @date 02/07/16 10:45
 */
public interface Location {

	/**
	 * Builds an address using latitude and longitude
	 *
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	Address geoToAddress(double latitude, double longitude);

	/**
	 * Return a formatted address from a generic string
	 *
	 * @param address the generic string with an address
	 * @return
	 */
	Address addressToGeo(String address);
}
