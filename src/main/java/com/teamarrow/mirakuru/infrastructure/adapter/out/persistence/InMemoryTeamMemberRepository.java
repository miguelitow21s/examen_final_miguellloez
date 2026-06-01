package com.teamarrow.mirakuru.infrastructure.adapter.out.persistence;

import com.teamarrow.mirakuru.application.port.out.TeamMemberRepository;
import com.teamarrow.mirakuru.domain.model.CodeName;
import com.teamarrow.mirakuru.domain.model.team.OperativeRole;
import com.teamarrow.mirakuru.domain.model.team.TeamMember;
import com.teamarrow.mirakuru.domain.model.team.TeamMemberId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * Driven adapter implementing {@link TeamMemberRepository}, pre-seeded with the
 * canonical Team Arrow roster so missions have operatives to coordinate from the
 * moment the application starts.
 */
@Repository
public class InMemoryTeamMemberRepository implements TeamMemberRepository {

    private final Map<TeamMemberId, TeamMember> store = new ConcurrentHashMap<>();

    public InMemoryTeamMemberRepository() {
        seed("Green Arrow", OperativeRole.FIELD);
        seed("Spartan", OperativeRole.FIELD);
        seed("Speedy", OperativeRole.FIELD);
        seed("Black Canary", OperativeRole.FIELD);
        seed("Overwatch", OperativeRole.TECH);
        seed("Arsenal", OperativeRole.SUPPORT);
    }

    private void seed(String codeName, OperativeRole role) {
        TeamMember member = new TeamMember(TeamMemberId.generate(), new CodeName(codeName), role);
        store.put(member.getId(), member);
    }

    @Override
    public Optional<TeamMember> findById(TeamMemberId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<TeamMember> findByCodeName(CodeName codeName) {
        return store.values().stream()
                .filter(member -> member.getCodeName().equals(codeName))
                .findFirst();
    }

    @Override
    public List<TeamMember> findAll() {
        return List.copyOf(store.values());
    }
}
