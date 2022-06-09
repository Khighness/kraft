package top.parak.kraft.core.node.role;

import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.schedule.ElectionTimeout;

import javax.annotation.concurrent.Immutable;

/**
 * Candidate node role.
 *
 * @author KHighness
 * @since 2022-03-19
 * @email parakovo@gmail.com
 */
@Immutable
public class CandidateNodeRole extends AbstractNodeRole {

    private final int votesCount;
    private final ElectionTimeout electionTimeout;

    /**
     * Create CandidateNodeRole.
     * <p>Default votes count is 1.</p>
     *
     * @param term            term
     * @param electionTimeout election timeout
     */
    public CandidateNodeRole(int term, ElectionTimeout electionTimeout) {
        this(term, 1, electionTimeout);
    }

    /**
     * Create CandidateNodeRole.
     *
     * @param term            term
     * @param votesCount      voted count
     * @param electionTimeout election timeout
     */
    public CandidateNodeRole(int term, int votesCount, ElectionTimeout electionTimeout) {
        super(RoleName.CANDIDATE, term);
        this.votesCount = votesCount;
        this.electionTimeout = electionTimeout;
    }

    /**
     * Get votes count.
     *
     * @return votes count
     */
    public int getVotesCount() {
        return votesCount;
    }

    @Override
    public NodeId getLeaderId(NodeId selfId) {
        return null;
    }

    @Override
    public void cancelTimeoutOrTask() {
        electionTimeout.cancel();
    }

    @Override
    public RoleState getState() {
        DefaultRoleState state = new DefaultRoleState(RoleName.CANDIDATE, term);
        state.setVotesCount(votesCount);
        return state;
    }

    @Override
    protected boolean doStateEquals(AbstractNodeRole role) {
        CandidateNodeRole that = (CandidateNodeRole) role;
        return this.votesCount == that.votesCount;
    }

    @Override
    public String toString() {
        return "CandidateNodeRole{" +
                "term=" + term +
                ", votesCount=" + votesCount +
                ", electionTimeout=" + electionTimeout +
                '}';
    }
}
