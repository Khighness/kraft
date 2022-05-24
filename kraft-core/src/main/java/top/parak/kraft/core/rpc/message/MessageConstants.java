package top.parak.kraft.core.rpc.message;

/**
 * Message constants.
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public class MessageConstants {

    public static final int MSG_TYPE_NODE_ID = 0;
    public static final int MSG_TYPE_REQUEST_VOTE_RPC = 1;
    public static final int MSG_TYPE_REQUEST_VOTE_RESULT = 2;
    public static final int MSG_TYPE_REQUEST_APPEND_ENTRIES_RPC = 3;
    public static final int MSG_TYPE_REQUEST_APPEND_ENTRIES_RESULT = 4;
    public static final int MSG_TYPE_REQUEST_INSTALL_SNAPSHOT_RPC = 5;
    public static final int MSG_TYPE_REQUEST_INSTALL_SNAPSHOT_RESULT = 6;

}
