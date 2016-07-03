package org.systemexception.ecommuter.api;

import org.springframework.stereotype.Service;
import org.systemexception.ecommuter.exceptions.LocationException;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.model.Persons;

/**
 * @author leo
 * @date 02/07/16 10:45
 */
@Service
public interface LocationApi {

	/**
	 * Builds an address using latitude and longitude
	 *
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	Address geoToAddress(double latitude, double longitude) throws LocationException;

	/**
	 * Return a formatted address from a generic string
	 *
	 * @param address the generic string with an address
	 * @return
	 */
	Address addressToGeo(String address) throws LocationException;

	/**
	 * Calculates distance between two addresses
	 *
	 * @param addressA
	 * @param addressB
	 * @return
	 */
	double distanceBetween(Address addressA, Address addressB);

	/**
	 * Finds persons nearby this area of interest
	 *
	 * @param person
	 * @param persons
	 * @return
	 */
	Persons findNearbyPersons(Person person, Persons persons, double radius);
}
