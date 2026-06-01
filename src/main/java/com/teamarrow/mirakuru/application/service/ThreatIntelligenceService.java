package com.teamarrow.mirakuru.application.service;

import com.teamarrow.mirakuru.application.port.in.GatherIntelligenceUseCase;
import com.teamarrow.mirakuru.application.port.out.AffectedRepository;
import com.teamarrow.mirakuru.application.port.out.ThreatIntelligenceSource;
import com.teamarrow.mirakuru.domain.exception.AffectedNotFoundException;
import com.teamarrow.mirakuru.domain.model.AffectedId;
import com.teamarrow.mirakuru.domain.model.AffectedIndividual;
import com.teamarrow.mirakuru.domain.model.intel.IntelReport;
import java.util.Comparator;
import java.util.List;

/**
 * Application Service that integrates intelligence from <strong>multiple
 * external sources</strong>.
 *
 * <p>It depends on a list of {@link ThreatIntelligenceSource} abstractions, not
 * on any concrete provider. At runtime Spring injects every adapter that
 * implements the port, so the team can plug in new feeds (A.R.G.U.S., A.C.G.,
 * informants) without this class ever changing. The reports are merged and
 * ordered by confidence, highest first.</p>
 */
public class ThreatIntelligenceService implements GatherIntelligenceUseCase {

    private final List<ThreatIntelligenceSource> sources;
    private final AffectedRepository affectedRepository;

    public ThreatIntelligenceService(List<ThreatIntelligenceSource> sources,
                                     AffectedRepository affectedRepository) {
        this.sources = List.copyOf(sources);
        this.affectedRepository = affectedRepository;
    }

    @Override
    public List<IntelReport> gatherFor(String affectedId) {
        AffectedId id = AffectedId.of(affectedId);
        AffectedIndividual affected = affectedRepository.findById(id)
                .orElseThrow(() -> new AffectedNotFoundException(id));
        return sources.stream()
                .map(source -> source.reportFor(affected.getCodeName()))
                .flatMap(java.util.Optional::stream)
                .sorted(Comparator.comparingInt(IntelReport::confidence).reversed())
                .toList();
    }
}
