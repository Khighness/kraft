package top.parak.kraft.core.log;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.parak.kraft.core.log.entry.Entry;
import top.parak.kraft.core.log.entry.EntryMeta;
import top.parak.kraft.core.log.sequence.EntrySequence;
import top.parak.kraft.core.log.sequence.MemoryEntrySequence;
import top.parak.kraft.core.log.snapshot.*;
import top.parak.kraft.core.node.NodeEndpoint;
import top.parak.kraft.core.rpc.message.InstallSnapshotRpc;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Memory log.
 *
 * @author KHighness
 * @since 2022-04-01
 * @email parakovo@gmail.com
 */
@NotThreadSafe
public class MemoryLog extends AbstractLog {

    private static final Logger logger = LoggerFactory.getLogger(MemoryLog.class);

    /**
     * Create MemoryLog.
     */
    public MemoryLog() {
        this(new EventBus());
    }

    /**
     * Create MemoryLog.
     *
     * @param eventBus event bus
     */
    public MemoryLog(EventBus eventBus) {
        this(new EmptySnapshot(), new MemoryEntrySequence(), eventBus);
    }

    /**
     * Create MemoryLog.
     *
     * @param snapshot      snapshot
     * @param entrySequence entry sequence
     * @param eventBus      event bus
     */
    public MemoryLog(Snapshot snapshot, EntrySequence entrySequence, EventBus eventBus) {
        super(eventBus);
        this.snapshot = snapshot;
        this.entrySequence = entrySequence;
    }

    @Override
    protected SnapshotBuilder newSnapshotBuilder(InstallSnapshotRpc firstRpc) {
        return new MemorySnapshotBuilder(firstRpc);
    }

    @Override
    protected void replaceSnapshot(Snapshot newSnapshot) {
        int logIndexOffset = newSnapshot.getLastIncludedIndex() + 1;
        MemoryEntrySequence newEntrySequence = new MemoryEntrySequence(logIndexOffset);
        List<Entry> remainingEntries = entrySequence.subView(logIndexOffset);
        newEntrySequence.append(remainingEntries);
        snapshot = newSnapshot;
        logger.debug("snapshot -> {}", snapshot);
        entrySequence = newEntrySequence;
        logger.debug("entry sequence -> {}", entrySequence);
    }

    @Override
    protected Snapshot generateSnapshot(EntryMeta lastAppliedEntryMeta, Set<NodeEndpoint> groupConfig) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            stateMachine.generateSnapshot(output);
        } catch (IOException e) {
            throw new LogException("failed to generate snapshot", e);
        }
        return new MemorySnapshot(lastAppliedEntryMeta.getIndex(), lastAppliedEntryMeta.getTerm(), output.toByteArray(), groupConfig);
    }

}
