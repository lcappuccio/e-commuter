package org.systemexception.ecommuter.pojo;

import static java.lang.Math.*;

/**
 * @author leo
 * @date 02/07/16 13:29
 */
public class HaversineUtil {

	private static final double EARTH_RADIUS = 6378;

	public double haversine(final double latitude1, final double longitude1, final double latitude2,
	                                final double longitude2) {

		final double latitude1Radian = toRadian(latitude1);
		final double longitude1Radian = toRadian(longitude1);
		final double latitude2Radian = toRadian(latitude2);
		final double longitude2Radian = toRadian(longitude2);

		final double arg1 = squareSineDifference(latitude2Radian, latitude1Radian);
		final double arg2 = squareSineDifference(longitude2Radian, longitude1Radian);
		final double arg3 = sqrt(arg1 + (cos(latitude1Radian) * cos(latitude2Radian) * arg2));
		return roundDoubleToOneDecimal(2 * EARTH_RADIUS * asin(arg3));
	}

	private double squareSineDifference(final double phi1, final double phi2) {
		return pow(sin((phi2 - phi1) / 2), 2);
	}

	private double toRadian(final double value) {
		return value * PI / 180;
	}

	private double roundDoubleToOneDecimal(final double value) {
		final int precision = 1;
		final int scale = (int) pow(10, precision);
		return (double) round(value * scale) / scale;
	}
}
