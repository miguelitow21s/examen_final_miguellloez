package com.teamarrow.mirakuru.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.teamarrow.mirakuru.domain.model.ThreatLevel;
import org.junit.jupiter.api.Test;

/** Unit tests for the mission staffing policy. */
class StandardMissionCoordinationServiceTest {

    private final MissionCoordinationService service = new StandardMissionCoordinationService();

    @Test
    void scalesRequiredOperativesWithThreatLevel() {
        assertThat(service.requiredOperativesFor(ThreatLevel.LOW)).isEqualTo(1);
        assertThat(service.requiredOperativesFor(ThreatLevel.MODERATE)).isEqualTo(2);
        assertThat(service.requiredOperativesFor(ThreatLevel.HIGH)).isEqualTo(3);
        assertThat(service.requiredOperativesFor(ThreatLevel.CRITICAL)).isEqualTo(4);
    }
}
