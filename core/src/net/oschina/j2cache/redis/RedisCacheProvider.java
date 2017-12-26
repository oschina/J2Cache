package net.oschina.j2cache.redis;

import net.oschina.j2cache.*;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Redis 缓存管理，实现对多种 Redis 运行模式的支持和自动适配，实现连接池管理等
 *
 * @author Winter Lau (javayou@gmail.com)
 * @author wendal
 */
public class RedisCacheProvider implements CacheProvider {

    private final static Logger log = LoggerFactory.getLogger(RedisCacheProvider.class);

    private RedisClient redisClient;
    private String namespace;
    protected ConcurrentHashMap<String, RedisCache> caches = new ConcurrentHashMap<>();

    public String name() {
        return "redis";
    }

    public RedisClient getClient() {
        return redisClient;
    }

    @Override
    public void start(Properties props) {
        //初始化 Redis 连接
        this.namespace = props.getProperty("namespace");
        try {
            JedisPoolConfig poolConfig = new JedisPoolConfig();//Redis 连接池配置
            HashMap<String, String> props2 = new HashMap<>();
            props.forEach((k, v) -> props2.put((String)k, (String)v));
            BeanUtils.populate(poolConfig, props2);

            String hosts = props.getProperty("hosts");
            String mode = props.getProperty("mode");
            String cluster_name = props.getProperty("cluster_name");
            String password = props.getProperty("password");
            int database = Integer.parseInt(props.getProperty("database"));
            this.redisClient = new RedisClient.Builder()
                    .mode(mode)
                    .hosts(hosts)
                    .password(password)
                    .cluster(cluster_name)
                    .database(database)
                    .poolConfig(poolConfig).newClient();

        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Failed to init redis client.", e);
        }
    }

    @Override
    public void stop() {
        caches.clear();
        try {
            redisClient.close();
        } catch (IOException e) {
            log.warn("Failed to close redis connection.", e);
        }
    }

    @Override
    public Cache buildCache(String regionName, boolean autoCreate, CacheExpiredListener listener) {
        RedisCache cache = caches.get(regionName);
        if (cache == null) {
            synchronized(RedisCacheProvider.class) {
                if(cache == null) {
                    cache = new RedisCache(this.namespace, regionName, redisClient);
                    caches.put(regionName, cache);
                }
            }
        }
        return cache;
    }

}
