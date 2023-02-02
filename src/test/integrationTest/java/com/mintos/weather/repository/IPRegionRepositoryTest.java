package com.mintos.weather.repository;

import com.mintos.weather.BaseIntegrationTest;
import com.mintos.weather.entity.IPRegionEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

public class IPRegionRepositoryTest extends BaseIntegrationTest {

    @Autowired
    IPRegionRepository ipRegionRepository;

    @Test
    public void shouldFindIPRegionEntityByIp() {
        Mono<IPRegionEntity> result = ipRegionRepository.findByIp("0.0.0.0");

        StepVerifier.create(result)
            .expectNextMatches(e -> UUID.fromString("3df99de8-7ae6-4342-bd6b-38f447e9079b").equals(e.id()))
            .verifyComplete();
    }

    @Test
    public void shouldFailToFindIPRegionEntityByIp() {
        Mono<IPRegionEntity> result = ipRegionRepository.findByIp("1.1.1.1");

        StepVerifier.create(result)
            .verifyComplete();
    }

}
