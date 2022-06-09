package top.parak.kraft.kvstore.message;

/**
 * GetCommandResponse message.
 *
 * @author KHighness
 * @since 2022-03-14
 * @email parakovo@gmail.com
 */
public class GetCommandResponse {

    private final boolean found;
    private final byte[] value;

    public GetCommandResponse(byte[] value) {
        this(value != null, value);
    }

    public GetCommandResponse(boolean found, byte[] value) {
        this.found = found;
        this.value = value;
    }

    public boolean isFound() {
        return found;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "GetCommandResponse{found=" + found + '}';
    }

}
