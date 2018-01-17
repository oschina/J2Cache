在j2cache.properties中配置,可以使用springRedis进行广播通知缓失效

```
j2cache.broadcast = net.oschina.j2cache.cache.support.redis.SpringRedisPubSubPolicy
```
在j2cache.properties中配置,使用springRedis替换二级缓存
```
j2cache.L2.provider_class = net.oschina.j2cache.cache.support.redis.SpringRedisProvider
```
如下两项配置在application.properties,可以开启对spring cahce的支持
```
j2cache.open-spring-cache=true  
```
```
spring.cache.type=none
```
