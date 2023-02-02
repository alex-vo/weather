package com.mintos.weather.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties({WeatherAPIConfigurationProperties.class, IPAPIConfigurationProperties.class})
public class ExternalAPIClientConfig {

    @Bean
    WebClient regionLookupWebClient(
        WebClient.Builder webClientBuilder,
        IPAPIConfigurationProperties ipAPIConfigurationProperties
    ) {
        return webClientBuilder
            .clientConnector(
                new ReactorClientHttpConnector(
                    HttpClient.create()
                        .responseTimeout(Duration.ofSeconds(1))
                )
            )
            .baseUrl(ipAPIConfigurationProperties.baseUri())
            .build();
    }

    @Bean
    WebClient weatherAPIWebClient(
        WebClient.Builder webClientBuilder,
        WeatherAPIConfigurationProperties weatherAPIConfigurationProperties
    ) {
        return webClientBuilder
            .clientConnector(
                new ReactorClientHttpConnector(
                    HttpClient.create()
                        .responseTimeout(Duration.ofSeconds(1))
                )
            )
            .baseUrl(weatherAPIConfigurationProperties.baseUri())
            .build();
    }

}
