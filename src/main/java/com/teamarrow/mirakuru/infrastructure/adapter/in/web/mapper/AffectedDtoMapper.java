package com.teamarrow.mirakuru.infrastructure.adapter.in.web.mapper;

import com.teamarrow.mirakuru.application.port.in.command.RegisterAffectedCommand;
import com.teamarrow.mirakuru.application.port.in.command.UpdateAffectedCommand;
import com.teamarrow.mirakuru.domain.model.AffectedIndividual;
import com.teamarrow.mirakuru.domain.model.Location;
import com.teamarrow.mirakuru.domain.service.ThreatAssessmentService;
import com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto.AffectedResponse;
import com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto.LocationResponse;
import com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto.RegisterAffectedRequest;
import com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto.UpdateAffectedRequest;
import org.springframework.stereotype.Component;

/**
 * Translates between the web DTOs and the application/domain types.
 *
 * <p>This mapper lives in the infrastructure layer so the domain never depends
 * on transport concerns. It also enriches the outgoing response with the
 * derived {@code priorityScore}, which it obtains from the {@link
 * ThreatAssessmentService} domain service rather than recomputing it — the rule
 * stays owned by the domain.</p>
 */
@Component
public class AffectedDtoMapper {

    private final ThreatAssessmentService assessmentService;

    public AffectedDtoMapper(ThreatAssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    public RegisterAffectedCommand toCommand(RegisterAffectedRequest request) {
        return new RegisterAffectedCommand(
                request.codeName(),
                request.mirakuruSaturation(),
                request.aggressionIndex(),
                request.locationSector(),
                request.latitude(),
                request.longitude());
    }

    public UpdateAffectedCommand toCommand(UpdateAffectedRequest request) {
        return new UpdateAffectedCommand(
                request.codeName(),
                request.mirakuruSaturation(),
                request.aggressionIndex(),
                request.locationSector(),
                request.latitude(),
                request.longitude(),
                request.status());
    }

    public AffectedResponse toResponse(AffectedIndividual affected) {
        Location location = affected.getLastKnownLocation();
        return new AffectedResponse(
                affected.getId().toString(),
                affected.getCodeName().value(),
                affected.getSaturation().percentage(),
                affected.getAggressionIndex().value(),
                affected.getStatus().name(),
                affected.getThreatLevel().name(),
                assessmentService.priorityScore(affected),
                new LocationResponse(location.sector(), location.latitude(), location.longitude()),
                affected.getRegisteredAt(),
                affected.getLastUpdatedAt());
    }
}
