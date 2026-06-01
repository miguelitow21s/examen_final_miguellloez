package com.teamarrow.mirakuru.domain.service;

import com.teamarrow.mirakuru.domain.model.ThreatLevel;

/**
 * Domain Service that holds the rules for coordinating missions between team
 * members.
 *
 * <p>Deciding how many operatives a target warrants is a policy that spans two
 * concepts (a target's {@link ThreatLevel} and the team's staffing), so it lives
 * in a Domain Service rather than inside the {@code Mission} aggregate. As with
 * the threat assessment, exposing it as an interface lets the staffing policy be
 * tuned without touching the aggregate (Open/Closed, Dependency Inversion).</p>
 */
public interface MissionCoordinationService {

    /** How many operatives must be assigned before a mission against this threat can launch. */
    int requiredOperativesFor(ThreatLevel threatLevel);
}
