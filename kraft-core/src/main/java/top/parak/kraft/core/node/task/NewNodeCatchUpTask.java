package top.parak.kraft.core.node.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.parak.kraft.core.node.NodeEndpoint;
import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.node.config.NodeConfig;
import top.parak.kraft.core.rpc.message.AppendEntriesResultMessage;
import top.parak.kraft.core.rpc.message.InstallSnapshotResultMessage;
import top.parak.kraft.core.rpc.message.InstallSnapshotRpc;

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

    synchronized void onReceiveAppendEntriesResult(AppendEntriesResultMessage resultMessage, int nextLogIndex) {
        assert nodeId.equals(resultMessage.getSourceNodeId());
        if (state != State.REPLICATING) {
            throw new IllegalStateException("receive append entries result when state is not replicating");
        }
        // initialize nextIndex
        if (nextIndex == 0) {
            nextIndex = nextLogIndex;
        }
        logger.debug("replication state of new node {}, next index {}, match index {}", nodeId, nextIndex, matchIndex);
        if (resultMessage.get().isSuccess()) {
            int lastEntryIndex = resultMessage.getRpc().getLastEntryIndex();
            assert lastEntryIndex >= 0;
            matchIndex = lastEntryIndex;
            nextIndex = lastEntryIndex + 1;
            lastAdvanceAt = System.currentTimeMillis();
            // finish catching up
            if (nextIndex >= nextLogIndex) {
                setStateANdNotify(State.REPLICATION_CATCH_UP);
                return;
            }
            // exceed max round
            if ((++round) > config.getNewNodeMaxRound()) {
                logger.info("node {} cannot catch up within max round", nodeId);
                setStateANdNotify(State.TIMEOUT);
                return;
            }
        } else {
            // cannot continue to catch up
            if (nextIndex <= 1) {
                logger.warn("node {} cannot back off next index more, stop replication", nodeId);
                setStateANdNotify(State.REPLICATION_FAILED);
                return;
            }
            nextIndex--;
            // slow network
            if (System.currentTimeMillis() - lastAdvanceAt >= config.getNewNodeAdvanceTimeout()) {
                logger.debug("node {} cannot make progress within timeout", nodeId);
                setStateANdNotify(State.TIMEOUT);
                return;
            }
        }
        context.doReplicateLog(endpoint, nextIndex);
        lastReplicatedAt = System.currentTimeMillis();
        notify();
    }

    synchronized void onReceiveInstallSnapshotResult(InstallSnapshotResultMessage resultMessage, int nextLogIndex) {
        assert nodeId.equals(resultMessage.getSourceNodeId());
        if (state != State.REPLICATING) {
            throw new IllegalStateException("receive append entries result when state is not replicating");
        }
        InstallSnapshotRpc rpc = resultMessage.getRpc();
        if (rpc.isDone()) {
            matchIndex = rpc.getLastIndex();
            nextIndex = rpc.getLastIndex() + 1;
            lastAdvanceAt = System.currentTimeMillis();
            if (nextIndex >= nextLogIndex) {
                setStateANdNotify(State.REPLICATION_CATCH_UP);
                return;
            }
            round++;
            context.doReplicateLog(endpoint, nextIndex);
        } else {
            context.sendInstallSnapshot(endpoint, rpc.getOffset() + rpc.getDataLength());
        }
        lastReplicatedAt = System.currentTimeMillis();
        notify();
    }

    private void setStateANdNotify(State state) {
        setState(state);
        done = true;
        notify();
    }

    @Override
    public String toString() {
        return "NewNodeCatchUpTask{" +
                "state=" + state +
                ", endpoint=" + endpoint +
                ", done=" + done +
                ", lastReplicatedAt=" + lastReplicatedAt +
                ", lastAdvanceAt=" + lastAdvanceAt +
                ", round=" + round +
                ", nextIndex=" + nextIndex +
                ", matchIndex=" + matchIndex +
                '}';
    }

}
