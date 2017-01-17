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

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final GeoApiContext geoApiContext = new GeoApiContext().setApiKey(Application.apiKey);
	private final HaversineUtil haversineUtil = new HaversineUtil();
	private static final String EMPTY_STRING = "";

	@Override
	public Address geoToAddress(final double latitude, final double longitude) throws Exception {
		logger.info("geoToAddress" + Constants.LOG_OBJECT_SEPARATOR + latitude + Constants.LOG_ITEM_SEPARATOR +
				longitude);
		GeocodingResult[] geocodingResults;
		geocodingResults = GeocodingApi.reverseGeocode(geoApiContext, new LatLng(latitude, longitude)).await();
		if (geocodingResults.length < 1) {
			logger.info("geoToAddressNoResult" + Constants.LOG_OBJECT_SEPARATOR + latitude +
					Constants.LOG_ITEM_SEPARATOR + longitude);
			return new Address();
		}
		GeocodingResult geocodingResult = geocodingResults[0];

		return geoCodingResultToAddress(geocodingResult);
	}

	@Override
	public Address addressToGeo(final String stringAddress) throws Exception {
		logger.info("addressToGeo" + Constants.LOG_OBJECT_SEPARATOR + stringAddress);
		GeocodingResult[] geocodingResults;
		geocodingResults = GeocodingApi.geocode(geoApiContext, stringAddress).await();
		if (geocodingResults.length < 1) {
			logger.info("addressToGeoNoGeo" + Constants.LOG_OBJECT_SEPARATOR + stringAddress);
			return new Address();
		}
		GeocodingResult geocodingResult = geocodingResults[0];

		return geoCodingResultToAddress(geocodingResult);
	}

	@Override
	public double distanceBetween(final Address addressA, final Address addressB) {
		logger.info("distanceBetween" + Constants.LOG_OBJECT_SEPARATOR +
				"(" + addressA.getLatitude() + Constants.LOG_ITEM_SEPARATOR + addressA.getLongitude() + ")" +
				Constants.LOG_ITEM_SEPARATOR +
				"(" + addressB.getLatitude() + Constants.LOG_ITEM_SEPARATOR + addressB.getLongitude() + ")");
		return haversineUtil.haversine(addressA.getLatitude(), addressA.getLongitude(),
				addressB.getLatitude(), addressB.getLongitude());
	}

	@Override
	public Persons findNearbyPersons(final Person person, final Persons persons, final double radius) {
		logger.info("findNearbyPersons" + Constants.LOG_OBJECT_SEPARATOR + person.getId() +
				Constants.LOG_ITEM_SEPARATOR + "distance " + radius);
		Persons nearbyPersons = new Persons();
		if (persons.getPersons().contains(person)) {
			persons.getPersons().remove(person);
		}
		for (Person innerPerson : persons.getPersons()) {
			double distanceBetweenHome = distanceBetween(person.getHomeAddress(), innerPerson.getHomeAddress());
			double distanceBetweenWork = distanceBetween(person.getWorkAddress(), innerPerson.getWorkAddress());
			if (distanceBetweenHome <= radius && distanceBetweenWork <= radius && !person.equals(innerPerson)) {
				logger.info("foundNearby" + Constants.LOG_OBJECT_SEPARATOR + person.getId() +
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

		for (AddressComponent addressComponent : geocodingResult.addressComponents) {
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
		Territory territory = new Territory(country, postalCode, locality);
		address.setTerritory(territory);

		return address;
	}
}
