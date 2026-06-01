package com.teamarrow.mirakuru.application.service;

import com.teamarrow.mirakuru.application.port.in.ListTeamMembersUseCase;
import com.teamarrow.mirakuru.application.port.out.TeamMemberRepository;
import com.teamarrow.mirakuru.domain.model.team.TeamMember;
import java.util.List;

/**
 * Application Service exposing read access to the Team Arrow roster.
 *
 * <p>Kept separate from {@link MissionService} so each service has a single
 * reason to change (Single Responsibility): listing the team is a team concern,
 * not a mission concern.</p>
 */
public class TeamService implements ListTeamMembersUseCase {

    private final TeamMemberRepository teamMemberRepository;

    public TeamService(TeamMemberRepository teamMemberRepository) {
        this.teamMemberRepository = teamMemberRepository;
    }

    @Override
    public List<TeamMember> listAll() {
        return teamMemberRepository.findAll();
    }
}
