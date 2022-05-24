package top.parak.kraft.core.rpc;

import top.parak.kraft.core.node.NodeEndpoint;
import top.parak.kraft.core.rpc.message.*;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Connector.
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public interface Connector {

    /**
     * Initialize the connector.
     * <p>
     * Should not call more than once.
     * </p>
     */
    void initialize();

    /**
     * Send RequestVote RPC.
     * <p>
     * Remember to exclude self node before sending.
     * </p>
     * <p>
     * Do nothing if destination endpoints is empty.
     * </p>
     *
     * @param rpc                  rpc
     * @param destinationEndpoints destination endpoints
     */
    void sendRequestVote(@Nonnull RequestVoteRpc rpc, @Nonnull Collection<NodeEndpoint> destinationEndpoints);

    /**
     * Reply RequestVote result.
     *
     * @param result     result
     * @param rpcMessage rpc message
     */
    void replyRequestVote(@Nonnull RequestVoteResult result, @Nonnull RequestVoteRpcMessage rpcMessage);

    /**
     * Send AppendEntries RPC.
     *
     * @param rpc                 rpc
     * @param destinationEndpoint destination endpoint
     */
    void sendAppendEntries(@Nonnull AppendEntriesRpc rpc, @Nonnull NodeEndpoint destinationEndpoint);

    /**
     * Reply AppendEntries result.
     *
     * @param result     result
     * @param rpcMessage rpc message
     */
    void replyAppendEntries(@Nonnull AppendEntriesResult result, @Nonnull AppendEntriesRpcMessage rpcMessage);

    /**
     * Send InstallSnapshot RPC.
     *
     * @param rpc                 rpc
     * @param destinationEndpoint destination endpoint
     */
    void sendInstallSnapshot(@Nonnull InstallSnapshotRpc rpc, @Nonnull NodeEndpoint destinationEndpoint);

    /**
     * Reply InstallSnapshot result.
     *
     * @param result     result
     * @param rpcMessage rpc message
     */
    void replyInstallSnapshot(@Nonnull InstallSnapshotResult result, @Nonnull InstallSnapshotRpcMessage rpcMessage);

    /**
     * Called when node becomes leader.
     * <p>
     * Connector may use this chance yo close inbound channels.
     * </p>
     */
    void resetChannels();

    /**
     * Close the connector.
     */
    void close();

}
