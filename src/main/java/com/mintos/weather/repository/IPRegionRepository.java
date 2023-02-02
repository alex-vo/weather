package com.mintos.weather.repository;

import com.mintos.weather.entity.IPRegionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IPRegionRepository extends ReactiveCrudRepository<IPRegionEntity, UUID> {

    Mono<IPRegionEntity> findByIp(String ip);

}
