package com.teamarrow.mirakuru.infrastructure.adapter.in.web;

import com.teamarrow.mirakuru.application.port.in.GatherIntelligenceUseCase;
import com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto.IntelReportResponse;
import com.teamarrow.mirakuru.infrastructure.adapter.in.web.mapper.AffectedDtoMapper;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Driving adapter that exposes the aggregated external intelligence for a given
 * affected individual: {@code GET /api/afectados/{id}/inteligencia}.
 *
 * <p>It returns the merged reports from every configured external source, proving
 * the "integrate information from multiple external sources" capability end to
 * end without the web layer knowing which sources exist.</p>
 */
@RestController
@RequestMapping("/api/afectados/{id}/inteligencia")
public class IntelligenceController {

    private final GatherIntelligenceUseCase gatherIntelligenceUseCase;
    private final AffectedDtoMapper mapper;

    public IntelligenceController(GatherIntelligenceUseCase gatherIntelligenceUseCase,
                                 AffectedDtoMapper mapper) {
        this.gatherIntelligenceUseCase = gatherIntelligenceUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public List<IntelReportResponse> gather(@PathVariable String id) {
        return gatherIntelligenceUseCase.gatherFor(id).stream()
                .map(mapper::toResponse)
                .toList();
    }
}
