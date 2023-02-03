package com.mintos.weather.service

import com.mintos.weather.entity.IPRegionEntity
import com.mintos.weather.repository.IPRegionRepository
import com.mintos.weather.service.externalapi.GeolocationAPIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.lang.System.currentTimeMillis
import java.util.UUID.randomUUID


@Service
class GeoLocationService(
    private val ipRegionRepository: IPRegionRepository,
    private val geolocationAPIService: GeolocationAPIService,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Cacheable(cacheNames = ["regionByIp"], key = "#ip")
    fun getRegionByIPAsync(ip: String): Deferred<IPRegionEntity> {
        return CoroutineScope(Dispatchers.Default).async {
            getRegionByIP(ip)
        }
    }

    suspend fun getRegionByIP(ip: String): IPRegionEntity {
        log.info("Looking up client region by ip [$ip]...")
        var ipRegion = ipRegionRepository.findByIp(ip)
        if (ipRegion == null) {
            log.info("No region present in the database by ip [$ip], executing API lookup...")
            ipRegion = processIPRegion(ip)
        }
        log.info("Found client region [${ipRegion.region}] by ip [$ip]")
        return ipRegion
    }

    suspend fun processIPRegion(ip: String): IPRegionEntity {
        val region = geolocationAPIService.retrieveRegion(ip)

        return ipRegionRepository.save(IPRegionEntity(randomUUID(), ip, region, currentTimeMillis()))
    }

}