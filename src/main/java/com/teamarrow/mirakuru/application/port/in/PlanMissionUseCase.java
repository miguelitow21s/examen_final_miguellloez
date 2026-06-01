package com.teamarrow.mirakuru.application.port.in;

import com.teamarrow.mirakuru.application.port.in.command.PlanMissionCommand;
import com.teamarrow.mirakuru.domain.model.mission.Mission;

/** Inbound port: open a new mission against a target. */
public interface PlanMissionUseCase {

    Mission plan(PlanMissionCommand command);
}
