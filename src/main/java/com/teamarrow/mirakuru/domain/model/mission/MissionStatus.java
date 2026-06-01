package com.teamarrow.mirakuru.domain.model.mission;

/**
 * Lifecycle of a mission.
 *
 * <ul>
 *   <li>{@code PLANNING} – being staffed; operatives can still be assigned.</li>
 *   <li>{@code IN_PROGRESS} – launched and under way.</li>
 *   <li>{@code COMPLETED} – finished successfully (terminal).</li>
 *   <li>{@code ABORTED} – called off (terminal).</li>
 * </ul>
 */
public enum MissionStatus {

    PLANNING,
    IN_PROGRESS,
    COMPLETED,
    ABORTED;

    public boolean isTerminal() {
        return this == COMPLETED || this == ABORTED;
    }
}
