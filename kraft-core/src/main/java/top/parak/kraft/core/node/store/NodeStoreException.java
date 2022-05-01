package top.parak.kraft.core.node.store;

/**
 * Thrown when failing to store state into store.
 *
 * @author KHighness
 * @since 2022-03-20
 * @email parakovo@gmail.com
 */
public class NodeStoreException extends RuntimeException {

    /**
     * Create NodeStoreException.
     *
     * @param cause cause
     */
    public NodeStoreException(Throwable cause) {
        super(cause);
    }

    /**
     * Create NodeStoreException.
     *
     * @param message message
     * @param cause cause
     */
    public NodeStoreException(String message, Throwable cause) {
        super(message, cause);
    }

}
