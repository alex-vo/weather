package com.mintos.weather.repository;

import com.mintos.weather.entity.WeatherEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface WeatherRepository extends ReactiveCrudRepository<WeatherEntity, UUID> {
}
