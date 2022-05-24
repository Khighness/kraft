package top.parak.kraft.core.rpc.message;

import top.parak.kraft.core.log.entry.Entry;
import top.parak.kraft.core.node.NodeId;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * AppendEntries RPC arguments.
 * <p>Invoker: Leader</p>
 * <p>Receiver: Follower & Candidate</p>
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public class AppendEntriesRpc implements Serializable {

    /**
     * The id of message.
     */
    private String messageId;
    /**
     * The term of leader.
     */
    private int term;
    /**
     * The id of leader.
     */
    private NodeId leaderId;
    /**
     * The index of the log entry immediately preceding new ones.
     */
    private int prevLogIndex = 0;
    /**
     * The term of the previous log entry.
     */
    private int prevLogTerm;
    /**
     * The log entries to store.
     * <p>Empty for heartbeat</p>
     * <p>May send more than one for efficiency.</p>
     */
    private List<Entry> entries = Collections.emptyList();
    /**
     * The commitIndex of leader.
     */
    private int leaderCommit;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public NodeId getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(NodeId leaderId) {
        this.leaderId = leaderId;
    }

    public int getPrevLogIndex() {
        return prevLogIndex;
    }

    public void setPrevLogIndex(int prevLogIndex) {
        this.prevLogIndex = prevLogIndex;
    }

    public int getPrevLogTerm() {
        return prevLogTerm;
    }

    public void setPrevLogTerm(int prevLogTerm) {
        this.prevLogTerm = prevLogTerm;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public int getLeaderCommit() {
        return leaderCommit;
    }

    public void setLeaderCommit(int leaderCommit) {
        this.leaderCommit = leaderCommit;
    }

    @Override
    public String toString() {
        return "AppendEntriesRpc{" +
                "messageId='" + messageId + '\'' +
                ", term=" + term +
                ", leaderId=" + leaderId +
                ", prevLogIndex=" + prevLogIndex +
                ", prevLogTerm=" + prevLogTerm +
                ", entries.size=" + entries.size() +
                ", leaderCommit=" + leaderCommit +
                '}';
    }

}
