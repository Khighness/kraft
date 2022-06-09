package top.parak.kraft.core.node.role;

import top.parak.kraft.core.node.NodeId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Role state.
 *
 * @author KHighness
 * @since 2022-03-18
 * @email parakovo@gmail.com
 */
public interface RoleState {

    int VOTES_COUNT_NOT_SET = -1;

    /**
     * Get role name.
     *
     * @return role name
     */
    @Nonnull
    RoleName getRoleName();

    /**
     * Get term.
     *
     * @return term
     */
    int getTerm();

    /**
     * Get votes count.
     *
     * @return votes count, {@link RoleState#VOTES_COUNT_NOT_SET} if unknown
     */
    int getVotesCount();

    /**
     * Get node id voted for.
     *
     * @return node id voted for
     */
    @Nullable
    NodeId getVotedFor();

    /**
     * Get leader id.
     *
     * @return leader id
     */
    @Nullable
    NodeId getLeaderId();

}
