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

public class AirportLocation {
	public String AirportCode;
	public String CityOrAirportName;
	public String Country;
	public String CountryAbbrviation;
	public String CountryCode;
	public String GMTOffset;
//	public int RunwayLengthFeet;
//	public int RunwayElevationFeet;
	public int LatitudeDegree;
	public int LatitudeMinute;
	public int LatitudeSecond;
	public String LatitudeNpeerS;
	public int LongitudeDegree;
	public int LongitudeMinute;
	public int LongitudeSeconds;
	public String LongitudeEperW;
	
	public Location toLocation() {
		return new Location(this.LatitudeDegree, this.LatitudeMinute, this.LatitudeSecond, "N".equalsIgnoreCase(this.LatitudeNpeerS),
				 this.LongitudeDegree, this.LongitudeMinute, this.LongitudeSeconds, "E".equalsIgnoreCase(this.LongitudeEperW));
	}
}
