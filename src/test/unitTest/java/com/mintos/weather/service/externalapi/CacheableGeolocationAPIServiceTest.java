package com.mintos.weather.service.externalapi;

import com.mintos.weather.entity.IPRegionEntity;
import com.mintos.weather.exception.RegionLookupException;
import com.mintos.weather.service.GeoLocationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.mintos.weather.util.TestUtils.prepareIPRegionEntity;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CacheableGeolocationAPIServiceTest {

    @Mock
    CacheManager cacheManager;
    @Mock
    GeoLocationService geoLocationService;

    @Spy
    @InjectMocks
    CacheableGeolocationAPIService cacheableGeolocationAPIService;

    private static final String IP = "1.1.1.1";

    @Test
    public void shouldGetRegionByIP() {
        IPRegionEntity ipRegionEntity = prepareIPRegionEntity(IP, "Riga", 0L);
        when(geoLocationService.getRegionByIP(IP)).thenReturn(Mono.just(ipRegionEntity));

        Mono<IPRegionEntity> result = cacheableGeolocationAPIService.getRegionByIP(IP);

        StepVerifier.create(result)
            .expectNext(ipRegionEntity)
            .verifyComplete();
        verify(cacheableGeolocationAPIService, never()).evictCache(IP);
    }

    @Test
    public void shouldThrowOriginalRegionLookupExceptionAndEvictCache() {
        RegionLookupException regionLookupException = new RegionLookupException("123");
        doReturn(Mono.fromCallable(() -> {
            throw regionLookupException;
        })).when(geoLocationService).getRegionByIP(IP);

        Mono<IPRegionEntity> result = cacheableGeolocationAPIService.getRegionByIP(IP);

        StepVerifier.create(result)
            .expectErrorMatches(throwable -> throwable == regionLookupException)
            .verify();
        verify(cacheableGeolocationAPIService).evictCache(IP);
    }

    @Test
    public void shouldWrapOriginalExceptionWithRegionLookupExceptionAndEvictCache() {
        RuntimeException originalException = new RuntimeException("123");
        doReturn(Mono.fromCallable(() -> {
            throw originalException;
        })).when(geoLocationService).getRegionByIP(IP);

        Mono<IPRegionEntity> result = cacheableGeolocationAPIService.getRegionByIP(IP);

        StepVerifier.create(result)
            .expectErrorMatches(throwable -> throwable instanceof RegionLookupException &&
                String.format("Failed to get region by ip %s", IP).equals(throwable.getMessage()) &&
                throwable.getCause() == originalException
            )
            .verify();
        verify(cacheableGeolocationAPIService).evictCache(IP);
    }

}
