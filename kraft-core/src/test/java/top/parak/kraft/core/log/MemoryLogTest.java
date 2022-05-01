package top.parak.kraft.core.log;

import com.google.common.eventbus.EventBus;
import org.junit.Assert;
import org.junit.Test;

import top.parak.kraft.core.log.entry.Entry;
import top.parak.kraft.core.log.entry.EntryMeta;
import top.parak.kraft.core.log.entry.GroupConfigEntry;
import top.parak.kraft.core.log.entry.NoOpEntry;
import top.parak.kraft.core.log.sequence.MemoryEntrySequence;
import top.parak.kraft.core.log.snapshot.EntryInSnapshotException;
import top.parak.kraft.core.log.snapshot.MemorySnapshot;
import top.parak.kraft.core.log.statemachine.EmptyStateMachine;
import top.parak.kraft.core.node.NodeEndpoint;
import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.rpc.message.AppendEntriesRpc;
import top.parak.kraft.core.rpc.message.InstallSnapshotRpc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MemoryLogTest {

    @Test
    public void testGetLastEntryMetaNoLogAndSnapshot() {
        MemoryLog memoryLog = new MemoryLog();
        EntryMeta lastEntryMeta = memoryLog.getLastEntryMeta();
        Assert.assertEquals(Entry.KIND_NO_OP, lastEntryMeta.getKind());
        Assert.assertEquals(0, lastEntryMeta.getIndex());
        Assert.assertEquals(0, lastEntryMeta.getTerm());
    }

    @Test
    public void testGetLastEntryMetaNoLog() {
        MemoryLog memoryLog = new MemoryLog(
                new MemorySnapshot(3, 2),
                new MemoryEntrySequence(4),
                new EventBus()
        );
        EntryMeta lastEntryMeta = memoryLog.getLastEntryMeta();
        Assert.assertEquals(3, lastEntryMeta.getIndex());
        Assert.assertEquals(2, lastEntryMeta.getTerm());
    }

    @Test
    public void testGetLastEntryMetaNoSnapshot() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1); // 1
        memoryLog.appendEntry(1); // 2
        EntryMeta lastEntryMeta = memoryLog.getLastEntryMeta();
        Assert.assertEquals(2, lastEntryMeta.getIndex());
        Assert.assertEquals(1, lastEntryMeta.getTerm());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAppendEntriesIllegalNextIndex() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.createAppendEntriesRpc(1, new NodeId("A"), 2, Log.ALL_ENTRIES);
    }

    @Test
    public void testCreateAppendEntriesRpcNoLog() {
        MemoryLog memoryLog = new MemoryLog();
        NodeId nodeId = new NodeId("A");
        AppendEntriesRpc rpc = memoryLog.createAppendEntriesRpc(
                1, nodeId, 1, Log.ALL_ENTRIES
        );
        Assert.assertEquals(1, rpc.getTerm());
        Assert.assertEquals(nodeId, rpc.getLeaderId());
        Assert.assertEquals(0, rpc.getPrevLogIndex());
        Assert.assertEquals(0, rpc.getEntries().size());
        Assert.assertEquals(0, rpc.getLeaderCommit());
    }

    @Test
    public void testCreateAppendEntriesRpcStartFromOne() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1); // 1
        memoryLog.appendEntry(1); // 2
        AppendEntriesRpc rpc = memoryLog.createAppendEntriesRpc(
                1, new NodeId("A"), 1, Log.ALL_ENTRIES
        );
        Assert.assertEquals(1, rpc.getTerm());
        Assert.assertEquals(0, rpc.getPrevLogIndex());
        Assert.assertEquals(0, rpc.getPrevLogTerm());
        Assert.assertEquals(2, rpc.getEntries().size());
        Assert.assertEquals(1, rpc.getEntries().get(0).getIndex());
    }

    @Test
    public void testCreateAppendEntriesRpcOneLogEntry() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1); // 1
        AppendEntriesRpc rpc = memoryLog.createAppendEntriesRpc(
                1, new NodeId("A"), 2, Log.ALL_ENTRIES
        );
        Assert.assertEquals(1, rpc.getTerm());
        Assert.assertEquals(1, rpc.getPrevLogIndex());
        Assert.assertEquals(0, rpc.getEntries().size());
        Assert.assertEquals(0, rpc.getLeaderCommit());
    }

    @Test
    public void testCreateAppendEntriesRpcTwoLogEntriesFrom2() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1); // 1
        memoryLog.appendEntry(1); // 2
        AppendEntriesRpc rpc = memoryLog.createAppendEntriesRpc(
                1, new NodeId("A"), 2, Log.ALL_ENTRIES
        );
        Assert.assertEquals(1, rpc.getTerm());
        Assert.assertEquals(1, rpc.getPrevLogIndex());
        Assert.assertEquals(1, rpc.getEntries().size());
        Assert.assertEquals(2, rpc.getEntries().get(0).getIndex());
    }

    @Test
    public void testCreateAppendEntriesRpcTwoLogEntriesFrom3() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1); // 1
        memoryLog.appendEntry(1); // 2
        AppendEntriesRpc rpc = memoryLog.createAppendEntriesRpc(
                1, new NodeId("A"), 3, Log.ALL_ENTRIES
        );
        Assert.assertEquals(1, rpc.getTerm());
        Assert.assertEquals(2, rpc.getPrevLogIndex());
        Assert.assertEquals(0, rpc.getEntries().size());
    }

    @Test
    public void testCreateAppendEntriesRpcLimit1() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1); // 1
        memoryLog.appendEntry(1); // 2
        memoryLog.appendEntry(1); // 3
        memoryLog.appendEntry(1); // 4
        AppendEntriesRpc rpc = memoryLog.createAppendEntriesRpc(
                1, new NodeId("A"), 3, 1
        );
        Assert.assertEquals(1, rpc.getTerm());
        Assert.assertEquals(2, rpc.getPrevLogIndex());
        Assert.assertEquals(1, rpc.getEntries().size());
        Assert.assertEquals(3, rpc.getEntries().get(0).getIndex());
    }

    @Test
    public void testCreateAppendEntriesRpcLimit2() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1); // 1
        memoryLog.appendEntry(1); // 2
        memoryLog.appendEntry(1); // 3
        memoryLog.appendEntry(1); // 4
        AppendEntriesRpc rpc = memoryLog.createAppendEntriesRpc(
                1, new NodeId("A"), 3, 2
        );
        Assert.assertEquals(1, rpc.getTerm());
        Assert.assertEquals(2, rpc.getPrevLogIndex());
        Assert.assertEquals(2, rpc.getEntries().size());
        Assert.assertEquals(3, rpc.getEntries().get(0).getIndex());
        Assert.assertEquals(4, rpc.getEntries().get(1).getIndex());
    }

    @Test
    public void testCreateAppendEntriesRpcLimit3() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1); // 1
        memoryLog.appendEntry(1); // 2
        memoryLog.appendEntry(1); // 3
        memoryLog.appendEntry(1); // 4
        AppendEntriesRpc rpc = memoryLog.createAppendEntriesRpc(
                1, new NodeId("A"), 3, 3
        );
        Assert.assertEquals(1, rpc.getTerm());
        Assert.assertEquals(2, rpc.getPrevLogIndex());
        Assert.assertEquals(2, rpc.getEntries().size());
        Assert.assertEquals(3, rpc.getEntries().get(0).getIndex());
        Assert.assertEquals(4, rpc.getEntries().get(1).getIndex());
    }

    @Test
    public void testCreateAppendEntriesUseSnapshot() {
        MemoryLog memoryLog = new MemoryLog(
                new MemorySnapshot(3, 2),
                new MemoryEntrySequence(4),
                new EventBus()
        );
        AppendEntriesRpc rpc = memoryLog.createAppendEntriesRpc(
                2, new NodeId("A"), 4, Log.ALL_ENTRIES
        );
        Assert.assertEquals(2, rpc.getTerm());
        Assert.assertEquals(3, rpc.getPrevLogIndex());
        Assert.assertEquals(2, rpc.getPrevLogTerm());
        Assert.assertEquals(0, rpc.getEntries().size());
    }

    @Test(expected = EntryInSnapshotException.class)
    public void testCreateAppendEntriesLogEmptyEntryInSnapshot() {
        MemoryLog memoryLog = new MemoryLog(
                new MemorySnapshot(3, 2),
                new MemoryEntrySequence(4),
                new EventBus()
        );
        memoryLog.createAppendEntriesRpc(
                2, new NodeId("A"), 3, Log.ALL_ENTRIES
        );
    }

    @Test(expected = EntryInSnapshotException.class)
    public void testCreateAppendEntriesLogNotEmptyEntryInSnapshot() {
        MemoryLog memoryLog = new MemoryLog(
                new MemorySnapshot(3, 2),
                new MemoryEntrySequence(4),
                new EventBus()
        );
        memoryLog.appendEntry(1); // 4
        memoryLog.createAppendEntriesRpc(
                2, new NodeId("A"), 3, Log.ALL_ENTRIES
        );
    }

    @Test
    public void testCreateInstallSnapshotRpcEmpty() {
        MemoryLog memoryLog = new MemoryLog();
        NodeId nodeId = new NodeId("A");
        InstallSnapshotRpc rpc = memoryLog.createInstallSnapshotRpc(1, nodeId, 0, 10);
        Assert.assertEquals(1, rpc.getTerm());
        Assert.assertEquals(nodeId, rpc.getLeaderId());
        Assert.assertEquals(0, rpc.getLastIndex());
        Assert.assertEquals(0, rpc.getLastTerm());
        Assert.assertEquals(0, rpc.getOffset());
        Assert.assertArrayEquals(new byte[0], rpc.getData());
        Assert.assertTrue(rpc.isDone());
    }

    @Test
    public void testCreateInstallSnapshotRpc() {
        MemoryLog memoryLog = new MemoryLog(
                new MemorySnapshot(3, 4, "test".getBytes(), Collections.emptySet()),
                new MemoryEntrySequence(4),
                new EventBus()
        );
        InstallSnapshotRpc rpc = memoryLog.createInstallSnapshotRpc(4, new NodeId("A"), 0, 2);
        Assert.assertEquals(3, rpc.getLastIndex());
        Assert.assertEquals(4, rpc.getLastTerm());
        Assert.assertArrayEquals("te".getBytes(), rpc.getData());
        Assert.assertFalse(rpc.isDone());
    }

    @Test
    public void testGetLastUncommittedGroupConfigEntry() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntryForAddNode(1, Collections.emptySet(), new NodeEndpoint("A", "localhost", 2333));
        memoryLog.appendEntryForRemoveNode(1, Collections.emptySet(), new NodeId("A"));
        GroupConfigEntry entry = memoryLog.getLastUncommittedGroupConfigEntry();
        Assert.assertNotNull(entry);
        Assert.assertEquals(Entry.KIND_REMOVE_NODE, entry.getKind());
        Assert.assertEquals(2, entry.getIndex());

    }

    @Test
    public void testGetLastUncommittedGroupConfigEntryEmpty() {
        MemoryLog memoryLog = new MemoryLog();
        Assert.assertNull(memoryLog.getLastUncommittedGroupConfigEntry());
    }

    @Test
    public void testGetLastUncommittedGroupConfigEntryCommitted() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntryForAddNode(1, Collections.emptySet(), new NodeEndpoint("A", "localhost", 2333));
        memoryLog.advanceCommitIndex(1, 1);
        Assert.assertNull(memoryLog.getLastUncommittedGroupConfigEntry());
    }

    @Test
    public void testGetNextLogEmpty() {
        MemoryLog memoryLog = new MemoryLog();
        Assert.assertEquals(1, memoryLog.getNextIndex());
    }

    @Test
    public void testGetNextLog() {
        MemoryLog memoryLog = new MemoryLog(
                new MemorySnapshot(3, 4),
                new MemoryEntrySequence(4),
                new EventBus()
        );
        Assert.assertEquals(4, memoryLog.getNextIndex());
    }

    @Test
    public void testIsNewerThanNoLog() {
        MemoryLog memoryLog = new MemoryLog();
        Assert.assertFalse(memoryLog.isNewerThan(0, 0));
    }

    @Test
    public void testIsNewerThanSame() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1); // index = 1, term = 1
        Assert.assertFalse(memoryLog.isNewerThan(1, 1));
    }

    @Test
    public void testIsNewerThanHighTerm() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(2); // index = 1, term = 2
        Assert.assertTrue(memoryLog.isNewerThan(1, 1));
    }

    @Test
    public void testIsNewerThanMoreLog() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1);
        memoryLog.appendEntry(1); // index = 2, term = 1
        Assert.assertTrue(memoryLog.isNewerThan(1, 1));
    }

    @Test
    public void testAppendEntryForAddNode() {
        MemoryLog memoryLog = new MemoryLog();
        Assert.assertNull(memoryLog.getLastUncommittedGroupConfigEntry());
        memoryLog.appendEntryForAddNode(1, Collections.emptySet(), new NodeEndpoint("A", "localhost", 2333));
        Assert.assertNotNull(memoryLog.getLastUncommittedGroupConfigEntry());
    }

    @Test
    public void testAppendEntryForRemoveNode() {
        MemoryLog memoryLog = new MemoryLog();
        Assert.assertNull(memoryLog.getLastUncommittedGroupConfigEntry());
        memoryLog.appendEntryForRemoveNode(1, Collections.emptySet(), new NodeId("A"));
        Assert.assertNotNull(memoryLog.getLastUncommittedGroupConfigEntry());
    }

    @Test
    public void testAppendEntriesFromLeaderNoLog() {
        MemoryLog memoryLog = new MemoryLog();
        Assert.assertTrue(memoryLog.appendEntriesFromLeader(0, 0, Arrays.asList(
                new NoOpEntry(1, 1),
                new NoOpEntry(2, 1)
        )));
        Assert.assertEquals(3, memoryLog.getNextIndex());
    }

    // prevLogIndex == snapshot.lastIncludedIndex
    // prevLogTerm == snapshot.lastIncludedTerm
    @Test
    public void testAppendEntriesFromLeaderSnapshot1() {
        MemoryLog memoryLog = new MemoryLog(
                new MemorySnapshot(3, 4),
                new MemoryEntrySequence(4),
                new EventBus()
        );
        Assert.assertTrue(memoryLog.appendEntriesFromLeader(3, 4, Collections.emptyList()));
    }

    // prevLogIndex == snapshot.lastIncludedIndex
    // prevLogTerm != snapshot.lastIncludedTerm
    @Test
    public void testAppendEntriesFromLeaderSnapshot2() {
        MemoryLog memoryLog = new MemoryLog(
                new MemorySnapshot(3, 4),
                new MemoryEntrySequence(4),
                new EventBus()
        );
        Assert.assertFalse(memoryLog.appendEntriesFromLeader(3, 5, Collections.emptyList()));
    }

    // prevLogIndex < snapshot.lastIncludedIndex
    @Test
    public void testAppendEntriesFromLeaderSnapshot3() {
        MemoryLog memoryLog = new MemoryLog(
                new MemorySnapshot(3, 4),
                new MemoryEntrySequence(4),
                new EventBus()
        );
        Assert.assertFalse(memoryLog.appendEntriesFromLeader(1, 4, Collections.emptyList()));
    }

    @Test
    public void testAppendEntriesFromLeaderPrevLogNotFound() {
        MemoryLog memoryLog = new MemoryLog();
        Assert.assertEquals(1, memoryLog.getNextIndex());
        Assert.assertFalse(memoryLog.appendEntriesFromLeader(1, 1, Collections.emptyList()));
    }

    @Test
    public void testAppendEntriesFromLeaderPrevLogTermNotMatch() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1);
        Assert.assertFalse(memoryLog.appendEntriesFromLeader(1, 2, Collections.emptyList()));
    }

    // (index, term)
    // follower: (1, 1), (2, 1)
    // leader  :         (2, 1), (3, 2)
    @Test
    public void testAppendEntriesFromLeaderSkip() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1); // 1
        memoryLog.appendEntry(1); // 2
        List<Entry> leaderEntries = Arrays.asList(
                new NoOpEntry(2, 1),
                new NoOpEntry(3, 2)
        );
        Assert.assertTrue(memoryLog.appendEntriesFromLeader(1, 1, leaderEntries));
    }

    @Test
    public void testAppendEntriesFromLeaderNoConflict() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1); // 1
        List<Entry> leaderEntries = Arrays.asList(
                new NoOpEntry(2, 1),
                new NoOpEntry(3, 1)
        );
        Assert.assertTrue(memoryLog.appendEntriesFromLeader(1, 1, leaderEntries));
    }

    // follower: (1, 1), (2, 1)
    // leader  :         (2, 2), (3, 2)
    @Test
    public void testAppendEntriesFromLeaderConflict1() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1); // 1
        memoryLog.appendEntry(1); // 2
        List<Entry> leaderEntries = Arrays.asList(
                new NoOpEntry(2, 2),
                new NoOpEntry(3, 2)
        );
        Assert.assertTrue(memoryLog.appendEntriesFromLeader(1, 1, leaderEntries));
    }

    // follower: (1, 1), (2, 1), (3, 1)
    // leader  :         (2, 1), (3, 2)
    @Test
    public void testAppendEntriesFromLeaderConflict2() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1); // 1
        memoryLog.appendEntry(1); // 2
        memoryLog.appendEntry(1); // 3
        List<Entry> leaderEntries = Arrays.asList(
                new NoOpEntry(2, 1),
                new NoOpEntry(3, 2)
        );
        Assert.assertTrue(memoryLog.appendEntriesFromLeader(1, 1, leaderEntries));
    }

    // follower: (1, 1), (2, 1), (3, 1, no-op, committed)
    // leader  :         (2, 1), (3, 2)
    @Test
    public void testAppendEntriesFromLeaderConflict3() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1); // 1
        memoryLog.appendEntry(1); // 2
        memoryLog.appendEntry(1); // 3
        memoryLog.advanceCommitIndex(3, 1);
        List<Entry> leaderEntries = Arrays.asList(
                new NoOpEntry(2, 1),
                new NoOpEntry(3, 2)
        );
        Assert.assertTrue(memoryLog.appendEntriesFromLeader(1, 1, leaderEntries));
    }

    // follower: (1, 1), (2, 1), (3, 1, general, committed)
    // leader  :         (2, 1), (3, 2)
    @Test
    public void testAppendEntriesFromLeaderConflict4() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1); // 1
        memoryLog.appendEntry(1); // 2
        memoryLog.appendEntry(1, "test".getBytes()); // 3
        memoryLog.advanceCommitIndex(3, 1);
        List<Entry> leaderEntries = Arrays.asList(
                new NoOpEntry(2, 1),
                new NoOpEntry(3, 2)
        );
        Assert.assertTrue(memoryLog.appendEntriesFromLeader(1, 1, leaderEntries));
        Assert.assertEquals(2, memoryLog.getCommitIndex());
    }

    // follower: (1, 1), (2, 1), (3, 1, group-config, committed)
    // leader  :         (2, 1), (3, 2)
    @Test
    public void testAppendEntriesFromLeaderConflict5() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1); // 1
        memoryLog.appendEntry(1); // 2
        memoryLog.appendEntryForRemoveNode(1, Collections.emptySet(), NodeId.of("A")); // 3
        memoryLog.advanceCommitIndex(3, 1);
        List<Entry> leaderEntries = Arrays.asList(
                new NoOpEntry(2, 1),
                new NoOpEntry(3, 2)
        );
        Assert.assertTrue(memoryLog.appendEntriesFromLeader(1, 1, leaderEntries));
        Assert.assertEquals(2, memoryLog.getCommitIndex());
    }

    @Test
    public void testAdvanceCommitIndexLessThanCurrentCommitIndex() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.advanceCommitIndex(0, 1);
    }

    @Test
    public void testAdvanceCommitIndexEntryNotFound() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.advanceCommitIndex(1, 1);
    }

    @Test
    public void testAdvanceCommitIndexNotCurrentTerm() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1);
        memoryLog.advanceCommitIndex(1, 2);
    }

    @Test
    public void testAdvanceCommitIndex() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1);
        Assert.assertEquals(0, memoryLog.commitIndex);
        memoryLog.advanceCommitIndex(1, 1);
        Assert.assertEquals(1, memoryLog.commitIndex);
    }

    @Test
    public void testAdvanceCommitIndexApplyEntries() {
        EmptyStateMachine stateMachine = new EmptyStateMachine();

        MemoryLog memoryLog = new MemoryLog();
        memoryLog.setStateMachine(stateMachine);
        memoryLog.appendEntry(1, "test".getBytes());
        memoryLog.appendEntry(1);
        Assert.assertEquals(0, stateMachine.getLastApplied());
        memoryLog.advanceCommitIndex(1, 1);
        Assert.assertEquals(1, stateMachine.getLastApplied());
    }

    @Test
    public void testAdvanceCommitIndexApplySnapshot() {
        EmptyStateMachine stateMachine = new EmptyStateMachine();

        MemoryLog memoryLog = new MemoryLog(
                new MemorySnapshot(3, 4),
                new MemoryEntrySequence(4),
                new EventBus()
        );
        memoryLog.setStateMachine(stateMachine);
        memoryLog.appendEntry(4, "test".getBytes()); // index 4
        Assert.assertEquals(0, stateMachine.getLastApplied());
        memoryLog.advanceCommitIndex(4, 4);
        Assert.assertEquals(4, stateMachine.getLastApplied());
    }

    @Test
    public void testAdvanceCommitIndexGenerateSnapshot() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1);
        memoryLog.advanceCommitIndex(1, 1);
        memoryLog.generateSnapshot(1, Collections.emptySet());
        memoryLog.appendEntry(2);
    }

    @Test
    public void testInstallSnapshotLessThanLastIncludedIndex() {
        MemoryLog memoryLog = new MemoryLog(
                new MemorySnapshot(3, 4),
                new MemoryEntrySequence(4),
                new EventBus()
        );
        InstallSnapshotRpc rpc = new InstallSnapshotRpc();
        rpc.setLastIndex(2);
        rpc.setLastTerm(3);
        Assert.assertEquals(InstallSnapshotState.StateName.ILLEGAL_INSTALL_SNAPSHOT_RPC, memoryLog.installSnapshot(rpc).getStateName());
    }

    @Test
    public void testInstallSnapshot() {
        EmptyStateMachine stateMachine = new EmptyStateMachine();
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.setStateMachine(stateMachine);
        InstallSnapshotRpc rpc = new InstallSnapshotRpc();
        rpc.setLastIndex(2);
        rpc.setLastTerm(3);
        rpc.setLastConfig(Collections.emptySet());
        rpc.setData(new byte[0]);
        rpc.setDone(true);
        Assert.assertEquals(0, memoryLog.commitIndex);
        Assert.assertEquals(0, stateMachine.getLastApplied());
        memoryLog.installSnapshot(rpc);
        Assert.assertEquals(2, memoryLog.commitIndex);
        Assert.assertEquals(2, stateMachine.getLastApplied());
    }

    @Test
    public void testInstallSnapshot2() {
        EmptyStateMachine stateMachine = new EmptyStateMachine();
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.setStateMachine(stateMachine);
        InstallSnapshotRpc rpc = new InstallSnapshotRpc();
        rpc.setLastIndex(2);
        rpc.setLastTerm(3);
        rpc.setLastConfig(Collections.emptySet());
        rpc.setData(new byte[0]);
        rpc.setDone(false);
        memoryLog.installSnapshot(rpc);
        Assert.assertEquals(0, memoryLog.commitIndex);
        Assert.assertEquals(0, stateMachine.getLastApplied());
    }


}