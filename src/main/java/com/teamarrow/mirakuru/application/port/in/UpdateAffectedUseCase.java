package com.teamarrow.mirakuru.application.port.in;

import com.teamarrow.mirakuru.application.port.in.command.UpdateAffectedCommand;
import com.teamarrow.mirakuru.domain.model.AffectedIndividual;

/**
 * Inbound port for updating the profile of an existing affected individual.
 */
public interface UpdateAffectedUseCase {

    AffectedIndividual update(String id, UpdateAffectedCommand command);
}
