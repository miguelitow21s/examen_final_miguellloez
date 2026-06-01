package com.teamarrow.mirakuru.domain.model.intel;

import com.teamarrow.mirakuru.domain.exception.DomainException;
import java.time.Instant;
import java.util.Objects;

/**
 * Value Object holding a single piece of intelligence about a subject, as
 * delivered by an external source (A.R.G.U.S., A.C.G., field informants, ...).
 *
 * <p>It is an immutable snapshot owned by the domain. The external source
 * provides the data, but the shape the rest of the system reasons about is this
 * domain type, not whatever wire format each provider happens to use.</p>
 */
public record IntelReport(String sourceName, String headline, int confidence, Instant reportedAt) {

    public IntelReport {
        if (sourceName == null || sourceName.isBlank()) {
            throw new DomainException("Intel source name must not be blank");
        }
        if (headline == null || headline.isBlank()) {
            throw new DomainException("Intel headline must not be blank");
        }
        if (confidence < 0 || confidence > 100) {
            throw new DomainException("Intel confidence must be between 0 and 100, got " + confidence);
        }
        sourceName = sourceName.trim();
        headline = headline.trim();
        Objects.requireNonNull(reportedAt, "reportedAt must not be null");
    }
}
