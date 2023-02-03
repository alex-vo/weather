package com.mintos.weather.dto.weatherapi

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class WeatherAPIResponseDto(
    val current: CurrentWeatherDto
)

data class CurrentWeatherDto(
    @JsonProperty("last_updated_epoch")
    val lastUpdatedEpoch: Long,
    @JsonProperty("temp_c")
    val temperatureCelsius: BigDecimal,
    @JsonProperty("wind_mph")
    val windMpH: BigDecimal,
    @JsonProperty("wind_dir")
    val windDirection: String,
    val condition: WeatherConditionDto,
)

data class WeatherConditionDto(
    val text: String
)