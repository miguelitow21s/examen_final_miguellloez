package com.teamarrow.mirakuru.domain.service;

import com.teamarrow.mirakuru.domain.model.AffectedIndividual;
import com.teamarrow.mirakuru.domain.model.AffectedStatus;
import com.teamarrow.mirakuru.domain.model.AggressionIndex;
import com.teamarrow.mirakuru.domain.model.MirakuruSaturation;
import com.teamarrow.mirakuru.domain.model.ThreatLevel;

/**
 * Default {@link ThreatAssessmentService} implementation that combines Mirakuru
 * saturation and aggression into a single weighted score.
 *
 * <p>Saturation (the biological driver) carries more weight than aggression (the
 * observable symptom). The same weighted base feeds the priority ranking, which
 * is then attenuated by the individual's status so that already-contained
 * subjects fall to the bottom of the engagement queue.</p>
 *
 * <p>This class is intentionally free of any framework annotation: it is pure
 * domain logic and is wired into the application as a bean from the
 * infrastructure configuration, keeping the core decoupled from Spring.</p>
 */
public class WeightedThreatAssessmentService implements ThreatAssessmentService {

    private static final double SATURATION_WEIGHT = 0.6;
    private static final double AGGRESSION_WEIGHT = 0.4;

    private static final double CRITICAL_THRESHOLD = 80;
    private static final double HIGH_THRESHOLD = 60;
    private static final double MODERATE_THRESHOLD = 35;

    @Override
    public ThreatLevel classify(MirakuruSaturation saturation, AggressionIndex aggressionIndex) {
        double score = weightedScore(saturation, aggressionIndex);
        if (score >= CRITICAL_THRESHOLD) {
            return ThreatLevel.CRITICAL;
        }
        if (score >= HIGH_THRESHOLD) {
            return ThreatLevel.HIGH;
        }
        if (score >= MODERATE_THRESHOLD) {
            return ThreatLevel.MODERATE;
        }
        return ThreatLevel.LOW;
    }

    @Override
    public int priorityScore(AffectedIndividual affected) {
        if (!affected.getStatus().isActiveThreat()) {
            return 0;
        }
        double base = weightedScore(affected.getSaturation(), affected.getAggressionIndex());
        double statusMultiplier = affected.getStatus() == AffectedStatus.AT_LARGE ? 1.0 : 0.5;
        return (int) Math.round(base * statusMultiplier);
    }

    /** Normalises aggression (0-10) onto the 0-100 scale and blends both signals. */
    private double weightedScore(MirakuruSaturation saturation, AggressionIndex aggressionIndex) {
        double aggressionNormalised = aggressionIndex.value() * 10.0;
        return saturation.percentage() * SATURATION_WEIGHT + aggressionNormalised * AGGRESSION_WEIGHT;
    }
}
