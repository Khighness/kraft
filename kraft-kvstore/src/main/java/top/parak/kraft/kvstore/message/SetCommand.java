package top.parak.kraft.kvstore.message;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import top.parak.kraft.kvstore.support.proto.Protos;

import java.util.UUID;

/**
 * @author KHighness
 * @since 2022-03-14
 * @email parakovo@gmail.com
 */
public class SetCommand {

    private final String requestId;
    private final String key;
    private final byte[] value;

    public SetCommand(String key, byte[] value) {
        this(UUID.randomUUID().toString(), key, value);
    }

    public SetCommand(String requestId, String key, byte[] value) {
        this.requestId = requestId;
        this.key = key;
        this.value = value;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getKey() {
        return key;
    }

    public byte[] getValue() {
        return value;
    }

    public static SetCommand fromBytes(byte[] bytes) {
        try {
            Protos.SetCommand protoCommand = Protos.SetCommand.parseFrom(bytes);
            return new SetCommand(
                    protoCommand.getRequestId(),
                    protoCommand.getKey(),
                    protoCommand.getValue().toByteArray()
            );
        } catch (InvalidProtocolBufferException e) {
            throw new IllegalStateException("failed to deserialize set command", e);
        }
    }

    public byte[] toBytes() {
        return Protos.SetCommand.newBuilder()
                .setRequestId(this.requestId)
                .setKey(this.key)
                .setValue(ByteString.copyFrom(this.value)).build().toByteArray();
    }

    @Override
    public String toString() {
        return "SetCommand{" +
                "key='" + key + '\'' +
                ", requestId='" + requestId + '\'' +
                '}';
    }

}
