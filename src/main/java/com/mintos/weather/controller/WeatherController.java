package com.mintos.weather.controller;

import com.mintos.weather.dto.WeatherDto;
import com.mintos.weather.service.IPAddressResolver;
import com.mintos.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class WeatherController {

    private final IPAddressResolver ipAddressResolver;
    private final WeatherService weatherService;

    @GetMapping("api/v1/weather")
    public Mono<WeatherDto> weather(ServerHttpRequest serverHttpRequest) {
        String ip = ipAddressResolver.getIp(serverHttpRequest);
        return weatherService.getWeather(ip);
    }

}
