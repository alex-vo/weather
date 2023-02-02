package com.mintos.weather.mapper;

import com.mintos.weather.dto.WeatherDto;
import com.mintos.weather.dto.weatherapi.CurrentWeatherDto;
import com.mintos.weather.dto.weatherapi.WeatherAPIResponseDto;
import com.mintos.weather.dto.weatherapi.WeatherConditionDto;
import com.mintos.weather.entity.WeatherEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WeatherMapperTest {

    WeatherMapper weatherMapper = new WeatherMapper();

    @Test
    public void shouldMapWeatherAPIResponseDtoToEntity() {
        UUID ipRegionId = randomUUID();
        WeatherAPIResponseDto dto = new WeatherAPIResponseDto(new CurrentWeatherDto(0L, BigDecimal.ZERO, BigDecimal.ONE, "W", new WeatherConditionDto("Clear")));

        WeatherEntity result = weatherMapper.toEntity(dto, ipRegionId);

        assertNotNull(result.id());
        assertEquals("Clear", result.condition());
        assertEquals(BigDecimal.ZERO, result.temperatureCelsius());
        assertEquals(BigDecimal.ONE, result.windMph());
        assertEquals("W", result.windDirection());
        assertEquals(0L, result.lastUpdatedEpoch());
        assertEquals(ipRegionId, result.ipRegionId());
    }

    @Test
    public void shoulMapWeatherEntityToDto() {
        UUID id = randomUUID();
        UUID ipRegionId = randomUUID();
        WeatherEntity weatherEntity = new WeatherEntity(id, "Windy", BigDecimal.ZERO, BigDecimal.ONE, "N", 0L, ipRegionId);

        WeatherDto result = weatherMapper.toDto(weatherEntity);

        assertEquals(id, result.id());
        assertEquals("Windy", result.condition());
        assertEquals(BigDecimal.ZERO, result.temperatureCelsius());
        assertEquals(BigDecimal.ONE, result.windMpH());
        assertEquals("N", result.windDirection());
        assertEquals(0L, result.lastUpdatedEpoch());
    }

}
