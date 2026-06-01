package com.teamarrow.mirakuru.domain.model;

import com.teamarrow.mirakuru.domain.exception.DomainException;
import java.util.Arrays;
import java.util.Locale;

/**
 * Lifecycle state of an affected individual as the team tracks and engages them.
 *
 * <ul>
 *   <li>{@code AT_LARGE} – active and uncontained, the highest operational concern.</li>
 *   <li>{@code MONITORED} – located and observed but not yet engaged.</li>
 *   <li>{@code NEUTRALIZED} – subdued or contained, no longer an active threat.</li>
 *   <li>{@code CURED} – the Mirakuru effect has been reversed; a terminal state.</li>
 * </ul>
 */
public enum AffectedStatus {

    AT_LARGE,
    MONITORED,
    NEUTRALIZED,
    CURED;

    /** Whether the individual still represents an active threat to the city. */
    public boolean isActiveThreat() {
        return this == AT_LARGE || this == MONITORED;
    }

    /** Whether this is a terminal state that locks further profile changes. */
    public boolean isTerminal() {
        return this == CURED;
    }

    /** Lenient, case-insensitive parser that fails with a domain-meaningful message. */
    public static AffectedStatus from(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("Status must not be blank");
        }
        try {
            return AffectedStatus.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new DomainException(
                    "Unknown status '" + raw + "'. Valid values: " + Arrays.toString(values()));
        }
    }
}
