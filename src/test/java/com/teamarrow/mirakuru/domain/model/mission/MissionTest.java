package com.teamarrow.mirakuru.domain.model.mission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.teamarrow.mirakuru.domain.exception.DomainException;
import com.teamarrow.mirakuru.domain.model.AffectedId;
import com.teamarrow.mirakuru.domain.model.team.TeamMemberId;
import org.junit.jupiter.api.Test;

/** Pure unit tests for the Mission aggregate's staffing and launch invariants. */
class MissionTest {

    private Mission planFor(int requiredOperatives) {
        return Mission.plan(MissionId.generate(), "Operation Glades",
                AffectedId.generate(), requiredOperatives);
    }

    @Test
    void newMissionStartsInPlanningAndIsNotReady() {
        Mission mission = planFor(2);
        assertThat(mission.getStatus()).isEqualTo(MissionStatus.PLANNING);
        assertThat(mission.isReadyToLaunch()).isFalse();
    }

    @Test
    void rejectsAssigningTheSameOperativeTwice() {
        Mission mission = planFor(2);
        TeamMemberId operative = TeamMemberId.generate();
        mission.assign(operative);
        assertThatThrownBy(() -> mission.assign(operative))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("already assigned");
    }

    @Test
    void cannotLaunchWhenUnderstaffed() {
        Mission mission = planFor(2);
        mission.assign(TeamMemberId.generate());
        assertThatThrownBy(mission::launch)
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("needs 2 operatives");
    }

    @Test
    void launchesWhenFullyStaffed() {
        Mission mission = planFor(2);
        mission.assign(TeamMemberId.generate());
        mission.assign(TeamMemberId.generate());
        assertThat(mission.isReadyToLaunch()).isTrue();
        mission.launch();
        assertThat(mission.getStatus()).isEqualTo(MissionStatus.IN_PROGRESS);
    }

    @Test
    void cannotAssignAfterLaunch() {
        Mission mission = planFor(1);
        mission.assign(TeamMemberId.generate());
        mission.launch();
        assertThatThrownBy(() -> mission.assign(TeamMemberId.generate()))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("PLANNING");
    }

    @Test
    void cannotAbortATerminalMission() {
        Mission mission = planFor(1);
        mission.abort();
        assertThat(mission.getStatus()).isEqualTo(MissionStatus.ABORTED);
        assertThatThrownBy(mission::abort).isInstanceOf(DomainException.class);
    }
}
