package top.parak.kraft.core.node;

import com.google.common.util.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.parak.kraft.core.log.statemachine.StateMachine;
import top.parak.kraft.core.node.role.AbstractNodeRole;
import top.parak.kraft.core.node.role.RoleNameANdLeaderId;
import top.parak.kraft.core.node.task.GroupConfigChangeTaskReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Node implementation.
 *
 * @author KHighness
 * @since 2022-05-28
 * @email parakovo@gmail.com
 */
@ThreadSafe
public class NodeImpl implements Node {

    private static final Logger logger = LoggerFactory.getLogger(NodeImpl.class);

    /**
     * Callback for async tasks。
     */
    private static final FutureCallback<Object> LOGGING_FUTURE_CALLBACK = new FutureCallback<Object>() {
        @Override
        public void onSuccess(@Nullable Object result) {
        }

        @Override
        public void onFailure(@Nonnull Throwable t) {
            logger.warn("failure", t);
        }
    };

    private final NodeContext context;
    @GuardedBy("this")
    private boolean started;
    private volatile AbstractNodeRole role;
    private final List<NodeRoleListener> roleListeners = new CopyOnWriteArrayList<>();

    @Override
    public void registerStateMachine(@Nonnull StateMachine stateMachine) {

    }

    @Nonnull
    @Override
    public RoleNameANdLeaderId getRoleNameANdLeaderId() {
        return null;
    }

    @Override
    public void addNoeRoleListener(@Nonnull NodeRoleListener listener) {

    }

    @Override
    public void start() {

    }

    @Override
    public void appendLog(@Nonnull byte[] commandBytes) {

    }

    @Override
    public GroupConfigChangeTaskReference addNode(@Nonnull NodeEndpoint endpoint) {
        return null;
    }

    @Override
    public GroupConfigChangeTaskReference removeNode(@Nonnull NodeId id) {
        return null;
    }

    @Override
    public void stop() throws InterruptedException {

    }

    private class NewNodeCatchUpTaskContextImpl implements

}
