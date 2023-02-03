package com.mintos.weather.repository

import com.mintos.weather.entity.WeatherEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface WeatherRepository : CoroutineCrudRepository<WeatherEntity, UUID>