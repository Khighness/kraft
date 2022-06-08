package top.parak.kraft.core.log.statemachine;

public interface StateMachineContext {

    void generateSnapshot(int lastIncludedIndex);

}
