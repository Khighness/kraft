package top.parak.kraft.kvstore.server;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import top.parak.kraft.kvstore.message.AddNodeCommand;
import top.parak.kraft.kvstore.message.RemoveNodeCommand;
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
            AddNodeCommand command = (AddNodeCommand) msg;
            Protos.AddNodeCommand protoCommand = Protos.AddNodeCommand.newBuilder()
                    .setNodeId(command.getNodeId())
                    .setHost(command.getHost())
                    .setPort(command.getPort())
                    .build();
            this.writeMessage(MessageConstants.MSG_TYPE_ADD_SERVER_COMMAND, protoCommand, out);
        } else if (msg instanceof RemoveNodeCommand) {
            RemoveNodeCommand command = (RemoveNodeCommand) msg;
            Protos.RemoveNodeCommand protoCommand = Protos.RemoveNodeCommand.newBuilder()
                    .setNodeId(command.getNodeId().getValue())
                    .build();
            this.writeMessage(MessageConstants.MSG_TYPE_REMOVE_SERVER_COMMAND, protoCommand, out);
        } else if (msg instanceof GetCommand) {
            GetCommand command = (GetCommand) msg;
            Protos.GetCommand protoGetCommand = Protos.GetCommand.newBuilder()
                    .setKey(command.getKey())
                    .build();
            this.writeMessage(MessageConstants.MSG_TYPE_GET_COMMAND, protoGetCommand, out);
        } else if (msg instanceof GetCommandResponse) {
            GetCommandResponse response = (GetCommandResponse) msg;
            byte[] value = response.getValue();
            Protos.GetCommandResponse protoResponse = Protos.GetCommandResponse.newBuilder()
                    .setFound(response.isFound())
                    .setValue(value != null ? ByteString.copyFrom(value) : ByteString.EMPTY)
                    .build();
            this.writeMessage(MessageConstants.MSG_TYPE_GET_COMMAND_RESPONSE, protoResponse, out);
        } else if (msg instanceof SetCommand) {
            SetCommand command = (SetCommand) msg;
            Protos.SetCommand protoSetCommand = Protos.SetCommand.newBuilder()
                    .setKey(command.getKey())
                    .setValue(ByteString.copyFrom(command.getValue()))
                    .build();
            this.writeMessage(MessageConstants.MSG_TYPE_SET_COMMAND, protoSetCommand, out);
        }
    }

    private void writeMessage(int messageType, MessageLite message, ByteBuf out) throws IOException {
        out.writeInt(messageType);
        byte[] bytes = message.toByteArray();
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }

}
