package org.systemexception.ecommuter.test.pojo;

import org.junit.Test;
import org.systemexception.ecommuter.pojo.HaversineUtil;

import static org.junit.Assert.assertEquals;

/**
 * @author leo
 * @date 02/07/16 13:34
 */
public class HaversineUtilTest {

	private final HaversineUtil sut = new HaversineUtil();

	@Test
	public void calculate_distance_luino_barcelona() {
		double luinoLat = 46.0021, luinoLong = 8.7507;
		double barcLat = 41.38879, barcLong = 2.15899;
		double distance = sut.haversine(luinoLat, luinoLong, barcLat, barcLong);
		assertEquals(737.9,distance,0);
	}

}