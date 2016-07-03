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
import org.systemexception.ecommuter.api.LocationApi;
import org.systemexception.ecommuter.exceptions.LocationImplException;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.pojo.HaversineUtil;

/**
 * @author leo
 * @date 02/07/16 01:23
 */
public class LocationImpl implements LocationApi {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final GeoApiContext geoApiContext = new GeoApiContext().setApiKey(Application.apiKey);
	private final HaversineUtil haversineUtil = new HaversineUtil();
	private final String logSeparator = ",";

	@Override
	public Address geoToAddress(double latitude, double longitude) throws LocationImplException {
		logger.info("GeoToAddress: (" + latitude + logSeparator + longitude + ")");
		GeocodingResult[] geocodingResults;
		try {
			geocodingResults = GeocodingApi.reverseGeocode(geoApiContext, new LatLng(latitude,
					longitude)).await();
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			logger.error(errorMessage);
			throw new LocationImplException(errorMessage);
		}
		if (geocodingResults.length < 1) {
			logger.info("GeoToAddress: no Address from (" + latitude + logSeparator + longitude + ")");
			return new Address();
		}
		GeocodingResult geocodingResult = geocodingResults[0];

		return geoCodingResultToAddress(geocodingResult);
	}

	@Override
	public Address addressToGeo(String stringAddress) throws LocationImplException {
		logger.info("AddressToGeo: " + stringAddress);
		GeocodingResult[] geocodingResults;
		try {
			geocodingResults = GeocodingApi.geocode(geoApiContext, stringAddress).await();
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			logger.error(errorMessage);
			throw new LocationImplException(errorMessage);
		}
		if (geocodingResults.length < 1) {
			logger.info("AddressToGeo: no Geo from Address " + stringAddress);
			return new Address();
		}
		GeocodingResult geocodingResult = geocodingResults[0];

		return geoCodingResultToAddress(geocodingResult);
	}

	@Override
	public double distanceBetween(Address addressA, Address addressB) {
		logger.info("DistanceBetween: (" + addressA.getLatitude() + logSeparator + addressA.getLongitude() +
				") to (" + addressB.getLatitude() + logSeparator + addressB.getLongitude() + ")");
		return haversineUtil.haversine(addressA.getLatitude(), addressA.getLongitude(),
				addressB.getLatitude(), addressB.getLongitude());
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