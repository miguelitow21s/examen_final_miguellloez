package com.teamarrow.mirakuru.application.port.in;

import com.teamarrow.mirakuru.domain.model.mission.Mission;
import java.util.List;

/** Inbound port: list every mission. */
public interface ListMissionsUseCase {

    List<Mission> listAll();
}
