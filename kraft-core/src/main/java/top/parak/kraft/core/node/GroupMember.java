package top.parak.kraft.core.node;

/**
 * State of group member.
 *
 * @author KHighness
 * @since 2022-03-19
 * @email parakovo@gmail.com
 * @see ReplicatingState
 */
public class GroupMember {

    private final NodeEndpoint endpoint;
    private ReplicatingState replicatingState;
    private boolean major;
    private boolean removing = false;

    GroupMember(NodeEndpoint endpoint) {
        this(endpoint, null, true);
    }

    GroupMember(NodeEndpoint endpoint, ReplicatingState replicatingState, boolean major) {
        this.endpoint = endpoint;
        this.replicatingState = replicatingState;
        this.major = major;
    }

    public NodeEndpoint getEndpoint() {
        return endpoint;
    }

    public ReplicatingState getReplicatingState() {
        return replicatingState;
    }

    public void setReplicatingState(ReplicatingState replicatingState) {
        this.replicatingState = replicatingState;
    }

    public boolean isMajor() {
        return major;
    }

    public void setMajor(boolean major) {
        this.major = major;
    }

    public boolean isRemoving() {
        return removing;
    }

    public void setRemoving() {
        this.removing = true;
    }

}
