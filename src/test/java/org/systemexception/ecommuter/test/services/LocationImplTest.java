package org.systemexception.ecommuter.test.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.systemexception.ecommuter.Application;
import org.systemexception.ecommuter.api.LocationApi;
import org.systemexception.ecommuter.exceptions.LocationException;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.services.LocationImpl;

import static org.junit.Assert.*;

/**
 * @author leo
 * @date 02/07/16 01:24
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
public class LocationImplTest {

	private final LocationApi sut = new LocationImpl();

	@Test
	public void address_to_geo() throws LocationException {
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
	public void geo_to_address() throws LocationException {
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

	@Test
	public void calculate_distance_for_addresses() throws LocationException {
		Address addressLuino = sut.addressToGeo("Piazza Garibaldi, Luino, VA");
		Address addressVarese = sut.addressToGeo("Piazza Giovanni XXIII, Varese, VA");
		double distanceBetween = sut.distanceBetween(addressLuino, addressVarese);

		assertEquals(19.7, distanceBetween, 0);
	}

	@Test
	public void calculate_distance_for_coordinates() throws LocationException {
		Address addressLuino = sut.geoToAddress(46.0021, 8.7507);
		Address addressBarcelona = sut.geoToAddress(41.38879, 2.15899);
		double distanceBetween = sut.distanceBetween(addressBarcelona, addressLuino);

		assertEquals(737.9, distanceBetween, 0);
	}

}