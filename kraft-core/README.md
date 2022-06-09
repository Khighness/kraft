## Core



### State



#### Persistent state 

Updated on stable storage before responding to RPCs

| Field       | Description                                                  |
| ----------- | ------------------------------------------------------------ |
| currentTerm | latest term KVStoreServer has seen (initialized to 0, increase monotonically) |
| votedFor    | candidateId that received vote in current term               |
| log[]       | log entries; each entry contains command for the state machine, and term when entry was received by leader (first index is 1) |



#### Volatile state


| Field       | Description                                                  |
| ----------- | ------------------------------------------------------------ |
| commitIndex | index of highest log entry known to be committed (initialized to 0, increase monotonically) |
| lastApplied | index of highest log entry applied to state machine (initialized to 0, increase monotonically) |



#### Leader state

Reinitialized after election

| Field        | Description                                                  |
| ------------ | ------------------------------------------------------------ |
| nextIndex[]  | for each KVStoreServer, index of the next log entry to send to that KVStoreServer (initialized to leader last log index + 1) |
| matchIndex[] | for each KVStoreServer, index of highest log entry known to be  replicated on KVStoreServer (initialized to 0, increase monotonically) |



### RPC



#### AppendEntries

Invoked by leader to replicate log entries; also used as heartbeat.

Request
| Field        | Description                                                  |
| ------------ | ------------------------------------------------------------ |
| term         | leader's term                                                |
| leaderId     | so follower can redirect clients                             |
| prevLogIndex | index of log entry immediately preceding new ones            |
| prevLogTerm  | term of prevLogIndex entry                                   |
| entries[]    | log entries to store (empty for heartbeat; may send more than one for efficiency) |
| leaderCommit | leader's commitIndex                                         |

Response
| Field   | Description                                                  |
| ------- | ------------------------------------------------------------ |
| term    | currentTerm, for leader to update itself                     |
| success | true if follower contained entry matching prevLogIndex and prevLogTerm |

Receiver imeplementation

1. Reply false if term < currentTerm.
2. Reply false if log doesn't contain an entry at prevLogIndex whose term matches prevLogTerm.
3. If an existing entry conflicts with a new one (same index but different terms), delete the existing entry and all that follow it.
4. Append any new entries not already in the log.
5. If leaderCommit > commitIndex, set commitIndex = min(leaderCommit, index of last new entry).



#### RequestVote

Invoked by candidates to gather votes.

Request
| Field        | Description                         |
| ------------ | ----------------------------------- |
| term         | candidate's term                    |
| candidateId  | candidate requesting vote           |
| lastLogIndex | index of candidate's last log entry |
| lastLogTerm  | term of candidate's last log entry  |

Response
| Field   | Description                                 |
| ------- | ------------------------------------------- |
| term    | currentTerm, for candidate to update itself |
| success | true means candidate received vote          |

Receiver implementation

1. Reply false if term < currentTerm.
2. If votedFor is null or candidate;s log ia at least as up-to-date as receiver's log, grant vote.



#### InstallingSnapshot

Invoked by leader to send chunks of a snapshot to a follower. Leaders always send chunks in order.

Request
| Field             | Description                                                  |
| ----------------- | ------------------------------------------------------------ |
| term              | leader's term                                                |
| leaderId          | so follower can redirect clients                             |
| lastIncludedIndex | the snapshot replaces all entries up through and including this index |
| lastIncludedTerm  | term of lastIncludedIndex                                    |
| offset            | byte offset where chunk is positioned in the snapshot file   |
| data[]            | raw bytes of the snapshot chunk, starting at offset          |
| done              | true if this is the last chunk                               |

Response
| Field | Description                              |
| ----- | ---------------------------------------- |
| term  | currentTerm, for leader to update itself |

Receiver implementation

1. Reply immediately if term < currentTerm.
2. Create new snapshot file is first chunk (offset is 0).
3. Write data into snapshot file at given offset.
4. Reply and wait for more data chunks if done is false.
5. Save snapshot file, discard any existing or partial snapshot with a smaller index.
6. If existing log entry has same index and term as snapshot's last included entry, retain log entries following it and reply
7. Discrad the entire log.
8. Reset state machine using snapshot contents (and load snapshot's cluster configuration).






### Rules



#### For all

- If commitIndex > lastApplied: increment lastApplied, apply log[lastApplied] to state machine.
- If RPC request or response contains term T > currentTerm: set currentTerm = T, convert to follower.



#### For Follower

- Repond to RPCs from candidates and leaders.
- If elections timeout elapses wihout receiving AppendEntries RPC from current leader or granting vote to candidate: convert to candidate.



#### For Candidate

- On conversion to candidate, start election:
  1. Increment current term.
  2. Vote for self.
  3. Reset election timer.
  4. Send Request Vote RPCs to all other servers.
- If votes received from majority of servers: become leader.
- If AppendEntries RPC received from new leader: convert to follower.
- If election timeout elapses: start new election.



#### For Leader

- Upon election: send initial empty AppendEntries RPCs (heartbeat) to each KVStoreServer; repeat during idle periods to prevent election timeouts.
- If command received from client: appen entry to local log, respond after entry applied to state machine.
- If last log index >= nextIndex for a follower: send AppendEntries RPC with log entries starting at nextIndex.
  - If successful: update nextIndex and matchIndex for follower.
  - If AppendEntries fails because of log inconsistency: decrement nextIndex and retry.
- If there exists an N such that N > commitIndex, a majority of matchIndex[i] >= N, and log[N].term == currentTerm: set commitIndex = N

