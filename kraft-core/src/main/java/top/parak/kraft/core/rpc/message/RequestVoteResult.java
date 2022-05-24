package top.parak.kraft.core.rpc.message;

import java.io.Serializable;

/**
 * RequestVote RPC results.
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public class RequestVoteResult implements Serializable {

    /**
     * Current term.
     */
    private int term;

    /**
     * If vote for candidate.
     */
    private boolean votedGranted;

    public RequestVoteResult(int term, boolean votedGranted) {
        this.term = term;
        this.votedGranted = votedGranted;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public boolean isVotedGranted() {
        return votedGranted;
    }

    public void setVotedGranted(boolean votedGranted) {
        this.votedGranted = votedGranted;
    }

    @Override
    public String toString() {
        return "RequestVoteResult{" +
                "term=" + term +
                ", votedGranted=" + votedGranted +
                '}';
    }

}
