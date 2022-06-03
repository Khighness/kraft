package top.parak.kraft.core.node;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.parak.kraft.core.log.entry.EntryMeta;
import top.parak.kraft.core.log.snapshot.EntryInSnapshotException;
import top.parak.kraft.core.log.statemachine.StateMachine;
import top.parak.kraft.core.node.role.*;
import top.parak.kraft.core.node.store.NodeStore;
import top.parak.kraft.core.node.task.*;
import top.parak.kraft.core.rpc.message.AppendEntriesRpc;
import top.parak.kraft.core.rpc.message.InstallSnapshotRpc;
import top.parak.kraft.core.rpc.message.RequestVoteRpc;
import top.parak.kraft.core.schedule.ElectionTimeout;
import top.parak.kraft.core.schedule.LogReplicationTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Node implementation.
 *
 * @author KHighness
 * @since 2022-05-28
 * @email parakovo@gmail.com
 */
@ThreadSafe
public class NodeImpl implements Node {

    private static final Logger logger = LoggerFactory.getLogger(NodeImpl.class);

    /**
     * Callback for async tasks。
     */
    private static final FutureCallback<Object> LOGGING_FUTURE_CALLBACK = new FutureCallback<Object>() {
        @Override
        public void onSuccess(@Nullable Object result) {
        }

        @Override
        public void onFailure(@Nonnull Throwable t) {
            logger.warn("failure", t);
        }
    };

    /**
     * Node context.
     */
    private final NodeContext context;
    /**
     * If start.
     */
    @GuardedBy("this")
    private boolean started;
    /**
     * Node role.
     */
    private volatile AbstractNodeRole role;
    /**
     * Node role listeners.
     */
    private final List<NodeRoleListener> roleListeners = new CopyOnWriteArrayList<>();

    private final NewNodeCatchUpTaskContext newNodeCatchUpTaskContext = new NewNodeCatchUpTaskContextImpl();
    private final NewNodeCatchUpTaskGroup newNodeCatchUpTaskGroup = new NewNodeCatchUpTaskGroup();
    private final GroupConfigChangeTaskContext groupConfigChangeTaskContext = new GroupConfigChangeTaskContextImpl();
    private volatile GroupConfigChangeTaskHolder groupConfigChangeTaskHolder = new GroupConfigChangeTaskHolder();

    /**
     * Create with context.
     *
     * @param context context
     */
    NodeImpl(NodeContext context) {
        this.context = context;
    }

    /**
     * Get context.
     *
     * @return context
     */
    NodeContext getContext() {
        return context;
    }

    @Override
    public synchronized void registerStateMachine(@Nonnull StateMachine stateMachine) {
        Preconditions.checkNotNull(stateMachine);
        context.log().setStateMachine(stateMachine);
    }

    @Nonnull
    @Override
    public RoleNameANdLeaderId getRoleNameANdLeaderId() {
        return role.getRoleNameANdLeaderId(context.selfId());
    }

    /**
     * Get role state.
     *
     * @return role state
     */
    @Nonnull
    RoleState getRoleState() {
        return role.getState();
    }

    @Override
    public void addNoeRoleListener(@Nonnull NodeRoleListener listener) {
        Preconditions.checkNotNull(listener);
        roleListeners.add(listener);
    }

    @Override
    public synchronized void start() {
        if (started) {
            return;
        }
        // initialize event-bus and connector
        context.eventBus().register(this);
        context.connector().initialize();

        // load term, votedFor from store and become follower
        NodeStore store = context.store();
        changeToRole(new FollowerNodeRole(store.getTerm(), store.getVotedFor(), null, scheduleElectionTimeout()));

        // set status
        started = true;
    }

    @Override
    public void appendLog(@Nonnull byte[] commandBytes) {
        Preconditions.checkNotNull(commandBytes);

    }

    @Override
    public GroupConfigChangeTaskReference addNode(@Nonnull NodeEndpoint endpoint) {
        return null;
    }

    @Override
    public GroupConfigChangeTaskReference removeNode(@Nonnull NodeId id) {
        return null;
    }

    @Override
    public void stop() throws InterruptedException {

    }

    /**
     * Change role.
     *
     * @param newRole new role
     */
    private void changeToRole(AbstractNodeRole newRole) {
        if (!isStableBetween(role, newRole)) {
            logger.debug("node {} role state changed {} -> {}", context.selfId(), role, newRole);
            RoleState state = newRole.getState();

            // update store
            NodeStore store = context.store();
            store.setTerm(state.getTerm());
            store.setVotedFor(state.getVotedFor());

            // notify listeners
            roleListeners.forEach(l -> l.nodeRoleChanged(state));
        }
        role = newRole;
    }

    private void becomeFollower(int term, NodeId votedFor, NodeId leaderId, boolean scheduleElectionTimeout) {

    }

    /**
     * Check if stable between two roles.
     * <p>
     * It is stable when role name does not change and role state except timout/task not change.
     * </p>
     * <p>
     * If role state except timeout/task does not change, it should update store or notify listeners.
     * </p>
     *
     * @param before role before
     * @param after  role after
     * @return true if stable, otherwise false
     * @see AbstractNodeRole#stateEquals(AbstractNodeRole)
     */
    private boolean isStableBetween(AbstractNodeRole before, AbstractNodeRole after) {
        assert after != null;
        return before != null && before.stateEquals(after);
    }

    /**
     * Schedule election timeout.
     *
     * @return election timeout
     */
    private ElectionTimeout scheduleElectionTimeout() {
        return context.scheduler().scheduleElectionTimeout(this::electionTimeout);
    }

    /**
     * Election timeout.
     * <p>
     * Source: scheduler.
     * </p>
     */
    private void electionTimeout() {
        context.taskExecutor().submit(this::doProcessElectionTimeout, LOGGING_FUTURE_CALLBACK);
    }

    /**
     * Process election timeout.
     */
    private void doProcessElectionTimeout() {
        if (role.getName() == RoleName.LEADER) {
            logger.warn("node {}, current role is leader, ignore election timeout", context.selfId());
            return;
        }

        // follower:  start election
        // candidate: restart election
        int newTerm = role.getTerm() + 1;
        role.cancelTimeoutOrTask();

        if (context.group().isStandalone()) {
            if (context.mode() == NodeMode.STANDBY) {
                logger.info("start with standby mode, skip election");
            } else {
                // become leader
                logger.info("become leader, term {}", newTerm);
                resetReplicatingStates();
                changeToRole(new LeaderNodeRole(newTerm, scheduleLogReplicationTask()));
                context.log().appendEntry(newTerm);
            }
        } else {
            logger.info("start election");
            changeToRole(new CandidateNodeRole(newTerm, scheduleElectionTimeout()));

            // request vote
            EntryMeta lastEntryMeta = context.log().getLastEntryMeta();
            RequestVoteRpc rpc = new RequestVoteRpc();
            rpc.setTerm(newTerm);
            rpc.setCandidateId(context.selfId());
            rpc.setLastLogIndex(lastEntryMeta.getIndex());
            rpc.setLastLogTerm(lastEntryMeta.getTerm());
            context.connector().sendRequestVote(rpc, context.group().listEndpointOfMajorExceptSelf());
        }
    }

    /**
     * Schedule log replication task.
     *
     * @return log replication task
     */
    private LogReplicationTask scheduleLogReplicationTask() {
        return context.scheduler().scheduleLogReplicationTask(this::replicateLog);
    }

    /**
     * Replicate log.
     * <p>
     * Source: scheduler.
     * </p>
     */
    private void replicateLog() {
        context.taskExecutor().submit(this::doReplicateLog, LOGGING_FUTURE_CALLBACK);
    }

    /**
     * Replicate log to other nodes.
     */
    private void doReplicateLog() {
        // just advance commit index if group is standalone
        if (context.group().isStandalone()) {
            context.log().advanceCommitIndex(context.log().getNextIndex() - 1, role.getTerm());
            return;
        }
        logger.debug("replicate log");
        for (GroupMember member : context.group().listReplicationTarget()) {
            if (member.shouldReplicate(context.config().getLogReplicationReadTimeout())) {
                doReplicateLog();
            }
        }
    }

    private void doReplicateLog(GroupMember member, int maxEntries) {
        member.replicatedNow();
        try {
            AppendEntriesRpc rpc = context.log().createAppendEntriesRpc(role.getTerm(), context.selfId(), member.getNextIndex(), maxEntries);
            context.connector().sendAppendEntries(rpc, member.getEndpoint());
        } catch (EntryInSnapshotException ignored) {
            logger.debug("log entry {} in snapshot, replicate with install snapshot rpc", member.getNextIndex());
            InstallSnapshotRpc rpc = context.log().createInstallSnapshotRpc(role.getTerm(), context.selfId(), 0, context.config().getSnapshotDataLength());
            context.connector().sendInstallSnapshot(rpc, member.getEndpoint());
        }
    }

    /**
     * Reset replicating states.
     */
    private void resetReplicatingStates() {
        context.group().resetReplicatingStates(context.log().getNextIndex());
    }


    private class NewNodeCatchUpTaskContextImpl implements NewNodeCatchUpTaskContext {

        @Override
        public void replicateLog(NodeEndpoint endpoint) {
            context.taskExecutor().submit(
                    () ->  doReplicateLog(endpoint, context.log().getNextIndex()),
                    LOGGING_FUTURE_CALLBACK
            );
        }

        @Override
        public void doReplicateLog(NodeEndpoint endpoint, int nextIndex) {
            try {
                AppendEntriesRpc rpc = context.log().createAppendEntriesRpc(role.getTerm(), context.selfId(), nextIndex, context.config().getMaxReplicationEntries());
                context.connector().sendAppendEntries(rpc, endpoint);
            } catch (EntryInSnapshotException ignored) {
                // change to install snapshot rpc if entry in snapshot
                logger.debug("log entry {} in snapshot, replicate with snapshot rpc", nextIndex);
                InstallSnapshotRpc rpc = context.log().createInstallSnapshotRpc(role.getTerm(), context.selfId(), 0, context.config().getSnapshotDataLength());
                context.connector().sendInstallSnapshot(rpc, endpoint);
            }
        }

        @Override
        public void sendInstallSnapshot(NodeEndpoint endpoint, int offset) {
            InstallSnapshotRpc rpc = context.log().createInstallSnapshotRpc(role.getTerm(), context.selfId(), offset, context.config().getSnapshotDataLength());
            context.connector().sendInstallSnapshot(rpc, endpoint);
        }

        @Override
        public void done(NewNodeCatchUpTask task) {

        }

    }

    private class GroupConfigChangeTaskContextImpl implements GroupConfigChangeTaskContext {

        @Override
        public void addNode(NodeEndpoint endpoint, int nextIndex, int matchIndex) {
            context.taskExecutor().submit(() -> {
                context.log().appendEntryForAddNode(role.getTerm(), context.group().listEndpointOfMajor(), endpoint);
                assert !context.selfId().equals(endpoint.getId());
                context.group().addNode(endpoint, nextIndex, matchIndex, true);
                NodeImpl.this.doReplicateLog();
            });
        }

        @Override
        public void downgradeNode(NodeId nodeId) {
            context.taskExecutor().submit(() -> {
                context.group().downgrade(nodeId);
                Set<NodeEndpoint> nodeEndpoints = context.group().listEndpointOfMajor();
                context.log().appendEntryForRemoveNode(role.getTerm(), nodeEndpoints, nodeId);
                NodeImpl.this.doReplicateLog();
            });
        }

        @Override
        public void removeNode(NodeId nodeId) {
            context.taskExecutor().submit(() -> {
                if (nodeId.equals(context.selfId())) {
                    logger.info("remove self from group, step down and standby");
                    becomeFollower(role.getTerm(), null, null, false);
                }
                context.group().removeNode(nodeId);
            }, LOGGING_FUTURE_CALLBACK);
        }

        @Override
        public void done() {
            synchronized (NodeImpl.class) {
                groupConfigChangeTaskHolder = new GroupConfigChangeTaskHolder();
            }
        }

    }

}
