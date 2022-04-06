package top.parak.kraft.kvstore.server;

import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.parak.kraft.core.log.statemachine.AbstractSingleThreadStateMachine;
import top.parak.kraft.core.node.Node;
import top.parak.kraft.core.node.role.RoleName;
import top.parak.kraft.core.node.role.RoleNameANdLeaderId;
import top.parak.kraft.core.node.task.GroupConfigChangeTaskReference;
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
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Netty rpc server service.
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public class NettyRpcServerService {

    private static final Logger logger = LoggerFactory.getLogger(NettyRpcServerService.class);

    private final Node node;
    private final ConcurrentHashMap<String, CommandRequest<?>> pendingCommands = new ConcurrentHashMap<>();
    private Map<String, byte[]> map = new HashMap<>();

    public NettyRpcServerService(Node node) {
        this.node = node;
        this.node.registerStateMachine(new StateMachineImpl());
    }

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

    public void removeNode(CommandRequest<RemoveNodeCommand> commandRequest) {
        Redirect redirect = checkLeadership();
        if (redirect != null) {
            commandRequest.reply(redirect);
            return;
        }

        RemoveNodeCommand command = commandRequest.getCommand();
        GroupConfigChangeTaskReference taskReference = this.node.removeNode(command.getNodeId());
        awaitResult(taskReference, commandRequest);
    }

    public void set(CommandRequest<SetCommand> commandRequest) {
        Redirect redirect = checkLeadership();
        if (redirect != null) {
            commandRequest.reply(redirect);
            return;
        }

        SetCommand command = commandRequest.getCommand();
        logger.debug("set {}", command.getKey());
        this.pendingCommands.put(command.getKey(), commandRequest);
        commandRequest.addCloseListener(() -> pendingCommands.remove(command.getRequestId()));
        this.node.appendLog(command.toBytes());
    }

    public void get(CommandRequest<GetCommand> commandRequest) {
        String key = commandRequest.getCommand().getKey();
        logger.debug("get {}", key);
        byte[] value = this.map.get(key);
        commandRequest.reply(new GetCommandResponse(value));
    }

    private Redirect checkLeadership() {
        RoleNameANdLeaderId state = node.getRoleNameANdLeaderId();
        if (state.getRoleName() != RoleName.LEADER) {
            return new Redirect(state.getLeaderId());
        }
        return null;
    }

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

    static void toSnapshot(Map<String, byte[]> map, OutputStream output) throws IOException {
        Protos.EntryList.Builder entryList = Protos.EntryList.newBuilder();
        map.forEach((key, value) -> entryList.addEntries(
                Protos.EntryList.Entry.newBuilder()
                        .setKey(key)
                        .setValue(ByteString.copyFrom(value))
                        .build()
        ));
        entryList.build().writeTo(output);
        entryList.build().getSerializedSize();
    }

    static Map<String, byte[]> fromSnapshot(InputStream input) throws IOException {
        Protos.EntryList entryList = Protos.EntryList.parseFrom(input);
        return entryList.getEntriesList().stream()
                .collect(Collectors.toMap(Protos.EntryList.Entry::getKey, e -> e.getValue().toByteArray()));
    }

    private class StateMachineImpl extends AbstractSingleThreadStateMachine {

        @Override
        protected void applyCommand(@Nonnull byte[] commandBytes) {
            SetCommand setCommand = SetCommand.fromBytes(commandBytes);
            map.put(setCommand.getKey(), setCommand.getValue());
            CommandRequest<?> commandRequest = pendingCommands.remove(setCommand.getRequestId());
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
