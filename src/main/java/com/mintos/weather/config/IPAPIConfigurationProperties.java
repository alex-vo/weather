package com.mintos.weather.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ipapi")
public record IPAPIConfigurationProperties(
    String baseUri
) {
}

