package com.mintos.weather.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record WeatherDto(
    UUID id,
    String condition,
    BigDecimal temperatureCelsius,
    BigDecimal windMpH,
    String windDirection,
    Long lastUpdatedEpoch
) {
}
