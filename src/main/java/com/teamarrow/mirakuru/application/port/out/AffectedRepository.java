package com.teamarrow.mirakuru.application.port.out;

import com.teamarrow.mirakuru.domain.model.AffectedId;
import com.teamarrow.mirakuru.domain.model.AffectedIndividual;
import com.teamarrow.mirakuru.domain.model.CodeName;
import java.util.List;
import java.util.Optional;

/**
 * Outbound port (driven side of the hexagon) for persisting affected
 * individuals.
 *
 * <p>It is defined in terms of the domain model only and lives with the
 * application core. The actual storage technology is an implementation detail
 * provided by an adapter in the infrastructure layer (in-memory today, a
 * relational or document database tomorrow) without the core ever changing.
 * This is the Dependency Inversion Principle made concrete: the core declares
 * the abstraction it needs and the infrastructure depends on it, not the other
 * way around.</p>
 */
public interface AffectedRepository {

    AffectedIndividual save(AffectedIndividual affected);

    Optional<AffectedIndividual> findById(AffectedId id);

    List<AffectedIndividual> findAll();

    boolean existsByCodeName(CodeName codeName);
}
