# e-commuter
WIP

**Master**

[![Build Status](https://travis-ci.org/lcappuccio/e-commuter.svg?branch=master)](https://travis-ci.org/lcappuccio/e-commuter)
[![codecov.io](https://codecov.io/github/lcappuccio/e-commuter/coverage.svg?branch=master)](https://codecov.io/github/lcappuccio/e-commuter?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ada9114fdc1a48ad93f6824fd40bbead)](https://www.codacy.com/app/lcappuccio/e-commuter?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=lcappuccio/e-commuter&amp;utm_campaign=Badge_Grade)

**Develop**

[![Build Status](https://travis-ci.org/lcappuccio/e-commuter.svg?branch=develop)](https://travis-ci.org/lcappuccio/e-commuter)
[![codecov.io](https://codecov.io/github/lcappuccio/e-commuter/coverage.svg?branch=develop)](https://codecov.io/github/lcappuccio/e-commuter?branch=develop)

## Usage

Set `API_KEY` environment variable with your Google Maps API Key.

## Logging

Logging in test `application.properties` has been disabled for class `org.systemexception.ecommuter.services.GeoApi`
to hide the api key in Travis logs, re-enable in local environment for debugging or analysis.