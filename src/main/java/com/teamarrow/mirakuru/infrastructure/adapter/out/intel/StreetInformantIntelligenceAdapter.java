package com.teamarrow.mirakuru.infrastructure.adapter.out.intel;

import com.teamarrow.mirakuru.application.port.out.ThreatIntelligenceSource;
import com.teamarrow.mirakuru.domain.model.CodeName;
import com.teamarrow.mirakuru.domain.model.intel.IntelReport;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Driven adapter that simulates a <strong>street-informant network</strong>
 * intelligence feed: lower confidence than satellite data, and it only "knows"
 * about subjects whose code name is reasonably short (a stand-in for partial
 * coverage). Demonstrates that different sources may or may not have data on a
 * given subject — the aggregator simply merges whatever is available.
 */
@Component
public class StreetInformantIntelligenceAdapter implements ThreatIntelligenceSource {

    @Override
    public String sourceName() {
        return "Street Informants";
    }

    @Override
    public Optional<IntelReport> reportFor(CodeName codeName) {
        if (codeName.value().length() > 40) {
            return Optional.empty(); // informants have no fix on this one
        }
        int confidence = 30 + Math.floorMod(codeName.value().hashCode(), 30); // 30-59
        String headline = "Last seen near the Glades: " + codeName.value();
        return Optional.of(new IntelReport(sourceName(), headline, confidence, Instant.now()));
    }
}
