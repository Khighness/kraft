package top.parak.kraft.core.log;

import top.parak.kraft.core.log.entry.*;
import top.parak.kraft.core.log.statemachine.StateMachine;
import top.parak.kraft.core.node.NodeEndpoint;
import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.rpc.message.AppendEntriesRpc;
import top.parak.kraft.core.rpc.message.InstallSnapshotRpc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * Log.
 *
 * @author KHighness
 * @since 2022-03-19
 * @email parakovo@gmail.com
 */
public interface Log {

    int ALL_ENTRIES = -1;

    /**
     * Get the metadata of the last log entry.
     *
     * @return the metadata of the last log entry
     */
    @Nonnull
    EntryMeta getLastEntryMeta();

    /**
     * Create rpc request to append log entries from log.
     *
     * @param term       current term
     * @param selfId     self node id
     * @param nextIndex  next index
     * @param maxEntries max entries
     * @return rpc request to append log entries
     */
    AppendEntriesRpc createAppendEntriesRpc(int term, NodeId selfId, int nextIndex, int maxEntries);

    /**
     * Create rpc request to install snapshot from log.
     *
     * @param term    current term
     * @param selfId  self node id
     * @param offset  data offset
     * @param length  data length
     * @return rpc request to install snapshot
     */
    InstallSnapshotRpc createInstallSnapshotRpc(int term, NodeId selfId, int offset, int length);

    /**
     * Get the last uncommitted group config entry.
     *
     * @return the last uncommitted group config entry, maybe {@code null}
     */
    @Nullable
    GroupConfigEntry getLastUncommittedGroupConfigEntry();

    /**
     * Get next log index.
     *
     * @return next log index
     */
    int getNextIndex();

    /**
     * Get commit index.
     *
     * @return commit index
     */
    int getCommitIndex();

    /**
     * Return if the candidate's last log entry is newer than the leader's last log entry.
     *
     * @param lastLogIndex the index of the leader's last log entry
     * @param lastLogTerm  the term of the leader's last log entry
     * @return true if the candidate's last log entry is newer than the leader's last log, otherwise false
     */
    boolean isNewerThan(int lastLogIndex, int lastLogTerm);

    /**
     * Append a no-operation log entry.
     *
     * @param term current term
     * @return no-operation log entry
     */
    NoOpEntry appendEntry(int term);

    /**
     * Append a general log entry.
     *
     * @param term    current term
     * @param command command in bytes
     * @return general log entry
     */
    GeneralEntry appendEntry(int term, byte[] command);

    /**
     * Append a log entry for adding node.
     *
     * @param term            current term
     * @param nodeEndpoints   current node configs
     * @param newNodeEndpoint new node config
     * @return add node entry
     */
    AddNodeEntry appendEntryForAddNode(int term, Set<NodeEndpoint> nodeEndpoints, NodeEndpoint newNodeEndpoint);

    /**
     * Append a log entry for removing node.
     *
     * @param term          current term
     * @param nodeEndpoints current node configs
     * @param nodeToRemove  id of node to be removed
     * @return log entry for removing node
     */
    RemoveNodeEntry appendEntryForRemoveNode(int term, Set<NodeEndpoint> nodeEndpoints, NodeId nodeToRemove);

    /**
     * Append log entries to log.
     *
     * @param prevLogIndex  expected index of previous log entry
     * @param prevLogTerm   expected term of previous log entry
     * @param leaderEntries log entries from leader to be appended
     * @return true if succeeded, false if previous log check failed
     */
    boolean appendEntriesFromLeader(int prevLogIndex, int prevLogTerm, List<Entry> leaderEntries);

    /**
     * Advance commitIndex.
     * <p>
     * The log entry with new {@code commitIndex} must be the same term sa the one in parameter,
     * otherwise commit index will not change.
     * </p>
     *
     * @param newCommitIndex new commitIndex
     * @param currentTerm    current term
     */
    void advanceCommitIndex(int newCommitIndex, int currentTerm);

    /**
     * Install snapshot.
     *
     * @param rpc rpc
     * @return install snapshot state
     */
    InstallSnapshotState installSnapshot(InstallSnapshotRpc rpc);

    /**
     * Generate snapshot.
     *
     * @param lastIncludedIndex last included index
     * @param groupConfig       group config
     */
    void generateSnapshot(int lastIncludedIndex, Set<NodeEndpoint> groupConfig);

    /**
     * Set state machine.
     * <p>
     * It will be called in the following cases
     * </p>
     * <ul>
     * <li>apply the log entry</li>
     * <li>generate snapshot</li>
     * <li>apply snapshot</li>
     * </ul>
     *
     * @param stateMachine state machine
     */
    void setStateMachine(StateMachine stateMachine);

    /**
     * Close log files.
     */
    void close();

}
