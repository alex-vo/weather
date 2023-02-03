package com.mintos.weather.service.externalapi

import com.mintos.weather.dto.ipapi.IPDto
import com.mintos.weather.exception.RegionLookupException
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class GeolocationAPIService(
    private val regionLookupWebClient: WebClient
) {

    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun retrieveRegion(ip: String): String {
        log.info("Calling IP API to retrieve region for ip [$ip]")
        try {
            val region = retrieveRegionFromAPI(ip)
            log.info("Retrieved region [$region] from IP API for ip [$ip]")
            return region
        } catch (e: WebClientRequestException) {
            throw RegionLookupException("Failed to connect to IP API", e)
        } catch (e: WebClientResponseException) {
            throw RegionLookupException("Received error response [${e.statusCode}] from IP API service: ${e.responseBodyAsString}")
        }
    }

    suspend fun retrieveRegionFromAPI(ip: String): String =
        regionLookupWebClient.get()
            .uri("/json/$ip")
            .retrieve()
            .bodyToMono<IPDto>()
            .awaitSingle()
            .regionName

}