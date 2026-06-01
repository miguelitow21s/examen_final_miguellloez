package com.teamarrow.mirakuru.domain.exception;

import com.teamarrow.mirakuru.domain.model.AffectedId;

/**
 * Raised when an operation references an affected individual that does not
 * exist. The web layer maps this to HTTP 404.
 */
public class AffectedNotFoundException extends DomainException {

    public AffectedNotFoundException(AffectedId id) {
        super("No affected individual found with id " + id);
    }
}
