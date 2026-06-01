package com.teamarrow.mirakuru.application.port.out;

import com.teamarrow.mirakuru.domain.model.CodeName;
import com.teamarrow.mirakuru.domain.model.intel.IntelReport;
import java.util.Optional;

/**
 * Outbound port that abstracts an <strong>external intelligence source</strong>
 * (A.R.G.U.S., A.C.G., street informants, ...).
 *
 * <p>This is the seam that lets the platform "integrate information from multiple
 * external sources" without coupling the core to any of them. Each provider is a
 * separate adapter implementing this interface; the application asks every
 * available source for what it knows. Adding a new feed is a new adapter and
 * <em>zero</em> changes to the core — the Open/Closed and Dependency Inversion
 * principles working together.</p>
 */
public interface ThreatIntelligenceSource {

    /** Human-readable name of the source, used to label the reports it returns. */
    String sourceName();

    /** The latest report this source has on the given code name, if any. */
    Optional<IntelReport> reportFor(CodeName codeName);
}
