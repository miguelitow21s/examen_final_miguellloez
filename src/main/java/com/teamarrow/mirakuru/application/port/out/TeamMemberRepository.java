package com.teamarrow.mirakuru.application.port.out;

import com.teamarrow.mirakuru.domain.model.CodeName;
import com.teamarrow.mirakuru.domain.model.team.TeamMember;
import com.teamarrow.mirakuru.domain.model.team.TeamMemberId;
import java.util.List;
import java.util.Optional;

/**
 * Outbound port for reading the Team Arrow roster. Missions assign operatives
 * resolved through this port.
 */
public interface TeamMemberRepository {

    Optional<TeamMember> findById(TeamMemberId id);

    Optional<TeamMember> findByCodeName(CodeName codeName);

    List<TeamMember> findAll();
}
