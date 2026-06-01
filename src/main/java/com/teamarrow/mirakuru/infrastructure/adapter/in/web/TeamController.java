package com.teamarrow.mirakuru.infrastructure.adapter.in.web;

import com.teamarrow.mirakuru.application.port.in.ListTeamMembersUseCase;
import com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto.TeamMemberResponse;
import com.teamarrow.mirakuru.infrastructure.adapter.in.web.mapper.MissionDtoMapper;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Driving adapter exposing the Team Arrow roster, so clients know which code
 * names they can assign to a mission.
 */
@RestController
@RequestMapping("/api/equipo")
public class TeamController {

    private final ListTeamMembersUseCase listTeamMembersUseCase;
    private final MissionDtoMapper mapper;

    public TeamController(ListTeamMembersUseCase listTeamMembersUseCase, MissionDtoMapper mapper) {
        this.listTeamMembersUseCase = listTeamMembersUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public List<TeamMemberResponse> listAll() {
        return listTeamMembersUseCase.listAll().stream()
                .map(mapper::toResponse)
                .toList();
    }
}
