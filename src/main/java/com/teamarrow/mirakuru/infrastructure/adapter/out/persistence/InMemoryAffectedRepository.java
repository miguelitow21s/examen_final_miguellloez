package com.teamarrow.mirakuru.infrastructure.adapter.out.persistence;

import com.teamarrow.mirakuru.application.port.out.AffectedRepository;
import com.teamarrow.mirakuru.domain.model.AffectedId;
import com.teamarrow.mirakuru.domain.model.AffectedIndividual;
import com.teamarrow.mirakuru.domain.model.CodeName;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * Driven adapter that implements the {@link AffectedRepository} outbound port
 * with a thread-safe in-memory store.
 *
 * <p>This is the only place that knows <em>how</em> affected individuals are
 * stored. Swapping it for a JPA, MongoDB or external-service adapter is a matter
 * of providing another implementation of the same port — the application core
 * stays untouched, which is exactly the decoupling Felicity asked for.</p>
 */
@Repository
public class InMemoryAffectedRepository implements AffectedRepository {

    private final Map<AffectedId, AffectedIndividual> store = new ConcurrentHashMap<>();

    @Override
    public AffectedIndividual save(AffectedIndividual affected) {
        store.put(affected.getId(), affected);
        return affected;
    }

    @Override
    public Optional<AffectedIndividual> findById(AffectedId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<AffectedIndividual> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public boolean existsByCodeName(CodeName codeName) {
        return store.values().stream()
                .anyMatch(affected -> affected.getCodeName().equals(codeName));
    }
}
