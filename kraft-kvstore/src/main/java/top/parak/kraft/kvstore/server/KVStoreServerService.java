package top.parak.kraft.kvstore.server;

import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.parak.kraft.core.log.statemachine.AbstractSingleThreadStateMachine;
import top.parak.kraft.core.log.statemachine.StateMachine;
import top.parak.kraft.core.log.statemachine.StateMachineContext;
import top.parak.kraft.core.node.task.GroupConfigChangeTaskReference;
import top.parak.kraft.core.node.Node;
import top.parak.kraft.core.node.role.RoleName;
import top.parak.kraft.core.node.role.RoleNameAndLeaderId;
import top.parak.kraft.core.service.AddNodeCommand;
import top.parak.kraft.core.service.RemoveNodeCommand;
import top.parak.kraft.kvstore.support.proto.Protos;
import top.parak.kraft.kvstore.message.*;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

/**
 * KV-store server service.
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public class KVStoreServerService {

    private static final Logger logger = LoggerFactory.getLogger(KVStoreServerService.class);

    /**
     * Raft node.
     */
    private final Node node;
    /**
     * Map to store pending commands.
     * <p>
     * As for {@link SetCommand}, it will be stored in {@link #pendingCommands} util
     * {@link StateMachine#applyLog(StateMachineContext, int, byte[], int)}.
     * </p>
     */
    private final ConcurrentMap<String, CommandRequest<?>> pendingCommands = new ConcurrentHashMap<>();
    /**
     * Map to store KV.
     */
    private Map<String, byte[]> map = new HashMap<>();

    /**
     * Create KVStoreServerService.
     *
     * @param node raft node
     */
    public KVStoreServerService(Node node) {
        this.node = node;
        this.node.registerStateMachine(new StateMachineImpl());

    }

    /**
     * Execute {@link AddNodeCommand}.
     *
     * @param commandRequest add node command request
     */
    public void addNode(CommandRequest<AddNodeCommand> commandRequest) {
        Redirect redirect = checkLeadership();
        if (redirect != null) {
            commandRequest.reply(redirect);
            return;
        }

        AddNodeCommand command = commandRequest.getCommand();
        GroupConfigChangeTaskReference taskReference = this.node.addNode(command.toNodeEndpoint());
        awaitResult(taskReference, commandRequest);
    }

    /**
     * Execute {@link RemoveNodeCommand}.
     *
     * @param commandRequest remove node command request
     */
    public void removeNode(CommandRequest<RemoveNodeCommand> commandRequest) {
        Redirect redirect = checkLeadership();
        if (redirect != null) {
            commandRequest.reply(redirect);
            return;
        }

        RemoveNodeCommand command = commandRequest.getCommand();
        GroupConfigChangeTaskReference taskReference = node.removeNode(command.getNodeId());
        awaitResult(taskReference, commandRequest);
    }

    /**
     * Execute {@link SetCommand}.
     *
     * @param commandRequest set command request
     */
    public void set(CommandRequest<SetCommand> commandRequest) {
        Redirect redirect = checkLeadership();
        if (redirect != null) {
            commandRequest.reply(redirect);
            return;
        }

        SetCommand command = commandRequest.getCommand();
        logger.debug("set {}", command.getKey());
        this.pendingCommands.put(command.getRequestId(), commandRequest);
        commandRequest.addCloseListener(() -> pendingCommands.remove(command.getRequestId()));
        this.node.appendLog(command.toBytes());
    }

    /**
     * Execute {@link GetCommand}.
     *
     * @param commandRequest get command request
     */
    public void get(CommandRequest<GetCommand> commandRequest) {
        String key = commandRequest.getCommand().getKey();
        logger.debug("get {}", key);
        byte[] value = this.map.get(key);
        // TODO view from node state machine
        commandRequest.reply(new GetCommandResponse(value));
    }

    /**
     * Check current node's leadership.
     *
     * @return null if current node is leader, otherwise <code>Redirect</code>
     */
    private Redirect checkLeadership() {
        RoleNameAndLeaderId state = node.getRoleNameAndLeaderId();
        if (state.getRoleName() != RoleName.LEADER) {
            return new Redirect(state.getLeaderId());
        }
        return null;
    }

    /**
     * Await result.
     *
     * @param taskReference  task reference
     * @param commandRequest command request
     * @param <T> T
     */
    private <T> void awaitResult(GroupConfigChangeTaskReference taskReference, CommandRequest<T> commandRequest) {
        try {
            switch (taskReference.getResult(3000L)) {
                case OK:
                    commandRequest.reply(Success.INSTANCE);
                    break;
                case TIMEOUT:
                    commandRequest.reply(new Failure(101, "timeout"));
                    break;
                default:
                    commandRequest.reply(new Failure(100, "error"));
            }
        } catch (TimeoutException e) {
            commandRequest.reply(new Failure(101, "timeout"));
        } catch (InterruptedException ignored) {
            commandRequest.reply(new Failure(100, "error"));
        }
    }

    /**
     * Transform map into output stream.
     *
     * @param map    map
     * @param output output stream
     * @throws IOException if IO exception occurs
     */
    static void toSnapshot(Map<String, byte[]> map, OutputStream output) throws IOException {
        Protos.EntryList.Builder entryList = Protos.EntryList.newBuilder();
        for (Map.Entry<String, byte[]> entry : map.entrySet()) {
            entryList.addEntries(
                    Protos.EntryList.Entry.newBuilder()
                            .setKey(entry.getKey())
                            .setValue(ByteString.copyFrom(entry.getValue())).build()
            );
        }
        entryList.build().writeTo(output);
        entryList.build().getSerializedSize();
    }

    /**
     * Transform input stream into map.
     *
     * @param input input stream
     * @return map
     * @throws IOException if IO exception occurs
     */
    static Map<String, byte[]> fromSnapshot(InputStream input) throws IOException {
        Map<String, byte[]> map = new HashMap<>();
        Protos.EntryList entryList = Protos.EntryList.parseFrom(input);
        for (Protos.EntryList.Entry entry : entryList.getEntriesList()) {
            map.put(entry.getKey(), entry.getValue().toByteArray());
        }
        return map;
    }

    /**
     * Implementation of {@link StateMachine}.
     */
    private class StateMachineImpl extends AbstractSingleThreadStateMachine {

        @Override
        protected void applyCommand(@Nonnull byte[] commandBytes) {
            SetCommand command = SetCommand.fromBytes(commandBytes);
            map.put(command.getKey(), command.getValue());
            CommandRequest<?> commandRequest = pendingCommands.remove(command.getRequestId());
            if (commandRequest != null) {
                commandRequest.reply(Success.INSTANCE);
            }
        }

        @Override
        protected void doApplySnapshot(@Nonnull InputStream input) throws IOException {
            map = fromSnapshot(input);
        }

        @Override
        public boolean shouldGenerateSnapshot(int firstLogIndex, int lastApplied) {
            return lastApplied - firstLogIndex > 1;
        }

        @Override
        public void generateSnapshot(@Nonnull OutputStream output) throws IOException {
            toSnapshot(map, output);
        }

    }

}
