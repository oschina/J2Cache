package net.oschina.j2cache.redis;

import net.oschina.j2cache.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Redis 缓存实现
 *
 * @author Winter Lau
 * @author wendal
 */
public class RedisCacheProvider implements CacheProvider {

    private final static Log log = LogFactory.getLog(RedisCacheProvider.class);
    private Properties props;
    private RedisClient redisClient;
    protected ConcurrentHashMap<String, RedisCache> caches = new ConcurrentHashMap<>();
    private String namespace;
    private JedisPoolConfig poolConfig = new JedisPoolConfig();

    public String name() {
        return "redis";
    }

    public RedisClient getClient() {
        return redisClient;
    }

    @Override
    public void start(Properties props) {
        this.props = props;
        //初始化 Redis 连接
        this.namespace = props.getProperty("namespace");
        HashMap<String, String> props2 = new HashMap<>();
        try {
            for (String pn : props.stringPropertyNames())
                props2.put(pn, props.getProperty(pn));
            BeanUtils.populate(poolConfig, props2);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Unable to init redis client.", e);
        }
        String hosts = props.getProperty("hosts");
        String mode = props.getProperty("mode");
        String cluster_name = props.getProperty("cluster_name");
        this.redisClient = new RedisClient(mode, hosts, cluster_name, poolConfig);
    }

    @Override
    public void stop() {
        caches.clear();
        try {
            redisClient.close();
        } catch (IOException e) {
            log.warn("Unable to close redis cluster connection.", e);
        }
    }

    @Override
    public Cache buildCache(String regionName, boolean autoCreate, CacheExpiredListener listener) throws CacheException {
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
