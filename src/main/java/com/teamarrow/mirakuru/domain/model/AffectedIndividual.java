package com.teamarrow.mirakuru.domain.model;

import com.teamarrow.mirakuru.domain.exception.DomainException;
import com.teamarrow.mirakuru.domain.service.ThreatAssessmentService;
import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate Root of the bounded context: an individual affected by the Mirakuru
 * serum that the team must identify, track and prioritise.
 *
 * <p>The aggregate owns its invariants. State can only change through the
 * intention-revealing methods {@link #register} and {@link #updateProfile},
 * never through open setters, so the object can never be left in an inconsistent
 * state. The derived {@link ThreatLevel} is recomputed inside the aggregate via
 * a double-dispatch call to the {@link ThreatAssessmentService} domain service,
 * which keeps the classification rule in one place while the aggregate stays in
 * control of its own data.</p>
 *
 * <p>Identity-based equality (by {@link AffectedId}) reflects that this is an
 * Entity, not a Value Object: two individuals with the same attributes are still
 * two different people.</p>
 */
public class AffectedIndividual {

    private final AffectedId id;
    private CodeName codeName;
    private MirakuruSaturation saturation;
    private AggressionIndex aggressionIndex;
    private Location lastKnownLocation;
    private AffectedStatus status;
    private ThreatLevel threatLevel;
    private final Instant registeredAt;
    private Instant lastUpdatedAt;

    private AffectedIndividual(AffectedId id, CodeName codeName, MirakuruSaturation saturation,
                              AggressionIndex aggressionIndex, Location lastKnownLocation,
                              AffectedStatus status, ThreatLevel threatLevel,
                              Instant registeredAt, Instant lastUpdatedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.codeName = Objects.requireNonNull(codeName, "codeName must not be null");
        this.saturation = Objects.requireNonNull(saturation, "saturation must not be null");
        this.aggressionIndex = Objects.requireNonNull(aggressionIndex, "aggressionIndex must not be null");
        this.lastKnownLocation = Objects.requireNonNull(lastKnownLocation, "location must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.threatLevel = Objects.requireNonNull(threatLevel, "threatLevel must not be null");
        this.registeredAt = Objects.requireNonNull(registeredAt, "registeredAt must not be null");
        this.lastUpdatedAt = Objects.requireNonNull(lastUpdatedAt, "lastUpdatedAt must not be null");
    }

    /**
     * Factory that registers a newly discovered individual. A fresh subject is
     * always considered {@code AT_LARGE} until the team proves otherwise, and the
     * threat level is assessed immediately on creation.
     */
    public static AffectedIndividual register(AffectedId id, CodeName codeName,
                                              MirakuruSaturation saturation, AggressionIndex aggressionIndex,
                                              Location location, ThreatAssessmentService assessment) {
        Objects.requireNonNull(assessment, "assessment service must not be null");
        Instant now = Instant.now();
        ThreatLevel level = assessment.classify(saturation, aggressionIndex);
        return new AffectedIndividual(id, codeName, saturation, aggressionIndex, location,
                AffectedStatus.AT_LARGE, level, now, now);
    }

    /**
     * Rebuilds an aggregate from already-persisted state. Used exclusively by the
     * persistence adapter; it trusts the stored values and does not re-run the
     * registration rules.
     */
    public static AffectedIndividual rehydrate(AffectedId id, CodeName codeName,
                                              MirakuruSaturation saturation, AggressionIndex aggressionIndex,
                                              Location location, AffectedStatus status, ThreatLevel threatLevel,
                                              Instant registeredAt, Instant lastUpdatedAt) {
        return new AffectedIndividual(id, codeName, saturation, aggressionIndex, location,
                status, threatLevel, registeredAt, lastUpdatedAt);
    }

    /**
     * Updates the mutable part of the profile and re-assesses the threat level.
     *
     * @throws DomainException if the individual is in a terminal state (cured),
     *                         which must never be silently overwritten.
     */
    public void updateProfile(CodeName codeName, MirakuruSaturation saturation,
                              AggressionIndex aggressionIndex, Location location,
                              AffectedStatus status, ThreatAssessmentService assessment) {
        Objects.requireNonNull(assessment, "assessment service must not be null");
        if (this.status.isTerminal()) {
            throw new DomainException(
                    "A cured individual can no longer be modified: " + id);
        }
        this.codeName = Objects.requireNonNull(codeName, "codeName must not be null");
        this.saturation = Objects.requireNonNull(saturation, "saturation must not be null");
        this.aggressionIndex = Objects.requireNonNull(aggressionIndex, "aggressionIndex must not be null");
        this.lastKnownLocation = Objects.requireNonNull(location, "location must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.threatLevel = assessment.classify(saturation, aggressionIndex);
        this.lastUpdatedAt = Instant.now();
    }

    public AffectedId getId() {
        return id;
    }

    public CodeName getCodeName() {
        return codeName;
    }

    public MirakuruSaturation getSaturation() {
        return saturation;
    }

    public AggressionIndex getAggressionIndex() {
        return aggressionIndex;
    }

    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    public AffectedStatus getStatus() {
        return status;
    }

    public ThreatLevel getThreatLevel() {
        return threatLevel;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public Instant getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AffectedIndividual that)) {
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
        return "AffectedIndividual{id=" + id + ", codeName=" + codeName.value()
                + ", threatLevel=" + threatLevel + ", status=" + status + '}';
    }
}
