package com.mintos.weather.controller;

import com.mintos.weather.BaseComponentTest;
import com.mintos.weather.dto.WeatherDto;
import com.mintos.weather.repository.IPRegionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

public class IPRegionCachingTest extends BaseComponentTest {

    @SpyBean
    IPRegionRepository ipRegionRepository;

    @Test
    public void shouldCacheIPAddressRegion_BothTimesSameRegionWeather() throws IOException {
        String weatherConditionRiga = "Light sleet showers";
        setUpIPAPIResponse(DEFAULT_IP, 200, "{\"regionName\": \"Riga\"}");
        setUpWeatherAPIResponse(
            "Riga",
            200,
            Files.readString(Path.of("src/test/componentTest/resources/mock_responses/riga_weather.json"))
        );

        WeatherDto result = performSuccessfulGet("/api/v1/weather", WeatherDto.class);

        assertEquals(weatherConditionRiga, result.condition());

        result = performSuccessfulGet("/api/v1/weather", WeatherDto.class);

        assertEquals(weatherConditionRiga, result.condition());
        verify(ipRegionRepository).findByIp(DEFAULT_IP);
    }

}
