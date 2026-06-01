package com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto;

import java.time.Instant;
import java.util.List;

/** Response body describing a mission. */
public record MissionResponse(
        String id,
        String name,
        String targetAffectedId,
        int requiredOperatives,
        int assignedOperatives,
        boolean readyToLaunch,
        String status,
        List<String> operativeIds,
        Instant createdAt) {
}
