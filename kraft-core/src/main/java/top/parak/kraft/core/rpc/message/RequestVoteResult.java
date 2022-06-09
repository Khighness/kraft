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

    private int term;
    private boolean voteGranted;

    public RequestVoteResult(int term, boolean voteGranted) {
        this.term = term;
        this.voteGranted = voteGranted;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public boolean isVoteGranted() {
        return voteGranted;
    }

    public void setVoteGranted(boolean voteGranted) {
        this.voteGranted = voteGranted;
    }

    @Override
    public String toString() {
        return "RequestVoteResult{" + "term=" + term +
                ", voteGranted=" + voteGranted +
                '}';
    }
}
