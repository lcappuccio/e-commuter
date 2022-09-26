package org.systemexception.ecommuter.services;

import com.google.maps.errors.ApiException;
import org.springframework.stereotype.Service;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.model.Persons;

import java.io.IOException;

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
	Address geoToAddress(double latitude, double longitude) throws IOException, InterruptedException, ApiException;

	/**
	 * Return a formatted address from a generic string
	 *
	 * @param address the generic string with an address
	 * @return
	 */
	Address addressToGeo(String address) throws IOException, InterruptedException, ApiException;

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
