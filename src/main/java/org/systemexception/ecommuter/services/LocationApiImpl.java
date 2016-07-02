package org.systemexception.ecommuter.services;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.systemexception.ecommuter.Application;
import org.systemexception.ecommuter.api.LocationApi;
import org.systemexception.ecommuter.model.Address;

/**
 * @author leo
 * @date 02/07/16 01:23
 */
public class LocationApiImpl implements LocationApi {

	private final GeoApiContext geoApiContext = new GeoApiContext().setApiKey(Application.apiKey);

	@Override
	public Address geoToAddress(double latitude, double longitude) throws Exception {
		GeocodingResult[] geocodingResults = GeocodingApi.reverseGeocode(geoApiContext, new LatLng(latitude,
				longitude)).await();
		GeocodingResult geocodingResult = geocodingResults[0];

		return geoCodingResultToAddress(geocodingResult);
	}

	@Override
	public Address addressToGeo(String stringAddress) throws Exception {
		GeocodingResult[] geocodingResults = GeocodingApi.geocode(geoApiContext, stringAddress).await();
		GeocodingResult geocodingResult = geocodingResults[0];

		return geoCodingResultToAddress(geocodingResult);
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
			}
		}

		return address;
	}
}
