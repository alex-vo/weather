package com.mintos.weather.mapper

import com.mintos.weather.dto.WeatherDto
import com.mintos.weather.dto.weatherapi.WeatherAPIResponseDto
import com.mintos.weather.entity.WeatherEntity
import java.util.UUID

fun WeatherAPIResponseDto.toEntity(ipRegionId: UUID): WeatherEntity =
    with(current) {
        return WeatherEntity(
            UUID.randomUUID(),
            condition.text,
            temperatureCelsius,
            windMpH,
            windDirection,
            lastUpdatedEpoch,
            ipRegionId
        )
    }

fun WeatherEntity.toDto(): WeatherDto = WeatherDto(
    id,
    condition,
    temperatureCelsius,
    windMph,
    windDirection,
    lastUpdatedEpoch
)