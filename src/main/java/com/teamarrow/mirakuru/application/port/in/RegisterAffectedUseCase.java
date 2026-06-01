package com.teamarrow.mirakuru.application.port.in;

import com.teamarrow.mirakuru.application.port.in.command.RegisterAffectedCommand;
import com.teamarrow.mirakuru.domain.model.AffectedIndividual;

/**
 * Inbound port (driving side of the hexagon) for registering a new affected
 * individual.
 *
 * <p>Each use case is its own single-method interface following the Interface
 * Segregation Principle: a client (such as the REST controller) depends only on
 * the exact capability it needs, not on a fat "service" interface. The driving
 * adapter talks to this abstraction, never to a concrete implementation.</p>
 */
public interface RegisterAffectedUseCase {

    AffectedIndividual register(RegisterAffectedCommand command);
}
