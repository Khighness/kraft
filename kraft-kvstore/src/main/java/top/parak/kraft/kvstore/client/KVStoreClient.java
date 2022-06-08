package top.parak.kraft.kvstore.client;

import top.parak.kraft.core.service.AddNodeCommand;
import top.parak.kraft.core.service.RemoveNodeCommand;
import top.parak.kraft.core.service.ServerRouter;
import top.parak.kraft.kvstore.message.GetCommand;
import top.parak.kraft.kvstore.message.SetCommand;

/**
 * KV-store client.
 *
 * @author KHighness
 * @since 2022-05-29
 * @email parakovo@gmail.com
 */
public class KVStoreClient {

    public static final String VERSION = "1.0.0";

    /**
     * Server router.
     */
    private final ServerRouter serverRouter;

    /**
     * Create KVStoreClient.
     *
     * @param serverRouter server router
     */
    public KVStoreClient(ServerRouter serverRouter) {
        this.serverRouter = serverRouter;
    }

    /**
     * Add node.
     *
     * @param nodeId        node's id
     * @param host          node's host
     * @param raftRpcPort   node's raft rpc port
     */
    public void addNode(String nodeId, String host, int raftRpcPort) {
        serverRouter.send(new AddNodeCommand(nodeId, host, raftRpcPort));
    }

    /**
     * Remove node.
     *
     * @param nodeId node's id
     */
    public void removeNode(String nodeId) {
        serverRouter.send(new RemoveNodeCommand(nodeId));
    }

    /**
     * Set key:value.
     *
     * @param key   key
     * @param value value
     */
    public void set(String key, byte[] value) {
        serverRouter.send(new SetCommand(key, value));
    }

    /**
     * Get value by key.
     *
     * @param key key
     * @return value
     */
    public byte[] get(String key) {
        return (byte[]) serverRouter.send(new GetCommand(key));
    }

    /**
     * Get server router.
     *
     * @return server router
     */
    public ServerRouter getServerRouter() {
        return serverRouter;
    }

}
