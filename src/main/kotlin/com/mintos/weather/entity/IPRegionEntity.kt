package com.mintos.weather.entity

import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("IP_REGION")
data class IPRegionEntity(
    val id: UUID,
    val ip: String,
    val region: String,
    val lookedUpAtEpoch: Long,
)