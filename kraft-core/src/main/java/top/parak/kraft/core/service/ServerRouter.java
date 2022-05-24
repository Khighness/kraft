package top.parak.kraft.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.parak.kraft.core.node.NodeId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

    private Object send(Object payload) {
        Collection<NodeId> candidateNodeIds = getCandidateNodeIds();
        return null;
    }

    private Collection<NodeId> getCandidateNodeIds() {
        return null;
    }
}
