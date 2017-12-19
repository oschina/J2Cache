package net.oschina.j2cache.redis;

import net.oschina.j2cache.Cache;
import net.oschina.j2cache.CacheException;
import net.oschina.j2cache.J2Cache;
import net.oschina.j2cache.util.SerializationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Redis 缓存基于Hashs实现
 *
 * @author wendal
 */
class RedisCache<V> implements Cache<String, V> {

    private final static Log log = LogFactory.getLog(RedisCache.class);

    // 记录region
    protected String namespace;
    protected String region;
    protected byte[] regionBytes;
    protected JedisCluster cluster;

    public RedisCache(String namespace, String region, JedisCluster cluster) {
        if (region == null || region.isEmpty())
            region = "_"; // 缺省region

        this.cluster = cluster;
        this.namespace = namespace;
        this.region = getRegionName(region);
        this.regionBytes = region.getBytes();
    }

    /**
     * 在region里增加一个可选的层级,作为命名空间,使结构更加清晰
     * 同时满足小型应用,多个J2Cache共享一个redis database的场景
     *
     * @param region
     * @return
     */
    private String getRegionName(String region) {
        if (namespace != null && !namespace.isEmpty()) {
            region = namespace + ":" + region;
        }
        return region;
    }

    private byte[] getKeyName(Object key) {
        if (key instanceof Number)
            return ("I:" + key).getBytes();
        else if (key instanceof String || key instanceof StringBuilder || key instanceof StringBuffer)
            return ("S:" + key).getBytes();
        return ("O:" + key).getBytes();
    }

    @Override
    public V get(String key) throws IOException, CacheException {
        if (null == key)
            return null;
        byte[] b = cluster.hget(regionBytes, getKeyName(key));
        return (V)SerializationUtils.deserialize(b);
    }

    @Override
    public void put(String key, V value) throws IOException, CacheException {
        if (key == null)
            return;
        if (value == null)
            evict(key);
        else
            cluster.hset(regionBytes, getKeyName(key), SerializationUtils.serialize(value));
    }

    @Override
    public void update(String key, V value) throws IOException, CacheException {
        this.put(key, value);
    }

    @Override
    public void evict(String key) throws CacheException {
        if (key == null)
            return;
        cluster.hdel(regionBytes, getKeyName(key));
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void evicts(List<String> keys) throws CacheException {
        if (keys == null || keys.size() == 0)
            return;
        int size = keys.size();
        byte[][] okeys = new byte[size][];
        for (int i = 0; i < size; i++) {
            okeys[i] = getKeyName(keys.get(i));
        }
        cluster.hdel(regionBytes, okeys);
    }

    @Override
    public Set<String> keys() throws CacheException {
        Set<String> keys = new HashSet<>();
        keys.addAll(cluster.hkeys(region));
        return keys;
    }

    public void clear() throws CacheException {
        cluster.del(regionBytes);
    }

    public void destroy() throws CacheException {
        this.clear();
    }

}
