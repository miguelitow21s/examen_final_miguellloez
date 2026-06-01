package com.teamarrow.mirakuru.application.port.in;

import com.teamarrow.mirakuru.domain.model.intel.IntelReport;
import java.util.List;

/**
 * Inbound port: gather everything the external sources know about an affected
 * individual, aggregating across every configured source.
 */
public interface GatherIntelligenceUseCase {

    List<IntelReport> gatherFor(String affectedId);
}
