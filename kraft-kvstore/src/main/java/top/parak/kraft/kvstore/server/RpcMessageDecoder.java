package top.parak.kraft.kvstore.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import top.parak.kraft.core.service.AddNodeCommand;
import top.parak.kraft.core.service.RemoveNodeCommand;
import top.parak.kraft.kvstore.message.MessageConstants;
import top.parak.kraft.kvstore.support.proto.Protos;
import top.parak.kraft.kvstore.message.*;

import java.util.List;

/**
 * Rpc message decoder.
 * <p><b>Protocol</b></p>
 * <pre>
 *   |<-------(4)------>|<-------(4)------>|<--MessageLength->|
 *   +------------------+------------------+------------------+
 *   |   MessageType    |   MessageLength  |   MessageBytes   |
 *   +------------------+------------------+------------------+
 * </pre>
 *
 * @author KHighness
 * @since 2022-03-30
 * @email parakovo@gmail.com
 */
public class RpcMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 8) {
            return;
        }

        in.markReaderIndex();
        int massageType = in.readInt();
        int messageLength = in.readInt();
        if (in.readableBytes() < messageLength) {
            in.resetReaderIndex();
            return;
        }

        byte[] message = new byte[messageLength];
        in.readBytes(message);
        switch (massageType) {
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
                Protos.AddNodeCommand protoAddNodeCommand = Protos.AddNodeCommand.parseFrom(message);
                out.add(new AddNodeCommand(protoAddNodeCommand.getNodeId(), protoAddNodeCommand.getHost(), protoAddNodeCommand.getPort()));
                break;
            case MessageConstants.MSG_TYPE_REMOVE_SERVER_COMMAND:
                Protos.RemoveNodeCommand protoRemoveNodeCommand = Protos.RemoveNodeCommand.parseFrom(message);
                out.add(new RemoveNodeCommand(protoRemoveNodeCommand.getNodeId()));
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
                throw new IllegalStateException("unexpected message type: " + massageType);
        }
    }

}
