package com.teamarrow.mirakuru.domain.model;

import com.teamarrow.mirakuru.domain.exception.DomainException;

/**
 * Value Object describing where an individual was last spotted: a human-readable
 * sector name plus geographic coordinates.
 *
 * <p>Grouping the three fields into one immutable concept keeps related data
 * together and guarantees the coordinates are always within valid geographic
 * ranges.</p>
 */
public record Location(String sector, double latitude, double longitude) {

    public Location {
        if (sector == null || sector.isBlank()) {
            throw new DomainException("Location sector must not be blank");
        }
        sector = sector.trim();
        if (latitude < -90 || latitude > 90) {
            throw new DomainException("Latitude must be between -90 and 90, got " + latitude);
        }
        if (longitude < -180 || longitude > 180) {
            throw new DomainException("Longitude must be between -180 and 180, got " + longitude);
        }
    }

    /** Sentinel location used when the team has no fix on the subject. */
    public static Location unknown() {
        return new Location("UNKNOWN", 0, 0);
    }
}
