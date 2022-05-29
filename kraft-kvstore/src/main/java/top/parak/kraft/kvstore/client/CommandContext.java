package top.parak.kraft.kvstore.client;

import ch.qos.logback.core.net.server.Client;
import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.rpc.Address;
import top.parak.kraft.core.service.ServerRouter;

import java.util.Map;

/**
 * Command context.
 *
 * @author KHighness
 * @since 2022-05-29
 * @email parakovo@gmail.com
 */
public class CommandContext {

    private final Map<NodeId, Address> serverMap;
    private KVStoreClient client;
    private boolean running = false;

    /**
     * Create CommandContext.
     *
     * @param serverMap server map
     */
    public CommandContext(Map<NodeId, Address> serverMap) {
        this.serverMap = serverMap;
        this.client = new KVStoreClient(buildServerRouter(serverMap));
    }

    private ServerRouter buildServerRouter(Map<NodeId, Address> serverMap) {
        ServerRouter serverRouter = new ServerRouter();
        for (NodeId nodeId : serverMap.keySet()) {
            Address address = serverMap.get(nodeId);
            serverRouter.add(nodeId, new SocketChannel(address.getHost(), address.getPort()));
        }
        return serverRouter;
    }

    public KVStoreClient getClient() {
        return client;
    }

    public NodeId getClientLeaderId() {
        return client.getServerRouter().getLeaderId();
    }

    public NodeId getClientLeader() {
        return client.getServerRouter().getLeaderId();
    }

    public void setClientLeader(NodeId nodeId) {
        client.getServerRouter().setLeaderId(nodeId);
    }

    public void clientAddServer(String nodeId, String host, int port) {
        serverMap.put(new NodeId(nodeId), new Address(host, port));
        client = new KVStoreClient(buildServerRouter(serverMap));
    }

    public boolean clientRemoveServer(String nodeId) {
        Address address = serverMap.remove(new NodeId(nodeId));
        if (address != null) {
            client = new KVStoreClient(buildServerRouter(serverMap));
            return true;
        }
        return false;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isRunning() {
        return running;
    }

    public void printServerList() {
        for (NodeId nodeId : serverMap.keySet()) {
            Address address = serverMap.get(nodeId);
            System.out.println(nodeId + "," + address.getHost() + "," + address.getPort());
        }
    }
}
