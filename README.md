# kraft
<p align="center">
<a href="https://www.parak.top/kraft/"><img src="https://img.shields.io/badge/java-reference-fff?logo=java&style=social" alt="Java"></a>
<a href="https://github.com/Khighness/kraft"><img src="https://img.shields.io/github/stars/Khighness/kraft?style=social" alt="Star"></a>
<br/><br/>
<span>⚔️ A distributed KV storage system based on <a href="https://raft.github.io/raft.pdf">raft</a> protocol ⚔️</span>
</p>



## Completed Feature

- [x] **leader election**
- [x] **append log entry**
- [x] **generate log snapshot**
- [x] **group member change**
- [x] **kv server and client**
- [ ] **prevote rpc**
- [ ] **log batch transfer and pipelining**
- [ ] **leadership transfer**
- [ ] **multi-raft**
- [ ] **transaction support**



## Core Architecture

<p align="center">
<img src="images/kraft-core-architecture.svg" alt="kraft-core-architecture" />
</p>




## Quick Start

1. Build and package via _maven_

```shell
$ git clone https://github.com/Khighness/kraft
$ cd kraft
$ mvn clean compile install
$ cd kraft-kvstore
$ mvn package assembly:single
```

2. Start server group: K1, K2, K3

```shell
# Start Server-K1
$ mvn exec:java -Dexec.mainClass="top.parak.kraft.kvstore.server.CommandServerLauncher" -Dexec.args="-gc K1,127.0.0.1,10001 K2,127.0.0.1,10002 K3,127.0.0.1,2333 -m group-member -i K1 -p2 10011"
# Start Server-K2
$ mvn exec:java -Dexec.mainClass="top.parak.kraft.kvstore.server.CommandServerLauncher" -Dexec.args="-gc K1,127.0.0.1,10001 K2,127.0.0.1,10002 K3,127.0.0.1,2333 -m group-member -i K2 -p2 10012"
# Srart Server-K3
$ mvn exec:java -Dexec.mainClass="top.parak.kraft.kvstore.server.CommandServerLauncher" -Dexec.args="-gc K1,127.0.0.1,10001 K2,127.0.0.1,10002 K3,127.0.0.1,2333 -m group-member -i K3 -p2 10013"
```

3. Start client

```shell
$ mvn exec:java -Dexec.mainClass="top.parak.kraft.kvstore.client.CommandClientLauncher" -Dexec.args="-sc K1,127.0.0.1,10011 K2,127.0.0.1,10012 K3,127.0.0.1,10013"

Welcome to KRaft KVStore Shell

***********************************************
current server list:

K1,127.0.0.1,10011
K2,127.0.0.1,10012
K3,127.0.0.1,10013
***********************************************
kvstore-client 1.0.0> kvstore-set me Khighness
kvstore-client 1.0.0> kvstore-get me
Khighness

```



## Mit License

Khighness's kraft is open-sourced system licensed under the [MIT license](https://github.com/Khighness/kraft/blob/master/LICENSE).
