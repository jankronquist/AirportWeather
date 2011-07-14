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
package com.jayway.camel.typeconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.jayway.airportweather.model.AirportLocation;
import com.jayway.airportweather.model.Location;

public class UsingReflectionTest {
	public static class AirportLocationDummy extends AirportLocation {
	}

	public static class LocationDummy extends Location {
		private final int dummy;

		public LocationDummy(AirportLocation o, int dummy) {
			super(o);
			this.dummy = dummy;
		}
	}

	@Test
	public void testUsingConstructor() {
		checkLocation(UsingConstructor.convertTo(Location.class, null, prepare(new AirportLocationDummy()), null));
		checkLocation(UsingConstructor.convertTo(Location.class, null, prepare(new AirportLocation()), null));
		assertNull(UsingConstructor.convertTo(LocationDummy.class, null, prepare(new AirportLocation()), null));
		assertNull(UsingConstructor.convertTo(Object.class, null, prepare(new AirportLocation()), null));
	}

	@Test
	public void testUsingMethod() {
		checkLocation(UsingMethod.convertTo(Location.class, null, prepare(new AirportLocationDummy()), null));
		checkLocation(UsingMethod.convertTo(Location.class, null, prepare(new AirportLocation()), null));
		assertNull(UsingMethod.convertTo(LocationDummy.class, null, prepare(new AirportLocation()), null));
		assertNull(UsingMethod.convertTo(Object.class, null, prepare(new AirportLocation()), null));
	}

	@Test
	public void usingMethodCallsToString() {
		assertEquals("0.0 0.0", UsingMethod.convertTo(String.class, null, new Location(0.0, 0.0), null));
	}

	private AirportLocation prepare(AirportLocation value) {
		value.LatitudeDegree = 10;
		value.LongitudeDegree = 20;
		value.LatitudeNpeerS = "N";
		value.LongitudeEperW = "E";
		return value;
	}

	private void checkLocation(Location location) {
		assertEquals(10.0, location.getLatitude(), 0.00001);
	}

}
