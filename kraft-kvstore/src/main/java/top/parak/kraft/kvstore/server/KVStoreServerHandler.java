package top.parak.kraft.kvstore.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.parak.kraft.core.service.AddNodeCommand;
import top.parak.kraft.core.service.RemoveNodeCommand;
import top.parak.kraft.kvstore.message.CommandRequest;
import top.parak.kraft.kvstore.message.GetCommand;
import top.parak.kraft.kvstore.message.SetCommand;

/**
 * KV-store server handler.
 *
 * <p><b>NOTE</b></p>
 * Custom {@link ChannelInboundHandlerAdapter} must release msg after invoking
 * {@link ChannelInboundHandlerAdapter#channelRead(ChannelHandlerContext, Object) channelRead}.
 * Custom {@link io.netty.channel.SimpleChannelInboundHandler SimpleChannelInboundHandler} can
 * help developers release msg after use, so it does not need to release msg manually.
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public class KVStoreServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ChannelInboundHandlerAdapter.class);

    private final KVStoreServerService service;

    public KVStoreServerHandler(KVStoreServerService service) {
        this.service = service;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof AddNodeCommand) {
                service.addNode(new CommandRequest<>((AddNodeCommand) msg, ctx.channel()));
            } else if (msg instanceof RemoveNodeCommand) {
                service.removeNode(new CommandRequest<>((RemoveNodeCommand) msg, ctx.channel()));
            } else if (msg instanceof GetCommand) {
                service.get(new CommandRequest<>((GetCommand) msg, ctx.channel()));
            } else if (msg instanceof SetCommand) {
                service.set(new CommandRequest<>((SetCommand) msg, ctx.channel()));
            }
        } finally {
            // Ensure that ByteBuf is released, otherwise there maybe memory leaks
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("kv-store client {} connected", ctx.channel().remoteAddress().toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("kv-store client connect exception: {}", cause.getMessage());
        ctx.close();
    }

}
