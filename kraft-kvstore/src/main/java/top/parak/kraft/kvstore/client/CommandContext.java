package top.parak.kraft.kvstore.client;

import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.rpc.Address;
import top.parak.kraft.core.service.ServerRouter;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Command context.
 *
 * @author KHighness
 * @since 2022-05-29
 * @email parakovo@gmail.com
 */
public class CommandContext {

    /**
     * Map to store server' id and address.
     * <p>
     * Key: {@link NodeId}, Value: {@link Address}.
     * </p>
     */
    private final Map<NodeId, Address> serverMap;
    /**
     * KV-store client.
     */
    private KVStoreClient client;
    /**
     * Running status.
     */
    private boolean running = false;

    /**
     * Command context.
     *
     * @author KHighness
     * @since 2022-05-29
     * @email parakovo@gmail.com
     */
    public CommandContext(Map<NodeId, Address> serverMap) {
        this.serverMap = serverMap;
        this.client = new KVStoreClient(buildServerRouter(serverMap));
    }

    /**
     * Build server router from server map.
     *
     * @param serverMap server map
     * @return server router
     */
    private ServerRouter buildServerRouter(Map<NodeId, Address> serverMap) {
        ServerRouter router = new ServerRouter();
        for (NodeId nodeId : serverMap.keySet()) {
            Address address = serverMap.get(nodeId);
            router.add(nodeId, new KVStoreClientSocketChannel(
                    new InetSocketAddress(address.getHost(), address.getPort()))
            );
        }
        return router;
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
     * Set leader's id.
     *
     * @param nodeId leader's id.
     */
    public void setClientLeaderId(NodeId nodeId) {
        client.getServerRouter().setLeaderId(nodeId);
    }

    /**
     * Add KVStoreServer.
     *
     * @param nodeId      node's id
     * @param host        node's host
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
     * Get running status.
     *
     * @return running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Print server list.
     */
    public void printSeverList() {
        for (NodeId nodeId : serverMap.keySet()) {
            Address address = serverMap.get(nodeId);
            System.out.println(nodeId + "," + address.getHost() + "," + address.getPort());
        }
    }

}
