package top.parak.kraft.core.node.role;

import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.schedule.ElectionTimeout;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * Follower node role.
 *
 * @author KHighness
 * @since 2022-03-18
 * @email parakovo@gmail.com
 */
@Immutable
public class FollowerNodeRole extends AbstractNodeRole {

    private final NodeId votedFor;
    private final NodeId leaderId;
    private final ElectionTimeout electionTimeout;

    /**
     * Create FollowerNodeRole.
     *
     * @param term            term
     * @param votedFor        voted for
     * @param leaderId        leader id
     * @param electionTimeout election timeout
     */
    public FollowerNodeRole(int term, NodeId votedFor, NodeId leaderId, ElectionTimeout electionTimeout) {
        super(RoleName.FOLLOWER, term);
        this.votedFor = votedFor;
        this.leaderId = leaderId;
        this.electionTimeout = electionTimeout;
    }

    /**
     * Get voted for.
     *
     * @return voted for
     */
    public NodeId getVotedFor() {
        return votedFor;
    }

    /**
     * Get leader id.
     *
     * @return leader id
     */
    public NodeId getLeaderId() {
        return leaderId;
    }

    @Override
    public NodeId getLeaderId(NodeId selfId) {
        return leaderId;
    }

    @Override
    public void cancelTimeoutOrTask() {
        electionTimeout.cancel();
    }

    @Override
    public RoleState getState() {
        DefaultRoleState state = new DefaultRoleState(RoleName.FOLLOWER, term);
        state.setVotedFor(votedFor);
        state.setLeaderId(leaderId);
        return state;
    }

    @Override
    protected boolean doStateEquals(AbstractNodeRole role) {
        FollowerNodeRole that = (FollowerNodeRole) role;
        return Objects.equals(this.votedFor, that.votedFor) && Objects.equals(this.leaderId, that.leaderId);
    }

    @Override
    public String toString() {
        return "FollowerNodeRole{" +
                "term=" + term +
                ", leaderId=" + leaderId +
                ", votedFor=" + votedFor +
                ", electionTimeout=" + electionTimeout +
                '}';
    }
}
