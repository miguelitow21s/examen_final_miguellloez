package com.teamarrow.mirakuru.domain.service;

import com.teamarrow.mirakuru.domain.model.ThreatLevel;

/**
 * Default staffing policy: the more dangerous the target, the more operatives the
 * mission requires on the ground.
 *
 * <pre>
 *   LOW      -> 1 operative
 *   MODERATE -> 2 operatives
 *   HIGH     -> 3 operatives
 *   CRITICAL -> 4 operatives
 * </pre>
 *
 * <p>Framework-free pure domain logic, wired as a bean from the infrastructure.</p>
 */
public class StandardMissionCoordinationService implements MissionCoordinationService {

    @Override
    public int requiredOperativesFor(ThreatLevel threatLevel) {
        return switch (threatLevel) {
            case LOW -> 1;
            case MODERATE -> 2;
            case HIGH -> 3;
            case CRITICAL -> 4;
        };
    }
}
