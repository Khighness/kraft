package top.parak.kraft.core.rpc.message;

import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.rpc.Channel;

/**
 * Abstract RPC message.
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public abstract class AbstractRpcMessage<T> {

    private final T rpc;
    private final NodeId sourceNodeId;
    private final Channel channel;

    AbstractRpcMessage(T rpc, NodeId sourceNodeId, Channel channel) {
        this.rpc = rpc;
        this.sourceNodeId = sourceNodeId;
        this.channel = channel;
    }

    public T get() {
        return this.rpc;
    }

    public NodeId getSourceNodeId() {
        return sourceNodeId;
    }

    public Channel getChannel() {
        return channel;
    }

}
