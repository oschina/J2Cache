J2Cache —— 基于 Ehcache 和 Redis 实现的两级 Java 缓存框架
===============

J2Cache 是 OSChina 目前正在使用的两级缓存框架。第一级缓存使用 Ehcache，第二级缓存使用 Redis 。由于大量的缓存读取会导致 L2 的网络成为整个系统的瓶颈，因此 L1 的目标是降低对 L2 的读取次数。该缓存框架主要用于集群环境中。单机也可使用，用于避免应用重启导致的 Ehcache 缓存数据丢失。

视频介绍：http://v.youku.com/v_show/id_XNzAzMTY5MjUy.html  
该项目提供付费咨询服务，详情请看：https://zb.oschina.net/market/opus/12_277

J2Cache 的两级缓存结构

L1： 进程内缓存(ehcache)   
L2： 集中式缓存，支持多种集中式缓存服务器，如 Redis、Memcached 等

由于大量的缓存读取会导致 L2 的网络带宽成为整个系统的瓶颈，因此 L1 的目标是降低对 L2 的读取次数

		 
## 数据读取

1. 读取顺序  -> L1 -> L2 -> DB

2. 数据更新

    1 从数据库中读取最新数据，依次更新 L1_1 -> L2 ，广播清除某个缓存信息  
    2 接收到广播，从 L1\_2 中清除指定的缓存信息


## 构建方法

***使用 Ant 构建***

1. 安装 Redis  
2. 修改 core/resource/j2cache.properties  配置使用已安装的 Redis 服务器
3. 执行 build.sh 进行项目编译  
4. 运行多个 runtest.sh 
5. 直接在 runtest 输入多个命令进行测试

***使用 Maven 构建***

$ mvn install

***项目直接导入 Eclipse 自动编译***

## 运行时所需 jar 包

1. lib/commons-logging-1.1.1.jar  
2. lib/commons-pool2-2.4.2.jar  
3. lib/ehcache-core-2.6.11.jar  
4. lib/fst-1.58.jar
5. lib/jedis-2.7.2.jar  
6. lib/jgroups-3.6.6.Final.jar  
7. lib/slf4j-*.jar

## Maven 支持 

`<dependency>  
  <groupId>net.oschina.j2cache</groupId>  
  <artifactId>j2cache-core</artifactId>  
  <version>1.2.0</version>  
</dependency>
`
## 示例代码

请看 core/Java/net/oschina/j2cache/CacheTester.java