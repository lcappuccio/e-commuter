package org.systemexception.ecommuter.services;

import static java.lang.Math.*;
import static java.lang.Math.PI;
import static java.lang.Math.sin;

/**
 * @author leo
 * @date 02/07/16 13:29
 */
public class HaversineService {

	private final double earthRadius = 6378;

	public double haversine(final double latitude1, final double longitude1, final double latitude2,
	                                final double longitude2) {

		double latitude1Radian = toRadian(latitude1);
		double longitude1Radian = toRadian(longitude1);
		double latitude2Radian = toRadian(latitude2);
		double longitude2Radian = toRadian(longitude2);

		double arg1 = squareSineDifference(latitude2Radian, latitude1Radian);
		double arg2 = squareSineDifference(longitude2Radian, longitude1Radian);
		double arg3 = sqrt(arg1 + (cos(latitude1Radian) * cos(latitude2Radian) * arg2));
		return roundDoubleToOneDecimal(2 * earthRadius * asin(arg3));
	}

	private double squareSineDifference(double phi1, double phi2) {
		return pow(sin((phi2 - phi1) / 2), 2);
	}

	private double toRadian(final double value) {
		return value * PI / 180;
	}

	private double roundDoubleToOneDecimal(double value) {
		int precision = 1;
		int scale = (int) Math.pow(10, precision);
		return (double) Math.round(value * scale) / scale;
	}
}
