package com.teamarrow.mirakuru.domain.exception;

import com.teamarrow.mirakuru.domain.model.CodeName;

/**
 * Raised when registering an individual whose code name is already taken. A code
 * name must unambiguously identify a single subject, so duplicates are rejected.
 * The web layer maps this to HTTP 409 (Conflict).
 */
public class DuplicateAffectedException extends DomainException {

    public DuplicateAffectedException(CodeName codeName) {
        super("An affected individual is already registered with code name '" + codeName.value() + "'");
    }
}
