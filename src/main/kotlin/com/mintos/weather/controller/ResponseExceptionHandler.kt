package com.mintos.weather.controller

import com.mintos.weather.exception.RegionLookupException
import com.mintos.weather.exception.WeatherLookupException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ResponseExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(RegionLookupException::class)
    fun handleRegionLookupException(e: RegionLookupException): ResponseEntity<Map<String, Any>> {
        log.error("Error during region lookup", e)
        return ResponseEntity(
            mapOf(
                "message" to (e.message ?: ""),
                "status" to HttpStatus.SERVICE_UNAVAILABLE.value()
            ),
            HttpStatus.SERVICE_UNAVAILABLE
        )
    }

    @ExceptionHandler(WeatherLookupException::class)
    fun handleWeatherLookupException(e: WeatherLookupException): ResponseEntity<Map<String, Any>> {
        log.error("Error during weather lookup", e)
        return ResponseEntity(
            mapOf(
                "message" to (e.message ?: ""),
                "status" to HttpStatus.SERVICE_UNAVAILABLE.value()
            ),
            HttpStatus.SERVICE_UNAVAILABLE
        )
    }

}