package com.mintos.weather.service.externalapi;

import com.mintos.weather.config.WeatherAPIConfigurationProperties;
import com.mintos.weather.dto.weatherapi.WeatherAPIResponseDto;
import com.mintos.weather.exception.WeatherLookupException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static com.mintos.weather.util.TestUtils.prepareWeatherAPIResponseDto;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeatherAPIServiceTest {

    @Mock
    WebClient weatherAPIWebClient = mock(WebClient.class);
    @Mock
    APIExecutionService apiExecutionService = mock(APIExecutionService.class);
    String apiKey = "123";
    String baseUri = "http://api.weatherapi.com";

    WeatherAPIService weatherAPIService;

    @BeforeEach
    public void setUp() {
        weatherAPIService = spy(
            new WeatherAPIService(
                weatherAPIWebClient,
                new WeatherAPIConfigurationProperties(apiKey, baseUri),
                apiExecutionService
            )
        );
    }

    @Test
    public void shouldRetrieveWeather() {
        WeatherAPIResponseDto weatherAPIResponseDto = prepareWeatherAPIResponseDto("Overcast", BigDecimal.ZERO, BigDecimal.ONE, "S", 0L);
        doReturn(Mono.just(weatherAPIResponseDto)).when(weatherAPIService).retrieveWeatherFromAPI("Riga");

        Mono<WeatherAPIResponseDto> result = weatherAPIService.retrieveWeather("Riga");

        StepVerifier.create(result)
            .expectNext(weatherAPIResponseDto)
            .verifyComplete();
    }

    @Test
    public void shouldThrowWeatherLookupException_WebClientResponseExceptionDuringAPIInvocation() {
        doReturn(Mono.fromCallable(() -> {
            throw new WebClientResponseException(400, "", null, "abc".getBytes(), null);
        })).when(weatherAPIService).retrieveWeatherFromAPI("Riga");

        Mono<WeatherAPIResponseDto> result = weatherAPIService.retrieveWeather("Riga");

        StepVerifier.create(result)
            .expectErrorMatches(throwable -> throwable instanceof WeatherLookupException &&
                "Received error response [400 BAD_REQUEST] from Weather API service: abc".equals(throwable.getMessage())
            )
            .verify();
    }

    @Test
    public void shouldThrowWeatherLookupException_GenericExceptionDuringAPIInvocation() {
        RuntimeException someGenericException = new RuntimeException("cannot connect to API");
        doReturn(Mono.fromCallable(() -> {
            throw someGenericException;
        })).when(weatherAPIService).retrieveWeatherFromAPI("Riga");

        Mono<WeatherAPIResponseDto> result = weatherAPIService.retrieveWeather("Riga");

        StepVerifier.create(result)
            .expectErrorMatches(throwable -> throwable.getCause().equals(someGenericException) &&
                "Failed to connect to Weather API".equals(throwable.getMessage())
            )
            .verify();
    }

    @Test
    public void shouldRetrieveWeatherFromAPI() {
        WeatherAPIResponseDto weatherAPIResponseDto = prepareWeatherAPIResponseDto("Overcast", BigDecimal.ZERO, BigDecimal.ONE, "S", 0L);
        when(apiExecutionService.getOneObject(weatherAPIWebClient, "/v1/current.json?key=123&q=Riga", WeatherAPIResponseDto.class))
            .thenReturn(Mono.just(weatherAPIResponseDto));

        Mono<WeatherAPIResponseDto> result = weatherAPIService.retrieveWeatherFromAPI("Riga");

        StepVerifier.create(result)
            .expectNext(weatherAPIResponseDto)
            .verifyComplete();
    }

}
