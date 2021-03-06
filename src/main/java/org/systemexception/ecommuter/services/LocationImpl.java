package org.systemexception.ecommuter.services;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.systemexception.ecommuter.Application;
import org.systemexception.ecommuter.enums.Constants;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.model.Persons;
import org.systemexception.ecommuter.model.Territory;
import org.systemexception.ecommuter.pojo.HaversineUtil;

/**
 * @author leo
 * @date 02/07/16 01:23
 */
public class LocationImpl implements LocationApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(LocationImpl.class);
	private static final String EMPTY_STRING = "";

	private final GeoApiContext geoApiContext = new GeoApiContext.Builder().apiKey(Application.API_KEY).build();
	private final HaversineUtil haversineUtil = new HaversineUtil();

	@Override
	public Address geoToAddress(final double latitude, final double longitude) throws Exception {
		LOGGER.info("geoToAddress" + Constants.LOG_OBJECT_SEPARATOR + latitude + Constants.LOG_ITEM_SEPARATOR +
				longitude);
		final GeocodingResult[] geocodingResults = GeocodingApi.reverseGeocode(geoApiContext, new LatLng(latitude, longitude)).await();
		if (geocodingResults.length < 1) {
			LOGGER.info("geoToAddressNoResult" + Constants.LOG_OBJECT_SEPARATOR + latitude +
					Constants.LOG_ITEM_SEPARATOR + longitude);
			return new Address();
		}
		final GeocodingResult geocodingResult = geocodingResults[0];

		return geoCodingResultToAddress(geocodingResult);
	}

	@Override
	public Address addressToGeo(final String stringAddress) throws Exception {
		LOGGER.info("addressToGeo" + Constants.LOG_OBJECT_SEPARATOR + stringAddress);
		final GeocodingResult[] geocodingResults = GeocodingApi.geocode(geoApiContext, stringAddress).await();
		if (geocodingResults.length < 1) {
			LOGGER.info("addressToGeoNoGeo" + Constants.LOG_OBJECT_SEPARATOR + stringAddress);
			return new Address();
		}
		final GeocodingResult geocodingResult = geocodingResults[0];

		return geoCodingResultToAddress(geocodingResult);
	}

	@Override
	public double distanceBetween(final Address addressA, final Address addressB) {
		return haversineUtil.haversine(addressA.getLatitude(), addressA.getLongitude(),
				addressB.getLatitude(), addressB.getLongitude());
	}

	@Override
	public Persons findNearbyPersons(final Person person, final Persons persons, final double radius) {
		LOGGER.info("findNearbyPersons" + Constants.LOG_OBJECT_SEPARATOR + person.getId() +
				Constants.LOG_ITEM_SEPARATOR + "distance " + radius);
		final Persons nearbyPersons = new Persons();
		persons.getPersons().remove(person);
		for (final Person innerPerson : persons.getPersons()) {
			double distanceBetweenHome = distanceBetween(person.getHomeAddress(), innerPerson.getHomeAddress());
			double distanceBetweenWork = distanceBetween(person.getWorkAddress(), innerPerson.getWorkAddress());
			if (distanceBetweenHome <= radius && distanceBetweenWork <= radius && !person.equals(innerPerson)) {
				LOGGER.info("foundNearby" + Constants.LOG_OBJECT_SEPARATOR + person.getId() +
						Constants.LOG_ITEM_SEPARATOR + innerPerson.getId());
				nearbyPersons.addPerson(innerPerson);
			}
		}
		return nearbyPersons;
	}

	private Address geoCodingResultToAddress(final GeocodingResult geocodingResult) {

		String country = EMPTY_STRING;
		String postalCode = EMPTY_STRING;
		String locality = EMPTY_STRING;
		Address address = new Address();

		address.setFormattedAddress(geocodingResult.formattedAddress);
		address.setLatitude(geocodingResult.geometry.location.lat);
		address.setLongitude(geocodingResult.geometry.location.lng);

		for (final AddressComponent addressComponent : geocodingResult.addressComponents) {
			for (AddressComponentType addressComponentType : addressComponent.types) {
				if (addressComponentType.equals(AddressComponentType.COUNTRY)) {
					country = addressComponent.shortName;
				}
				if (addressComponentType.equals(AddressComponentType.POSTAL_CODE)) {
					postalCode = addressComponent.shortName;
				}
				if (addressComponentType.equals(AddressComponentType.LOCALITY)) {
					locality = addressComponent.shortName;
				}
				if (addressComponentType.equals(AddressComponentType.ROUTE)) {
					address.setRoute(addressComponent.shortName);
				}
				if (addressComponentType.equals(AddressComponentType.STREET_NUMBER)) {
					address.setStreetNumber(addressComponent.shortName);
				}
			}
		}
		final Territory territory = new Territory(country, postalCode, locality);
		address.setTerritory(territory);

		return address;
	}
}
