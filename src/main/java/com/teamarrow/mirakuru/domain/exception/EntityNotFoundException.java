package com.teamarrow.mirakuru.domain.exception;

/**
 * Common supertype for "the aggregate you asked for does not exist" failures.
 *
 * <p>Centralising the concept lets the web layer map <em>any</em> missing
 * aggregate (affected individual, mission, team member) to HTTP 404 with a
 * single handler, instead of one handler per type. Adding a new aggregate
 * therefore needs no change in the error-handling adapter — an application of
 * the Open/Closed Principle.</p>
 */
public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
