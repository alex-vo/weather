package com.mintos.weather.service.externalapi;

import com.mintos.weather.config.WeatherAPIConfigurationProperties;
import com.mintos.weather.dto.weatherapi.WeatherAPIResponseDto;
import com.mintos.weather.exception.WeatherLookupException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherAPIService {

    private final WebClient weatherAPIWebClient;
    private final WeatherAPIConfigurationProperties weatherAPIConfigurationProperties;
    private final APIExecutionService apiExecutionService;

    public Mono<WeatherAPIResponseDto> retrieveWeather(String region) {
        log.info(String.format("Calling Weather API for weather data in region [%s]", region));
        return retrieveWeatherFromAPI(region)
            .map(weatherAPIResponseDto -> {
                log.info(String.format("Retrieved data from Weather API for region [%s]", region));
                return weatherAPIResponseDto;
            })
            .doOnError(throwable -> {
                if (throwable instanceof WebClientResponseException e) {
                    throw new WeatherLookupException(String.format("Received error response [%s] from Weather API service: %s", e.getStatusCode(), e.getResponseBodyAsString()));
                }

                throw new WeatherLookupException("Failed to connect to Weather API", throwable);
            });
    }

    Mono<WeatherAPIResponseDto> retrieveWeatherFromAPI(String region) {
        return apiExecutionService.getOneObject(
            weatherAPIWebClient,
            String.format("/v1/current.json?key=%s&q=%s", weatherAPIConfigurationProperties.key(), region),
            WeatherAPIResponseDto.class
        );
    }

}
