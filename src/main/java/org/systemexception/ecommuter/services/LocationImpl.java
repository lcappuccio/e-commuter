package org.systemexception.ecommuter.services;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.systemexception.ecommuter.Application;
import org.systemexception.ecommuter.api.LocationApi;
import org.systemexception.ecommuter.api.LoggerApi;
import org.systemexception.ecommuter.exceptions.LocationException;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.model.Persons;
import org.systemexception.ecommuter.pojo.HaversineUtil;
import org.systemexception.ecommuter.pojo.LoggerService;

/**
 * @author leo
 * @date 02/07/16 01:23
 */
public class LocationImpl implements LocationApi {

	private final LoggerApi logger = LoggerService.getFor(this.getClass());
	private final GeoApiContext geoApiContext = new GeoApiContext().setApiKey(Application.apiKey);
	private final HaversineUtil haversineUtil = new HaversineUtil();

	@Override
	public Address geoToAddress(final double latitude, final double longitude) throws LocationException {
		logger.geoToAddress(latitude, longitude);
		GeocodingResult[] geocodingResults;
		try {
			geocodingResults = GeocodingApi.reverseGeocode(geoApiContext, new LatLng(latitude,
					longitude)).await();
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			logger.geoToAddressError(latitude, longitude);
			throw new LocationException(errorMessage);
		}
		if (geocodingResults.length < 1) {
			logger.geoToAddressNoResult(latitude, longitude);
			return new Address();
		}
		GeocodingResult geocodingResult = geocodingResults[0];

		return geoCodingResultToAddress(geocodingResult);
	}

	@Override
	public Address addressToGeo(final String stringAddress) throws LocationException {
		logger.addressToGeo(stringAddress);
		GeocodingResult[] geocodingResults;
		try {
			geocodingResults = GeocodingApi.geocode(geoApiContext, stringAddress).await();
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			logger.addressToGeoError(stringAddress);
			throw new LocationException(errorMessage);
		}
		if (geocodingResults.length < 1) {
			logger.addressToGeoNoGeo(stringAddress);
			return new Address();
		}
		GeocodingResult geocodingResult = geocodingResults[0];

		return geoCodingResultToAddress(geocodingResult);
	}

	@Override
	public double distanceBetween(final Address addressA, final Address addressB) {
		logger.distanceBetween(addressA, addressB);
		return haversineUtil.haversine(addressA.getLatitude(), addressA.getLongitude(),
				addressB.getLatitude(), addressB.getLongitude());
	}

	@Override
	public Persons findNearbyPersons(final Person person, final Persons persons, final double radius) {
		logger.findNearbyPersons(person, persons, radius);
		Persons nearbyPersons = new Persons();
		if (persons.getPersons().contains(person)) {
			persons.getPersons().remove(person);
			logger.findNearbyPersonsRemoveSelf(person);
		}
		for (Person innerPerson : persons.getPersons()) {
			double distanceBetweenHome = distanceBetween(person.getHomeAddress(), innerPerson.getHomeAddress());
			double distanceBetweenWork = distanceBetween(person.getWorkAddress(), innerPerson.getWorkAddress());
			if (distanceBetweenHome <= radius && distanceBetweenWork <= radius) {
				logger.foundNearby(innerPerson, distanceBetweenHome, distanceBetweenWork);
				nearbyPersons.addPerson(innerPerson);
			} else {
				logger.excludedNearby(innerPerson, distanceBetweenHome, distanceBetweenWork);
			}
		}
		return nearbyPersons;
	}

	private Address geoCodingResultToAddress(final GeocodingResult geocodingResult) {
		Address address = new Address();
		address.setFormattedAddress(geocodingResult.formattedAddress);
		address.setLatitude(geocodingResult.geometry.location.lat);
		address.setLongitude(geocodingResult.geometry.location.lng);

		for (AddressComponent addressComponent : geocodingResult.addressComponents) {
			for (AddressComponentType addressComponentType : addressComponent.types) {
				if (addressComponentType.equals(AddressComponentType.COUNTRY)) {
					address.setCountry(addressComponent.longName);
				}
				if (addressComponentType.equals(AddressComponentType.POSTAL_CODE)) {
					address.setPostalCode(addressComponent.longName);
				}
				if (addressComponentType.equals(AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1)) {
					address.setAdministrativeAreaLevel1(addressComponent.longName);
				}
				if (addressComponentType.equals(AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_2)) {
					address.setAdministrativeAreaLevel2(addressComponent.longName);
				}
				if (addressComponentType.equals(AddressComponentType.LOCALITY)) {
					address.setLocality(addressComponent.longName);
				}
				if (addressComponentType.equals(AddressComponentType.ROUTE)) {
					address.setRoute(addressComponent.longName);
				}
				if (addressComponentType.equals(AddressComponentType.STREET_NUMBER)) {
					address.setStreetNumber(addressComponent.longName);
				}
			}
		}

		return address;
	}
}
