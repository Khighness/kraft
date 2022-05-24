package top.parak.kraft.core.rpc.nio;

import top.parak.kraft.core.rpc.Channel;
import top.parak.kraft.core.rpc.message.*;
import top.parak.kraft.core.service.ChannelException;

import javax.annotation.Nonnull;

/**
 * Nio-based channel.
 *
 * @author KHighness
 * @since 2022-04-14
 * @email parakovo@gmail.com
 */
class NioChannel implements Channel {

    private final io.netty.channel.Channel nettChannel;

    NioChannel(io.netty.channel.Channel nettChannel) {
        this.nettChannel = nettChannel;
    }

    @Override
    public void writeRequestVoteRpc(@Nonnull RequestVoteRpc rpc) {
        nettChannel.writeAndFlush(rpc);
    }

    @Override
    public void writeRequestVoteResult(@Nonnull RequestVoteResult result) {
        nettChannel.writeAndFlush(result);
    }

    @Override
    public void writeAppendEntriesRpc(@Nonnull AppendEntriesRpc rpc) {
        nettChannel.writeAndFlush(rpc);
    }

    @Override
    public void writeAppendEntriesResult(@Nonnull AppendEntriesResult result) {
        nettChannel.writeAndFlush(result);
    }

    @Override
    public void writeInstallSnapshotRpc(@Nonnull InstallSnapshotRpc rpc) {
        nettChannel.writeAndFlush(rpc);
    }

    @Override
    public void writeInstallSnapshotResult(@Nonnull InstallSnapshotResult result) {
        nettChannel.writeAndFlush(result);
    }

    @Override
    public void close() {
        try {
            nettChannel.close().sync();
        } catch (InterruptedException e) {
            throw new ChannelException("failed to close", e);
        }
    }

    io.netty.channel.Channel getDelegate() {
        return nettChannel;
    }

}
