# Airport weather service

This is a a service that can return the weather at an airport, implemented using 
Apache Camel and freely available SOAP web services.

Example usage:

       mvn exec:java -Dexec.mainClass="com.jayway.airportweather.Main" -Dexec.args="JFK" 

Goals of this project:

- Use web services from different providers
- Be able to test the routes
- Include error handling
- Try various methods for transformation

Notice that webservicex.net is quite unreliable during US working hours, 
probably because of to too many requests. 

Also notice that the web services only support US airports!

For more information see http://blog.jayway.com/2011/07/14/apache_camel_and_soap/

