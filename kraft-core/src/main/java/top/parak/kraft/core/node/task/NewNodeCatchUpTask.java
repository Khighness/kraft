package top.parak.kraft.core.node.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.parak.kraft.core.node.NodeEndpoint;
import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.node.config.NodeConfig;

import java.util.concurrent.Callable;

/**
 * New node catch up task.
 *
 * @author KHighness
 * @since 2022-06-02
 * @email parakovo@gmail.com
 */
public class NewNodeCatchUpTask implements Callable<NewNodeCatchUpTaskResult> {

    private enum State {
        START,
        REPLICATING,
        REPLICATION_FAILED,
        REPLICATION_CATCH_UP,
        TIMEOUT,
    }

    private static final Logger logger = LoggerFactory.getLogger(NewNodeCatchUpTask.class);
    private final NewNodeCatchUpTaskContext context;
    private final NodeEndpoint endpoint;
    private final NodeId nodeId;
    private final NodeConfig config;
    private State state = State.START;
    private boolean done = false;
    private long lastReplicatedAt;
    private long lastAdvanceAt;
    private int round = 1;
    private int nextIndex = 0;
    private int matchIndex = 0;

    public NewNodeCatchUpTask(NewNodeCatchUpTaskContext context, NodeEndpoint endpoint, NodeConfig config) {
        this.context = context;
        this.endpoint = endpoint;
        this.nodeId = endpoint.getId();
        this.config = config;
    }

    public NodeId getNodeId() {
        return nodeId;
    }

    @Override
    public NewNodeCatchUpTaskResult call() throws Exception {
        logger.debug("task start");
        setState(State.START);
        context.replicateLog(endpoint);
        lastReplicatedAt = System.currentTimeMillis();
        lastAdvanceAt = lastReplicatedAt;
        setState(State.REPLICATING);
        while (!done) {
            wait(config.getNewNodeReadTimeout());
            // 1. done
            // 2. replicate -> no response within timeout
            if (System.currentTimeMillis() - lastReplicatedAt >= config.getNewNodeReadTimeout()) {
                logger.debug("node {} does not response within read timeout", endpoint.getId());
                state = State.TIMEOUT;
                break;
            }
        }
        logger.debug("task done");
        context.done(this);
        return null;
    }

    private NewNodeCatchUpTaskResult mapResult(State state) {
        switch (state) {
            case REPLICATION_CATCH_UP:
                return new NewNodeCatchUpTaskResult(nextIndex, matchIndex);
            case REPLICATION_FAILED:
                return new NewNodeCatchUpTaskResult(NewNodeCatchUpTaskResult.State.REPLICATED_FAILED);
            default:
                return new NewNodeCatchUpTaskResult(NewNodeCatchUpTaskResult.State.TIMEOUT);
        }
    }

    private void setState(State state) {
        logger.debug("state -> {}", state);
        this.state = state;
    }

}
