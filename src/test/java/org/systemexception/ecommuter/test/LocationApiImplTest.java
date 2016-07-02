package org.systemexception.ecommuter.test;

import org.junit.Test;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.services.LocationApiImpl;

import static org.junit.Assert.*;

/**
 * @author leo
 * @date 02/07/16 01:24
 */
public class LocationApiImplTest {

	private final LocationApiImpl sut = new LocationApiImpl();

	@Test
	public void address_to_geo() throws Exception {
		String address = "Piazza del Duomo Milano";
		Address geoCodingResult = sut.addressToGeo(address);

		assertNotNull(geoCodingResult);
		assertNotEquals(geoCodingResult, address);
		assertEquals("Piazza del Duomo, Milano, Italy", geoCodingResult.getFormattedAddress());
		assertEquals(45.4641776, geoCodingResult.getLatitude(), 0);
		assertEquals(9.1899885, geoCodingResult.getLongitude(), 0);
	}

}