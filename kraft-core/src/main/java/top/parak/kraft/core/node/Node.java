package top.parak.kraft.core.node;

import top.parak.kraft.core.log.statemachine.StateMachine;
import top.parak.kraft.core.node.role.RoleNameAndLeaderId;
import top.parak.kraft.core.node.task.GroupConfigChangeTaskReference;

import javax.annotation.Nonnull;

/**
 * Node.
 *
 * @author KHighness
 * @since 2022-03-18
 * @email parakovo@gmail.com
 */
public interface Node {

    /**
     * Register state machine to node.
     * <p>State machine should be registered before node starts, or it may not task effect.</p>
     *
     * @param stateMachine state machine
     */
    void registerStateMachine(@Nonnull StateMachine stateMachine);

    /**
     * Get current role name and leader id.
     * <p><b>Available results</b></p>
     * <ul>
     * <li>FOLLOWER, current leader</li>
     * <li>CANDIDATE, <code>null</code></li>
     * <li>LEADER, self id</li>
     * </ul>
     *
     * @return role name and leader id
     */
    @Nonnull
    RoleNameAndLeaderId getRoleNameAndLeaderId();

    /**
     * Add node role listener.
     *
     * @param listener listener
     */
    void addNodeRoleListener(@Nonnull NodeRoleListener listener);

    /**
     * Start node.
     */
    void start();

    /**
     * Append log.
     *
     * @param commandBytes command bytes
     * @throws NotLeaderException if current node is not a leader
     */
    void appendLog(@Nonnull byte[] commandBytes);

    /**
     * Add node.
     *
     * @param endpoint new node endpoint
     * @return task reference
     * @throws NotLeaderException    if current node is not a leader
     * @throws IllegalStateException if group config change concurrently
     */
    @Nonnull
    GroupConfigChangeTaskReference addNode(@Nonnull NodeEndpoint endpoint);

    /**
     * Remove node.
     *
     * @param id node id
     * @return task reference
     * @throws NotLeaderException    if current node is not a leader
     * @throws IllegalStateException if group config change concurrently
     */
    @Nonnull
    GroupConfigChangeTaskReference removeNode(@Nonnull NodeId id);

    /**
     * Stop node.
     *
     * @throws InterruptedException if interrupted
     */
    void stop() throws InterruptedException;

}
