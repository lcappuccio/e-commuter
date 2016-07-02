package org.systemexception.ecommuter.test;

import org.junit.Test;
import org.systemexception.ecommuter.api.LocationApi;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.services.LocationApiImpl;

import static org.junit.Assert.*;

/**
 * @author leo
 * @date 02/07/16 01:24
 */
public class LocationApiImplTest {

	private final LocationApi sut = new LocationApiImpl();

	@Test
	public void address_to_geo() throws Exception {
		String stringAddress = "Piazza del Duomo Milano";
		Address geoFromAddress = sut.addressToGeo(stringAddress);

		assertNotNull(geoFromAddress);
		assertNotEquals(geoFromAddress, stringAddress);
		assertEquals("Piazza del Duomo, Milano, Italy", geoFromAddress.getFormattedAddress());
		assertEquals(45.4641776, geoFromAddress.getLatitude(), 0);
		assertEquals(9.1899885, geoFromAddress.getLongitude(), 0);
		assertEquals("Italy", geoFromAddress.getCountry());
		assertEquals("Lombardia", geoFromAddress.getAdministrativeAreaLevel1());
		assertEquals("Città Metropolitana di Milano", geoFromAddress.getAdministrativeAreaLevel2());
		assertEquals("Milano", geoFromAddress.getLocality());
		assertEquals("Italy", geoFromAddress.getCountry());
		assertEquals("Piazza del Duomo", geoFromAddress.getRoute());
	}

	@Test
	public void geo_to_address() throws Exception {
		Address addressFromGeo = sut.geoToAddress(45.4641776, 9.1899885);

		assertNotNull(addressFromGeo);
		assertEquals("Piazza del Duomo, 6, 20122 Milano, Italy", addressFromGeo.getFormattedAddress());
		assertEquals(45.4635507, addressFromGeo.getLatitude(), 0);
		assertEquals(9.1903881, addressFromGeo.getLongitude(), 0);
		assertEquals("Italy", addressFromGeo.getCountry());
		assertEquals("Lombardia", addressFromGeo.getAdministrativeAreaLevel1());
		assertEquals("Città Metropolitana di Milano", addressFromGeo.getAdministrativeAreaLevel2());
		assertEquals("Milano", addressFromGeo.getLocality());
		assertEquals("Italy", addressFromGeo.getCountry());
		assertEquals("Piazza del Duomo", addressFromGeo.getRoute());
		assertEquals("20122", addressFromGeo.getPostalCode());
	}

}