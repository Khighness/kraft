package top.parak.kraft.core.node;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import top.parak.kraft.core.log.entry.Entry;
import top.parak.kraft.core.log.entry.EntryMeta;
import top.parak.kraft.core.node.role.RoleName;
import top.parak.kraft.core.node.role.RoleState;
import top.parak.kraft.core.node.store.MemoryNodeStore;
import top.parak.kraft.core.rpc.ConnectorAdapter;
import top.parak.kraft.core.rpc.MockConnector;
import top.parak.kraft.core.rpc.message.AppendEntriesRpc;
import top.parak.kraft.core.rpc.message.RequestVoteRpc;
import top.parak.kraft.core.schedule.NullScheduler;
import top.parak.kraft.core.support.task.DirectTaskExecutor;
import top.parak.kraft.core.support.task.ListeningTaskExecutor;
import top.parak.kraft.core.support.task.SingleThreadTaskExecutor;
import top.parak.kraft.core.support.task.TaskExecutor;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class NodeImplTest {

    private static class WaitConnector extends ConnectorAdapter {

        private boolean sent = false;

        @Override
        public void sendAppendEntries(@Nonnull AppendEntriesRpc rpc, @Nonnull NodeEndpoint destinationEndpoint) {
            appendEntriesRpcSent();
        }

        private void appendEntriesRpcSent() {
            sent = true;
            notify();
        }

        synchronized void awaitAppendEntriesRpc() throws InterruptedException {
            if (!sent) {
                wait();
            }
            sent = false;
        }

        void reset() {
            sent = false;
        }
    }

    private static TaskExecutor taskExecutor;
    private static TaskExecutor groupConfigChangeTaskExecutor;
    private static TaskExecutor cachedThreadTaskExecutor;
    private static final AtomicInteger cachedThreadId = new AtomicInteger(0);

    private NodeBuilder newNodeBuilder(NodeId selfId, NodeEndpoint... endpoints) {
        return new NodeBuilder(Arrays.asList(endpoints), selfId)
                .setScheduler(new NullScheduler())
                .setConnector(new MockConnector())
                .setTaskExecutor(new DirectTaskExecutor(true));
    }

    private AppendEntriesRpc createAppendEntriesRpc(int lastEntryIndex) {
        AppendEntriesRpc rpc = new AppendEntriesRpc();
        rpc.setPrevLogIndex(lastEntryIndex);
        return rpc;
    }

    private void checkWithTaskExecutor(NodeImpl node, Runnable r) throws Throwable {
        try {
            node.getContext().taskExecutor().submit(r).get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    @BeforeClass
    public static void beforeClass() {
        taskExecutor = new SingleThreadTaskExecutor("node-test");
        groupConfigChangeTaskExecutor = new SingleThreadTaskExecutor("group-config-change-test");
        cachedThreadTaskExecutor = new ListeningTaskExecutor(Executors.newCachedThreadPool(r ->
                new Thread("cached-thread-" + cachedThreadId.incrementAndGet()))
        );
    }

    @Test
    public void testStartFresh() {
        NodeImpl node = (NodeImpl) newNodeBuilder(NodeId.of("A"), new NodeEndpoint("A", "127.0.0.1", 2333))
                .build();
        node.start();

        RoleState state = node.getRoleState();
        Assert.assertEquals(RoleName.FOLLOWER, state.getRoleName());
        Assert.assertEquals(0, state.getTerm());
        Assert.assertNull(state.getVotedFor());
    }

    @Test
    public void testStartLoadFromStore() {
        NodeImpl node = (NodeImpl) newNodeBuilder(NodeId.of("A"), new NodeEndpoint("A", "127.0.0.1", 2333))
                .setStore(new MemoryNodeStore(1, NodeId.of("B")))
                .build();
        node.start();

        RoleState state = node.getRoleState();
        Assert.assertEquals(RoleName.FOLLOWER, state.getRoleName());
        Assert.assertEquals(1, state.getTerm());
        Assert.assertEquals(NodeId.of("B"), state.getVotedFor());
    }

    @Test
    public void testStop() throws InterruptedException {
        NodeImpl node = (NodeImpl) newNodeBuilder(NodeId.of("A"), new NodeEndpoint("A", "127.0.0.1", 2333))
                .build();
        node.start();
        node.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void testStopIllegal() throws InterruptedException {
        NodeImpl node = (NodeImpl) newNodeBuilder(NodeId.of("A"), new NodeEndpoint("A", "127.0.0.1", 2333))
                .build();
        node.stop();
    }

    @Test
    public void testElectionTimeoutStandalone() {
        NodeImpl node = (NodeImpl) newNodeBuilder(NodeId.of("A"), new NodeEndpoint("A", "127.0.0.1", 2333))
                .build();
        node.start();
        node.electionTimeout();

        RoleState state = node.getRoleState();
        Assert.assertEquals(RoleName.LEADER, state.getRoleName());
        Assert.assertEquals(1, state.getTerm());

        // no-op log entry
        EntryMeta lastEntryMeta = node.getContext().log().getLastEntryMeta();
        Assert.assertEquals(Entry.KIND_NO_OP, lastEntryMeta.getKind());
        Assert.assertEquals(1, lastEntryMeta.getIndex());
        Assert.assertEquals(1, lastEntryMeta.getTerm());
    }

    @Test
    public void testElectionTimeoutStandby() {
        NodeImpl node = (NodeImpl) newNodeBuilder(NodeId.of("A"), new NodeEndpoint("A", "127.0.0.1", 2333))
                .setStore(new MemoryNodeStore(1, null))
                .setStandby(true)
                .build();
        node.start();
        node.electionTimeout();

        RoleState state = node.getRoleState();
        Assert.assertEquals(RoleName.FOLLOWER, state.getRoleName());
        Assert.assertEquals(1, state.getTerm());
    }

    @Test
    public void testElectionTimeoutWhenLeader() {
        NodeImpl node = (NodeImpl) newNodeBuilder(NodeId.of("A"), new NodeEndpoint("A", "127.0.0.1", 2333))
                .build();
        node.start();
        node.electionTimeout();
        node.electionTimeout();
    }

    @Test
    public void testElectionTimeoutWhenFollower() {
        NodeImpl node = (NodeImpl) newNodeBuilder(NodeId.of("A"),
                new NodeEndpoint("A", "127.0.0.1", 2333),
                new NodeEndpoint("B", "127.0.0.1", 2334),
                new NodeEndpoint("C", "127.0.0.1", 2335)
        ).build();
        node.start();
        node.electionTimeout();

        RoleState state = node.getRoleState();
        Assert.assertEquals(RoleName.CANDIDATE, state.getRoleName());
        Assert.assertEquals(1, state.getTerm());
        Assert.assertEquals(1, state.getVotesCount());

        MockConnector mockConnector = (MockConnector) node.getContext().connector();
        RequestVoteRpc rpc = (RequestVoteRpc) mockConnector.getRpc();
        Assert.assertEquals(1, rpc.getTerm());
        Assert.assertEquals(NodeId.of("A"), rpc.getCandidateId());
        Assert.assertEquals(0, rpc.getLastLogIndex());
        Assert.assertEquals(0, rpc.getLastLogTerm());
    }

    @Test
    public void testElectionTimeoutWHenCandidate() {
        NodeImpl node = (NodeImpl) newNodeBuilder(NodeId.of("A"),
                new NodeEndpoint("A", "127.0.0.1", 2333),
                new NodeEndpoint("B", "127.0.0.1", 2334),
                new NodeEndpoint("C", "127.0.0.1", 2335)
        ).build();
        node.start();
        node.electionTimeout();
        node.electionTimeout();

        RoleState state = node.getRoleState();
        Assert.assertEquals(RoleName.CANDIDATE, state.getRoleName());
        Assert.assertEquals(2, state.getTerm());
        Assert.assertEquals(1, state.getVotesCount());

        MockConnector mockConnector = (MockConnector) node.getContext().connector();
        RequestVoteRpc rpc = (RequestVoteRpc) mockConnector.getLastMessage().getRpc();
        Assert.assertEquals(2, rpc.getTerm());
        Assert.assertEquals(NodeId.of("A"), rpc.getCandidateId());
        Assert.assertEquals(0, rpc.getLastLogIndex());
        Assert.assertEquals(0, rpc.getLastLogTerm());
    }

    @Test
    public void testReplicateLogStandalone() {
        NodeImpl node = (NodeImpl) newNodeBuilder(NodeId.of("A"),
                new NodeEndpoint("A", "127.0.0.1", 2333)
        ).build();
        node.start();
        node.electionTimeout();
        Assert.assertEquals(0, node.getContext().log().getCommitIndex());
        node.replicateLog();
        Assert.assertEquals(1, node.getContext().log().getCommitIndex());
    }

}
