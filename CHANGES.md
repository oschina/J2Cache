# J2Cache 版本更新记录


**J2Cache 1.3.0 (2015-11-5)**

* 支持使用 Redis 发布订阅机制实现缓存更新通知，用于替换 JGroups 组播方式，两种方式可在 j2cache.properties 中进行配置切换 (感谢 @flyfox 330627517@qq.com)
* 对 J2Cache 的调用进行重构，无法直接从老版本升级，需要更改调用方式为 J2Cache.getChannel()

**J2Cache 1.2.0 (2015-10-27)**

* 升级 jedis 和 jgroups 到最新版本
* 使用 Maven 模块对项目结构进行重新整理