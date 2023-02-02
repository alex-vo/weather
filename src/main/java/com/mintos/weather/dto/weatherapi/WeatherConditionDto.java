package com.mintos.weather.dto.weatherapi;

import lombok.NonNull;

public record WeatherConditionDto(
    @NonNull
    String text
) {
}
