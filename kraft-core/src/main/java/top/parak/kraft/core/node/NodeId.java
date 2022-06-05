package top.parak.kraft.core.node;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * Node id.
 *
 * @author KHighness
 * @since 2022-03-18
 * @email parakovo@gmail.com
 */
@Immutable
public class NodeId {

    private final String value;

    /**
     * Create NodeId.
     *
     * @param value value
     */
    public NodeId(@Nonnull String value) {
        this.value = value;
    }

    /**
     * Create NodeId.
     *
     * @param value value
     */
    public static NodeId of(@Nonnull String value) {
        return new NodeId(value);
    }

    /**
     * Get value.
     *
     * @return value
     */
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeId nodeId = (NodeId) o;
        return value.equals(nodeId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return this.value;
    }

}
