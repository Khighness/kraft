package top.parak.kraft.core.log;

import top.parak.kraft.core.node.NodeEndpoint;

import java.util.Set;

/**
 * Install snapshot state.
 *
 * @author KHighness
 * @since 2022-04-07
 * @email parakovo@gmail.com
 */
public class InstallSnapshotState {

    public enum StateName {
        ILLEGAL_INSTALL_SNAPSHOT_RPC,
        INSTALLING,
        INSTALLED
    }

    private final StateName stateName;
    private Set<NodeEndpoint> lastConfig;

    public InstallSnapshotState(StateName stateName) {
        this.stateName = stateName;
    }

    public InstallSnapshotState(StateName stateName, Set<NodeEndpoint> lastConfig) {
        this.stateName = stateName;
        this.lastConfig = lastConfig;
    }

    public StateName getStateName() {
        return stateName;
    }

    public Set<NodeEndpoint> getLastConfig() {
        return lastConfig;
    }

}
