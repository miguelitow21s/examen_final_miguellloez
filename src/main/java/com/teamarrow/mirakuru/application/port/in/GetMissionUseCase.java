package com.teamarrow.mirakuru.application.port.in;

import com.teamarrow.mirakuru.domain.model.mission.Mission;

/** Inbound port: read a single mission by id. */
public interface GetMissionUseCase {

    Mission getById(String missionId);
}
