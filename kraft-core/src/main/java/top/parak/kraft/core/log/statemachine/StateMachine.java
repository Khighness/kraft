package top.parak.kraft.core.log.statemachine;

import top.parak.kraft.core.log.snapshot.Snapshot;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;

/**
 * State machine.
 *
 * @author KHighness
 * @since 2022-03-18
 * @email parakovo@gmail.com
 */
public interface StateMachine {

    /**
     * Get the index of the last applied log.
     *
     * @return the index of the last applied log
     */
    int getLastApplied();

    /**
     * Apply the log entry to the state machine.
     *
     * @param context      the context of the state machine
     * @param index        the index of the log entry to be applied
     * @param commandBytes the bytes of the command
     * @param firstLogIndex the index of the first applied log
     */
    void applyLog(StateMachineContext context, int index, @Nonnull byte[] commandBytes, int firstLogIndex);

    /**
     * Return whether the state machine should generate snapshot or not.
     *
     * @param firstLogIndex the index of the first applied log, may not be {@code 0}
     * @param lastApplied  the index of the last applied log
     * @return true if the state machine should generate snapshot, otherwise false
     */
    boolean shouldGenerateSnapshot(int firstLogIndex, int lastApplied);

    /**
     * Generate snapshot of state machine into the output stream.
     *
     * @param output the output stream
     * @throws IOException if IO exception occurs
     */
    void generateSnapshot(@Nonnull OutputStream output) throws IOException;

    /**
     * Apply the snapshot to the state machine
     *
     * @param snapshot the snapshot
     * @throws IOException if IO exception occurs
     */
    void applySnapshot(@Nonnull Snapshot snapshot) throws IOException;

    /**
     * Shutdown the state machine.
     */
    void shutdown();

}
