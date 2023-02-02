package com.mintos.weather.dto.weatherapi;

import lombok.NonNull;

public record WeatherAPIResponseDto(
    @NonNull
    CurrentWeatherDto current
) {
}
