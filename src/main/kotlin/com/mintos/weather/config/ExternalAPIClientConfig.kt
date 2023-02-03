package com.mintos.weather.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration


@Configuration
@EnableConfigurationProperties(WeatherAPIConfigurationProperties::class, IPAPIConfigurationProperties::class)
class ExternalAPIClientConfig {

    @Bean
    fun regionLookupWebClient(
        webClientBuilder: WebClient.Builder,
        ipAPIConfigurationProperties: IPAPIConfigurationProperties
    ): WebClient {
        return webClientBuilder
            .clientConnector(
                ReactorClientHttpConnector(
                    HttpClient.create()
                        .responseTimeout(Duration.ofSeconds(1))
                )
            )
            .baseUrl(ipAPIConfigurationProperties.baseUri)
            .build()
    }

    @Bean
    fun weatherAPIWebClient(
        webClientBuilder: WebClient.Builder,
        weatherAPIConfigurationProperties: WeatherAPIConfigurationProperties
    ): WebClient {
        return webClientBuilder
            .clientConnector(
                ReactorClientHttpConnector(
                    HttpClient.create()
                        .responseTimeout(Duration.ofSeconds(1))
                )
            )
            .baseUrl(weatherAPIConfigurationProperties.baseUri)
            .build()
    }

}