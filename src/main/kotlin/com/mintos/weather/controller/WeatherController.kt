package com.mintos.weather.controller

import com.mintos.weather.dto.WeatherDto
import com.mintos.weather.service.WeatherService
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class WeatherController(
    private val weatherService: WeatherService,
) {

    @GetMapping("api/v1/weather")
    suspend fun weather(serverHttpRequest: ServerHttpRequest): WeatherDto {
//        val ip = serverHttpRequest.remoteAddress!!.address!!.hostAddress
        val ip = "212.3.197.132"
        return weatherService.getWeather(ip)
    }

}