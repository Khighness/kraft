package top.parak.kraft.core.log.statemachine;

/**
 * The context of {@link StateMachine}
 *
 * @author KHighness
 * @since 2022-03-18
 * @email parakovo@gmail.com
 */
public interface StateMachineContext {

    /**
     * Generate snapshot.
     *
     * @param lastIncludedIndex last index of included log
     */
    void generateSnapshot(int lastIncludedIndex);

}
