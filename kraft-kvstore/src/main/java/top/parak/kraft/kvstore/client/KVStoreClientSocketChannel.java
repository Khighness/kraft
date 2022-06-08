package top.parak.kraft.kvstore.client;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;

import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.service.*;
import top.parak.kraft.kvstore.message.MessageConstants;
import top.parak.kraft.kvstore.server.KVStoreServer;
import top.parak.kraft.kvstore.support.proto.Protos;
import top.parak.kraft.kvstore.message.GetCommand;
import top.parak.kraft.kvstore.message.SetCommand;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * KV-store client socket channel which is implemented by blocking io (bio).
 * <p>
 * {@link KVStoreClient} will create a socket channel for every {@link KVStoreServer}.
 * All socket channels will be managed by {@link CommandContext}.
 * </p>
 *
 * @author KHighness
 * @since 2022-05-29
 * @email parakovo@gmail.com
 */
public class KVStoreClientSocketChannel implements Channel {

    private final String host;
    private final int port;

    public KVStoreClientSocketChannel(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object send(Object message) {
        try (Socket socket = new Socket()) {
            socket.setTcpNoDelay(true);
            socket.connect(new InetSocketAddress(this.host, this.port));
            this.write(socket.getOutputStream(), message);
            return this.read(socket.getInputStream());
        } catch (IOException e) {
            throw new ChannelException("failed to send and receive", e);
        }
    }

    /**
     * Receive message from KV-store server.
     *
     * @param input input stream
     * @return bytes
     * @throws IOException if IO exception occurs
     */
    private Object read(InputStream input) throws IOException {
        DataInputStream dataInput = new DataInputStream(input);
        int messageType = dataInput.readInt();
        int messageLength = dataInput.readInt();
        byte[] message = new byte[messageLength];
        dataInput.readFully(message);
        switch (messageType) {
            case MessageConstants.MSG_TYPE_SUCCESS:
                return null;
            case MessageConstants.MSG_TYPE_FAILURE:
                Protos.Failure protoFailure = Protos.Failure.parseFrom(message);
                throw new ChannelException("error code " + protoFailure.getErrorCode() + ", message " + protoFailure.getMessage());
            case MessageConstants.MSG_TYPE_REDIRECT:
                Protos.Redirect protoRedirect = Protos.Redirect.parseFrom(message);
                throw new RedirectException(new NodeId(protoRedirect.getLeaderId()));
            case MessageConstants.MSG_TYPE_GET_COMMAND_RESPONSE:
                Protos.GetCommandResponse protoGetCommandResponse = Protos.GetCommandResponse.parseFrom(message);
                if (!protoGetCommandResponse.getFound()) {
                    return null;
                }
                return protoGetCommandResponse.getValue().toByteArray();
            default:
                throw new ChannelException("unexpected message type " + messageType);
        }
    }

    /**
     * Send message to KV-store server.
     *
     * @param output output stream
     * @param message message
     * @throws IOException if IO exception occurs
     */
    private void write(OutputStream output, Object message) throws IOException {
        if (message instanceof GetCommand) {
            Protos.GetCommand protoGetCommand = Protos.GetCommand.newBuilder()
                    .setKey(((GetCommand) message).getKey())
                    .build();
            this.write(output, MessageConstants.MSG_TYPE_GET_COMMAND, protoGetCommand);
        } else if (message instanceof SetCommand) {
            SetCommand setCommand = (SetCommand) message;
            Protos.SetCommand protoSetCommand = Protos.SetCommand.newBuilder()
                    .setKey(setCommand.getKey())
                    .setValue(ByteString.copyFrom(setCommand.getValue()))
                    .build();
            this.write(output, MessageConstants.MSG_TYPE_SET_COMMAND, protoSetCommand);
        } else if (message instanceof AddNodeCommand) {
            AddNodeCommand command = (AddNodeCommand) message;
            Protos.AddNodeCommand protoAddServerCommand = Protos.AddNodeCommand.newBuilder()
                    .setNodeId(command.getNodeId())
                    .setHost(command.getHost())
                    .setPort(command.getPort())
                    .build();
            this.write(output, MessageConstants.MSG_TYPE_ADD_SERVER_COMMAND, protoAddServerCommand);
        } else if (message instanceof RemoveNodeCommand) {
            RemoveNodeCommand command = (RemoveNodeCommand) message;
            Protos.RemoveNodeCommand protoRemoveServerCommand = Protos.RemoveNodeCommand.newBuilder()
                    .setNodeId(command.getNodeId().getValue())
                    .build();
            this.write(output, MessageConstants.MSG_TYPE_REMOVE_SERVER_COMMAND, protoRemoveServerCommand);
        }
    }

    /**
     * Do write message.
     *
     * @param output       output stream
     * @param messageType  message type
     * @param message      message
     * @throws IOException if IO exception occurs
     */
    private void write(OutputStream output, int messageType, MessageLite message) throws IOException {
        DataOutputStream dataOutput = new DataOutputStream(output);
        byte[] messageBytes = message.toByteArray();
        dataOutput.writeInt(messageType);
        dataOutput.writeInt(messageBytes.length);
        dataOutput.write(messageBytes);
        dataOutput.flush();
    }

}
