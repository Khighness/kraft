package top.parak.kraft.core.rpc.message;

import top.parak.kraft.core.node.NodeId;

/**
 * Message constants.
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public class MessageConstants {

    /**
     * Message type of {@link NodeId}.
     */
    public static final int MSG_TYPE_NODE_ID = 0;
    /**
     * Message type of {@link RequestVoteRpc}.
     */
    public static final int MSG_TYPE_REQUEST_VOTE_RPC = 1;
    /**
     * Message type of {@link RequestVoteResult}.
     */
    public static final int MSG_TYPE_REQUEST_VOTE_RESULT = 2;
    /**
     * Message type of {@link AppendEntriesRpc}.
     */
    public static final int MSG_TYPE_APPEND_ENTRIES_RPC = 3;
    /**
     * Message type of {@link AppendEntriesResult}.
     */
    public static final int MSG_TYPE_APPEND_ENTRIES_RESULT = 4;
    /**
     * Message type of {@link InstallSnapshotRpc}.
     */
    public static final int MSG_TYPE_INSTALL_SNAPSHOT_PRC = 5;
    /**
     * Message type of {@link InstallSnapshotResult}.
     */
    public static final int MSG_TYPE_INSTALL_SNAPSHOT_RESULT = 6;

}
