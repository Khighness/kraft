package top.parak.kraft.kvstore.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.parak.kraft.kvstore.message.AddNodeCommand;
import top.parak.kraft.kvstore.message.RemoveNodeCommand;
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

    private static final Logger logger = LoggerFactory.getLogger(KVStoreServerHandler.class);

    private final KVStoreServerService KVStoreServerService;

    public KVStoreServerHandler(KVStoreServerService KVStoreServerService) {
        this.KVStoreServerService = KVStoreServerService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof AddNodeCommand) {
            KVStoreServerService.addNode(new CommandRequest<>((AddNodeCommand) msg, ctx.channel()));
        } else if (msg instanceof RemoveNodeCommand) {
            KVStoreServerService.removeNode(new CommandRequest<>((RemoveNodeCommand) msg, ctx.channel()));
        } else if (msg instanceof GetCommand) {
            KVStoreServerService.get(new CommandRequest<>((GetCommand) msg, ctx.channel()));
        } else if (msg instanceof SetCommand) {
            KVStoreServerService.set(new CommandRequest<>((SetCommand) msg, ctx.channel()));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("kv-store client [{}] connected", ctx.channel().remoteAddress().toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("kv-store client [{}] occurs exception: {}", ctx.channel().remoteAddress().toString(), cause.getMessage());
        ctx.close();
    }

}
