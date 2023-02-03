package com.mintos.weather.service.externalapi

import com.mintos.weather.config.WeatherAPIConfigurationProperties
import com.mintos.weather.dto.weatherapi.WeatherAPIResponseDto
import com.mintos.weather.exception.WeatherLookupException
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class WeatherAPIService(
    private val weatherAPIWebClient: WebClient,
    weatherAPIConfigurationProperties: WeatherAPIConfigurationProperties,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val weatherAPIKey = weatherAPIConfigurationProperties.key

    suspend fun retrieveWeather(region: String): WeatherAPIResponseDto {
        log.info("Calling Weather API for weather data in region [$region]")
        try {
            val weatherAPIResponseDto = retrieveWeatherFromAPI(region)
            log.info("Retrieved data from Weather API for region [$region]")
            return weatherAPIResponseDto
        } catch (e: WebClientRequestException) {
            throw WeatherLookupException("Failed to connect to Weather API", e)
        } catch (e: WebClientResponseException) {
            throw WeatherLookupException("Received error response [${e.statusCode}] from Weather API service: ${e.responseBodyAsString}")
        }
    }

    suspend fun retrieveWeatherFromAPI(region: String): WeatherAPIResponseDto =
        weatherAPIWebClient.get()
            .uri("/v1/current.json?key=$weatherAPIKey&q=$region")
            .retrieve()
            .bodyToMono<WeatherAPIResponseDto>()
            .awaitSingle()

}