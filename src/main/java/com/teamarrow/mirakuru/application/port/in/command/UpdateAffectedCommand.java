package com.teamarrow.mirakuru.application.port.in.command;

/**
 * Immutable command carrying the raw input needed to update an existing affected
 * individual. Unlike registration, an update may also change the lifecycle
 * {@code status} (e.g. from AT_LARGE to NEUTRALIZED).
 */
public record UpdateAffectedCommand(
        String codeName,
        int mirakuruSaturation,
        int aggressionIndex,
        String locationSector,
        double latitude,
        double longitude,
        String status) {
}
