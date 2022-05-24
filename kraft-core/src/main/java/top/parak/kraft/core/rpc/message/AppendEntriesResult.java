package top.parak.kraft.core.rpc.message;

import java.io.Serializable;

/**
 * AppendEntries RPC result.
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public class AppendEntriesResult implements Serializable {

    /**
     * RPC message id.
     */
    private final String rpcMessageId;
    /**
     * Current term.
     * <p>Help leader update term.</p>
     */
    private final int term;
    /**
     * If the follower has appended the log entries or already has the log entries.
     */
    private final boolean success;

    public AppendEntriesResult(String rpcMessageId, int term, boolean success) {
        this.rpcMessageId = rpcMessageId;
        this.term = term;
        this.success = success;
    }

    public String getRpcMessageId() {
        return rpcMessageId;
    }

    public int getTerm() {
        return term;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "AppendEntriesResult{" +
                "rpcMessageId='" + rpcMessageId + '\'' +
                ", term=" + term +
                ", success=" + success +
                '}';
    }

}
