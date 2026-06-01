package com.teamarrow.mirakuru.domain.exception;

import com.teamarrow.mirakuru.domain.model.mission.MissionId;

/**
 * Raised when an operation references a mission that does not exist.
 * Mapped to HTTP 404.
 */
public class MissionNotFoundException extends EntityNotFoundException {

    public MissionNotFoundException(MissionId id) {
        super("No mission found with id " + id);
    }
}
