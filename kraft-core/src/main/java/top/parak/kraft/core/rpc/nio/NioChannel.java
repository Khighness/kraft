package top.parak.kraft.core.rpc.nio;

import top.parak.kraft.core.rpc.Channel;
import top.parak.kraft.core.rpc.ChannelException;
import top.parak.kraft.core.rpc.message.*;

import javax.annotation.Nonnull;

/**
 * NIO channel.
 *
 * @author KHighness
 * @since 2022-04-14
 * @email parakovo@gmail.com
 * @see java.nio.channels.NetworkChannel
 */
class NioChannel implements Channel {

    private final io.netty.channel.Channel nettyChannel;

    NioChannel(io.netty.channel.Channel nettyChannel) {
        this.nettyChannel = nettyChannel;
    }

    @Override
    public void writeRequestVoteRpc(@Nonnull RequestVoteRpc rpc) {
        nettyChannel.writeAndFlush(rpc);
    }

    @Override
    public void writeRequestVoteResult(@Nonnull RequestVoteResult result) {
        nettyChannel.writeAndFlush(result);
    }

    @Override
    public void writeAppendEntriesRpc(@Nonnull AppendEntriesRpc rpc) {
        nettyChannel.writeAndFlush(rpc);
    }

    @Override
    public void writeAppendEntriesResult(@Nonnull AppendEntriesResult result) {
        nettyChannel.writeAndFlush(result);
    }

    @Override
    public void writeInstallSnapshotRpc(@Nonnull InstallSnapshotRpc rpc) {
        nettyChannel.writeAndFlush(rpc);
    }

    @Override
    public void writeInstallSnapshotResult(@Nonnull InstallSnapshotResult result) {
        nettyChannel.writeAndFlush(result);
    }

    @Override
    public void close() {
        try {
            nettyChannel.close().sync();
        } catch (InterruptedException e) {
            throw new ChannelException("failed to close", e);
        }
    }

    io.netty.channel.Channel getDelegate() {
        return nettyChannel;
    }

}
