package com.teamarrow.mirakuru.domain.model.team;

/**
 * The role a team member plays during an operation.
 *
 * <ul>
 *   <li>{@code FIELD} – front-line operatives who can be deployed against a target.</li>
 *   <li>{@code TECH} – overwatch / intelligence support (e.g. Felicity).</li>
 *   <li>{@code SUPPORT} – logistics and backup.</li>
 *   <li>{@code COMMAND} – strategic command of the operation.</li>
 * </ul>
 */
public enum OperativeRole {

    FIELD,
    TECH,
    SUPPORT,
    COMMAND;

    /** Only field operatives count towards the muscle a mission needs on the ground. */
    public boolean isDeployable() {
        return this == FIELD;
    }
}
