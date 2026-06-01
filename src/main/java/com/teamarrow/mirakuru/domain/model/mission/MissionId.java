package com.teamarrow.mirakuru.domain.model.mission;

import com.teamarrow.mirakuru.domain.exception.DomainException;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object identifying a {@link Mission}.
 */
public record MissionId(UUID value) {

    public MissionId {
        Objects.requireNonNull(value, "MissionId value must not be null");
    }

    public static MissionId generate() {
        return new MissionId(UUID.randomUUID());
    }

    public static MissionId of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("Mission id must not be blank");
        }
        try {
            return new MissionId(UUID.fromString(raw.trim()));
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Invalid mission id: " + raw);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
