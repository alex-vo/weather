package com.mintos.weather.service;

import com.mintos.weather.dto.WeatherDto;
import com.mintos.weather.entity.IPRegionEntity;
import com.mintos.weather.entity.WeatherEntity;
import com.mintos.weather.mapper.WeatherMapper;
import com.mintos.weather.repository.WeatherRepository;
import com.mintos.weather.service.externalapi.CacheableGeolocationAPIService;
import com.mintos.weather.service.externalapi.WeatherAPIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    private final WeatherRepository weatherRepository;
    private final CacheableGeolocationAPIService cacheableGeolocationAPIService;
    private final WeatherAPIService weatherAPIService;
    private final WeatherMapper weatherMapper;

    public Mono<WeatherDto> getWeather(String ip) {
        log.info(String.format("Started processing weather lookup request for [%s]", ip));
        return cacheableGeolocationAPIService.getRegionByIP(ip)
            .flatMap(this::processWeatherForRegion)
            .map(weatherEntity -> {
                log.info(String.format("Finished processing weather lookup request for [%s]", ip));
                return weatherMapper.toDto(weatherEntity);
            });
    }

    Mono<WeatherEntity> processWeatherForRegion(IPRegionEntity ipRegion) {
        return weatherAPIService.retrieveWeather(ipRegion.region())
            .map(weatherAPIResponseDto -> weatherMapper.toEntity(weatherAPIResponseDto, ipRegion.id()))
            .flatMap(weatherRepository::save);
    }
}
