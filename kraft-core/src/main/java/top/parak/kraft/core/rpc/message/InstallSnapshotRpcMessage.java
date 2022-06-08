package top.parak.kraft.core.rpc.message;

import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.rpc.Channel;

import javax.annotation.Nullable;

public class InstallSnapshotRpcMessage extends AbstractRpcMessage<InstallSnapshotRpc> {

    public InstallSnapshotRpcMessage(InstallSnapshotRpc rpc, NodeId sourceNodeId, @Nullable Channel channel) {
        super(rpc, sourceNodeId, channel);
    }

}
