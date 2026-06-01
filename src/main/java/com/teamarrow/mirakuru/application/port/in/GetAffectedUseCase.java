package com.teamarrow.mirakuru.application.port.in;

import com.teamarrow.mirakuru.domain.model.AffectedIndividual;

/**
 * Inbound port for retrieving a single affected individual by its identifier.
 */
public interface GetAffectedUseCase {

    AffectedIndividual getById(String id);
}
