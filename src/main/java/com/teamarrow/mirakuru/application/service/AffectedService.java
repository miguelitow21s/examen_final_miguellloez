package com.teamarrow.mirakuru.application.service;

import com.teamarrow.mirakuru.application.port.in.GetAffectedUseCase;
import com.teamarrow.mirakuru.application.port.in.ListAffectedUseCase;
import com.teamarrow.mirakuru.application.port.in.RegisterAffectedUseCase;
import com.teamarrow.mirakuru.application.port.in.UpdateAffectedUseCase;
import com.teamarrow.mirakuru.application.port.in.command.RegisterAffectedCommand;
import com.teamarrow.mirakuru.application.port.in.command.UpdateAffectedCommand;
import com.teamarrow.mirakuru.application.port.out.AffectedRepository;
import com.teamarrow.mirakuru.domain.exception.AffectedNotFoundException;
import com.teamarrow.mirakuru.domain.exception.DuplicateAffectedException;
import com.teamarrow.mirakuru.domain.model.AffectedId;
import com.teamarrow.mirakuru.domain.model.AffectedIndividual;
import com.teamarrow.mirakuru.domain.model.AffectedStatus;
import com.teamarrow.mirakuru.domain.model.AggressionIndex;
import com.teamarrow.mirakuru.domain.model.CodeName;
import com.teamarrow.mirakuru.domain.model.Location;
import com.teamarrow.mirakuru.domain.model.MirakuruSaturation;
import com.teamarrow.mirakuru.domain.service.ThreatAssessmentService;
import java.util.Comparator;
import java.util.List;

/**
 * Application Service that implements every inbound use case for affected
 * individuals.
 *
 * <p>Its job is orchestration, not business logic: it translates raw commands
 * into domain Value Objects, drives the aggregate and the {@link
 * ThreatAssessmentService} domain service, and persists through the {@link
 * AffectedRepository} outbound port. The actual rules (validation, threat
 * classification, lifecycle constraints) stay inside the domain where they
 * belong.</p>
 *
 * <p>It depends only on abstractions — the outbound port and the domain service
 * interface — and carries no framework annotations, so the whole core can be
 * unit-tested without Spring and wired from the infrastructure layer.</p>
 */
public class AffectedService implements RegisterAffectedUseCase, GetAffectedUseCase,
        ListAffectedUseCase, UpdateAffectedUseCase {

    private final AffectedRepository repository;
    private final ThreatAssessmentService assessmentService;

    public AffectedService(AffectedRepository repository, ThreatAssessmentService assessmentService) {
        this.repository = repository;
        this.assessmentService = assessmentService;
    }

    @Override
    public AffectedIndividual register(RegisterAffectedCommand command) {
        CodeName codeName = new CodeName(command.codeName());
        if (repository.existsByCodeName(codeName)) {
            throw new DuplicateAffectedException(codeName);
        }
        AffectedIndividual affected = AffectedIndividual.register(
                AffectedId.generate(),
                codeName,
                new MirakuruSaturation(command.mirakuruSaturation()),
                new AggressionIndex(command.aggressionIndex()),
                new Location(command.locationSector(), command.latitude(), command.longitude()),
                assessmentService);
        return repository.save(affected);
    }

    @Override
    public AffectedIndividual getById(String id) {
        AffectedId affectedId = AffectedId.of(id);
        return repository.findById(affectedId)
                .orElseThrow(() -> new AffectedNotFoundException(affectedId));
    }

    @Override
    public List<AffectedIndividual> listAll() {
        return repository.findAll().stream()
                .sorted(Comparator.comparingInt(assessmentService::priorityScore).reversed())
                .toList();
    }

    @Override
    public AffectedIndividual update(String id, UpdateAffectedCommand command) {
        AffectedId affectedId = AffectedId.of(id);
        AffectedIndividual affected = repository.findById(affectedId)
                .orElseThrow(() -> new AffectedNotFoundException(affectedId));
        affected.updateProfile(
                new CodeName(command.codeName()),
                new MirakuruSaturation(command.mirakuruSaturation()),
                new AggressionIndex(command.aggressionIndex()),
                new Location(command.locationSector(), command.latitude(), command.longitude()),
                AffectedStatus.from(command.status()),
                assessmentService);
        return repository.save(affected);
    }
}
