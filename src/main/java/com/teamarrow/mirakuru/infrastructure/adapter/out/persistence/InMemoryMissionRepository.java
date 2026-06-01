package com.teamarrow.mirakuru.infrastructure.adapter.out.persistence;

import com.teamarrow.mirakuru.application.port.out.MissionRepository;
import com.teamarrow.mirakuru.domain.model.mission.Mission;
import com.teamarrow.mirakuru.domain.model.mission.MissionId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * Driven adapter implementing {@link MissionRepository} with an in-memory store.
 */
@Repository
public class InMemoryMissionRepository implements MissionRepository {

    private final Map<MissionId, Mission> store = new ConcurrentHashMap<>();

    @Override
    public Mission save(Mission mission) {
        store.put(mission.getId(), mission);
        return mission;
    }

    @Override
    public Optional<Mission> findById(MissionId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Mission> findAll() {
        return List.copyOf(store.values());
    }
}
