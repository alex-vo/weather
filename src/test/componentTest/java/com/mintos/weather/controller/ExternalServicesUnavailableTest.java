package com.mintos.weather.controller;

import com.mintos.weather.dto.ErrorDto;
import com.mintos.weather.entity.IPRegionEntity;
import com.mintos.weather.repository.IPRegionRepository;
import com.mintos.weather.service.IPAddressResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;

class ExternalServicesUnavailableTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
            "spring.r2dbc.url=r2dbc:h2:mem:///~/db/weatherdb",
            "spring.r2dbc.username=sa",
            "spring.r2dbc.password=",
            "spring.liquibase.url=jdbc:h2:mem:~/db/weatherdb;DB_CLOSE_DELAY=-1",
            "spring.liquibase.user=sa",
            "spring.liquibase.password=",
            "weatherapi.base-uri=http://localhost:1081/weather",
            "ipapi.base-uri=http://localhost:1081/ip"
        ).applyTo(applicationContext.getEnvironment());
    }
}

@SpringBootTest
@ContextConfiguration(initializers = {ExternalServicesUnavailableTestInitializer.class})
@AutoConfigureWebTestClient
public class ExternalServicesUnavailableTest {

    private static final String IP = "0.0.0.0";

    @Autowired
    WebTestClient webTestClient;

    @SpyBean
    IPAddressResolver ipAddressResolver;
    @SpyBean
    IPRegionRepository ipRegionRepository;

    @BeforeEach
    public void setUp() {
        doReturn(IP).when(ipAddressResolver).getIp(any(ServerHttpRequest.class));
    }

    @Test
    public void shouldHandleIPAPINoConnection() {
        ErrorDto result = performFailingWeatherAPIGet();

        assertEquals(503, result.status());
        assertEquals("Failed to connect to IP API", result.message());
    }

    @Test
    public void shouldHandleWeatherAPINoConnection() {
        doReturn(Mono.just(new IPRegionEntity(UUID.randomUUID(), IP, "Riga", 0L)))
            .when(ipRegionRepository).findByIp(IP);
        ErrorDto result = performFailingWeatherAPIGet();

        assertEquals(503, result.status());
        assertEquals("Failed to connect to Weather API", result.message());
    }

    private ErrorDto performFailingWeatherAPIGet() {
        return webTestClient
            .get()
            .uri("/api/v1/weather")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(503)
            .expectBody(ErrorDto.class)
            .returnResult()
            .getResponseBody();
    }

}
