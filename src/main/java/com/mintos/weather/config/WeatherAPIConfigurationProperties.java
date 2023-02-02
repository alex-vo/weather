package com.mintos.weather.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weatherapi")
public record WeatherAPIConfigurationProperties(
    String key,
    String baseUri
) {
}
