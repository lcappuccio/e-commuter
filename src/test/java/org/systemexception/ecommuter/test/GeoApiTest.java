package org.systemexception.ecommuter.test;

import org.junit.Test;
import org.systemexception.ecommuter.services.GeoApi;

import static org.junit.Assert.assertNotNull;

/**
 * @author leo
 * @date 02/07/16 01:24
 */
public class GeoApiTest {

	private final GeoApi sut = new GeoApi();

	@Test
	public void canary_test() throws Exception {
		String address = sut.getAddress();
		assertNotNull(address);
	}

}