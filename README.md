# 优惠券平台
## 1. Nacos
### 1.1 Nacos 体系架构
#### 1.1.1 服务注册与服务发现
![](https://files.mdnice.com/user/19026/b628fc10-6642-4eb0-8c03-b13544eaaf36.png)

首先，服务 B 集群向注册中心发起了注册，将自己的地址信息上报到注册中心，这个过程就是服务注册。接下来，每隔一段时间，服务 A 就会从服务中心获取服务 B 集群的服务列表，或者由服务中心将服务列表的变动推送给服务 A，这个过程叫做服务发现；最后，服务 A 根据本地负载均衡策略，从服务列表中选取某一个服务 B 的节点，发起服务调用.

如果服务 B 集群因为未知的网络故障导致无法响应服务，这时候服务 A 向服务 B 发起了服务调用，就会发生超时或者服务无响应的异常情况。业界通用的解决方案是 “heartbeat”，即：“心跳检查”。

![](https://files.mdnice.com/user/19026/cd3b1321-01e0-4af3-94ab-909f811775b5.png)

所有的服务都要在注册中心进行注册，每个节点都需要每隔一段时间向注册中心同步自己当前的状态（心跳）；如果节点持续发送心跳信息，则一切正常，服务可以被发现；如果注册中心在一段时间内没有收到 Client 的心跳包，注册中心就会将这个节点标记为下线状态，进而将该服务从服务列表中剔除。

#### 1.1.2 Nacos 领域模型
Nacos 的领域模型从上至下分为服务，集群和实例三层。

![](https://files.mdnice.com/user/19026/36277f45-53d7-4239-b6d4-bfe2ce576ecf.png)
#### 1.1.3 Nacos 数据模型
Nacos 的数据模型有三个层次结构，分别是 Namespace，Group 以及 Service ID；通过 Namespace + Group + Service ID 就可以精准定位到一个具体的微服务。譬如：我想调用生产环境下 A 分组的订单服务，那么对应的服务寻址的 Key 就是类似 Production.A.orderService 的组合

![](https://files.mdnice.com/user/19026/e407b72b-3f63-44b4-9f6f-c3814dacee2d.png)

#### 1.1.4 Nacos 基本架构
Nacos 的核心功能有两个，一个是 **Naming Service**，也就我们用来做服务发现的模块；另一个是 **Config Service**，用来提供配置项管理、动态更新配置和元数据的功能。

Nacos 基本架构图如下：

![](https://files.mdnice.com/user/19026/042ae87f-af3c-4439-87dc-fab52a661b95.png)

#### 1.1.5 Nacos 的一致性协议

分布式系统的 CAP 理论：

- C：Consistency，即：一致性；其强调在分布式系统中的所有数据备份，在同一时刻是否为同样的值
- A：Availability，可用性，只要收到用户的请求，服务器就必须给出回应
- P：Partition tolerance，分区容错性，它指的是分布式系统在遇到某节点或网络分区故障的时候，仍然能够对外提供满足一致性或可用性的服务

分布式系统要求必须具有分区容错性，而 C 和 A 则无法全部保证，如果选择 CP，那么在某节点写入时，数据同步到其他节点后才可以响应下一个请求，这段时间内服务是不可用的；反之如果选择 AP，则一致性无法保证。

常用的注册中心里，CP 架构的代表为 Zookeeper，AP 架构的代表为 Eureka。

那么注册中心到底该侧重 AP 还是 CP，这里推荐阅读阿里中间件的博客[《阿里巴巴为什么不用zookeeper?》](https://mp.weixin.qq.com/s?__biz=MjM5MDE0Mjc4MA==&mid=2651007830&idx=1&sn=7382412cd4a2243b34f69c3cf4aa5a20&scene=21) ，通过阅读文章，我们可以看到，阿里的观点为注册中心应该侧重 AP，而不是 CP。

当系统为 CP 架构时，会出现破坏服务本身的可联通性原则的问题：

![](https://files.mdnice.com/user/19026/7d5a4116-114c-46cc-b09d-0e59e089166a.png)

上图为 Zookeeper 作为注册中心的集群架构图。

我们看到机房 1 部署了两个节点：ZK1，ZK2；机房 2 部署了两个节点：ZK3，ZK4；机房 3 部署了一个节点 ZK5。

当机房 3 出现网络分区 (Network Partitioned) 的时候，导致机房 3 与其他机房的网络不通，机房 3 在网络上成了孤岛，即："脑裂问题"。

虽然整体 ZooKeeper 服务是可用的，但是节点 ZK5 是不可写的，因为联系不上 Leader。

也就是说，这时候机房 3 的应用服务 svcB 是不可以重新部署，重新启动，扩容或者缩容的，但是站在网络和服务调用的角度看，机房 3 的 svcA 虽然无法调用机房 1 和机房 2 的 svcB, 但是与机房 3 的 svcB 之间的网络明明是 OK 的啊，为什么不让我调用本机房的服务？在 CP 架构的强一致性协议中，此时只有机房 1 与 机房 2 可用，机房 3 是不可用的。

如果是 AP 系统，譬如 Nacos 的 Distro 协议，就不存在脑裂问题。假如客户端与机房 1，机房 2 构成的集群以及机房 3 单独构成的集群都保持联通，那么经过一段时间随机请求的心跳，这两个集群仍然有全量的服务；假如部分客户端只能与机房 1，机房 2 构成的集群联通，那么就将服务注册到它们构成的集群上，另外一部分客户端只能与机房 3 的 ZK5 节点联通，就将服务注册到 ZK5 节点上，虽然这机房 3 与机房 1，2 之间互相不可调用，但是这一点要比强一致性协议导致的问题好的多。如果是强一致性，机房 3 的整个服务将不可用。

整个结论表明：注册中心如果做到最终一致性（弱一致性或收敛一致性），其实是完全可以接受的。

Nacos 内部支持两种一致性协议，一种是侧重一致性的 Raft 协议（CP），基于集群中选举出来的 Leader 节点进行数据写入（Nacos 1.x 版本采用的是 Raft 协议，2.x 后改为 JRaft 协议）；另一种是 Distro 协议（AP），它是一个侧重可用性（或最终一致性）的分布式一致性协议。

对于服务注册与发现（Naming Service）， Nacos 采用的是 AP 协议，为了保障可用性，Nacos 尽最大可能保证 Naming Service 可以对外提供服务。Nacos 的服务注册与发现采取了心跳机制可自动完成服务数据补偿。如果数据丢失的话，可以通过该机制快速弥补数据丢失，并保障最终一致性。

而对于配置管理（Config Service），Nacos 则采用了 CP 协议。配置数据，是直接在 Nacos 服务端进行创建并进行管理的，必须保证大部分的节点都保存了此配置数据才能认为配置被成功保存了。为了避免丢失配置变更引起严重故障，Nacos 使用了强⼀致性共识算法（Raft），保障集群中大部分的节点是强⼀致的。
                        
##### 1.1.5.1 Raft 协议

关于理解 Raft 协议的原理，强烈推荐：🔗 http://thesecretlivesofdata.com/raft/

Raft 协议重要的操作有两个：
1. Leader Election
2. Log Replication

###### Leader Election

Raft 协议中，节点有三种状态（角色）： 
- **follower**
- **candidate**
- **leader**

leader 节点的作用就是接收客户端的所有请求，Raft 协议保证任何时刻只有一个 leader 节点，leader 节点的主要工作有：
- 处理客户端的写请求
- 管理日志复制（Log Replication）
- 不断发送心跳信息给其他节点

follower 节点相当于普通群众，用于接收和处理来自 leader 的消息，当 leader 发送的心跳超时，便会主动站出来，推荐自己成为候选人（candidate）。

candidate 为候选人节点，用于选举 leader，它会向其他节点发送投票请求，通知其他节点给自己投票，如果用赢得超过半数节点的选票，则会升级为 leader 节点。

Leader Election 简而言之就是选举；选举过程：

1. 所有的节点最开始都是 follower 状态，在一定的时间内没有收到 leader 节点的心跳，则会进入 candidate 状态，参与选举。节点从 follower 状态到 candidate 状态有一个时间差，叫做 election timeout，它是一个从 150 ms 至 300 ms 的随机值
2. 当某个节点从 follower 变为 candidate 后，便会开启一个 election term。election term 可以理解为任期，每个任期由单调递增的数字所标识，任期编号随着选举的举行而递增变化，其作用为充当逻辑时钟；follower 成为 candidate 后，会增加自己的任期编号，譬如，节点 A 的前任期编号为 1，那么当节点 A 成为 candidate 后，会将自己的任期编号加 1 变成 2。candidate 节点会给自己投票，然后给其他节点发送投票请求，让其他节点给自己投票，并等待其他节点回复
3. 如果 follower 收到的票数过半，则会成为 leader 节点，从 candidate 节点变为 leader 节点后，节点会将任期编号再加一。leader 节点会给其他节点发送心跳，如果一个 candidate 或者 leader 节点，发现自己的任期编号比其他节点小，那么它将会立即转换成 follower 节点。所以其他节点收到 leader 的心跳后，判断自己的任期编号比当前 leader 的任期编号小，便会自行切换成为 follower 节点

选举过程中存在的几种情况：

1. 假如有三个节点 A B C；A B 均成为了 candidate，并同时发起选举，而 A 发送的的选举消息先到达 C ，C 给 A 投了一票，当 B 的消息到达 C 时，C 将不会给 B 投票，这是因为 Raft 协议有一个约束，即：每个节点最多投票一次。由于 A 和 B 均会给自己投票，所以，A 胜出成为 leader 节点之后，便会给 B,C 发心跳消息。 B 收到 A 节点发送的心跳，发现节点 A 的 term 不低于自己的 term， 知道有已经有 leader 节点了，于是转换成 follower
2. 假如有四个节点 A B C D；选举过程中，A B 同时成为了 candidate 节点，C 投给了 A 一票，D 投给了 B 一票，这样便出现了平票的情况，也没有任何节点获得超过半数的投票，这时便会超时且重新发起选举，如果经常出现平票，就延长了系统不可用的时间，所以 Raft 算法增加了随机的 election timeout 尽量避免平票的发生

###### Log Replication

在 Raft 集群中，每个服务器可以看作是一个复制状态机（Replicated State Machine）：

![](https://files.mdnice.com/user/19026/dfb1cf5d-dea9-478e-8633-f421e4324064.png)

复制状态机的工作过程：

1. 客户端发起请求
2. 共识模块执行共识算法，对日志进行复制，将日志复制至集群内的各个节点
3. 日志应用（apply）到状态机
4. 服务端返回请求结果

在 Raft 算法中，指令以**日志**形式进行复制。集群内只有 leader 节点才能接收客户端请求，其他所有节点都接收来自 leader 的复制日志，以达到所有节点日志的一致，并最终达到状态的一致。

Raft 日志由日志项（log entry）构成，其包含日志项索引，任期编号以及指令：

![](https://files.mdnice.com/user/19026/33c246f1-4f5a-484c-8df9-dcf46d6c7949.png)

1. 日志项索引，即：log index，它是一个连续的，单调递增的整型号码
2. 任期编号为创建这条日志项的 leader 节点的任期编号，在上图中，黄，绿，蓝标识了不同的任期
3. 指令，即客户端请求状态机需要执行的命令，如上图中 `x <- 3`，表示一条指令，含义为将变量 `x` 赋值为 3

Log Replication：

leader 节点收到了客户端的请求后，会执行日志复制（log replication）,具体过程见下图：

![](https://files.mdnice.com/user/19026/85edcd28-cebb-4713-bd72-0f3cfe43e21d.png)

1. 客户端发送请求，领导者接收到客户端请求，根据请求中的指令，创建一个新的日志项，并附加（append）到 leader 日志中
2. leader 通过日志复制 RPC（AppendEntries RPC），将新的日志项复制至其他节点
3. 当 leader 将日志项成功复制至集群**大多数节点**的时候，日志项处于 committed 状态，leader 可将这个日志项应用（apply）到自己的状态机中
4. leader 将客户端请求返回
5. 当其他节点，即 follower，接收到 leader 的心跳消息或新的日志复制消息（这两种消息均会附上 leader 最大已提交日志项索引），如果 follower 发现 leader 已提交某日志项，而自己还没将该日志项应用至状态机，那么，follower就将该日志项应用至自己的状态机中

Raft 日志的一致性：

1. leader 通过日志复制 RPC 的一致性检查，找到 follower 与自己相同日志项的最大索引值。那么，在该索引值之前的日志，领导者和跟随者是一致的，之后的日志，就不一致了
2. leader 强制将 follower 该索引值之后的所有日志项删除，并将 leader 该索引值之后的所有日志项同步至 follower，以实现日志的一致

因此，Raft 算法处理 leader 与 follower 日志不一致的关键是找出上述的最大索引值。为解决该问题，Raft 引入两个变量来方便找到这一最大索引值：
- prevLogIndex
- prevLogTerm

![](https://files.mdnice.com/user/19026/ee387fb8-48e6-45d6-ab44-a35c10ec3bf7.png)

如上图所示，如果 leader 需要将索引值为 8 的日志项复制到 follower，那么 prevLogIndex 为 7，prevLogTerm 为 4。

1. leader 通过日志复制 RPC 消息，发送当前自己最新日志项给 follower，该消息的 prevLogIndex 为 7，prevLogTerm 为 4
2. 由于 follower 在其日志中，无法找到索引值为 7，任期编号为 4 的日志项，即 follower 的日志和 leader 的不一致，所以，follower 会拒绝接收新的日志项，返回失败
3. 这时，leader 递减 prevLogIndex 值为 6，prevLogTerm 变为 3，重新发送日志复制 RPC 消息
4. 此时，follower 在其日志中，找到了索引值为 6，任期编号为 3 的日志项，故 follower 返回成功
5. leader 收到 follower 成功返回后，知道在索引值为 6 的位置之前的所有日志项，均与自己的相同。于是通过日志复制 RPC ，复制并覆盖索引值为 6 之后的日志项，以达到 follower 的日志与 leader 的日志一致
6. 日志复制过程中，只有 follower 的日志项会被 leader 的日志覆盖更新，leader 的日志从不会被覆盖或删除


##### 1.1.5.2 Distro 协议

Distro 协议是 Nacos 社区研发的⼀种 AP 分布式协议，是面向临时节点设计的⼀种分布式协议，其保证了在某些 Nacos 节点宕机后，整个临时节点处理系统依旧可以正常工作。其数据存储在缓存中，并且会在启动时进行全量数据同步，并定期进行数据校验。与 Raft 协议不同，Raft 协议中，只有 Leader 节点可以处理写请求，并通过 Log Replication 使其他节点同步；而对于 Distro 协议来说，每个 Distro 节点都可以接收到读写请求。

Distro协议的主要设计思想如下：

1. Nacos 中，每个节点是平等的都可以处理写请求，同时把新数据同步到其他节点。
2. 每个节点只负责部分数据，定时发送自己负责数据的校验值到其他节点来保持数据⼀致性。
3. 每个节点独立处理读请求，及时从本地发出响应。

Distro 协议的核心类为 `DistroConsistencyServiceImpl`；Distro 协议工作原理：

1. 数据初始化，新加入的 Distro 节点会进行全量数据拉取。具体操作是轮询所有的 Distro 节点，通过向其他的机器发送请求拉取全量数据。在全量拉取操作完成之后，Nacos 的每台机器上都维护了当前的所有注册上来的非持久化实例数据。
2. 数据校验，在 Distro 集群启动之后，各台机器之间会**定期的发送心跳进行数据校验**。如果某台机器校验发现与其他机器数据不一致，则会进行全量拉取请求将数据补齐。
3. 写操作，当注册非持久化的实例的写请求打到某台 Nacos 服务器时，首先被 Filter 拦截，根据请求的 IP 端口信息转发到对应的 Distro 责任节点上处理请求。Distro 协议还会定期执行 Sync 任务，将本机所负责的所有的实例信息同步到其他节点上。
4. 读操作，由于每台机器上都存放了全量数据，因此在每⼀次读操作中，Distro 机器会直接从本地拉取数据，快速响应。
