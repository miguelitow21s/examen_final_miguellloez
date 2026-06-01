package com.teamarrow.mirakuru.domain.model.mission;

import com.teamarrow.mirakuru.domain.exception.DomainException;
import com.teamarrow.mirakuru.domain.model.AffectedId;
import com.teamarrow.mirakuru.domain.model.team.TeamMemberId;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Aggregate Root that coordinates an operation against a Mirakuru target.
 *
 * <p>The aggregate guards the rules of mission staffing: operatives may only be
 * assigned while {@code PLANNING}, the same operative cannot be assigned twice,
 * and a mission cannot be launched until it has at least the number of operatives
 * that the target's threat level demands ({@code requiredOperatives}, computed by
 * the {@code MissionCoordinationService} domain service). The set of assignees is
 * exposed as an unmodifiable copy so callers can never bypass these rules.</p>
 */
public class Mission {

    private final MissionId id;
    private final String name;
    private final AffectedId target;
    private final int requiredOperatives;
    private final Set<TeamMemberId> assignedOperatives = new LinkedHashSet<>();
    private MissionStatus status;
    private final Instant createdAt;

    private Mission(MissionId id, String name, AffectedId target, int requiredOperatives,
                    MissionStatus status, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.target = Objects.requireNonNull(target, "target must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        if (name == null || name.isBlank()) {
            throw new DomainException("Mission name must not be blank");
        }
        if (requiredOperatives < 1) {
            throw new DomainException("A mission requires at least one operative");
        }
        this.name = name.trim();
        this.requiredOperatives = requiredOperatives;
    }

    /** Factory that opens a new mission in the PLANNING state. */
    public static Mission plan(MissionId id, String name, AffectedId target, int requiredOperatives) {
        return new Mission(id, name, target, requiredOperatives, MissionStatus.PLANNING, Instant.now());
    }

    /** Rebuilds a mission from persisted state. */
    public static Mission rehydrate(MissionId id, String name, AffectedId target, int requiredOperatives,
                                    Set<TeamMemberId> assignedOperatives, MissionStatus status, Instant createdAt) {
        Mission mission = new Mission(id, name, target, requiredOperatives, status, createdAt);
        if (assignedOperatives != null) {
            mission.assignedOperatives.addAll(assignedOperatives);
        }
        return mission;
    }

    /**
     * Assigns an operative to the mission.
     *
     * @throws DomainException if the mission has already left planning or the
     *                         operative is already assigned.
     */
    public void assign(TeamMemberId operative) {
        Objects.requireNonNull(operative, "operative must not be null");
        if (status != MissionStatus.PLANNING) {
            throw new DomainException("Operatives can only be assigned while the mission is in PLANNING");
        }
        if (assignedOperatives.contains(operative)) {
            throw new DomainException("Operative " + operative + " is already assigned to this mission");
        }
        assignedOperatives.add(operative);
    }

    /**
     * Launches the mission.
     *
     * @throws DomainException if it is not in planning or is understaffed for the
     *                         target's threat level.
     */
    public void launch() {
        if (status != MissionStatus.PLANNING) {
            throw new DomainException("Only a mission in PLANNING can be launched");
        }
        if (assignedOperatives.size() < requiredOperatives) {
            throw new DomainException("Mission needs " + requiredOperatives
                    + " operatives but only " + assignedOperatives.size() + " are assigned");
        }
        this.status = MissionStatus.IN_PROGRESS;
    }

    /** Calls off the mission. */
    public void abort() {
        if (status.isTerminal()) {
            throw new DomainException("A " + status + " mission can no longer be aborted");
        }
        this.status = MissionStatus.ABORTED;
    }

    /** True when the mission already has enough operatives to launch. */
    public boolean isReadyToLaunch() {
        return status == MissionStatus.PLANNING && assignedOperatives.size() >= requiredOperatives;
    }

    public MissionId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public AffectedId getTarget() {
        return target;
    }

    public int getRequiredOperatives() {
        return requiredOperatives;
    }

    public Set<TeamMemberId> getAssignedOperatives() {
        return Collections.unmodifiableSet(assignedOperatives);
    }

    public MissionStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Mission that)) {
            return false;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Mission{id=" + id + ", name=" + name + ", status=" + status
                + ", assigned=" + assignedOperatives.size() + "/" + requiredOperatives + '}';
    }
}
