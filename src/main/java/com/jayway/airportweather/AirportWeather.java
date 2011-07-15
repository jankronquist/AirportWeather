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
package com.jayway.airportweather;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.dataformat.xstream.XStreamDataFormat;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.commons.lang.time.DateUtils;

import com.jayway.airportweather.model.AirportLocation;
import com.jayway.airportweather.model.Location;
import com.jayway.xml.CDataTransformer;
import com.jayway.xml.MissingFieldIgnoringXStream;
import com.thoughtworks.xstream.XStream;

public class AirportWeather {
	public static final String GET_MAXIMUM_TEMPERATUR_AT_AIRPORT = "direct:getMaximumTemperaturAtAirport";
	public static final String FROM_NDFD_TO_TEMP_IN_CELCIUS = "direct:fromNDFDToTempInCelcius";
	public static final String FROM_LOCATION_TO_NDFD = "direct:fromLocationToNDFD";
	public static final String FROM_AIRPORT_INFORMATION_TO_LOCATION = "direct:fromAirportInformationToLocation";
	public static final String INVOKE_NDFD_GEN = "direct:invokeNDFDgen";
	public static final String GET_AIRPORT_INFORMATION_BY_AIRPORT_CODE = "direct:getAirportInformationByAirportCode";
	public static final String INVOKE_GET_AIRPORT_INFORMATION_BY_AIRPORT_CODE = "direct:invokeGetAirportInformationByAirportCode";

	public static CamelContext makeCamelContext() throws Exception {
		SimpleRegistry registry = new SimpleRegistry();
		registry.put("cdataTransformer", new CDataTransformer());
		registry.put("temperatureTransformer", new TemperatureTransformer());

		CamelContext context = new DefaultCamelContext(registry);
		
		return context;
	}

	public static RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			public void configure() throws Exception {
				from(INVOKE_GET_AIRPORT_INFORMATION_BY_AIRPORT_CODE)
					.to("velocity:getAirportInformationByAirportCode.vm")
					.to("spring-ws:http://www.webservicex.net/airport.asmx?soapAction=http://www.webserviceX.NET/getAirportInformationByAirportCode")
					.transform().xpath("/n:getAirportInformationByAirportCodeResponse/n:getAirportInformationByAirportCodeResult/text()", new Namespaces("n", "http://www.webserviceX.NET"))
					.transform().method("cdataTransformer");
				from(GET_AIRPORT_INFORMATION_BY_AIRPORT_CODE)
					.to(INVOKE_GET_AIRPORT_INFORMATION_BY_AIRPORT_CODE)
					.choice()
						.when().xpath("count(/NewDataSet/Table)=0").rollback("No Airport found")
						.otherwise();
				from(INVOKE_NDFD_GEN)
					.to("spring-ws:http://www.weather.gov/forecasts/xml/SOAP_server/ndfdXMLserver.php?soapAction=http://www.weather.gov/forecasts/xml/DWMLgen/wsdl/ndfdXML.wsdl#NDFDgen")
					.transform().xpath("/ns1:NDFDgenResponse/dwmlOut/text()", new Namespaces("ns1", "http://www.weather.gov/forecasts/xml/DWMLgen/wsdl/ndfdXML.wsdl"))
					.transform().method("cdataTransformer");
				from(FROM_AIRPORT_INFORMATION_TO_LOCATION)
					.transform().xpath("/NewDataSet/Table[1]")
					.unmarshal(xmlToAirPortLocation())
					.convertBodyTo(Location.class);
				from(FROM_LOCATION_TO_NDFD)
					.transform().method(LocationAndTime.class)
					.to("velocity:NDFDgen.vm")
					.to(INVOKE_NDFD_GEN);
				from(FROM_NDFD_TO_TEMP_IN_CELCIUS)
					.choice()
						.when().xpath("count(/dwml/data/parameters/temperature[@type='maximum']/value) = 0").rollback("No temperature found")
						.otherwise()
					.transform().xpath("/dwml/data/parameters/temperature[@type='maximum']/value[1]/text()")
					.convertBodyTo(Integer.class)
					.transform().method("temperatureTransformer", "fromFahrenheitToCelsius");
				from(GET_MAXIMUM_TEMPERATUR_AT_AIRPORT)
					.to(GET_AIRPORT_INFORMATION_BY_AIRPORT_CODE)
					.to(FROM_AIRPORT_INFORMATION_TO_LOCATION)
					.to(FROM_LOCATION_TO_NDFD)
					.to(FROM_NDFD_TO_TEMP_IN_CELCIUS);
			}
		};
	}
	
	public static class LocationAndTime {
		private static final String NDFN_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm";

		public static Map<String, Object> convert(Location location) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("location", location);
			Date today = new Date();
			Date tomorrow = DateUtils.addDays(today, 1);
			SimpleDateFormat dateFormat = new SimpleDateFormat(NDFN_DATE_FORMAT);
			map.put("startTime", dateFormat.format(today));
			map.put("endTime", dateFormat.format(tomorrow));
			return map;
		}
	}

	private static XStreamDataFormat xmlToAirPortLocation() {
		XStream xstream = new MissingFieldIgnoringXStream();
		xstream.alias("Table", AirportLocation.class);
		XStreamDataFormat dataFormat = new XStreamDataFormat(xstream);
		return dataFormat;
	}

}
