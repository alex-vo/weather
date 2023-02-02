package com.mintos.weather.service;

import com.mintos.weather.dto.WeatherDto;
import com.mintos.weather.dto.weatherapi.WeatherAPIResponseDto;
import com.mintos.weather.entity.IPRegionEntity;
import com.mintos.weather.entity.WeatherEntity;
import com.mintos.weather.mapper.WeatherMapper;
import com.mintos.weather.repository.WeatherRepository;
import com.mintos.weather.service.externalapi.CacheableGeolocationAPIService;
import com.mintos.weather.service.externalapi.WeatherAPIService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static com.mintos.weather.util.TestUtils.*;
import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    @Mock
    WeatherRepository weatherRepository;
    @Mock
    CacheableGeolocationAPIService cacheableGeolocationAPIService;
    @Mock
    WeatherAPIService weatherAPIService;
    @Mock
    WeatherMapper weatherMapper;

    @Spy
    @InjectMocks
    WeatherService weatherService;

    private static final String IP = "1.1.1.1";

    @Test
    public void shouldGetWeatherByIp() {
        WeatherEntity weatherEntity = prepareWeatherEntity("Windy", BigDecimal.ZERO, BigDecimal.ONE, "N", 0L, randomUUID());
        WeatherDto dto = new WeatherDto(weatherEntity.id(), "", BigDecimal.ZERO, BigDecimal.ONE, "E", 0L);
        IPRegionEntity ipRegionEntity = new IPRegionEntity(UUID.randomUUID(), IP, "Riga", 0L);
        when(cacheableGeolocationAPIService.getRegionByIP(IP)).thenReturn(Mono.just(ipRegionEntity));
        when(weatherMapper.toDto(weatherEntity)).thenReturn(dto);
        doReturn(Mono.just(weatherEntity)).when(weatherService).processWeatherForRegion(ipRegionEntity);

        Mono<WeatherDto> result = weatherService.getWeather(IP);

        StepVerifier.create(result)
            .expectNext(dto)
            .verifyComplete();
    }

    @Test
    public void shouldProcessWeatherForRegion() {
        IPRegionEntity ipRegionEntity = prepareIPRegionEntity(IP, "Riga", 0L);
        WeatherAPIResponseDto weatherAPIResponseDto = prepareWeatherAPIResponseDto("Overcast", BigDecimal.ZERO, BigDecimal.ONE, "W", 0L);
        WeatherEntity weatherEntity = prepareWeatherEntity("Overcast", BigDecimal.ZERO, BigDecimal.ONE, "W", 0L, ipRegionEntity.id());
        when(weatherRepository.save(weatherEntity)).thenReturn(Mono.just(weatherEntity));
        when(weatherMapper.toEntity(weatherAPIResponseDto, ipRegionEntity.id())).thenReturn(weatherEntity);
        when(weatherAPIService.retrieveWeather("Riga")).thenReturn(Mono.just(weatherAPIResponseDto));

        Mono<WeatherEntity> result = weatherService.processWeatherForRegion(ipRegionEntity);

        StepVerifier.create(result)
            .expectNext(weatherEntity)
            .verifyComplete();
        verify(weatherRepository).save(weatherEntity);
    }

}
