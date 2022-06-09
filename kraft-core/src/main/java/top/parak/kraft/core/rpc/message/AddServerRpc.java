package top.parak.kraft.core.rpc.message;

import top.parak.kraft.core.node.NodeEndpoint;

/**
 * AddServer RPC arguments.
 *
 * @author KHighness
 * @since 2022-05-24
 * @email parakovo@gmail.com
 */
public class AddServerRpc {

    private final NodeEndpoint newServer;

    public AddServerRpc(NodeEndpoint newServer) {
        this.newServer = newServer;
    }

    public NodeEndpoint getNewServer() {
        return newServer;
    }

    @Override
    public String toString() {
        return "AddServerRpc{" +
                "newServer=" + newServer +
                '}';
    }

}
