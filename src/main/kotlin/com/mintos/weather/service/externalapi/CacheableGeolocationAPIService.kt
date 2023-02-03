package com.mintos.weather.service.externalapi

import com.mintos.weather.entity.IPRegionEntity
import com.mintos.weather.service.GeoLocationService
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service

@Service
class CacheableGeolocationAPIService(
    private val geoLocationService: GeoLocationService,
    private val cacheManager: CacheManager,
) {

    suspend fun getRegionByIP(ip: String): IPRegionEntity {
        try {
            return geoLocationService.getRegionByIPAsync(ip)
                .await()
        } catch (e: Exception) {
            cacheManager.getCache("regionByIp")
                ?.evict(ip)
            throw e
        }
    }
}