package top.parak.kraft.core.node.role;

import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.schedule.LogReplicationTask;

import javax.annotation.concurrent.Immutable;

/**
 * Leader node role.
 *
 * @author KHighness
 * @since 2022-03-19
 * @email parakovo@gmail.com
 */
@Immutable
public class LeaderNodeRole extends AbstractNodeRole {

    private final LogReplicationTask logReplicationTask;

    public LeaderNodeRole(int term, LogReplicationTask logReplicationTask) {
        super(RoleName.LEADER, term);
        this.logReplicationTask = logReplicationTask;
    }

    @Override
    public NodeId getLeaderId(NodeId selfId) {
        return selfId;
    }

    @Override
    public void cancelTimeoutOrTask() {
        logReplicationTask.cancel();
    }

    @Override
    public RoleState getState() {
        return new DefaultRoleState(RoleName.LEADER, term);
    }

    @Override
    protected boolean doStateEquals(AbstractNodeRole role) {
        return true;
    }

    @Override
    public String toString() {
        return "LeaderNodeRole{" +
                "term=" + term +
                ", logReplicationTask=" + logReplicationTask +
                '}';
    }
}
