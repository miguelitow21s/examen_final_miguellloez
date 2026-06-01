package com.teamarrow.mirakuru.application.port.in;

import com.teamarrow.mirakuru.domain.model.AffectedIndividual;
import java.util.List;

/**
 * Inbound port for listing every registered affected individual.
 *
 * <p>The returned list is ordered by engagement priority (most dangerous
 * active threats first), satisfying the "prioritise targets" capability of the
 * system.</p>
 */
public interface ListAffectedUseCase {

    List<AffectedIndividual> listAll();
}
