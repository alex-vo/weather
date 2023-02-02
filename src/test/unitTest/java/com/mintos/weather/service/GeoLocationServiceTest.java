package com.mintos.weather.service;

import com.mintos.weather.entity.IPRegionEntity;
import com.mintos.weather.repository.IPRegionRepository;
import com.mintos.weather.service.externalapi.GeolocationAPIService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GeoLocationServiceTest {

    @Mock
    IPRegionRepository ipRegionRepository;
    @Mock
    GeolocationAPIService geolocationAPIService;

    @Spy
    @InjectMocks
    GeoLocationService geoLocationService;

    private static final String IP = "1.1.1.1";

    @Test
    public void shouldProcessIPRegion() {
        long start = System.currentTimeMillis();
        when(geolocationAPIService.retrieveRegion(IP)).thenReturn(Mono.just("Riga"));
        when(ipRegionRepository.save(any(IPRegionEntity.class)))
            .thenAnswer(invocation -> Mono.just(invocation.getArguments()[0]));

        Mono<IPRegionEntity> result = geoLocationService.processIPRegion(IP);

        StepVerifier.create(result)
            .expectNextMatches(ipRegionEntity -> ipRegionEntity.id() != null &&
                IP.equals(ipRegionEntity.ip()) &&
                "Riga".equals(ipRegionEntity.region()) &&
                start < ipRegionEntity.lookedUpAtEpoch())
            .verifyComplete();
    }

    @Test
    public void shouldGetRegionByIPFromDb() {
        IPRegionEntity ipRegionEntity = new IPRegionEntity(UUID.randomUUID(), IP, "Riga", 0L);
        when(ipRegionRepository.findByIp(IP)).thenReturn(Mono.just(ipRegionEntity));

        Mono<IPRegionEntity> result = geoLocationService.getRegionByIP(IP);

        StepVerifier.create(result)
            .expectNext(ipRegionEntity)
            .verifyComplete();
    }

    @Test
    public void shouldProcessMissingRegionInDb() {
        IPRegionEntity ipRegionEntity = new IPRegionEntity(UUID.randomUUID(), IP, "Riga", 0L);
        when(ipRegionRepository.findByIp(IP)).thenReturn(Mono.empty());
        doReturn(Mono.just(ipRegionEntity)).when(geoLocationService).processIPRegion(IP);

        Mono<IPRegionEntity> result = geoLocationService.getRegionByIP(IP);

        StepVerifier.create(result)
            .expectNext(ipRegionEntity)
            .verifyComplete();
    }

}
