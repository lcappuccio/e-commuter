package org.systemexception.ecommuter.pojo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author leo
 * @date 02/07/16 13:34
 */
public class HaversineUtilTest {

	private final HaversineUtil sut = new HaversineUtil();

	@Test
	public void calculate_distance_luino_barcelona() {
		final double luinoLat = 46.0021, luinoLong = 8.7507;
		final double barcLat = 41.38879, barcLong = 2.15899;
		final double distance = sut.haversine(luinoLat, luinoLong, barcLat, barcLong);
		assertEquals(737.9,distance,0);
	}

}