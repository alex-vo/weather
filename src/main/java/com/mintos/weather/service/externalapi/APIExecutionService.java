package com.mintos.weather.service.externalapi;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class APIExecutionService {

    public <T> Mono<T> getOneObject(WebClient webClient, String uri, Class<T> clazz) {
        return webClient.get()
            .uri(uri)
            .retrieve()
            .bodyToMono(clazz);
    }

}
