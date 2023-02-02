package com.mintos.weather.mapper;

import com.mintos.weather.dto.WeatherDto;
import com.mintos.weather.dto.weatherapi.WeatherAPIResponseDto;
import com.mintos.weather.entity.WeatherEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WeatherMapper {

    public WeatherEntity toEntity(WeatherAPIResponseDto dto, UUID ipRegionId) {
        return new WeatherEntity(
            UUID.randomUUID(),
            dto.current().condition().text(),
            dto.current().temperatureCelsius(),
            dto.current().windMpH(),
            dto.current().windDirection(),
            dto.current().lastUpdatedEpoch(),
            ipRegionId
        );
    }

    public WeatherDto toDto(WeatherEntity weatherEntity) {
        return new WeatherDto(
            weatherEntity.id(),
            weatherEntity.condition(),
            weatherEntity.temperatureCelsius(),
            weatherEntity.windMph(),
            weatherEntity.windDirection(),
            weatherEntity.lastUpdatedEpoch()
        );
    }

}
