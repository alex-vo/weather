package com.mintos.weather.dto

import java.math.BigDecimal
import java.util.UUID

data class WeatherDto(
    val id: UUID,
    val condition: String,
    val temperatureCelsius: BigDecimal,
    val windMpH: BigDecimal,
    val windDirection: String,
    val lastUpdatedEpoch: Long,
)
