package top.parak.kraft.core.node;

import top.parak.kraft.core.rpc.Address;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * Node endpoint.
 *
 * @author KHighness
 * @since 2022-03-18
 * @email parakovo@gmail.com
 */
@Immutable
public class NodeEndpoint {

    private final NodeId id;
    private final Address address;

    /**
     * Create NodeEndpoint.
     *
     * @param id      nodeId
     * @param address address
     */
    public NodeEndpoint(NodeId id, Address address) {
        this.id = id;
        this.address = address;
    }

    /**
     * Create NodeEndpoint.
     *
     * @param id   node id
     * @param host host
     * @param port port
     */
    public NodeEndpoint(@Nonnull String id, @Nonnull String host, int port) {
        this(new NodeId(id), new Address(host, port));
    }

    /**
     * Get node id.
     *
     * @return id
     */
    @Nonnull
    public NodeId getId() {
        return this.id;
    }

    /**
     * Get address.
     *
     * @return address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Get host.
     *
     * @return host
     */
    @Nonnull
    public String getHost() {
        return this.address.getHost();
    }

    /**
     * Get port.
     *
     * @return port
     */
    public int getPort() {
        return this.address.getPort();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeEndpoint)) return false;
        NodeEndpoint that = (NodeEndpoint) o;
        return id.equals(that.id) && address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address);
    }

    @Override
    public String toString() {
        return "NodeEndpoint{" +
                "id=" + id +
                ", address=" + address +
                '}';
    }

}
