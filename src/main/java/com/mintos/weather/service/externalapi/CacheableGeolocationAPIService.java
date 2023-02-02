package com.mintos.weather.service.externalapi;

import com.mintos.weather.entity.IPRegionEntity;
import com.mintos.weather.exception.RegionLookupException;
import com.mintos.weather.service.GeoLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CacheableGeolocationAPIService {

    private final CacheManager cacheManager;
    private final GeoLocationService geoLocationService;

    public Mono<IPRegionEntity> getRegionByIP(String ip) {
        return geoLocationService.getRegionByIP(ip)
            .doOnError(throwable -> {
                evictCache(ip);
                if (throwable instanceof RegionLookupException) {
                    throw (RegionLookupException) throwable;
                }

                throw new RegionLookupException(String.format("Failed to get region by ip %s", ip), throwable);
            });
    }

    void evictCache(String ip) {
        Cache cache = cacheManager.getCache("regionByIp");
        if (cache != null) {
            cache.evict(ip);
        }
    }

}
