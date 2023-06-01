## 优惠券平台

### Nacos 体系架构

#### 领域模型
Nacos 的领域模型从上至下分为服务，集群和实例三层。
#### 数据模型
Nacos 的数据模型有三个层次结构，分别是 Namespace，Group 以及 Service ID；通过 Namespace + Group + Service ID 就可以精准定位到一个具体的微服务。譬如：我想调用生产环境下 A 分组的订单服务，那么对应的服务寻址的 Key 就是类似 Production.A.orderService 的组合
#### 基本架构
Nacos 的核心功能有两个，一个是 Naming Service，也就我们用来做服务发现的模块；另一个是 Config Service，用来提供配置项管理、动态更新配置和元数据的功能。