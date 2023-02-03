package com.mintos.weather.entity

import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.util.UUID

@Table("WEATHER")
data class WeatherEntity(
    val id: UUID,
    val condition: String,
    val temperatureCelsius: BigDecimal,
    val windMph: BigDecimal,
    val windDirection: String,
    val lastUpdatedEpoch: Long,
    val ipRegionId: UUID
)