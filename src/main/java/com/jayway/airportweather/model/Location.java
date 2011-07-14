/*
 * Copyright 2011 Jan Kronquist.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jayway.airportweather.model;

public class Location {
	private final double latitude;
	private final double longitude;

	// Enable this to test the UsingConstructor type converter.
	public Location(AirportLocation o) {
		this(o.LatitudeDegree, o.LatitudeMinute, o.LatitudeSecond, "N".equalsIgnoreCase(o.LatitudeNpeerS),
			 o.LongitudeDegree, o.LongitudeMinute, o.LongitudeSeconds, "E".equalsIgnoreCase(o.LongitudeEperW));
	}

	public Location(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Location(double latitudeDegree, double latitudeMinute, double latitudeSecond, boolean north,
			double longitudeDegree, double longitudeMinute, double longitudeSeconds, boolean east) {
		int eastMultiplier = east ? 1 : -1;
		int northMultiplier = north ? 1 : -1;
		this.latitude = northMultiplier * (latitudeDegree + latitudeMinute/60.0 + latitudeSecond/3600.0);
		this.longitude = eastMultiplier * (longitudeDegree +  longitudeMinute/60.0 + longitudeSeconds/3600.0);
	}
	
	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	@Override
	public String toString() {
		return latitude + " " + longitude;
	}
}
