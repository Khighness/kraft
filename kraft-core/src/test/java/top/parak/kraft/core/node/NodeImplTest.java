package top.parak.kraft.core.node;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import top.parak.kraft.core.log.entry.Entry;
import top.parak.kraft.core.log.entry.EntryMeta;
import top.parak.kraft.core.node.role.RoleName;
import top.parak.kraft.core.node.role.RoleState;
import top.parak.kraft.core.node.store.MemoryNodeStore;
import top.parak.kraft.core.node.task.GroupConfigChangeTaskReference;
import top.parak.kraft.core.rpc.ConnectorAdapter;
import top.parak.kraft.core.rpc.MockConnector;
import top.parak.kraft.core.rpc.message.AppendEntriesRpc;
import top.parak.kraft.core.rpc.message.RequestVoteResult;
import top.parak.kraft.core.rpc.message.RequestVoteRpc;
import top.parak.kraft.core.schedule.NullScheduler;
import top.parak.kraft.core.support.task.DirectTaskExecutor;
import top.parak.kraft.core.support.task.ListeningTaskExecutor;
import top.parak.kraft.core.support.task.SingleThreadTaskExecutor;
import top.parak.kraft.core.support.task.TaskExecutor;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class NodeImplTest {

    private static class WaitConnector extends ConnectorAdapter {

        private boolean sent = false;

        @Override
        public synchronized void sendAppendEntries(@Nonnull AppendEntriesRpc rpc, @Nonnull NodeEndpoint destinationEndpoint) {
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
                new Thread(r, "cached-thread-" + cachedThreadId.incrementAndGet())));
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
        NodeImpl node = (NodeImpl) newNodeBuilder(
                NodeId.of("A"),
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

    @Test
    public void testReplicateLog() {
        NodeImpl node = (NodeImpl) newNodeBuilder(
                NodeId.of("A"),
                new NodeEndpoint("A", "127.0.0.1", 2333),
                new NodeEndpoint("B", "127.0.0.1", 2334),
                new NodeEndpoint("C", "127.0.0.1", 2335)
        ).build();
        node.start();
        node.electionTimeout();
        node.onReceiveRequestVoteResult(new RequestVoteResult(1, true));
        node.replicateLog();

        MockConnector mockConnector = (MockConnector) node.getContext().connector();
        Assert.assertEquals(3, mockConnector.getMessageCount());

        // check destination node id
        List<MockConnector.Message> messages = mockConnector.getMessages();
        // request vote rpc + append entries rpc * 2
        Assert.assertEquals(3, messages.size());
        Set<NodeId> destinationIds = messages.subList(1, 3).stream()
                .map(MockConnector.Message::getDestinationNodeId)
                .collect(Collectors.toSet());
        Assert.assertEquals(2, destinationIds.size());
        Assert.assertTrue(destinationIds.contains(NodeId.of("B")));
        Assert.assertTrue(destinationIds.contains(NodeId.of("C")));

        AppendEntriesRpc rpc = (AppendEntriesRpc) messages.get(2).getRpc();
        Assert.assertEquals(1, rpc.getTerm());
    }

    @Test
    public void testReplicateLogSkipReplicating() {
        NodeImpl node = (NodeImpl) newNodeBuilder(
                NodeId.of("A"),
                new NodeEndpoint("A", "127.0.0.1", 2333),
                new NodeEndpoint("B", "127.0.0.1", 2334),
                new NodeEndpoint("C", "127.0.0.1", 2335)
        ).build();
        node.start();
        node.electionTimeout();
        node.onReceiveRequestVoteResult(new RequestVoteResult(1, true));
        node.getContext().group().findMember(NodeId.of("B")).replicatedNow();
        node.replicateLog();

        MockConnector mockConnector = (MockConnector) node.getContext().connector();
        // request vote rpc + append entries rpc
        Assert.assertEquals(2, mockConnector.getMessageCount());
    }

    @Test(expected = NotLeaderException.class)
    public void testAppendLogWhenFollower() {
        NodeImpl node = (NodeImpl) newNodeBuilder(
                NodeId.of("A"),
                new NodeEndpoint("A", "127.0.0.1", 2333),
                new NodeEndpoint("B", "127.0.0.1", 2334),
                new NodeEndpoint("C", "127.0.0.1", 2335)
        ).build();
        node.start();
        node.appendLog("test".getBytes());
    }

    @Test(expected = NotLeaderException.class)
    public void testAppendLogWhenCandidate() {
        NodeImpl node = (NodeImpl) newNodeBuilder(
                NodeId.of("A"),
                new NodeEndpoint("A", "127.0.0.1", 2333),
                new NodeEndpoint("B", "127.0.0.1", 2334),
                new NodeEndpoint("C", "127.0.0.1", 2335)
        ).build();
        node.start();
        node.electionTimeout();
        node.appendLog("test".getBytes());
    }

    @Test
    public void testAppendLog() {
        NodeImpl node = (NodeImpl) newNodeBuilder(
                NodeId.of("A"),
                new NodeEndpoint("A", "127.0.0.1", 2333),
                new NodeEndpoint("B", "127.0.0.1", 2334),
                new NodeEndpoint("C", "127.0.0.1", 2335)
        ).build();
        node.start();
        node.electionTimeout();
        node.onReceiveRequestVoteResult(new RequestVoteResult(1, true));
        node.appendLog("test".getBytes());

        MockConnector mockConnector = (MockConnector) node.getContext().connector();
        // request vote rpc + append entries rpc(no-op entry, general entry)
        Assert.assertEquals(3, mockConnector.getMessageCount());
    }

    @Test(expected = NotLeaderException.class)
    public void testAddNodeWhenFollower() {
        NodeImpl node = (NodeImpl) newNodeBuilder(
                NodeId.of("A"),
                new NodeEndpoint("A", "127.0.0.1", 2333),
                new NodeEndpoint("B", "127.0.0.1", 2334),
                new NodeEndpoint("C", "127.0.0.1", 2335)
        ).build();
        node.start();
        node.addNode(new NodeEndpoint("D", "127.0.0.1", 2336));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNodeSelf() {
        NodeImpl node = (NodeImpl) newNodeBuilder(
                NodeId.of("A"),
                new NodeEndpoint("A", "127.0.0.1", 2333),
                new NodeEndpoint("B", "127.0.0.1", 2334),
                new NodeEndpoint("C", "127.0.0.1", 2335)
        ).build();
        node.start();
        node.electionTimeout();
        node.onReceiveRequestVoteResult(new RequestVoteResult(1, true));
        node.addNode(new NodeEndpoint("A", "127.0.0.1", 2333));
    }

    private AtomicInteger id;
    private ListeningTaskExecutor listeningExecutor;

    @Test
    public void testAddNodeSampleNode() throws Throwable {
        WaitConnector connector = new WaitConnector();
        NodeImpl node = (NodeImpl) newNodeBuilder(
                NodeId.of("A"),
                new NodeEndpoint("A", "127.0.0.1", 2333),
                new NodeEndpoint("B", "127.0.0.1", 2334),
                new NodeEndpoint("C", "127.0.0.1", 2335))
                .setConnector(connector)
                .setTaskExecutor(taskExecutor)
                .setGroupConfigChangeTaskExecutor(groupConfigChangeTaskExecutor)
                .build();
        node.start();
        node.electionTimeout();
        node.processRequestVoteResult(new RequestVoteResult(1, true)).get();

        Future<GroupConfigChangeTaskReference> future = cachedThreadTaskExecutor.submit(() ->
                node.addNode(new NodeEndpoint("D", "127.0.0.1", 2336))
        );
        connector.awaitAppendEntriesRpc();
        try {
            node.addNode(new NodeEndpoint("D", "127.0.0.1", 2336));
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }
        future.cancel(true);
    }

    @Test
    public void testAddNode() throws Throwable {

    }

}
