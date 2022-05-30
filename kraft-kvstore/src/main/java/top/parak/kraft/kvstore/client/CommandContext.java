package top.parak.kraft.kvstore.client;

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
            serverRouter.add(nodeId, new KVStoreClientSocketChannel(address.getHost(), address.getPort()));
        }
        return serverRouter;
    }

    /**
     * Get KVStoreClient.
     *
     * @return KVStoreClient
     */
    public KVStoreClient getClient() {
        return client;
    }

    /**
     * Get leader's id.
     *
     * @return leader's id
     */
    public NodeId getClientLeaderId() {
        return client.getServerRouter().getLeaderId();
    }

    /**
     * Get leader.
     *
     * @return leader
     */
    public NodeId getClientLeader() {
        return client.getServerRouter().getLeaderId();
    }

    /**
     * Set leader.
     *
     * @param nodeId leader's id.
     */
    public void setClientLeader(NodeId nodeId) {
        client.getServerRouter().setLeaderId(nodeId);
    }

    /**
     * Add KVStoreServer.
     *
     * @param nodeId node's id
     * @param host   node's host
     * @param servicePort node's KV service port
     */
    public void clientAddServer(String nodeId, String host, int servicePort) {
        serverMap.put(new NodeId(nodeId), new Address(host, servicePort));
        client = new KVStoreClient(buildServerRouter(serverMap));
    }

    /**
     * Remove KVStoreServer.
     *
     * @param nodeId node's id.
     * @return true if remove node successfully, otherwise node does not exist
     */
    public boolean clientRemoveServer(String nodeId) {
        Address address = serverMap.remove(new NodeId(nodeId));
        if (address != null) {
            client = new KVStoreClient(buildServerRouter(serverMap));
            return true;
        }
        return false;
    }

    /**
     * Set running status.
     *
     * @param running running
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * Get running status
     *
     * @return running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Print server list.
     */
    public void printServerList() {
        for (NodeId nodeId : serverMap.keySet()) {
            Address address = serverMap.get(nodeId);
            System.out.println(nodeId + "," + address.getHost() + "," + address.getPort());
        }
    }

}
