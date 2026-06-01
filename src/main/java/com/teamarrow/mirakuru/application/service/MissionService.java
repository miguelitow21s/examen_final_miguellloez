package com.teamarrow.mirakuru.application.service;

import com.teamarrow.mirakuru.application.port.in.AssignOperativeUseCase;
import com.teamarrow.mirakuru.application.port.in.GetMissionUseCase;
import com.teamarrow.mirakuru.application.port.in.LaunchMissionUseCase;
import com.teamarrow.mirakuru.application.port.in.ListMissionsUseCase;
import com.teamarrow.mirakuru.application.port.in.PlanMissionUseCase;
import com.teamarrow.mirakuru.application.port.in.command.PlanMissionCommand;
import com.teamarrow.mirakuru.application.port.out.AffectedRepository;
import com.teamarrow.mirakuru.application.port.out.MissionRepository;
import com.teamarrow.mirakuru.application.port.out.TeamMemberRepository;
import com.teamarrow.mirakuru.domain.exception.AffectedNotFoundException;
import com.teamarrow.mirakuru.domain.exception.MissionNotFoundException;
import com.teamarrow.mirakuru.domain.exception.TeamMemberNotFoundException;
import com.teamarrow.mirakuru.domain.model.AffectedId;
import com.teamarrow.mirakuru.domain.model.AffectedIndividual;
import com.teamarrow.mirakuru.domain.model.CodeName;
import com.teamarrow.mirakuru.domain.model.mission.Mission;
import com.teamarrow.mirakuru.domain.model.mission.MissionId;
import com.teamarrow.mirakuru.domain.model.team.TeamMember;
import com.teamarrow.mirakuru.domain.service.MissionCoordinationService;
import java.util.List;

/**
 * Application Service that orchestrates the mission use cases.
 *
 * <p>It coordinates three aggregates/ports without leaking business rules: it
 * reads the target {@link AffectedIndividual} to learn its threat level, asks the
 * {@link MissionCoordinationService} domain service how many operatives that
 * warrants, opens the {@link Mission} aggregate, resolves operatives against the
 * {@link TeamMemberRepository}, and lets the aggregate enforce the staffing and
 * launch rules. The orchestration lives here; the rules live in the domain.</p>
 */
public class MissionService implements PlanMissionUseCase, AssignOperativeUseCase,
        LaunchMissionUseCase, GetMissionUseCase, ListMissionsUseCase {

    private final MissionRepository missionRepository;
    private final AffectedRepository affectedRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MissionCoordinationService coordinationService;

    public MissionService(MissionRepository missionRepository,
                          AffectedRepository affectedRepository,
                          TeamMemberRepository teamMemberRepository,
                          MissionCoordinationService coordinationService) {
        this.missionRepository = missionRepository;
        this.affectedRepository = affectedRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.coordinationService = coordinationService;
    }

    @Override
    public Mission plan(PlanMissionCommand command) {
        AffectedId targetId = AffectedId.of(command.targetAffectedId());
        AffectedIndividual target = affectedRepository.findById(targetId)
                .orElseThrow(() -> new AffectedNotFoundException(targetId));
        int requiredOperatives = coordinationService.requiredOperativesFor(target.getThreatLevel());
        Mission mission = Mission.plan(MissionId.generate(), command.name(),
                target.getId(), requiredOperatives);
        return missionRepository.save(mission);
    }

    @Override
    public Mission assign(String missionId, String operativeCodeName) {
        Mission mission = loadMission(missionId);
        TeamMember operative = teamMemberRepository.findByCodeName(new CodeName(operativeCodeName))
                .orElseThrow(() -> new TeamMemberNotFoundException(operativeCodeName));
        mission.assign(operative.getId());
        return missionRepository.save(mission);
    }

    @Override
    public Mission launch(String missionId) {
        Mission mission = loadMission(missionId);
        mission.launch();
        return missionRepository.save(mission);
    }

    @Override
    public Mission getById(String missionId) {
        return loadMission(missionId);
    }

    @Override
    public List<Mission> listAll() {
        return missionRepository.findAll();
    }

    private Mission loadMission(String missionId) {
        MissionId id = MissionId.of(missionId);
        return missionRepository.findById(id)
                .orElseThrow(() -> new MissionNotFoundException(id));
    }
}
