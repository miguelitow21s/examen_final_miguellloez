package com.teamarrow.mirakuru.application.port.in.command;

/**
 * Command to open a new mission against an existing affected individual.
 * The required number of operatives is not part of the input: it is derived by
 * the domain from the target's threat level.
 */
public record PlanMissionCommand(String name, String targetAffectedId) {
}
