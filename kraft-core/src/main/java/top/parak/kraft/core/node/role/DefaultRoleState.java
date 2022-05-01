package top.parak.kraft.core.node.role;

import top.parak.kraft.core.node.NodeId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Default role state.
 *
 * @author KHighness
 * @since 2022-03-18
 * @email parakovo@gmail.com
 */
public class DefaultRoleState implements RoleState {

    private final RoleName roleName;
    private final int term;
    private int votesCount = VOTES_COUNT_NOT_SET;
    private NodeId votedFor;
    private NodeId leaderId;

    public DefaultRoleState(RoleName roleName, int term) {
        this.roleName = roleName;
        this.term = term;
    }

    @Nonnull
    @Override
    public RoleName getRoleName() {
        return roleName;
    }

    @Override
    public int getTerm() {
        return term;
    }

    @Override
    public int getVotesCount() {
        return votesCount;
    }

    public void setVotesCount(int votesCount) {
        this.votesCount = votesCount;
    }

    @Nullable
    @Override
    public NodeId getVotedFor() {
        return votedFor;
    }

    public void setVotedFor(NodeId votedFor) {
        this.votedFor = votedFor;
    }

    @Nullable
    @Override
    public NodeId getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(NodeId leaderId) {
        this.leaderId = leaderId;
    }


    @Override
    public String toString() {
        switch (this.roleName) {
            case FOLLOWER:
                return "Follower{term}" + this.term + ", votedFor=" + this.votedFor + ", leaderId=" + this.leaderId + "}";
            case CANDIDATE:
                return "Candidate{term=" + this.term + ", votedCount=" + this.votesCount + "}";
            case LEADER:
                return "Leader{term=" + this.term + "}";
            default:
                throw new IllegalStateException("unexpected node role name [" + this.roleName + "]");
        }
    }

}