package com.teamarrow.mirakuru.infrastructure.adapter.in.web.mapper;

import com.teamarrow.mirakuru.application.port.in.command.PlanMissionCommand;
import com.teamarrow.mirakuru.domain.model.mission.Mission;
import com.teamarrow.mirakuru.domain.model.team.TeamMember;
import com.teamarrow.mirakuru.domain.model.team.TeamMemberId;
import com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto.MissionResponse;
import com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto.PlanMissionRequest;
import com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto.TeamMemberResponse;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Maps between the mission/team DTOs and the application/domain types.
 * Lives in infrastructure so the domain stays free of transport concerns.
 */
@Component
public class MissionDtoMapper {

    public PlanMissionCommand toCommand(PlanMissionRequest request) {
        return new PlanMissionCommand(request.name(), request.targetAffectedId());
    }

    public MissionResponse toResponse(Mission mission) {
        List<String> operativeIds = mission.getAssignedOperatives().stream()
                .map(TeamMemberId::toString)
                .toList();
        return new MissionResponse(
                mission.getId().toString(),
                mission.getName(),
                mission.getTarget().toString(),
                mission.getRequiredOperatives(),
                mission.getAssignedOperatives().size(),
                mission.isReadyToLaunch(),
                mission.getStatus().name(),
                operativeIds,
                mission.getCreatedAt());
    }

    public TeamMemberResponse toResponse(TeamMember member) {
        return new TeamMemberResponse(
                member.getId().toString(),
                member.getCodeName().value(),
                member.getRole().name());
    }
}
