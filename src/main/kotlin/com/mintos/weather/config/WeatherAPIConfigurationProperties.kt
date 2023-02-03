package com.mintos.weather.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "weatherapi")
data class WeatherAPIConfigurationProperties(
    val key: String,
    val baseUri: String,
)
