package net.oschina.j2cache.redis;

import net.oschina.j2cache.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
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
    private JedisCluster cluster;
    protected ConcurrentHashMap<String, RedisCache> caches = new ConcurrentHashMap<>();
    private String namespace;
    private String channel;
    private JedisPoolConfig poolConfig = new JedisPoolConfig();

    public String name() {
        return "redis";
    }

    @Override
    public void start(Properties props) throws CacheException {
        this.props = props;
        //初始化 Redis 连接
        this.namespace = props.getProperty("namespace");
        this.channel = props.getProperty("channel");
        HashMap<String, String> props2 = new HashMap<>();
        try {
            for (String pn : props.stringPropertyNames())
                props2.put(pn, props.getProperty(pn));
            BeanUtils.populate(poolConfig, props2);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Unable to init redis client.", e);
        }
        Set<HostAndPort> hps = new TreeSet<>();
        String hosts = props.getProperty("hosts");
        for(String node : hosts.split(",")){
            String[] infos = node.split(":");
            String host = infos[0];
            int port = (infos.length > 1)?Integer.parseInt(infos[1]):6379;
            hps.add(new HostAndPort(host, port));
        }

        cluster = new JedisCluster(hps, poolConfig);
    }

    @Override
    public void stop() {
        caches.clear();
        try {
            cluster.close();
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
                    cache = new RedisCache(this.namespace, regionName, cluster);
                    caches.put(regionName, cache);
                }
            }
        }
        return cache;
    }

}
