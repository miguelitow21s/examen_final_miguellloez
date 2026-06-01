package com.teamarrow.mirakuru.domain.model;

/**
 * Classification of how dangerous an affected individual is, ordered from least
 * to most severe. The ordinal ordering is part of the contract and is used to
 * compare severities.
 */
public enum ThreatLevel {

    LOW,
    MODERATE,
    HIGH,
    CRITICAL;

    /** True when this level is the same or more severe than {@code other}. */
    public boolean isAtLeast(ThreatLevel other) {
        return this.ordinal() >= other.ordinal();
    }
}
