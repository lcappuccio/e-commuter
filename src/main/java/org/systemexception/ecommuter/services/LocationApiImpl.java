package org.systemexception.ecommuter.services;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import org.systemexception.ecommuter.Application;
import org.systemexception.ecommuter.api.LocationApi;
import org.systemexception.ecommuter.model.Address;

/**
 * @author leo
 * @date 02/07/16 01:23
 */
public class LocationApiImpl implements LocationApi {

	@Override
	public Address geoToAddress(double latitude, double longitude) throws Exception {
		return null;
	}

	@Override
	public Address addressToGeo(String stringAddress) throws Exception {
		GeoApiContext geoApiContext = new GeoApiContext().setApiKey(Application.apiKey);
		GeocodingResult[] geocodingResults =  GeocodingApi.geocode(geoApiContext, stringAddress).await();
		GeocodingResult geocodingResult = geocodingResults[0];

		Address address = new Address();
		address.setFormattedAddress(geocodingResult.formattedAddress);
		address.setLatitude(geocodingResult.geometry.location.lat);
		address.setLongitude(geocodingResult.geometry.location.lng);

		return address;
	}
}
