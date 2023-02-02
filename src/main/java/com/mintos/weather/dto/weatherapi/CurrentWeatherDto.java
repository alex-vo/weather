package com.mintos.weather.dto.weatherapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

import java.math.BigDecimal;

public record CurrentWeatherDto(

    @NonNull
    @JsonProperty("last_updated_epoch")
    Long lastUpdatedEpoch,

    @NonNull
    @JsonProperty("temp_c")
    BigDecimal temperatureCelsius,

    @NonNull
    @JsonProperty("wind_mph")
    BigDecimal windMpH,

    @NonNull
    @JsonProperty("wind_dir")
    String windDirection,

    @NonNull
    WeatherConditionDto condition
) {
}
