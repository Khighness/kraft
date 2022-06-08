package top.parak.kraft.core.rpc.message;

import top.parak.kraft.core.node.NodeEndpoint;

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
