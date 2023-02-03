package com.mintos.weather.repository

import com.mintos.weather.entity.IPRegionEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface IPRegionRepository : CoroutineCrudRepository<IPRegionEntity, UUID> {

    suspend fun findByIp(ip: String): IPRegionEntity?
}