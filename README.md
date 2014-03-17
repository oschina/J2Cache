J2Cache Java二级缓存框架
===============

OSChina 缓存框架重构

采用二级缓存结构

L1： 进程内缓存(ehcache) 
L2： 集中式缓存，支持多种集中式缓存服务器，如 Redis、Memcached 等

由于大量的缓存读取会导致 L2 的网络成为整个系统的瓶颈，因此 L1 的目标是降低对 L2 的读取次数

网络拓扑结构

         A1(L1_1)
Web  -				- L2 - DB
		 A2(L1_2)
		 
*** 数据读取 ***

1. 读取顺序  -> L1 -> L2 -> DB

2. 数据更新

A1 从数据库中读取最新数据，依次更新 L1_1 -> L2 ，广播清除某个缓存信息
A2 接收到广播，从 L1_2 中清除指定的缓存信息

[jar包依赖]
slf-xxx.jar 是 ehcache 所需


测试方法：

1. 安装 Redis  
2. 修改 src/redis.properties  配置使用已安装的 Redis 服务器
3. 执行 build.sh 进行项目编译  
4. 运行多个 runtest.sh 
5. 直接在 runtest 输入多个命令进行测试

运行时所需 jar 包：

1. lib/commons-beanutils-1.8.2.jar  
3. lib/commons-logging-1.1.1.jar  
4. lib/commons-pool-1.6.jar  
5. lib/ehcache-2.7.5.jar  
6. lib/jedis-2.2.1.jar  
7. lib/jgroups-3.4.0.Final.jar  
8. lib/slf4j-*.jar
9. lib/fst-1.36.jar

您可以使用ant和maven俩种方式构建项目。