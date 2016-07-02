package org.systemexception.ecommuter.services;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import org.systemexception.ecommuter.Application;

/**
 * @author leo
 * @date 02/07/16 01:23
 */
public class GeoApi {

	public String getAddress(String address) throws Exception {
		GeoApiContext geoApiContext = new GeoApiContext().setApiKey(Application.apiKey);
		GeocodingResult[] geocodingResults =  GeocodingApi.geocode(geoApiContext, address).await();
		return geocodingResults[0].formattedAddress;
	}
}
