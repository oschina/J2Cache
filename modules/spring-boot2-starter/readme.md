此为 spring boot2 版本！！
如下即可使用j2cache缓存方法
```
@Autowired
private CacheChannel cacheChannel;
```
在application.properties中支持指定j2cache配置文件，让你开发环境和生产环境分离
```
j2cache.config-location=/j2cache-${spring.profiles.active}.properties
```
如下两项配置在application.properties,可以开启对spring cahce的支持
```
j2cache.open-spring-cache=true  
```
```
spring.cache.type=none
```
在j2cache.properties中配置,可以使用springRedis进行广播通知缓失效
```
j2cache.broadcast = net.oschina.j2cache.cache.support.redis.SpringRedisPubSubPolicy
```
在j2cache.properties中配置,使用springRedis替换二级缓存
```
j2cache.L2.provider_class = net.oschina.j2cache.cache.support.redis.SpringRedisProvider
```

