package com.teamarrow.mirakuru.infrastructure.adapter.out.intel;

import com.teamarrow.mirakuru.application.port.out.ThreatIntelligenceSource;
import com.teamarrow.mirakuru.domain.model.CodeName;
import com.teamarrow.mirakuru.domain.model.intel.IntelReport;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Driven adapter that simulates the <strong>A.R.G.U.S.</strong> intelligence
 * feed. In a real deployment this would call an external REST API or message
 * queue; here it returns a deterministic, simulated report.
 *
 * <p>It is one of several interchangeable {@link ThreatIntelligenceSource}
 * implementations. Spring discovers it automatically and the application
 * aggregates it together with the other sources.</p>
 */
@Component
public class ArgusIntelligenceAdapter implements ThreatIntelligenceSource {

    @Override
    public String sourceName() {
        return "A.R.G.U.S.";
    }

    @Override
    public Optional<IntelReport> reportFor(CodeName codeName) {
        // Simulated lookup: A.R.G.U.S. always has a satellite fix.
        int confidence = 70 + Math.floorMod(codeName.value().hashCode(), 30); // 70-99
        String headline = "Satellite tracking active on " + codeName.value();
        return Optional.of(new IntelReport(sourceName(), headline, confidence, Instant.now()));
    }
}
