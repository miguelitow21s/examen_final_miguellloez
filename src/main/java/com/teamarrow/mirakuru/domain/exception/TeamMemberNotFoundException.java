package com.teamarrow.mirakuru.domain.exception;

/**
 * Raised when a mission references a team member that is not on the roster.
 * Mapped to HTTP 404.
 */
public class TeamMemberNotFoundException extends EntityNotFoundException {

    public TeamMemberNotFoundException(String reference) {
        super("No team member found for '" + reference + "'");
    }
}
