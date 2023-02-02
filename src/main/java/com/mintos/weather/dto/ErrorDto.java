package com.mintos.weather.dto;

public record ErrorDto(
    String message,
    int status
) {
}
