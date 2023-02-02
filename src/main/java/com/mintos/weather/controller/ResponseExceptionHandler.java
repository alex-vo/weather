package com.mintos.weather.controller;

import com.mintos.weather.dto.ErrorDto;
import com.mintos.weather.exception.RegionLookupException;
import com.mintos.weather.exception.WeatherLookupException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ResponseExceptionHandler {

    @ExceptionHandler(RegionLookupException.class)
    ResponseEntity<ErrorDto> handleRegionLookupException(RegionLookupException e) {
        log.error("Error during region lookup", e);
        return new ResponseEntity<>(
            new ErrorDto(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE.value()),
            HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    @ExceptionHandler(WeatherLookupException.class)
    ResponseEntity<ErrorDto> handleWeatherLookupException(WeatherLookupException e) {
        log.error("Error during weather lookup", e);
        return new ResponseEntity<>(
            new ErrorDto(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE.value()),
            HttpStatus.SERVICE_UNAVAILABLE
        );
    }

}
