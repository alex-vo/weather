package com.mintos.weather.util;

import com.mintos.weather.dto.weatherapi.CurrentWeatherDto;
import com.mintos.weather.dto.weatherapi.WeatherAPIResponseDto;
import com.mintos.weather.dto.weatherapi.WeatherConditionDto;
import com.mintos.weather.entity.IPRegionEntity;
import com.mintos.weather.entity.WeatherEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtils {

    public static WeatherAPIResponseDto prepareWeatherAPIResponseDto(
        String condition,
        BigDecimal temperatureCelsius,
        BigDecimal windMpH,
        String windDirection,
        Long lastUpdatedEpoch
    ) {
        return new WeatherAPIResponseDto(
            new CurrentWeatherDto(
                lastUpdatedEpoch,
                temperatureCelsius,
                windMpH,
                windDirection,
                new WeatherConditionDto(condition)
            )
        );
    }

    public static IPRegionEntity prepareIPRegionEntity(String ip, String region, Long lookedUpAtEpoch) {
        return new IPRegionEntity(randomUUID(), ip, region, lookedUpAtEpoch);
    }

    public static WeatherEntity prepareWeatherEntity(
        String condition,
        BigDecimal temperatureCelsius,
        BigDecimal windMph,
        String windDirection,
        Long lastUpdatedEpoch,
        UUID ipRegionId
    ) {
        return new WeatherEntity(randomUUID(), condition, temperatureCelsius, windMph, windDirection, lastUpdatedEpoch, ipRegionId);
    }

}
