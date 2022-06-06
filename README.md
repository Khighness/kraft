# kraft

A distributed KV based on [raft](https://raft.github.io/raft.pdf) protocol.



## completed feature

- [x] leader election
- [x] append log entry
- [x] generate log snapshot
- [x] group member change
- [x] kv server and client
- [ ] prevote rpc
- [ ] log batch transfer and pipelining
- [ ] leadership transfer
- [ ] multi-raft
- [ ] transaction support



## core architecture

![kraft-core-architecture](images/kraft-core-architecture.png)


## mit license

Khighness's kraft is open-sourced software licensed under the [MIT license](https://github.com/Khighness/kraft/blob/master/LICENSE).
