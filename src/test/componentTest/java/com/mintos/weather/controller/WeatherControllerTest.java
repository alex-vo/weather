package com.mintos.weather.controller;

import com.mintos.weather.BaseComponentTest;
import com.mintos.weather.dto.ErrorDto;
import com.mintos.weather.dto.WeatherDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WeatherControllerTest extends BaseComponentTest {

    @Test
    public void shouldGetWeatherForIp() throws IOException {
        setUpIPAPIResponse("1.1.1.1", 200, "{\"regionName\": \"Riga\"}");
        setUpWeatherAPIResponse(
            "Riga",
            200,
            Files.readString(Path.of("src/test/componentTest/resources/mock_responses/riga_weather.json"))
        );

        WeatherDto result = performSuccessfulGet("/api/v1/weather", WeatherDto.class);

        assertNotNull(result.id());
        assertEquals("Light sleet showers", result.condition());
        assertEquals(BigDecimal.valueOf(0.0), result.temperatureCelsius());
        assertEquals(BigDecimal.valueOf(4.3), result.windMpH());
        assertEquals("NW", result.windDirection());
        assertEquals(1675277100, result.lastUpdatedEpoch());
    }

    @Test
    public void shouldHandleIPAPIClientError() {
        setUpIPAPIResponse("1.1.1.1", 400, "bad request");

        ErrorDto result = performGet("/api/v1/weather", 503, ErrorDto.class);

        assertEquals(503, result.status());
        assertEquals("Received error response [400 BAD_REQUEST] from IP API service: bad request", result.message());
    }

    @Test
    public void shouldHandleIPAPIServerError() {
        setUpIPAPIResponse("1.1.1.1", 500, "internal server error");

        ErrorDto result = performGet("/api/v1/weather", 503, ErrorDto.class);

        assertEquals(503, result.status());
        assertEquals("Received error response [500 INTERNAL_SERVER_ERROR] from IP API service: internal server error", result.message());
    }

    @Test
    public void shouldHandleWeatherAPIClientError() {
        setUpIPAPIResponse("1.1.1.1", 200, "{\"regionName\": \"Riga\"}");
        setUpWeatherAPIResponse("Riga", 400, "bad request");

        ErrorDto result = performGet("/api/v1/weather", 503, ErrorDto.class);

        assertEquals(503, result.status());
        assertEquals("Received error response [400 BAD_REQUEST] from Weather API service: bad request", result.message());
    }

    @Test
    public void shouldHandleWeatherAPIServerError() {
        setUpIPAPIResponse("1.1.1.1", 200, "{\"regionName\": \"Riga\"}");
        setUpWeatherAPIResponse("Riga", 501, "not implemented");

        ErrorDto result = performGet("/api/v1/weather", 503, ErrorDto.class);

        assertEquals(503, result.status());
        assertEquals("Received error response [501 NOT_IMPLEMENTED] from Weather API service: not implemented", result.message());
    }

}
