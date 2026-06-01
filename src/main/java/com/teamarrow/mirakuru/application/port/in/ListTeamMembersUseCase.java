package com.teamarrow.mirakuru.application.port.in;

import com.teamarrow.mirakuru.domain.model.team.TeamMember;
import java.util.List;

/** Inbound port: list the Team Arrow roster (useful to know who can be assigned). */
public interface ListTeamMembersUseCase {

    List<TeamMember> listAll();
}
