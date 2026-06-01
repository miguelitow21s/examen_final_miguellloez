package com.teamarrow.mirakuru.domain.model.team;

import com.teamarrow.mirakuru.domain.exception.DomainException;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object identifying a member of Team Arrow.
 */
public record TeamMemberId(UUID value) {

    public TeamMemberId {
        Objects.requireNonNull(value, "TeamMemberId value must not be null");
    }

    public static TeamMemberId generate() {
        return new TeamMemberId(UUID.randomUUID());
    }

    public static TeamMemberId of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("Team member id must not be blank");
        }
        try {
            return new TeamMemberId(UUID.fromString(raw.trim()));
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Invalid team member id: " + raw);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
