package top.parak.kraft.core.node.role;

import top.parak.kraft.core.node.NodeId;

import javax.annotation.concurrent.Immutable;

/**
 * Role name and leader id.
 *
 * @author KHighness
 * @since 2022-03-18
 * @email parakovo@gmail.com
 */
@Immutable
public class RoleNameANdLeaderId {

    private final RoleName roleName;
    private final NodeId leaderId;

    /**
     * Create RoleNameANdLeaderId.
     *
     * @param roleName role name
     * @param leaderId leader id
     */
    public RoleNameANdLeaderId(RoleName roleName, NodeId leaderId) {
        this.roleName = roleName;
        this.leaderId = leaderId;
    }

    /**
     * Get role name.
     *
     * @return role name.
     */
    public RoleName getRoleName() {
        return roleName;
    }

    /**
     * Get leader id.
     * @return leader id.
     */
    public NodeId getLeaderId() {
        return leaderId;
    }

}
