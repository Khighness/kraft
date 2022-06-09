package top.parak.kraft.core.rpc.message;

import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.rpc.Channel;

import javax.annotation.Nullable;

/**
 * InstallSnapshotRpc message.
 *
 * @author KHighness
 * @since 2022-04-13
 * @email parakovo@gmail.com
 */
public class InstallSnapshotRpcMessage extends AbstractRpcMessage<InstallSnapshotRpc> {

    public InstallSnapshotRpcMessage(InstallSnapshotRpc rpc, NodeId sourceNodeId, @Nullable Channel channel) {
        super(rpc, sourceNodeId, channel);
    }

}
