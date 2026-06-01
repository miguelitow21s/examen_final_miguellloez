package com.teamarrow.mirakuru.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.teamarrow.mirakuru.domain.model.AffectedId;
import com.teamarrow.mirakuru.domain.model.AffectedIndividual;
import com.teamarrow.mirakuru.domain.model.AggressionIndex;
import com.teamarrow.mirakuru.domain.model.CodeName;
import com.teamarrow.mirakuru.domain.model.Location;
import com.teamarrow.mirakuru.domain.model.MirakuruSaturation;
import com.teamarrow.mirakuru.domain.model.ThreatLevel;
import org.junit.jupiter.api.Test;

/**
 * Pure unit tests for the threat assessment rules. They need no Spring context
 * and no mocks — a direct benefit of keeping the domain framework-free.
 */
class WeightedThreatAssessmentServiceTest {

    private final ThreatAssessmentService service = new WeightedThreatAssessmentService();

    @Test
    void classifiesMaxedOutSubjectAsCritical() {
        ThreatLevel level = service.classify(new MirakuruSaturation(100), new AggressionIndex(10));
        assertThat(level).isEqualTo(ThreatLevel.CRITICAL);
    }

    @Test
    void classifiesDormantSubjectAsLow() {
        ThreatLevel level = service.classify(new MirakuruSaturation(10), new AggressionIndex(1));
        assertThat(level).isEqualTo(ThreatLevel.LOW);
    }

    @Test
    void saturationWeighsMoreThanAggression() {
        // score = 70*0.6 + 50*0.4 = 42 + 20 = 62 -> HIGH
        ThreatLevel level = service.classify(new MirakuruSaturation(70), new AggressionIndex(5));
        assertThat(level).isEqualTo(ThreatLevel.HIGH);
    }

    @Test
    void neutralizedSubjectHasZeroPriority() {
        AffectedIndividual affected = AffectedIndividual.register(
                AffectedId.generate(), new CodeName("Soldier"),
                new MirakuruSaturation(100), new AggressionIndex(10),
                Location.unknown(), service);

        affected.updateProfile(new CodeName("Soldier"), new MirakuruSaturation(100),
                new AggressionIndex(10), Location.unknown(),
                com.teamarrow.mirakuru.domain.model.AffectedStatus.NEUTRALIZED, service);

        assertThat(service.priorityScore(affected)).isZero();
    }

    @Test
    void atLargeSubjectOutranksMonitoredOne() {
        AffectedIndividual atLarge = AffectedIndividual.register(
                AffectedId.generate(), new CodeName("AtLarge"),
                new MirakuruSaturation(80), new AggressionIndex(8),
                Location.unknown(), service);

        AffectedIndividual monitored = AffectedIndividual.register(
                AffectedId.generate(), new CodeName("Monitored"),
                new MirakuruSaturation(80), new AggressionIndex(8),
                Location.unknown(), service);
        monitored.updateProfile(new CodeName("Monitored"), new MirakuruSaturation(80),
                new AggressionIndex(8), Location.unknown(),
                com.teamarrow.mirakuru.domain.model.AffectedStatus.MONITORED, service);

        assertThat(service.priorityScore(atLarge))
                .isGreaterThan(service.priorityScore(monitored));
    }
}
