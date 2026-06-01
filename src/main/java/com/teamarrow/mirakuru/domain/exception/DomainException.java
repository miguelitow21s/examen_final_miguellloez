package com.teamarrow.mirakuru.domain.exception;

/**
 * Base type for every business-rule violation raised inside the domain.
 *
 * <p>Keeping a single root exception lets the outer layers translate domain
 * failures into transport-specific responses (HTTP status codes, for example)
 * without the domain knowing anything about those technologies. This is a key
 * enabler of the Dependency Inversion Principle: the domain throws meaningful
 * exceptions and the infrastructure decides how to present them.</p>
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}
