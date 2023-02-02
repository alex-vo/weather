package com.mintos.weather.entity;

import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table("WEATHER")
public record WeatherEntity(
    UUID id,
    String condition,
    BigDecimal temperatureCelsius,
    BigDecimal windMph,
    String windDirection,
    Long lastUpdatedEpoch,
    UUID ipRegionId
) {
}
