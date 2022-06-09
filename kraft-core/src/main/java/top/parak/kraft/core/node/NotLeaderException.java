package top.parak.kraft.core.node;

import com.google.common.base.Preconditions;
import top.parak.kraft.core.node.role.RoleName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    public NotLeaderException(@Nonnull RoleName roleName, @Nullable NodeEndpoint leaderEndpoint) {
        super("not leader");
        Preconditions.checkNotNull(roleName);
        this.roleName = roleName;
        this.leaderEndpoint = leaderEndpoint;
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
     * Get leader endpoint.
     *
     * @return leader endpoint
     */
    @Nullable
    public NodeEndpoint getLeaderEndpoint() {
        return leaderEndpoint;
    }

}
