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

    /**
     * Create AbstractNodeRole.
     *
     * @param name role name
     * @param term term
     */
    AbstractNodeRole(RoleName name, int term) {
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
    public RoleNameAndLeaderId getNameAndLeaderId(NodeId selfId) {
        return new RoleNameAndLeaderId(name, getLeaderId(selfId));
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

    /**
     * Compare role state.
     *
     * @param that role to be compared
     * @return true if equal, otherwise false
     */
    public boolean stateEquals(AbstractNodeRole that) {
        if (this.name != that.name || this.term != that.term) {
            return false;
        }
        return doStateEquals(that);
    }

    /**
     * Do compare role state.
     *
     * @param role role to be compared
     * @return true if equal, otherwise false
     */
    protected abstract boolean doStateEquals(AbstractNodeRole role);

}
