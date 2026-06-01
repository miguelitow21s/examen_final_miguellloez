package com.teamarrow.mirakuru.domain.model;

import com.teamarrow.mirakuru.domain.exception.DomainException;

/**
 * Value Object holding the alias the team uses to track an affected individual
 * (e.g. "Deathstroke", "Mirakuru Soldier #14").
 *
 * <p>The invariants (non-blank, bounded length) are enforced in the constructor,
 * so a {@code CodeName} instance is always valid by construction. Validation
 * lives in the domain, never in the controller, which keeps the rules in a
 * single authoritative place.</p>
 */
public record CodeName(String value) {

    private static final int MAX_LENGTH = 80;

    public CodeName {
        if (value == null || value.isBlank()) {
            throw new DomainException("Code name must not be blank");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new DomainException("Code name must not exceed " + MAX_LENGTH + " characters");
        }
    }
}
