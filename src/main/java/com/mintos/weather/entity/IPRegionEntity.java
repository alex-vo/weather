package com.mintos.weather.entity;

import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("IP_REGION")
public record IPRegionEntity(
    UUID id,
    String ip,
    String region,
    Long lookedUpAtEpoch
) {
}
