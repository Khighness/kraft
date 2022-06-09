package top.parak.kraft.core.log.entry;

import com.google.protobuf.InvalidProtocolBufferException;

import top.parak.kraft.core.Protos;
import top.parak.kraft.core.node.NodeEndpoint;
import top.parak.kraft.core.node.NodeId;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Log entry factory.
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public class EntryFactory {

    /**
     * Create log entry.
     * <ul>
     * <li>{@link NoOpEntry}</li>
     * <li>{@link GeneralEntry}</li>
     * <li>{@link AddNodeEntry}</li>
     * <li>{@link RemoveNodeEntry}</li>
     * </ul>
     *
     * @param kind          the kind of the log entry
     * @param index         the index of the log entry
     * @param term          the term of the log entry
     * @param commandBytes  the command bytes of the log entry
     * @return log entry
     */
    public Entry create(int kind, int index, int term, byte[] commandBytes) {
        try {
            switch (kind) {
                case Entry.KIND_NO_OP:
                    return new NoOpEntry(index, term);
                case Entry.KIND_GENERAL:
                    return new GeneralEntry(index, term, commandBytes);
                case Entry.KIND_ADD_NODE:
                    Protos.AddNodeCommand addNodeCommand = Protos.AddNodeCommand.parseFrom(commandBytes);
                    return new AddNodeEntry(index, term, asNodeEndpoints(addNodeCommand.getNodeEndpointsList()), asNodeEndpoint(addNodeCommand.getNewNodeEndpoint()));
                case Entry.KIND_REMOVE_NODE:
                    Protos.RemoveNodeCommand removeNodeCommand = Protos.RemoveNodeCommand.parseFrom(commandBytes);
                    return new RemoveNodeEntry(index, term, asNodeEndpoints(removeNodeCommand.getNodeEndpointsList()), new NodeId(removeNodeCommand.getNodeToRemove()));
                default:
                    throw new IllegalArgumentException("unexpected entry kind " + kind);
            }
        } catch (InvalidProtocolBufferException e) {
            throw new IllegalStateException("failed to parse command", e);
        }
    }

    private Set<NodeEndpoint> asNodeEndpoints(Collection<Protos.NodeEndpoint> protoNodeEndpoints) {
        return protoNodeEndpoints.stream().map(this::asNodeEndpoint).collect(Collectors.toSet());
    }

    private NodeEndpoint asNodeEndpoint(Protos.NodeEndpoint protoNodeEndpoint) {
        return new NodeEndpoint(protoNodeEndpoint.getId(), protoNodeEndpoint.getHost(), protoNodeEndpoint.getPort());
    }

}
