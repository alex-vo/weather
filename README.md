# Weather Service

This service aims to retrieve weather conditions for the client's IP location region. 
Client location region is retrieved from the `http://ip-api.com/` service by the IP address.
Weather conditions are retrieved from the `http://api.weatherapi.com/` service by the region.

To run tests locally make sure Docker is installed, then execute

```
./gradlew test
```

To start the application locally execute

```
./gradlew bootRun
```

![example workflow](https://github.com/alex-vo/weather/workflows/build/badge.svg)
[![Maintainability](https://api.codeclimate.com/v1/badges/0444faf405c546139a39/maintainability)](https://codeclimate.com/github/alex-vo/weather/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/0444faf405c546139a39/test_coverage)](https://codeclimate.com/github/alex-vo/weather/test_coverage)