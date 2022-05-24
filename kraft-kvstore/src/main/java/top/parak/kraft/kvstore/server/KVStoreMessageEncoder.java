package top.parak.kraft.kvstore.server;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import top.parak.kraft.core.service.AddNodeCommand;
import top.parak.kraft.core.service.RemoveNodeCommand;
import top.parak.kraft.kvstore.message.MessageConstants;
import top.parak.kraft.kvstore.support.proto.Protos;
import top.parak.kraft.kvstore.message.*;

import java.io.IOException;

/**
 * KV-store message decoder.
 *
 * <p><b>Transport protocol between KV-server and KV-client</b></p>
 * <pre>
 *   |<-------(4)------>|<-------(4)------>|<--ContentLength->|
 *   +------------------+------------------+------------------+
 *   |   Message Type   |  Message Length  | Message Content  |
 *   +------------------+------------------+------------------+
 * </pre>
 *
 * @author KHighness
 * @since 2022-03-30
 * @email parakovo@gmail.com
 */
public class KVStoreMessageEncoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (msg instanceof Success) {
            this.writeMessage(MessageConstants.MSG_TYPE_SUCCESS, Protos.Success.newBuilder().build(), out);
        } else if (msg instanceof Failure) {
            Failure failure = (Failure) msg;
            Protos.Failure protoFailure = Protos.Failure.newBuilder()
                    .setErrorCode(failure.getErrorCode())
                    .setMessage(failure.getMessage())
                    .build();
            this.writeMessage(MessageConstants.MSG_TYPE_FAILURE, protoFailure, out);
        } else if (msg instanceof Redirect) {
            Redirect redirect = (Redirect) msg;
            Protos.Redirect protoRedirect = Protos.Redirect.newBuilder()
                    .setLeaderId(redirect.getLeaderId())
                    .build();
            this.writeMessage(MessageConstants.MSG_TYPE_REDIRECT, protoRedirect, out);
        } else if (msg instanceof AddNodeCommand) {
            AddNodeCommand addNodeCommand = (AddNodeCommand) msg;
            Protos.AddNodeCommand protoAddNodeCommand = Protos.AddNodeCommand.newBuilder()
                    .setNodeId(addNodeCommand.getNodeId())
                    .setHost(addNodeCommand.getHost())
                    .setPort(addNodeCommand.getPort())
                    .build();
            this.writeMessage(MessageConstants.MSG_TYPE_ADD_SERVER_COMMAND, protoAddNodeCommand, out);
        } else if (msg instanceof RemoveNodeCommand) {
            RemoveNodeCommand removeNodeCommand = (RemoveNodeCommand) msg;
            Protos.RemoveNodeCommand protoRemoveNodeCommand = Protos.RemoveNodeCommand.newBuilder()
                    .setNodeId(removeNodeCommand.getNodeId().getValue())
                    .build();
            this.writeMessage(MessageConstants.MSG_TYPE_REMOVE_SERVER_COMMAND, protoRemoveNodeCommand, out);
        } else if (msg instanceof GetCommand) {
            GetCommand getCommand = (GetCommand) msg;
            Protos.GetCommand protoGetCommand = Protos.GetCommand.newBuilder()
                    .setKey(getCommand.getKey())
                    .build();
            this.writeMessage(MessageConstants.MSG_TYPE_GET_COMMAND, protoGetCommand, out);
        } else if (msg instanceof GetCommandResponse) {
            GetCommandResponse getCommandResponse = (GetCommandResponse) msg;
            byte[] value = getCommandResponse.getValue();
            Protos.GetCommandResponse protoGetCommandResponse = Protos.GetCommandResponse.newBuilder()
                    .setFound(getCommandResponse.isFound())
                    .setValue(value != null ? ByteString.copyFrom(value) : ByteString.EMPTY)
                    .build();
            this.writeMessage(MessageConstants.MSG_TYPE_GET_COMMAND_RESPONSE, protoGetCommandResponse, out);
        } else if (msg instanceof SetCommand) {
            SetCommand setCommand = (SetCommand) msg;
            Protos.SetCommand protoSetCommand = Protos.SetCommand.newBuilder()
                    .setKey(setCommand.getKey())
                    .setValue(ByteString.copyFrom(setCommand.getValue()))
                    .build();
            this.writeMessage(MessageConstants.MSG_TYPE_SET_COMMAND, protoSetCommand, out);
        }
    }

    private void writeMessage(int messageType, MessageLite message, ByteBuf out) throws IOException {
        // 1. write message type (4 bytes)
        out.writeInt(messageType);
        // 2. write message
        byte[] bytes = message.toByteArray();
        // 2.1 write message length (4 bytes)
        out.writeInt(bytes.length);
        // 2.2 write message content (unfixed bytes)
        out.writeBytes(bytes);
    }

}
