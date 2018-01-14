package net.oschina.j2cache.redis;

import net.oschina.j2cache.CacheException;
import net.oschina.j2cache.util.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.BinaryJedisCommands;
import redis.clients.jedis.MultiKeyCommands;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Redis 缓存操作封装，基于 region+key 实现多个 Region 的缓存（
 * @author Winter Lau(javayou@gmail.com)
 */
public class RedisGenericCache implements RedisCache {

    private final static Logger log = LoggerFactory.getLogger(RedisGenericCache.class);

    private String namespace;
    private String region;
    private RedisClient client;

    /**
     * 缓存构造
     * @param namespace 命名空间，用于在多个实例中避免 key 的重叠
     * @param region 缓存区域的名称
     * @param client 缓存客户端接口
     */
    public RedisGenericCache(String namespace, String region, RedisClient client) {
        if (region == null || region.isEmpty())
            region = "_"; // 缺省region

        this.client = client;
        this.namespace = namespace;
        this.region = getRegionName(region);
    }

    /**
     * 在region里增加一个可选的层级,作为命名空间,使结构更加清晰
     * 同时满足小型应用,多个J2Cache共享一个redis database的场景
     *
     * @param region
     * @return
     */
    private String getRegionName(String region) {
        if (namespace != null && !namespace.isEmpty())
            region = namespace + ":" + region;
        return region;
    }

    private byte[] key(String key) {
        return (this.region + ":" + key).getBytes();
    }

    @Override
    public byte[] getBytes(String key) {
        try {
            return client.get().get(key(key));
        } finally {
            client.release();
        }
    }

    @Override
    public boolean exists(String key) {
        try {
            return client.get().exists(key(key));
        } finally {
            client.release();
        }
    }

    @Override
    public Long incr(String key, long l) {
        try {
            return client.get().incrBy(key(key), l);
        } finally {
            client.release();
        }
    }

    @Override
    public Long decr(String key, long l) {
        try {
            return client.get().decrBy(key(key), l);
        } finally {
            client.release();
        }
    }

    @Override
    public void put(String key, Serializable value) throws IOException {
        if (value == null)
            evict(key);
        else {
            try {
                client.get().set(key(key), SerializationUtils.serialize(value));
            } finally {
                client.release();
            }
        }
    }

    @Override
    public void putAll(Map<String, Serializable> elements) {
        try {
            BinaryJedisCommands cmd = client.get();
            for(String key : elements.keySet())
                cmd.set(key(key), SerializationUtils.serialize(elements.get(key)));
        } catch (IOException e) {
            log.error("Failed putAll", e);
        } finally {
            client.release();
        }
    }

    /**
     * 性能可能极其低下，谨慎使用
     */
    @Override
    public Collection<String> keys() {
        try {
            BinaryJedisCommands cmd = client.get();
            if (cmd instanceof MultiKeyCommands)
                return ((MultiKeyCommands) cmd).keys(this.region + ":*");
        } finally {
            client.release();
        }
        throw new CacheException("keys() not implemented in Redis Generic Mode");
    }

    @Override
    public void evict(String... keys) {
        if (keys == null || keys.length == 0)
            return;
        try {
            BinaryJedisCommands cmd = client.get();
            for (String key : keys)
                cmd.del(key(key));
        } finally {
            client.release();
        }
    }

    /**
     * 性能可能极其低下，谨慎使用
     */
    @Override
    public void clear() {
        try {
            BinaryJedisCommands cmd = client.get();
            if (cmd instanceof MultiKeyCommands)
                ((MultiKeyCommands)cmd).del(((MultiKeyCommands) cmd).keys(this.region + ":*").stream().toArray(String[]::new));
            else
                throw new CacheException("clear() not implemented in Redis Generic Mode");
        } finally {
            client.release();
        }
    }
}
