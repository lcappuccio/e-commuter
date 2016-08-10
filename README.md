# e-commuter
Use Google Maps Geocoding API to calculate distances between two addresses of a collection of persons.

**Master**

[![Build Status](https://travis-ci.org/lcappuccio/e-commuter.svg?branch=master)](https://travis-ci.org/lcappuccio/e-commuter)
[![codecov.io](https://codecov.io/github/lcappuccio/e-commuter/coverage.svg?branch=master)](https://codecov.io/github/lcappuccio/e-commuter?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ada9114fdc1a48ad93f6824fd40bbead)](https://www.codacy.com/app/lcappuccio/e-commuter?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=lcappuccio/e-commuter&amp;utm_campaign=Badge_Grade)
[![codebeat badge](https://codebeat.co/badges/a2f9fc57-5721-4ccd-a732-8949bdf98a8b)](https://codebeat.co/projects/github-com-lcappuccio-e-commuter)

**Develop**

[![Build Status](https://travis-ci.org/lcappuccio/e-commuter.svg?branch=develop)](https://travis-ci.org/lcappuccio/e-commuter)
[![codecov.io](https://codecov.io/github/lcappuccio/e-commuter/coverage.svg?branch=develop)](https://codecov.io/github/lcappuccio/e-commuter?branch=develop)

## Implementation

We're using the Google API only to geocode an address and viceversa (reverse geocoding).
The haversine formula is used to calculate the distance between two addresses in order to keep API calls to a minimum.

## Data Model

See `e-commuter.pdf` for more details.

## Territories

Download a Postal Code collection from [Geonames](http://www.geonames.org) and use it as you please. Please keep in
mind that duplicates will generate an exception. I've used a SQL database to clean down the Italian file.

## Configuration

Set `API_KEY` environment variable with your Google Maps API Key.

### Application

Launch the application and use the included Postman collection to interact with the API.
1. Load a territories set (or use the included `geo_data.csv`)
2. Add the persons included in `persons.txt`
3. Locate nearby persons
4. Play with addresses and geo coordinates to fetch addresses from Google Geocode API

### Adding data

1. Create a work address invoking endpoint `addressToGeo`, in the JSON specify the formatted address string, the 
other parameters are not necessary
2. Create a home address as above
3. Paste the above addresses into the fields workAddress and homeAddress of a Person object and send to endpoint `addPerson` 
4. Repeat previous steps for various persons living and working nearby
5. Send one of the created persons to `nearbyPersons` with the desired radius to extract a list of Persons living and
 working nearby

## Endpoints

Automated documentation provided by Swagger: [API Documentation](http://localhost:8080/swagger-ui.html)

## Monitoring

Spring Boot actuators are deployed, set the `management.port` parameter in `application.properties`:

* http://your_ip:management.port/beans
* http://your_ip:management.port/metrics
* http://your_ip:management.port/autoconfig

## WARNING

Any class that references `org.systemexception.ecommuter.services.LocationImpl` will produce logs with your google
api key, remember to use the spring context to disable the logs if using public repositories or continuous
integration services.

Logging in test `application.properties` has been disabled for class `org.systemexception.ecommuter.services.GeoApi`
to hide the api key in Travis logs, re-enable in local environment for debugging or analysis.

This implies that the Spring context has to be booted in order to read the properties file and disable the Google
logging, add the necessary configuration annotations to every impacted test or class:

```
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
```

## ToDo

In sparse order:

- Add google api key in properties file if needed, otherwise fetch key from environment variable
- Frontend
- Investigate Google Directions API to calculate distance with different travel types (foot, bike, transport)
- Pending `spring-boot-starter-data-neo4j` release for refactor
- Add export functionality
- Add endpoints to query database data

## Credits

- [Geonames](http://www.geonames.org) for the Postal Code Database