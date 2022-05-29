package top.parak.kraft.kvstore.message;

/**
 * GetCommand Message.
 *
 * @author KHighness
 * @since 2022-03-14
 * @email parakovo@gmail.com
 */
public class GetCommand {

    private final String key;

    public GetCommand(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "GetCommand{" +
                "key='" + key + '\'' +
                '}';
    }
}
