package top.parak.kraft.core.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Node group.
 * <p>
 * Used to record the members in cluster and find members.
 * </p>
 *
 * @author KHighness
 * @since 2022-03-19
 * @email parakovo@gmail.com
 */
@NotThreadSafe
public class NodeGroup {

    private static final Logger logger = LoggerFactory.getLogger(NodeGroup.class);
    private final NodeId selfId;
    private Map<NodeId, GroupMember> memberMap;

    /**
     * Create group with single node(standalone).
     *
     * @param endpoint endpoint
     */
    public NodeGroup(NodeEndpoint endpoint) {
        this(Collections.singleton(endpoint), endpoint.getId());
    }

    /**
     * Create group with node list(group).
     *
     * @param endpoints endpoints
     * @param selfId    self id
     */
    public NodeGroup(Collection<NodeEndpoint> endpoints, NodeId selfId) {
        this.memberMap = buildMemberMap(endpoints);
        this.selfId = selfId;
    }

    /**
     * Build member map from endpoints.
     *
     * @param endpoints endpoints
     * @return member map
     */
    private Map<NodeId, GroupMember> buildMemberMap(Collection<NodeEndpoint> endpoints) {
        Map<NodeId, GroupMember> map = new HashMap<>();
        endpoints.forEach(endpoint -> map.put(endpoint.getId(), new GroupMember(endpoint)));
        if (map.isEmpty()) {
            throw new IllegalArgumentException("endpoints is empty");
        }
        return map;
    }

    /**
     * Check if member is unique one in group, in other word, check if standalone mode.
     *
     * @return true if only one member and the id of member equals to specified id, otherwise false
     */
    public boolean isStandalone() {
        return memberMap.size() == 1 && memberMap.containsKey(selfId);
    }

    /**
     * Get count of major.
     * <p>
     * For election.
     * </p>
     *
     * @return count
     * @see GroupMember#isMajor()
     */
    public int getCountOfMajor() {
        return (int) memberMap.values().stream().filter(GroupMember::isMajor).count();
    }

    /**
     * Find self.
     *
     * @return self
     */
    @Nonnull
    public GroupMember findSelf() {
        return findMember(selfId);
    }

    /**
     * Find member by id.
     * <p>
     * Thrown exception if member not found.
     * </p>
     *
     * @param id id
     * @return member, never be {@code null}
     */
    @Nonnull
    public GroupMember findMember(NodeId id) {
        GroupMember member = memberMap.get(id);
        if (member == null) {
            throw new IllegalArgumentException("no such node " + id);
        }
        return member;
    }

    /**
     * Get member by id.
     *
     * @param id id
     * @return member, maybe {@code null}
     */
    @Nullable
    public GroupMember getMember(NodeId id) {
        return memberMap.get(id);
    }

    /**
     * Check if node is major member.
     *
     * @param id id
     * @return true if member exists and member id major, otherwise false.
     */
    public boolean isMemberOfMajor(NodeId id) {
        GroupMember member = memberMap.get(id);
        return member != null && member.isMajor();
    }

    /**
     * Upgrade member to major member.
     *
     * @param id id
     * @throws IllegalArgumentException if member not found
     * @see #findMember(NodeId)
     */
    public void upgrade(NodeId id) {
        logger.info("upgrade node {}", id);
        findMember(id).setMajor(true);
    }

    /**
     * Downgrade member (set major to {@code false}).
     *
     * @param id id
     * @throws IllegalArgumentException if member not found
     */
    public void downgrade(NodeId id) {
        logger.info("downgrade node {}", id);
        GroupMember member = findMember(id);
        member.setMajor(false);
        member.setRemoving();
    }

    /**
     * Add member to group.
     *
     * @param endpoint   endpoint
     * @param nextIndex  next index
     * @param matchIndex match index
     * @return added member
     */
    public GroupMember addNode(NodeEndpoint endpoint, int nextIndex, int matchIndex, boolean major) {
        logger.info("add node {} to group", endpoint.getId());
        ReplicatingState replicatingState = new ReplicatingState(nextIndex, matchIndex);
        GroupMember member = new GroupMember(endpoint, replicatingState, major);
        memberMap.put(endpoint.getId(), member);
        return member;
    }

    /**
     * Remove member.
     *
     * @param id id
     */
    public void removeNode(NodeId id) {
        logger.info("node {} removed", id);
        memberMap.remove(id);
    }

    public void updateNodes(Set<NodeEndpoint> endpoints) {
        memberMap = buildMemberMap(endpoints);
        logger.info("group changed -> {}", memberMap.keySet());
    }

    /**
     * Reset replicating state.
     *
     * @param nextLogIndex next log index
     */
    public void resetReplicatingStates(int nextLogIndex) {
        for (GroupMember member : memberMap.values()) {
            if (!member.idEquals(selfId)) {
                member.setReplicatingState(new ReplicatingState(nextLogIndex));
            }
        }
    }

    /**
     * Get match index of major members.
     * <p>
     * To get match index of major in group, sort match indexes and get the middle one.
     * </p>
     *
     * @return match index
     */
    public int getMatchIndexOfMajor() {
        List<NodeMatchIndex> matchIndexes = new ArrayList<>();
        for (GroupMember member : memberMap.values()) {
            if (member.isMajor() && !member.idEquals(selfId)) {
                matchIndexes.add(new NodeMatchIndex(member.getId(), member.getMatchIndex()));
            }
        }
        int count = matchIndexes.size();
        if (count == 0) {
            throw  new IllegalStateException("standalone or no major node");
        }
        Collections.sort(matchIndexes);
        logger.debug("match indexes {}", matchIndexes);
        return matchIndexes.get(count / 2).getMatchIndex();
    }

    /**
     * List replication target.
     * <p>Self is not replication target</p>
     *
     * @return replication targets
     */
    public Collection<GroupMember> listReplicationTarget() {
        return memberMap.values().stream()
                .filter(m -> !m.idEquals(selfId))
                .collect(Collectors.toList());
    }

    /**
     * List endpoint of major members.
     *
     * @return endpoints
     */
    public Set<NodeEndpoint> listEndpointOfMajor() {
        return memberMap.values().stream()
                .filter(GroupMember::isMajor)
                .map(GroupMember::getEndpoint)
                .collect(Collectors.toSet());
    }

    /**
     * List endpoint of major members except self.
     *
     * @return endpoints except self
     */
    public Set<NodeEndpoint> listEndpointOfMajorExceptSelf() {
        return memberMap.values().stream()
                .filter(m -> m.isMajor() && !m.idEquals(selfId))
                .map(GroupMember::getEndpoint)
                .collect(Collectors.toSet());
    }

    /**
     * Node match index.
     *
     * @see NodeGroup#getMatchIndexOfMajor()
     */
    private static class NodeMatchIndex implements Comparable<NodeMatchIndex> {

        private final NodeId nodeId;
        private final int matchIndex;

        NodeMatchIndex(NodeId nodeId, int matchIndex) {
            this.nodeId = nodeId;
            this.matchIndex = matchIndex;
        }

        public int getMatchIndex() {
            return matchIndex;
        }

        @Override
        public String toString() {
            return "<" + nodeId + ',' + matchIndex + '>';
        }

        @Override
        public int compareTo(NodeMatchIndex o) {
            return -Integer.compare(o.matchIndex, this.matchIndex);
        }

    }

}
