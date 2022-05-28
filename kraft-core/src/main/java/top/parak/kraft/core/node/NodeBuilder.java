package top.parak.kraft.core.node;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import io.netty.channel.nio.NioEventLoopGroup;

import top.parak.kraft.core.log.Log;
import top.parak.kraft.core.node.config.NodeConfig;
import top.parak.kraft.core.log.FileLog;
import top.parak.kraft.core.log.MemoryLog;
import top.parak.kraft.core.node.store.FileNodeStore;
import top.parak.kraft.core.node.store.MemoryNodeStore;
import top.parak.kraft.core.node.store.NodeStore;
import top.parak.kraft.core.rpc.Connector;
import top.parak.kraft.core.rpc.nio.NioConnector;
import top.parak.kraft.core.schedule.DefaultScheduler;
import top.parak.kraft.core.schedule.Scheduler;
import top.parak.kraft.core.support.task.ListeningTaskExecutor;
import top.parak.kraft.core.support.task.TaskExecutor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Executors;

/**
 * Node builder.
 *
 * @author KHighness
 * @since 2022-04-01
 * @email parakovo@gmail.com
 */
public class NodeBuilder {

    /**
     * Node group.
     */
    private final NodeGroup group;

    /**
     * Self id.
     */
    private final NodeId selfId;

    /**
     * Event bus.
     */
    private final EventBus eventBus;

    /**
     * Node configuration.
     */
    private NodeConfig config = new NodeConfig();

    /**
     * Start as standby or not.
     */
    private boolean standby = false;

    /**
     * Log.
     * If data directory specified, {@link FileLog} will be created.
     * Default to {@link MemoryLog}
     */
    private Log log = null;

    /**
     * Store for current term and last node id voted for.
     * If data directory specified, {@link FileNodeStore} will be created.
     * Default to {@link MemoryNodeStore}.
     */
    private NodeStore store = null;

    /**
     * Scheduler, INTERNAL.
     */
    private Scheduler scheduler = null;

    /**
     * Connector, component to communicate between nodes, INTERNAL.
     */
    private Connector connector = null;

    /**
     * Task executor for node, INTERNAL.
     */
    private TaskExecutor taskExecutor = null;

    /**
     * Task executor for group config change task, INTERNAL.
     */
    private TaskExecutor groupConfigChangeTaskExecutor = null;

    /**
     * Event loop group for worker.
     * If specified, reuse, otherwise create one.
     */
    private NioEventLoopGroup workerGroup = null;

    /**
     * Create NodeBuilder whose type is standby.
     *
     * @param endpoint endpoint.
     */
    public NodeBuilder(@Nonnull NodeEndpoint endpoint) {
        this(Collections.singleton(endpoint), endpoint.getId());
    }

    /**
     * Create NodeBuilder whose type is group member.
     *
     * @param endpoints endpoints
     * @param selfId    self id
     */
    private NodeBuilder(@Nonnull Collection<NodeEndpoint> endpoints, @Nonnull NodeId selfId) {
        Preconditions.checkNotNull(endpoints);
        Preconditions.checkNotNull(selfId);
        this.group = new NodeGroup(endpoints, selfId);
        this.selfId = selfId;
        this.eventBus = new EventBus(selfId.getValue());
    }

    /**
     * Create NodeBuilder.
     *
     * @param selfId self id
     * @param group  group
     */
    public NodeBuilder(@Nonnull NodeId selfId, @Nonnull NodeGroup group) {
        Preconditions.checkNotNull(selfId);
        Preconditions.checkNotNull(group);
        this.selfId = selfId;
        this.group = group;
        this.eventBus = new EventBus(selfId.getValue());
    }

    /**
     * Set configuration.
     *
     * @param config config
     * @return this
     */
    public NodeBuilder setConfig(NodeConfig config) {
        Preconditions.checkNotNull(config);
        this.config = config;
        return this;
    }

    /**
     * Set standby.
     *
     * @param standby standby
     * @return this
     */
    public NodeBuilder setStandby(boolean standby) {
        this.standby = standby;
        return this;
    }

    /**
     * Set store.
     *
     * @param store store
     * @return this
     */
    public NodeBuilder setStore(NodeStore store) {
        Preconditions.checkNotNull(store);
        this.store = store;
        return this;
    }

    /**
     * Set scheduler.
     *
     * @param scheduler scheduler
     * @return this
     */
    public NodeBuilder setScheduler(Scheduler scheduler) {
        Preconditions.checkNotNull(scheduler);
        this.scheduler = scheduler;
        return this;
    }

    /**
     * Set connector.
     *
     * @param connector connector
     * @return this
     */
    public NodeBuilder setConnector(Connector connector) {
        Preconditions.checkNotNull(connector);
        this.connector = connector;
        return this;
    }

    /**
     * Set task executor.
     *
     * @param taskExecutor task executor
     * @return this
     */
    public NodeBuilder setTaskExecutor(TaskExecutor taskExecutor) {
        Preconditions.checkNotNull(taskExecutor);
        this.taskExecutor = taskExecutor;
        return this;
    }

    /**
     * Set group config change task executor.
     *
     * @param groupConfigChangeTaskExecutor group config change task executor
     * @return this
     */
    public NodeBuilder setGroupConfigChangeTaskExecutor(TaskExecutor groupConfigChangeTaskExecutor) {
        Preconditions.checkNotNull(groupConfigChangeTaskExecutor);
        this.groupConfigChangeTaskExecutor = groupConfigChangeTaskExecutor;
        return this;
    }

    /**
     * Set event loop group for worker.
     *
     * @param workerGroup event loop group for worker
     * @return this
     */
    public NodeBuilder setWorkerGroup(NioEventLoopGroup workerGroup) {
        Preconditions.checkNotNull(workerGroup);
        this.workerGroup = workerGroup;
        return this;
    }

    /**
     * Set data directory.
     *
     * @param dataDirPath data directory.
     * @return this
     */
    public NodeBuilder setDataDir(@Nullable String dataDirPath) {
        if (dataDirPath == null || dataDirPath.isEmpty()) {
            return this;
        }
        File dataDir = new File(dataDirPath);
        if (!dataDir.isDirectory() || !dataDir.exists()) {
            throw new IllegalArgumentException("[" + dataDirPath + "] isn't a directory, or it doesn't exist");
        }
        log = new FileLog(dataDir, eventBus);
        store = new FileNodeStore(new File(dataDir, FileNodeStore.FILE_NAME));
        return this;
    }

    /**
     * Build context for node.
     *
     * @return node context
     */
    @Nonnull
    private NodeContext buildContext() {
        NodeContext nodeContext = new NodeContext();
        nodeContext.setConfig(config);
        nodeContext.setMode(evaluateMode());
        nodeContext.setLog(log != null ? log : new MemoryLog(eventBus));
        nodeContext.setSelfId(selfId);
        nodeContext.setConfig(config);
        nodeContext.setEventBus(eventBus);
        nodeContext.setScheduler(scheduler != null ? scheduler : new DefaultScheduler(config));
        nodeContext.setConnector(connector != null ? connector : createNioCreator());
        nodeContext.setTaskExecutor(taskExecutor != null ? taskExecutor : new ListeningTaskExecutor(
                Executors.newSingleThreadExecutor(r -> new Thread(r, "node"))
        ));
        nodeContext.setGroupConfigChangeTaskExecutor(groupConfigChangeTaskExecutor != null ? groupConfigChangeTaskExecutor :
                new ListeningTaskExecutor(Executors.newSingleThreadExecutor(r -> new Thread(r, "group-config-change"))));
        return nodeContext;
    }

    /**
     * Evaluate mode.
     *
     * @return mode
     * @see NodeGroup#isStandalone()
     */
    @Nonnull
    public NodeMode evaluateMode() {
        if (standby) {
            return NodeMode.STANDBY;
        }
        if (group.isStandalone()) {
            return NodeMode.STANDALONE;
        }
        return NodeMode.GROUP_MEMBER;
    }

    /**
     * Create nio connector.
     *
     * @return nio connector
     */
    @Nonnull
    private NioConnector createNioCreator() {
        int port = group.findSelf().getEndpoint().getPort();
        if (workerGroup != null) {
            return new NioConnector(workerGroup, selfId, eventBus, port, config.getLogReplicationInterval());
        }
        return new NioConnector(new NioEventLoopGroup(config.getNioWorkerThreads()), false,
                selfId, eventBus, port, config.getLogReplicationInterval());
    }

}
