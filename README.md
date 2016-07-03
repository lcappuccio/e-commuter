# e-commuter
Use Google Maps Geocoding API to calculate distances between two addresses or geographic coordinates.

**Master**

[![Build Status](https://travis-ci.org/lcappuccio/e-commuter.svg?branch=master)](https://travis-ci.org/lcappuccio/e-commuter)
[![codecov.io](https://codecov.io/github/lcappuccio/e-commuter/coverage.svg?branch=master)](https://codecov.io/github/lcappuccio/e-commuter?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ada9114fdc1a48ad93f6824fd40bbead)](https://www.codacy.com/app/lcappuccio/e-commuter?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=lcappuccio/e-commuter&amp;utm_campaign=Badge_Grade)

**Develop**

[![Build Status](https://travis-ci.org/lcappuccio/e-commuter.svg?branch=develop)](https://travis-ci.org/lcappuccio/e-commuter)
[![codecov.io](https://codecov.io/github/lcappuccio/e-commuter/coverage.svg?branch=develop)](https://codecov.io/github/lcappuccio/e-commuter?branch=develop)

## Implementation

We're using the Google API only to geocode an address and viceversa (reverse geocoding).
The haversine formula is used to calculate the distance between two addresses in order to keep API calls to a minimum.

## Data Model

See `e-commuter.pdf` for more details.

## Usage

Set `API_KEY` environment variable with your Google Maps API Key.

**WARNING**

Any class that references `org.systemexception.ecommuter.services.LocationImpl` will produce logs with your google
api key, remember to use the spring context to disable the logs if using public repositories or continuous
integration services.

Logging in test `application.properties` has been disabled for class `org.systemexception.ecommuter.services.GeoApi`
to hide the api key in Travis logs, re-enable in local environment for debugging or analysis.

This implies that the Spring context has to be booted in order to read the properties file, add to every impacted
test the necessary configuration annotations:

```
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
```

## ToDo

- REST endpoints
- Frontend
- Investigate Google Directions API to calculate distance with different travel types (foot, bike, transport)
- Pending `spring-boot-starter-data-neo4j` release