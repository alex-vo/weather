package com.mintos.weather.service;

import com.mintos.weather.entity.IPRegionEntity;
import com.mintos.weather.repository.IPRegionRepository;
import com.mintos.weather.service.externalapi.GeolocationAPIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static java.lang.System.currentTimeMillis;
import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeoLocationService {

    private final IPRegionRepository ipRegionRepository;
    private final GeolocationAPIService geolocationAPIService;

    @Cacheable(cacheNames = {"regionByIp"}, key = "#ip")
    public Mono<IPRegionEntity> getRegionByIP(String ip) {
        log.info(String.format("Looking up client region by ip [%s]...", ip));
        return ipRegionRepository.findByIp(ip)
            .map(Optional::of)
            .defaultIfEmpty(Optional.empty())
            .flatMap(o -> {
                if (o.isPresent()) {
                    return Mono.just(o.get());
                }

                return processIPRegion(ip);
            });
    }

    Mono<IPRegionEntity> processIPRegion(String ip) {
        log.info(String.format("No region present in the database by ip [%s], executing API lookup...", ip));
        return geolocationAPIService.retrieveRegion(ip)
            .flatMap(region -> ipRegionRepository.save(new IPRegionEntity(randomUUID(), ip, region, currentTimeMillis())));
    }

}
