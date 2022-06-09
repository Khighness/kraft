package top.parak.kraft.core.node.store;

/**
 * Thrown when failing to store state into store.
 *
 * @author KHighness
 * @since 2022-03-20
 * @email parakovo@gmail.com
 */
public class NodeStoreException extends RuntimeException {

    public NodeStoreException(Throwable cause) {
        super(cause);
    }

    public NodeStoreException(String message, Throwable cause) {
        super(message, cause);
    }

}
