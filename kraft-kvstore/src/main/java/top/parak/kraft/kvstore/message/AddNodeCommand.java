package top.parak.kraft.kvstore.message;

import top.parak.kraft.core.node.NodeEndpoint;

/**
 * AddNodeCommand message.
 *
 * @author KHighness
 * @since 2022-06-09
 * @email parakovo@gmail.com
 */
public class AddNodeCommand {

    private final String nodeId;
    private final String host;
    private final int port;

    public AddNodeCommand(String nodeId, String host, int port) {
        this.nodeId = nodeId;
        this.host = host;
        this.port = port;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public NodeEndpoint toNodeEndpoint() {
        return new NodeEndpoint(nodeId, host, port);
    }

}
