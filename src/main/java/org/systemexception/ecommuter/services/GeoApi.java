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

	public String getAddress() throws Exception {
		// Replace the API key below with a valid API key.
		GeoApiContext context = new GeoApiContext().setApiKey(Application.apiKey);
		GeocodingResult[] results =  GeocodingApi.geocode(context, "Piazza del Duomo, Milano, 20100")
				.await();
		return results[0].formattedAddress;
	}
}
