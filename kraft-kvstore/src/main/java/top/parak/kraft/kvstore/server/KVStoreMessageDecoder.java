package top.parak.kraft.kvstore.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import top.parak.kraft.kvstore.message.AddNodeCommand;
import top.parak.kraft.kvstore.message.RemoveNodeCommand;
import top.parak.kraft.kvstore.message.MessageConstants;
import top.parak.kraft.kvstore.support.proto.Protos;
import top.parak.kraft.kvstore.message.*;

import java.util.List;

/**
 * KV-store message decoder.
 *
 * <p><b>Transport protocol between KV-store server and KV-store client</b></p>
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
public class KVStoreMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 8) return;

        in.markReaderIndex();
        int messageType = in.readInt();
        int messageLength = in.readInt();
        if (in.readableBytes() < messageLength) {
            in.resetReaderIndex();
            return;
        }

        byte[] message = new byte[messageLength];
        in.readBytes(message);
        switch (messageType) {
            case MessageConstants.MSG_TYPE_SUCCESS:
                out.add(Success.INSTANCE);
                break;
            case MessageConstants.MSG_TYPE_FAILURE:
                Protos.Failure protoFailure = Protos.Failure.parseFrom(message);
                out.add(new Failure(protoFailure.getErrorCode(), protoFailure.getMessage()));
                break;
            case MessageConstants.MSG_TYPE_REDIRECT:
                Protos.Redirect protoRedirect = Protos.Redirect.parseFrom(message);
                out.add(new Redirect(protoRedirect.getLeaderId()));
                break;
            case MessageConstants.MSG_TYPE_ADD_SERVER_COMMAND:
                Protos.AddNodeCommand protoAddServerCommand = Protos.AddNodeCommand.parseFrom(message);
                out.add(new AddNodeCommand(protoAddServerCommand.getNodeId(), protoAddServerCommand.getHost(), protoAddServerCommand.getPort()));
                break;
            case MessageConstants.MSG_TYPE_REMOVE_SERVER_COMMAND:
                Protos.RemoveNodeCommand protoRemoveServerCommand = Protos.RemoveNodeCommand.parseFrom(message);
                out.add(new RemoveNodeCommand(protoRemoveServerCommand.getNodeId()));
                break;
            case MessageConstants.MSG_TYPE_GET_COMMAND:
                Protos.GetCommand protoGetCommand = Protos.GetCommand.parseFrom(message);
                out.add(new GetCommand(protoGetCommand.getKey()));
                break;
            case MessageConstants.MSG_TYPE_GET_COMMAND_RESPONSE:
                Protos.GetCommandResponse protoGetCommandResponse = Protos.GetCommandResponse.parseFrom(message);
                out.add(new GetCommandResponse(protoGetCommandResponse.getFound(), protoGetCommandResponse.getValue().toByteArray()));
                break;
            case MessageConstants.MSG_TYPE_SET_COMMAND:
                Protos.SetCommand protoSetCommand = Protos.SetCommand.parseFrom(message);
                out.add(new SetCommand(protoSetCommand.getKey(), protoSetCommand.getValue().toByteArray()));
                break;
            default:
                throw new IllegalStateException("unexpected message type " + messageType);
        }
    }

}
