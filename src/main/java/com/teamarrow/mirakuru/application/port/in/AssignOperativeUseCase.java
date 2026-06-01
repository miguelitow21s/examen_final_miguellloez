package com.teamarrow.mirakuru.application.port.in;

import com.teamarrow.mirakuru.domain.model.mission.Mission;

/** Inbound port: assign a team member (by code name) to a mission. */
public interface AssignOperativeUseCase {

    Mission assign(String missionId, String operativeCodeName);
}
