package com.mintos.weather.service.externalapi;

import com.mintos.weather.dto.ipapi.IPDto;
import com.mintos.weather.exception.RegionLookupException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeolocationAPIService {

    private final WebClient regionLookupWebClient;
    private final APIExecutionService apiExecutionService;

    public Mono<String> retrieveRegion(String ip) {
        log.info(String.format("Calling IP API to retrieve region for ip [%s]", ip));
        return retrieveRegionFromAPI(ip)
            .doOnError(throwable -> {
                if (throwable instanceof WebClientResponseException e) {
                    throw new RegionLookupException(String.format("Received error response [%s] from IP API service: %s", e.getStatusCode(), e.getResponseBodyAsString()));
                }
                throw new RegionLookupException("Failed to connect to IP API", throwable);
            });
    }

    Mono<String> retrieveRegionFromAPI(String ip) {
        return apiExecutionService.getOneObject(regionLookupWebClient, String.format("/json/%s", ip), IPDto.class)
            .map(IPDto::regionName);
    }

}
