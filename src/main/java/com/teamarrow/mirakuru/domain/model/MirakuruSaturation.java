package com.teamarrow.mirakuru.domain.model;

import com.teamarrow.mirakuru.domain.exception.DomainException;

/**
 * Value Object representing how saturated an individual's bloodstream is with
 * the Mirakuru serum, expressed as a percentage between 0 and 100.
 *
 * <p>Saturation is the primary clinical signal the team uses to estimate how
 * far the serum has progressed. A value above {@link #LETHAL_THRESHOLD} marks a
 * subject whose body may not survive the transformation.</p>
 */
public record MirakuruSaturation(int percentage) {

    private static final int MIN = 0;
    private static final int MAX = 100;
    private static final int LETHAL_THRESHOLD = 90;

    public MirakuruSaturation {
        if (percentage < MIN || percentage > MAX) {
            throw new DomainException(
                    "Mirakuru saturation must be between " + MIN + " and " + MAX + ", got " + percentage);
        }
    }

    /** True when saturation has reached a level that is medically critical. */
    public boolean isLethal() {
        return percentage >= LETHAL_THRESHOLD;
    }
}
