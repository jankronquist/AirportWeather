package com.jayway.airportweather;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;

public class Main {
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Syntax: com.jayway.airportweather.Main <AirtportCode>");
		} else {
			CamelContext context = AirportWeather.makeCamelContext();
			context.addRoutes(AirportWeather.createRouteBuilder());
			context.start();
			try {
				ProducerTemplate template = context.createProducerTemplate();
				Number result = (Number) template.requestBody(AirportWeather.GET_MAXIMUM_TEMPERATUR_AT_AIRPORT, args[0]);
				System.out.println(result);
			} finally {
				context.stop();
			}
		}
	}
}
