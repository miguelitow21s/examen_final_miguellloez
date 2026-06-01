package com.teamarrow.mirakuru.application.port.out;

import com.teamarrow.mirakuru.domain.model.mission.Mission;
import com.teamarrow.mirakuru.domain.model.mission.MissionId;
import java.util.List;
import java.util.Optional;

/**
 * Outbound port for persisting missions. Defined in terms of the domain only;
 * the storage technology is an infrastructure detail.
 */
public interface MissionRepository {

    Mission save(Mission mission);

    Optional<Mission> findById(MissionId id);

    List<Mission> findAll();
}
