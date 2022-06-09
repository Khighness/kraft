package top.parak.kraft.core.node.role;

import com.google.common.base.Preconditions;

import top.parak.kraft.core.node.NodeId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Role name and leader id.
 *
 * @author KHighness
 * @since 2022-03-18
 * @email parakovo@gmail.com
 */
@Immutable
public class RoleNameAndLeaderId {

    private final RoleName roleName;
    private final NodeId leaderId;

    /**
     * Create RoleNameANdLeaderId.
     *
     * @param roleName role name
     * @param leaderId leader id
     */
    public RoleNameAndLeaderId(@Nonnull RoleName roleName, @Nullable NodeId leaderId) {
        Preconditions.checkNotNull(roleName);
        this.roleName = roleName;
        this.leaderId = leaderId;
    }

    /**
     * Get role name.
     *
     * @return role name
     */
    @Nonnull
    public RoleName getRoleName() {
        return roleName;
    }

    /**
     * Get leader id.
     *
     * @return leader id
     */
    @Nullable
    public NodeId getLeaderId() {
        return leaderId;
    }

}
