package top.parak.kraft.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.parak.kraft.core.node.NodeId;

import java.util.*;

/**
 * Server router.
 *
 * @author KHighness
 * @since 2022-05-01
 * @email parakovo@gmail.com
 */
public class ServerRouter {

    private static Logger logger = LoggerFactory.getLogger(ServerRouter.class);
    private final Map<NodeId, Channel> availableServers = new HashMap<>();
    private NodeId leaderId;

    /**
     * Send message to server.
     *
     * @param message message
     * @return result
     * @throws NoAvailableServerException if no available server
     */
    public Object send(Object message) {
        for (NodeId nodeId : getCandidateNodeIds()) {
            try {
                Object result = doSend(nodeId, message);
                this.leaderId = nodeId;
                return result;
            } catch (RedirectException e) {
                logger.debug("not a leader server, redirect to server {}", e.getLeaderId());
                this.leaderId = e.getLeaderId();
                return doSend(e.getLeaderId(), message);
            } catch (Exception e) {
                logger.debug("failed to process with server " + nodeId + ", cause " + e.getMessage());
            }
        }
        throw new NoAvailableServerException("no available server");
    }

    private Collection<NodeId> getCandidateNodeIds() {
        if (availableServers.isEmpty()) {
            throw new NoAvailableServerException("no available server");
        }

        if (leaderId != null) {
            List<NodeId> nodeIds = new ArrayList<>();
            nodeIds.add(leaderId);
            for (NodeId nodeId : availableServers.keySet()) {
                if (!nodeId.equals(leaderId)) {
                    nodeIds.add(nodeId);
                }
            }
            return nodeIds;
        }

        return availableServers.keySet();
    }

    private Object doSend(NodeId id, Object payload) {
        Channel channel = this.availableServers.get(id);
        if (channel == null) {
            throw new IllegalStateException("no such channel to server " + id);
        }
        logger.debug("send request to server {}", id);
        return channel.send(payload);
    }

    /**
     * Add a socket channel to server without creating connection.
     * The socket is created when client sends message to server.
     *
     * @param id      node id
     * @param channel channel
     */
    public void add(NodeId id, Channel channel) {
        this.availableServers.put(id, channel);
    }

    /**
     * Get leader id.
     *
     * @return leader id.
     */
    public NodeId getLeaderId() {
        return leaderId;
    }

    /**
     * Set leader id.
     *
     * @param leaderId leader id
     */
    public void setLeaderId(NodeId leaderId) {
        if (!availableServers.containsKey(leaderId)) {
            throw new IllegalStateException("no such server [" + leaderId + "] in list");
        }
        this.leaderId = leaderId;
    }

}
