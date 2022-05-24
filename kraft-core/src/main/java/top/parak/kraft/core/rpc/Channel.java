package top.parak.kraft.core.rpc;

import top.parak.kraft.core.rpc.message.*;

import javax.annotation.Nonnull;

/**
 * Channel between nodes.
 *
 * @author KHighness
 * @since 2022-03-18
 * @email parakovo@gmail.com
 */
public interface Channel {

    /**
     * Write RequestVote RPC.
     *
     * @param rpc rpc
     */
    void writeRequestVoteRpc(@Nonnull RequestVoteRpc rpc);

    /**
     * Write RequestVote result.
     *
     * @param result result
     */
    void writeRequestVoteResult(@Nonnull RequestVoteResult result);

    /**
     * Write AppendEntries RPC.
     *
     * @param rpc rpc
     */
    void writeAppendEntriesRpc(@Nonnull AppendEntriesRpc rpc);

    /**
     * Write AppendEntries result.
     *
     * @param result result
     */
    void writeAppendEntriesResult(@Nonnull AppendEntriesResult result);

    /**
     * Write InstallSnapshot RPC.
     *
     * @param rpc rpc
     */
    void writeInstallSnapshotRpc(@Nonnull InstallSnapshotRpc rpc);

    /**
     * Write InstallSnapshot result.
     *
     * @param result result
     */
    void writeInstallSnapshotResult(@Nonnull InstallSnapshotResult result);

    /**
     * Close the channel.
     */
    void close();

}
