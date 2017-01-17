package net.oschina.j2cache.redis;

import net.oschina.j2cache.Cache;
import net.oschina.j2cache.CacheException;
import net.oschina.j2cache.J2Cache;
import net.oschina.j2cache.util.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Redis 缓存基于Hashs实现
 *
 * @author wendal
 */
public class RedisCache implements Cache {

    private final static Logger log = LoggerFactory.getLogger(RedisCache.class);

    // 记录region
    protected byte[] region2;
    protected String region;
    protected RedisCacheProxy redisCacheProxy;

    public RedisCache(String region, RedisCacheProxy redisCacheProxy) {
        if (region == null || region.isEmpty())
            region = "_"; // 缺省region

        region = getRegionName(region);
        this.redisCacheProxy = redisCacheProxy;
        this.region = region;
        this.region2 = region.getBytes();
    }

    /**
     * 在region里增加一个可选的层级,作为命名空间,使结构更加清晰
     * 同时满足小型应用,多个J2Cache共享一个redis database的场景
     *
     * @param region
     * @return
     */
    private String getRegionName(String region) {
        String nameSpace = J2Cache.getConfig().getProperty("redis.namespace", "");
        if (nameSpace != null && !nameSpace.isEmpty()) {
            region = nameSpace + ":" + region;
        }
        return region;
    }

    protected byte[] getKeyName(Object key) {
        if (key instanceof Number)
            return ("I:" + key).getBytes();
        else if (key instanceof String || key instanceof StringBuilder || key instanceof StringBuffer)
            return ("S:" + key).getBytes();
        return ("O:" + key).getBytes();
    }

    public Object get(Object key) throws CacheException {
        if (null == key)
            return null;
        Object obj = null;
        try {
            byte[] b = redisCacheProxy.hget(region2, getKeyName(key));
            if (b != null)
                obj = SerializationUtils.deserialize(b);
        } catch (Exception e) {
            log.error("Error occured when get data from redis2 cache", e);
            if (e instanceof IOException || e instanceof NullPointerException)
                evict(key);
        }
        return obj;
    }

    public void put(Object key, Object value) throws CacheException {
        if (key == null)
            return;
        if (value == null)
            evict(key);
        else {
            try {
                redisCacheProxy.hset(region2, getKeyName(key), SerializationUtils.serialize(value));
            } catch (Exception e) {
                throw new CacheException(e);
            }
        }
    }

    public void update(Object key, Object value) throws CacheException {
        put(key, value);
    }

    public void evict(Object key) throws CacheException {
        if (key == null)
            return;
        try {
            redisCacheProxy.hdel(region2, getKeyName(key));
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public void evict(List keys) throws CacheException {
        if (keys == null || keys.size() == 0)
            return;
        try {
            int size = keys.size();
            byte[][] okeys = new byte[size][];
            for (int i = 0; i < size; i++) {
                okeys[i] = getKeyName(keys.get(i));
            }
            redisCacheProxy.hdel(region2, okeys);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public List<String> keys() throws CacheException {
        try {
            return new ArrayList<String>(redisCacheProxy.hkeys(region));
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public void clear() throws CacheException {
        try {
            redisCacheProxy.del(region2);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public void destroy() throws CacheException {
        this.clear();
    }

	@Override
	public void put(Object key, Object value, Integer expireInSec) throws CacheException {
		if (key == null)
            return;
        if (value == null)
            evict(key);
        else {
            try {
                redisCacheProxy.hset(region2, getKeyName(key), SerializationUtils.serialize(value), expireInSec);
            } catch (Exception e) {
                throw new CacheException(e);
            }
        }
	}

	@Override
	public void update(Object key, Object value, Integer expireInSec) throws CacheException {
		put(key, value, expireInSec);
	}
}
