package top.parak.kraft.core.node;

import com.google.common.eventbus.EventBus;

import top.parak.kraft.core.log.Log;
import top.parak.kraft.core.node.config.NodeConfig;
import top.parak.kraft.core.node.store.NodeStore;
import top.parak.kraft.core.rpc.Connector;
import top.parak.kraft.core.schedule.Scheduler;
import top.parak.kraft.core.support.task.TaskExecutor;

/**
 * Node context.
 * <p>
 * Node context shouldn't change after initialization.
 * </p>
 *
 * @author KHighness
 * @since 2022-05-26
 * @email parakovo@gmail.com
 */
public class NodeContext {

    /**
     * Self id.
     */
    private NodeId selfId;
    /**
     * Node group.
     */
    private NodeGroup group;
    /**
     * Node mode.
     */
    private NodeMode mode;
    /**
     * Node store.
     */
    private NodeStore store;
    /**
     * Node config.
     */
    private NodeConfig config;
    /**
     * Log component.
     */
    private Log log;
    /**
     * Network io connector.
     */
    private Connector connector;
    /**
     * Scheduler component.
     */
    private Scheduler scheduler;
    /**
     * Event bus.
     */
    private EventBus eventBus;
    /**
     * Task executor.
     */
    private TaskExecutor taskExecutor;
    /**
     * Group config change task executor
     */
    private TaskExecutor groupConfigChangeTaskExecutor;

    public NodeId selfId() {
        return selfId;
    }

    public void setSelfId(NodeId selfId) {
        this.selfId = selfId;
    }

    public NodeGroup group() {
        return group;
    }

    public void setGroup(NodeGroup group) {
        this.group = group;
    }

    public NodeMode mode() {
        return mode;
    }

    public void setMode(NodeMode mode) {
        this.mode = mode;
    }

    public NodeStore store() {
        return store;
    }

    public void setStore(NodeStore store) {
        this.store = store;
    }

    public NodeConfig config() {
        return config;
    }

    public void setConfig(NodeConfig config) {
        this.config = config;
    }

    public Log log() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public Connector connector() {
        return connector;
    }

    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    public Scheduler scheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public EventBus eventBus() {
        return eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public TaskExecutor taskExecutor() {
        return taskExecutor;
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public TaskExecutor groupConfigChangeTaskExecutor() {
        return groupConfigChangeTaskExecutor;
    }

    public void setGroupConfigChangeTaskExecutor(TaskExecutor groupConfigChangeTaskExecutor) {
        this.groupConfigChangeTaskExecutor = groupConfigChangeTaskExecutor;
    }

}
