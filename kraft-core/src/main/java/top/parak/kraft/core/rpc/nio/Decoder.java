package top.parak.kraft.core.rpc.nio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import top.parak.kraft.core.log.entry.EntryFactory;

import java.util.List;

/**
 * Decoder.
 *
 * @author KHighness
 * @since 2022-04-14
 * @email parakovo@gmail.com
 */
public class Decoder extends ByteToMessageDecoder {

    private final EntryFactory entryFactory = new EntryFactory();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //
    }

}
