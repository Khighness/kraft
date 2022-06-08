package top.parak.kraft.core.rpc.nio;

import com.google.common.eventbus.EventBus;
import top.parak.kraft.core.node.NodeId;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty Handler to deal with remote node's connection.
 *
 * @author KHighness
 * @since 2022-05-24
 * @email parakovo@gmail.com
 */
public class FromRemoteHandler extends AbstractHandler {

    private static final Logger logger = LoggerFactory.getLogger(FromRemoteHandler.class);
    private final InboundChannelGroup channelGroup;

    /**
     * Create FromRemoteHandler.
     *
     * @param eventBus     eventBus
     * @param channelGroup inbound channel group
     */
    FromRemoteHandler(EventBus eventBus, InboundChannelGroup channelGroup) {
        super(eventBus);
        this.channelGroup = channelGroup;
    }

    /**
     * Receive message from remote node.
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof NodeId) {
            remoteId = (NodeId) msg;
            NioChannel nioChannel = new NioChannel(ctx.channel());
            channel = nioChannel;
            channelGroup.add(remoteId, nioChannel);
            return;
        }

        logger.debug("receive {} from {}", msg, remoteId);
        super.channelRead(ctx, msg);
    }

}
