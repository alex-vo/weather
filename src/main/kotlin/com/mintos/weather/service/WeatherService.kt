package com.mintos.weather.service

import com.mintos.weather.dto.WeatherDto
import com.mintos.weather.entity.IPRegionEntity
import com.mintos.weather.entity.WeatherEntity
import com.mintos.weather.mapper.toDto
import com.mintos.weather.mapper.toEntity
import com.mintos.weather.repository.WeatherRepository
import com.mintos.weather.service.externalapi.CacheableGeolocationAPIService
import com.mintos.weather.service.externalapi.WeatherAPIService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class WeatherService(
    private val weatherRepository: WeatherRepository,
    private val cacheableGeolocationAPIService: CacheableGeolocationAPIService,
    private val weatherAPIService: WeatherAPIService,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun getWeather(ip: String): WeatherDto {
        log.info("Started processing weather lookup request for [$ip]")
        val ipRegion = cacheableGeolocationAPIService.getRegionByIP(ip)
        val weatherDto = processWeatherForRegion(ipRegion)
            .toDto()
        log.info("Finished processing weather lookup request for [$ip]")
        return weatherDto
    }

    suspend fun processWeatherForRegion(ipRegion: IPRegionEntity): WeatherEntity {
        val weatherEntity = weatherAPIService.retrieveWeather(ipRegion.region)
            .toEntity(ipRegion.id)

        return weatherRepository.save(weatherEntity)
    }

}