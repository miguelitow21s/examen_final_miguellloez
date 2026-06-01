package com.teamarrow.mirakuru.domain.service;

import com.teamarrow.mirakuru.domain.model.AffectedIndividual;
import com.teamarrow.mirakuru.domain.model.AggressionIndex;
import com.teamarrow.mirakuru.domain.model.MirakuruSaturation;
import com.teamarrow.mirakuru.domain.model.ThreatLevel;

/**
 * Domain Service that encapsulates the business rules for classifying and
 * prioritising threats.
 *
 * <p>This logic does not belong naturally to a single Value Object: classifying
 * a threat combines saturation and aggression, and prioritising a target also
 * weighs the individual's lifecycle status. Per DDD, behaviour that spans
 * several concepts and is stateless lives in a Domain Service rather than being
 * forced into an entity or value object.</p>
 *
 * <p>It is declared as an interface (not a concrete class) so the assessment
 * strategy can evolve — Felicity may tune the rules as the crisis develops —
 * without touching the aggregate or the use cases. This honours the
 * Open/Closed and Dependency Inversion principles.</p>
 */
public interface ThreatAssessmentService {

    /**
     * Classifies the danger an individual poses based on biological saturation
     * and observed aggression.
     */
    ThreatLevel classify(MirakuruSaturation saturation, AggressionIndex aggressionIndex);

    /**
     * Produces a numeric priority score (higher = engage sooner) used to rank
     * targets. Individuals that are no longer an active threat score zero.
     */
    int priorityScore(AffectedIndividual affected);
}
