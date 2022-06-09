package top.parak.kraft.core.node;

import top.parak.kraft.core.node.role.RoleState;

import javax.annotation.Nonnull;

/**
 * Node role listener.
 *
 * @author KHighness
 * @since 2022-03-19
 * @email parakovo@gmail.com
 */
public interface NodeRoleListener {

    /**
     * Called when node's role changes.
     * <p>
     * e.g. Follower -> Candidate
     * </p>
     *
     * @param roleState role state
     */
    void nodeRoleChanged(@Nonnull RoleState roleState);

}
