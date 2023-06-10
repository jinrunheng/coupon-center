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

#### 1.1.5 Nacos 一致性协议

Nacos 的一致性协议用来确保 Nacos 集群中各个节点之间数据的一致性。

Nacos 内部支持两种一致性协议，一种是侧重一致性的 Raft 协议，基于集群中选举出来的 Leader 节点进行数据写入；另一种是针对临时节点的 Distro 协议，它是一个侧重可用性（或最终一致性）的分布式一致性协议。

##### 1.1.5.1 Raft 协议

对于非临时节点，Nacos 采用的是 Raft 协议。

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

对于临时节点，Nacos 采用的是 Distro 协议。