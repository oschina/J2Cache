# J2Cache 版本更新记录

**J2Cache 2.0-beta**

* 要求 Java 8 支持
* 全 Maven 模块化，去掉老版本的 Ant 支持
* 重构内部的各个接口，更加清晰直观，减少依赖关系
* 支持多种 Redis 单机和集群模式，并启用 Redis 连接池
* 支持带密码认证的 Redis 服务
* 支持 Ehcache 3.x (j2cache.L1.provider_class = ehcache3)
* 启用线程方式发送缓存失效的广播通知，避免网络问题导致的堵塞
* [重要] 尽管接口变化不大，但是 J2Cache 2.0 的接口跟 1.x 不兼容
* 对 Hibernate 以及其他框架的支持将在后期通过模块的方式引入项目中

**J2Cache 1.4.0 ()**

**J2Cache 1.3.0 (2015-11-5)**

* 支持使用 Redis 发布订阅机制实现缓存更新通知，用于替换 JGroups 组播方式，两种方式可在 j2cache.properties 中进行配置切换 (感谢 @flyfox 330627517@qq.com)
* 对 J2Cache 的调用进行重构，无法直接从老版本升级，需要更改调用方式为 J2Cache.getChannel()

**J2Cache 1.2.0 (2015-10-27)**

* 升级 jedis 和 jgroups 到最新版本
* 使用 Maven 模块对项目结构进行重新整理