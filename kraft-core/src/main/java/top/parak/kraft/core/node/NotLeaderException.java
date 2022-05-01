package top.parak.kraft.core.node;

import top.parak.kraft.core.node.role.RoleName;

/**
 * Thrown when the current node role is not a leader.
 *
 * @author KHighness
 * @since 2022-03-19
 * @email parakovo@gmail.com
 */
public class NotLeaderException extends RuntimeException {

    private final RoleName roleName;
    private final NodeEndpoint leaderEndpoint;


    /**
     * Create NotLeaderException.
     *
     * @param roleName       role name
     * @param leaderEndpoint leader endpoint
     */
    public NotLeaderException(RoleName roleName, NodeEndpoint leaderEndpoint) {
        this.roleName = roleName;
        this.leaderEndpoint = leaderEndpoint;
    }

    /**
     * Get role name.
     *
     * @return role name
     */
    public RoleName getRoleName() {
        return roleName;
    }

    /**
     * Get leader endpoint.
     *
     * @return leader point
     */
    public NodeEndpoint getLeaderEndpoint() {
        return leaderEndpoint;
    }

}
