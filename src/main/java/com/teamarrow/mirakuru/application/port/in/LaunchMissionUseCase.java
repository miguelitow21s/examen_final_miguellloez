package com.teamarrow.mirakuru.application.port.in;

import com.teamarrow.mirakuru.domain.model.mission.Mission;

/** Inbound port: launch a fully-staffed mission. */
public interface LaunchMissionUseCase {

    Mission launch(String missionId);
}
