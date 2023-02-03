package com.mintos.weather.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "ipapi")
data class IPAPIConfigurationProperties(
    val baseUri: String
)
