package top.parak.kraft.core.rpc.message;

import top.parak.kraft.core.node.NodeEndpoint;

/**
 * RemoveServer RPC arguments.
 *
 * @author KHighness
 * @since 2022-05-24
 * @email parakovo@gmail.com
 */
public class RemoveServerRpc {

    private final NodeEndpoint oldServer;

    public RemoveServerRpc(NodeEndpoint oldServer) {
        this.oldServer = oldServer;
    }

    public NodeEndpoint getOldServer() {
        return oldServer;
    }

    @Override
    public String toString() {
        return "RemoveServerRpc{" +
                "oldServer=" + oldServer +
                '}';
    }

}
