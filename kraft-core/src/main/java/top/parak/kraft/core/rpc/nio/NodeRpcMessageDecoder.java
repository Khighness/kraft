package top.parak.kraft.core.rpc.nio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import top.parak.kraft.core.log.entry.EntryFactory;
import top.parak.kraft.core.node.NodeEndpoint;
import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.rpc.message.*;
import top.parak.kraft.core.support.proto.Protos;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Node-RPC message decoder.
 *
 * <p><b>Transport protocol between nodes in group</b></p>
 * <pre>
 *   |<-------(4)------>|<-------(4)------>|<--ContentLength->|
 *   +------------------+------------------+------------------+
 *   |   Message Type   |  Message Length  | Message Content  |
 *   +------------------+------------------+------------------+
 * </pre>
 *
 * @author KHighness
 * @since 2022-04-14
 * @email parakovo@gmail.com
 */
public class NodeRpcMessageDecoder extends ByteToMessageDecoder {

    private final EntryFactory entryFactory = new EntryFactory();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int availableBytes = in.readableBytes();
        if (availableBytes < 8) return;

        in.markReaderIndex();
        int messageType = in.readInt();
        int messageLength = in.readInt();
        if (in.readableBytes() < messageLength) {
            in.resetReaderIndex();
            return;
        }

        byte[] message = new byte[messageLength];
        in.readBytes(message);
        switch (messageType) {
            case MessageConstants.MSG_TYPE_NODE_ID:
                out.add(new NodeId(new String(message)));
                break;
            case MessageConstants.MSG_TYPE_REQUEST_VOTE_RPC:
                Protos.RequestVoteRpc protoRVRpc = Protos.RequestVoteRpc.parseFrom(message);
                RequestVoteRpc rvRpc = new RequestVoteRpc();
                rvRpc.setTerm(protoRVRpc.getTerm());
                rvRpc.setCandidateId(new NodeId(protoRVRpc.getCandidateId()));
                rvRpc.setLastLogIndex(protoRVRpc.getLastLogIndex());
                rvRpc.setLastLogTerm(protoRVRpc.getLastLogTerm());
                out.add(rvRpc);
                break;
            case MessageConstants.MSG_TYPE_REQUEST_VOTE_RESULT:
                Protos.RequestVoteResult protoRVResult = Protos.RequestVoteResult.parseFrom(message);
                RequestVoteResult rvResult = new RequestVoteResult(protoRVResult.getTerm(), protoRVResult.getVoteGranted());
                out.add(rvResult);
                break;
            case MessageConstants.MSG_TYPE_REQUEST_APPEND_ENTRIES_RPC:
                Protos.AppendEntriesRpc protoAERpc = Protos.AppendEntriesRpc.parseFrom(message);
                AppendEntriesRpc aeRpc = new AppendEntriesRpc();
                aeRpc.setTerm(protoAERpc.getTerm());
                aeRpc.setLeaderId(new NodeId(protoAERpc.getLeaderId()));
                aeRpc.setLeaderCommit(protoAERpc.getLeaderCommit());
                aeRpc.setPrevLogIndex(protoAERpc.getPrevLogIndex());
                aeRpc.setPrevLogTerm(protoAERpc.getTerm());
                aeRpc.setEntries(protoAERpc.getEntriesList().stream().map(e ->
                        entryFactory.create(e.getKind(), e.getIndex(), e.getTerm(), e.getCommand().toByteArray())
                ).collect(Collectors.toList()));
                out.add(aeRpc);
                break;
            case MessageConstants.MSG_TYPE_REQUEST_APPEND_ENTRIES_RESULT:
                Protos.AppendEntriesResult protoAEResult = Protos.AppendEntriesResult.parseFrom(message);
                AppendEntriesResult aeResult = new AppendEntriesResult(
                        protoAEResult.getRpcMessageId(), protoAEResult.getTerm(), protoAEResult.getSuccess()
                );
                out.add(aeResult);
                break;
            case MessageConstants.MSG_TYPE_REQUEST_INSTALL_SNAPSHOT_RPC:
                Protos.InstallSnapshotRpc protoISRpc = Protos.InstallSnapshotRpc.parseFrom(message);
                InstallSnapshotRpc isRpc = new InstallSnapshotRpc();
                isRpc.setTerm(protoISRpc.getTerm());
                isRpc.setLeaderId(new NodeId(protoISRpc.getLeaderId()));
                isRpc.setLastIndex(protoISRpc.getLastIndex());
                isRpc.setLastTerm(protoISRpc.getLastTerm());
                isRpc.setLastConfig(protoISRpc.getLastConfigList().stream().map(e ->
                        new NodeEndpoint(e.getId(), e.getHost(), e.getPort())
                ).collect(Collectors.toSet()));
                isRpc.setOffset(protoISRpc.getOffset());
                isRpc.setData(protoISRpc.getData().toByteArray());
                isRpc.setDone(protoISRpc.getDone());
                out.add(isRpc);
                break;
            case MessageConstants.MSG_TYPE_REQUEST_INSTALL_SNAPSHOT_RESULT:
                Protos.InstallSnapshotResult protoISResult = Protos.InstallSnapshotResult.parseFrom(message);
                InstallSnapshotResult isResult = new InstallSnapshotResult(protoISResult.getTerm());
                out.add(isResult);
                break;
        }
    }

}
