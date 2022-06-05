package top.parak.kraft.core.node.role;

import top.parak.kraft.core.node.NodeId;

/**
 * Abstract node role.
 *
 * @author KHighness
 * @since 2022-03-18
 * @email parakovo@gmail.com
 */
public abstract class AbstractNodeRole {

    private final RoleName name;
    protected final int term;

    protected AbstractNodeRole(RoleName name, int term) {
        this.name = name;
        this.term = term;
    }

    /**
     * Get role name.
     *
     * @return role name
     */
    public RoleName getName() {
        return name;
    }

    /**
     * Get term.
     *
     * @return term
     */
    public int getTerm() {
        return term;
    }

    /**
     * Get role name and leader id by the node's own id.
     *
     * @param selfId the node's own id
     * @return role name and leader id
     */
    public RoleNameANdLeaderId getRoleNameANdLeaderId(NodeId selfId) {
        return new RoleNameANdLeaderId(name, getLeaderId(selfId));
    }

    /**
     * Get leader id by the node's own id.
     *
     * @param selfId the node's own id
     * @return role name and leader id
     */
    public abstract NodeId getLeaderId(NodeId selfId);

    /**
     * Cancel election timeout or log replication task.
     */
    public abstract void cancelTimeoutOrTask();

    /**
     * Get role state.
     *
     * @return role state
     */
    public abstract RoleState getState();

    public boolean stateEquals(AbstractNodeRole that) {
        if (this.name != that.name || this.term != that.term) {
            return false;
        }
        return doStateEquals(that);
    }

    protected abstract boolean doStateEquals(AbstractNodeRole role);

}
