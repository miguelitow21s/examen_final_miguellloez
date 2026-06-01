package com.teamarrow.mirakuru.domain.model.team;

import com.teamarrow.mirakuru.domain.model.CodeName;
import java.util.Objects;

/**
 * Entity representing a member of Team Arrow who can be assigned to missions.
 *
 * <p>It has identity ({@link TeamMemberId}); two operatives with the same alias
 * are still different people. The {@link CodeName} Value Object is reused here,
 * a small example of avoiding duplication across the model.</p>
 */
public class TeamMember {

    private final TeamMemberId id;
    private final CodeName codeName;
    private final OperativeRole role;

    public TeamMember(TeamMemberId id, CodeName codeName, OperativeRole role) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.codeName = Objects.requireNonNull(codeName, "codeName must not be null");
        this.role = Objects.requireNonNull(role, "role must not be null");
    }

    public TeamMemberId getId() {
        return id;
    }

    public CodeName getCodeName() {
        return codeName;
    }

    public OperativeRole getRole() {
        return role;
    }

    /** Whether this member can be counted as boots-on-the-ground for a mission. */
    public boolean isDeployable() {
        return role.isDeployable();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TeamMember that)) {
            return false;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "TeamMember{id=" + id + ", codeName=" + codeName.value() + ", role=" + role + '}';
    }
}
