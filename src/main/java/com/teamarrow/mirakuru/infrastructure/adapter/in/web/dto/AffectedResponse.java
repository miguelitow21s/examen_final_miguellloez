package com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto;

import java.time.Instant;

/**
 * Response body returned by every affected-individual endpoint.
 *
 * <p>Keeping a dedicated response DTO (instead of serialising the domain
 * aggregate directly) decouples the public API contract from the internal model:
 * the domain can evolve freely without breaking external consumers, and we
 * choose exactly which fields — including the derived {@code threatLevel} and
 * {@code priorityScore} — to expose.</p>
 */
public record AffectedResponse(
        String id,
        String codeName,
        int mirakuruSaturation,
        int aggressionIndex,
        String status,
        String threatLevel,
        int priorityScore,
        LocationResponse location,
        Instant registeredAt,
        Instant lastUpdatedAt) {
}
