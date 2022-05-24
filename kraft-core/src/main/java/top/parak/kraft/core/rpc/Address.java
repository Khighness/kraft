package top.parak.kraft.core.rpc;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Node address.
 *
 * @author KHighness
 * @since 2022-03-18
 * @email parakovo@gmail.com
 */
@Immutable
public class Address {

    private final String host;
    private final int port;

    /**
     * Create Address.
     *
     * @param host host
     * @param port port
     */
    public Address(@Nonnull  String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Get host.
     *
     * @return host
     */
    public String getHost() {
        return host;
    }

    /**
     * Get port.
     *
     * @return port
     */
    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "Address{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }

}
