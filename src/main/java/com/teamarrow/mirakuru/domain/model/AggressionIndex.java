package com.teamarrow.mirakuru.domain.model;

import com.teamarrow.mirakuru.domain.exception.DomainException;

/**
 * Value Object representing the observed aggression of an affected individual on
 * a 0 (docile) to 10 (uncontrollable rampage) scale.
 *
 * <p>Where {@link MirakuruSaturation} measures the biological cause, the
 * aggression index measures the behavioural effect the field team can actually
 * observe. Both feed the threat assessment.</p>
 */
public record AggressionIndex(int value) {

    private static final int MIN = 0;
    private static final int MAX = 10;

    public AggressionIndex {
        if (value < MIN || value > MAX) {
            throw new DomainException(
                    "Aggression index must be between " + MIN + " and " + MAX + ", got " + value);
        }
    }
}
