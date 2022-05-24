package top.parak.kraft.core.rpc.nio;

import com.google.common.eventbus.EventBus;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.parak.kraft.core.node.NodeId;

/**
 * Netty Handler to connect remote node.
 *
 * @author KHighness
 * @since 2022-05-24
 * @email parakovo@gmail.com
 */
public class ToRemoteHandler extends AbstractHandler {

    private static final Logger logger = LoggerFactory.getLogger(ToRemoteHandler.class);
    private final NodeId selfId;

    public ToRemoteHandler(EventBus eventBus, NodeId remoteId, NodeId selfId) {
        super(eventBus);
        this.remotedId = remoteId;
        this.selfId = selfId;
    }

    /**
     * Send selfId to remote node when connecting successfully.
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.write(selfId);
        channel = new NioChannel(ctx.channel());
    }

    /**
     * Receive message from remote node.
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("receive {} from {}", msg, remotedId);
        super.channelRead(ctx, msg);
    }

}
