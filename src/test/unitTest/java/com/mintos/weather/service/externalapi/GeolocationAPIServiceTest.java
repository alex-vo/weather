package com.mintos.weather.service.externalapi;

import com.mintos.weather.dto.ipapi.IPDto;
import com.mintos.weather.exception.RegionLookupException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GeolocationAPIServiceTest {

    @Mock
    WebClient regionLookupWebClient;
    @Mock
    APIExecutionService apiExecutionService;

    @Spy
    @InjectMocks
    GeolocationAPIService geolocationAPIService;

    private static final String IP = "1.1.1.1";

    @Test
    public void shouldRetrieveRegion() {
        doReturn(Mono.just("Riga")).when(geolocationAPIService).retrieveRegionFromAPI(IP);

        Mono<String> result = geolocationAPIService.retrieveRegionFromAPI(IP);

        StepVerifier.create(result)
            .expectNext("Riga")
            .verifyComplete();
    }

    @Test
    public void shouldThrowRegionLookupException_WebClientResponseExceptionDuringAPIInvocation() {
        doReturn(Mono.fromCallable(() -> {
            throw new WebClientResponseException(403, "", null, "abc".getBytes(), null);
        })).when(geolocationAPIService).retrieveRegionFromAPI(IP);

        Mono<String> result = geolocationAPIService.retrieveRegion(IP);

        StepVerifier.create(result)
            .expectErrorMatches(throwable -> throwable instanceof RegionLookupException &&
                "Received error response [403 FORBIDDEN] from IP API service: abc".equals(throwable.getMessage())
            )
            .verify();
    }

    @Test
    public void shouldThrowRegionLookupException_GenericExceptionDuringAPIInvocation() {
        RuntimeException genericException = new RuntimeException("cannot connect to IP API");
        doReturn(Mono.fromCallable(() -> {
            throw genericException;
        })).when(geolocationAPIService).retrieveRegionFromAPI(IP);

        Mono<String> result = geolocationAPIService.retrieveRegion(IP);

        StepVerifier.create(result)
            .expectErrorMatches(throwable -> throwable instanceof RegionLookupException &&
                genericException == throwable.getCause() &&
                "Failed to connect to IP API".equals(throwable.getMessage())
            )
            .verify();
    }

    @Test
    public void shouldRetrieveRegionFromAPI() {
        IPDto ipDto = new IPDto("Riga");
        when(apiExecutionService.getOneObject(regionLookupWebClient, "/json/" + IP, IPDto.class)).thenReturn(Mono.just(ipDto));

        Mono<String> result = geolocationAPIService.retrieveRegionFromAPI(IP);

        StepVerifier.create(result)
            .expectNext("Riga")
            .verifyComplete();
    }

}
