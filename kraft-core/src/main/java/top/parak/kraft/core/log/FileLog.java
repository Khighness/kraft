package top.parak.kraft.core.log;

import com.google.common.eventbus.EventBus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.parak.kraft.core.log.entry.Entry;
import top.parak.kraft.core.log.entry.EntryMeta;
import top.parak.kraft.core.log.sequence.FileEntrySequence;
import top.parak.kraft.core.log.snapshot.*;
import top.parak.kraft.core.node.NodeEndpoint;
import top.parak.kraft.core.rpc.message.InstallSnapshotRpc;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * File log.
 *
 * @author KHighness
 * @since 2022-04-06
 * @email parakovo@gmail.com
 */
@NotThreadSafe
public class FileLog extends AbstractLog {

    private static final Logger logger = LoggerFactory.getLogger(FileLog.class);
    private final RootDir rootDir;

    /**
     * Create FileLog.
     *
     * @param baseDir  base directory
     * @param eventBus event bus
     */
    public FileLog(File baseDir, EventBus eventBus) {
        super(eventBus);
        rootDir= new RootDir(baseDir);

        LogGeneration latestGeneration = rootDir.getLatestGeneration();
        snapshot = new EmptySnapshot();
        if (latestGeneration != null) {
            if (latestGeneration.getSnapshotFile().exists()) {
                snapshot = new FileSnapshot(latestGeneration);
            }
            FileEntrySequence fileEntrySequence = new FileEntrySequence(latestGeneration,
                    snapshot.getLastIncludedIndex() + 1);
            commitIndex = fileEntrySequence.getCommitIndex();
            entrySequence = fileEntrySequence;
            groupConfigEntryList = entrySequence.buildGroupConfigEntryList();
        } else {
            LogGeneration firstGeneration = rootDir.createFirstGeneration();
            entrySequence = new FileEntrySequence(firstGeneration, 1);
        }
    }

    @Override
    protected SnapshotBuilder newSnapshotBuilder(InstallSnapshotRpc firstRpc) {
        return new FileSnapshotBuilder(firstRpc, rootDir.getLogDirForInstalling());
    }

    @Override
    protected void replaceSnapshot(Snapshot newSnapshot) {
        FileSnapshot fileSnapshot = (FileSnapshot) newSnapshot;
        int lastIncludedIndex = fileSnapshot.getLastIncludedIndex();
        int logIndexOffset = lastIncludedIndex + 1;

        List<Entry> remainingEntries = entrySequence.subView(logIndexOffset);
        FileEntrySequence newEntrySequence = new FileEntrySequence(fileSnapshot.getLogDir(), logIndexOffset);
        newEntrySequence.append(remainingEntries);
        newEntrySequence.commit(Math.max(commitIndex, lastIncludedIndex));
        newEntrySequence.close();

        snapshot.close();
        entrySequence.close();
        newEntrySequence.close();

        LogDir generation = rootDir.rename(fileSnapshot.getLogDir(), lastIncludedIndex);
        snapshot = new FileSnapshot(generation);
        logger.debug("snapshot -> {}", snapshot);
        entrySequence = new FileEntrySequence(generation, logIndexOffset);
        logger.debug("entry sequence -> {}", entrySequence);
        groupConfigEntryList = entrySequence.buildGroupConfigEntryList();
        commitIndex = entrySequence.getCommitIndex();
    }

    @Override
    protected Snapshot generateSnapshot(EntryMeta lastAppliedEntryMeta, Set<NodeEndpoint> groupConfig) {
        LogDir logDir = rootDir.getLogDirForGenerating();
        try (FileSnapshotWriter snapshotWriter = new FileSnapshotWriter(
                logDir.getSnapshotFile(), lastAppliedEntryMeta.getIndex(), lastAppliedEntryMeta.getTerm(),groupConfig)
        ) {
            stateMachine.generateSnapshot(snapshotWriter.getOutput());
        } catch (IOException e) {
            throw new LogException("failed to generate snapshot", e);
        }
        return new FileSnapshot(logDir);
    }

}
