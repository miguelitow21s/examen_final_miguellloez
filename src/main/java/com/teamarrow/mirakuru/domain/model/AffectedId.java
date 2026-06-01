package com.teamarrow.mirakuru.domain.model;

import com.teamarrow.mirakuru.domain.exception.DomainException;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object that uniquely identifies an {@link AffectedIndividual}.
 *
 * <p>Wrapping the raw {@link UUID} in a dedicated type prevents primitive
 * obsession: the rest of the system talks about an {@code AffectedId} instead
 * of an anonymous string, so it is impossible to accidentally pass, say, a
 * mission id where an affected id is expected.</p>
 */
public record AffectedId(UUID value) {

    public AffectedId {
        Objects.requireNonNull(value, "AffectedId value must not be null");
    }

    /** Generates a brand new identity for a newly registered individual. */
    public static AffectedId generate() {
        return new AffectedId(UUID.randomUUID());
    }

    /** Rebuilds an identity from its textual representation, validating the format. */
    public static AffectedId of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("Affected id must not be blank");
        }
        try {
            return new AffectedId(UUID.fromString(raw.trim()));
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Invalid affected id: " + raw);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
