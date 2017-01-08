package net.oschina.j2cache.redis;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.oschina.j2cache.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zhangyw
 * @ClassName JedisTemplate
 * @Description
 * @date 16/11/25 10:41
 */
public class ShardedJedisTemplate extends AbstractJedisTemplate<ShardedJedis, ShardedJedisPoolFactory> {

    public ShardedJedisTemplate() {
    }

    public ShardedJedisTemplate(ShardedJedisPoolFactory jedisPoolFactory) {
        this.setJedisPoolFactory(jedisPoolFactory);
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    @Override
    public Object getObject(String key) {
        Object value = null;
        ShardedJedis jedis = null;

        byte[] keybytes = getBytesKey(key);
        byte[] valbytes = null;
        try {
            jedis = getResource();
            if (jedis.exists(keybytes)) {
                valbytes = jedis.get(keybytes);
            }
        } catch (Exception e) {
            logger.error("getObject {}", key, e);
        } finally {
            returnResource(jedis);
        }

        if (valbytes != null) {
            value = toObject(valbytes);
            logger.debug("getObject {} = {}", key, value);
        }
        return value;
    }

    /**
     * 设置缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时 秒 为单位
     * @return
     */
    @Override
    public String setObject(String key, Object value, int cacheSeconds) {
        String result = null;
        ShardedJedis jedis = null;

        byte[] keybytes = getBytesKey(key);
        byte[] valbytes = toBytes(value);

        try {
            jedis = getResource();
            result = jedis.set(keybytes, valbytes);
            if (cacheSeconds != 0) {
                jedis.expire(keybytes, cacheSeconds);
            }
            logger.debug("setObject {} = {}", key, value);
        } catch (Exception e) {
            logger.error("setObject {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 获取List缓存
     *
     * @param key 键
     * @return 值
     */
    @Override
    public List<Object> getObjectList(String key) {
        List<Object> value = null;
        ShardedJedis jedis = null;

        byte[] keybytes = getBytesKey(key);
        List<byte[]> list = null;
        try {
            jedis = getResource();
            if (jedis.exists(keybytes)) {
                list = jedis.lrange(keybytes, 0, -1);
            }
        } catch (Exception e) {
            logger.warn("getObjectList {}", key, e);
        } finally {
            returnResource(jedis);
        }

        if (list != null) {
            value = Lists.newArrayList();
            for (byte[] bs : list) {
                value.add(toObject(bs));
            }
            logger.debug("getObjectList {} = {}", key, value);
        }

        return value;
    }

    /**
     * 设置List缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    @Override
    public long setObjectList(String key, List<Object> value, int cacheSeconds) {
        long result = 0;
        ShardedJedis jedis = null;

        byte[] keybytes = getBytesKey(key);
        byte[][] bs = new byte[value.size()][];
        for (int i = 0; i < value.size(); i++) {
            bs[i] = getBytesKey(value.get(i));
        }

        try {
            jedis = getResource();
            if (jedis.exists(keybytes)) {
                jedis.del(key);
            }

            result = jedis.rpush(keybytes, bs);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setObjectList {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setObjectList {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向List缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    @Override
    public long listObjectAdd(String key, Object... value) {
        long result = 0;
        ShardedJedis jedis = null;

        byte[] keybytes = getBytesKey(key);
        List<byte[]> list = Lists.newArrayList();
        for (Object o : value) {
            list.add(toBytes(o));
        }
        try {
            jedis = getResource();
            result = jedis.rpush(keybytes, (byte[][]) list.toArray());
            logger.debug("listObjectAdd {} = {}", key, value);
        } catch (Exception e) {
            logger.error("listObjectAdd {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    @Override
    public Set<Object> getObjectSet(String key) {
        Set<Object> value = null;
        ShardedJedis jedis = null;

        byte[] keybytes = getBytesKey(key);
        Set<byte[]> set = null;
        try {
            jedis = getResource();
            if (jedis.exists(keybytes)) {
                set = jedis.smembers(keybytes);
            }
        } catch (Exception e) {
            logger.warn("getObjectSet {}", key, e);
        } finally {
            returnResource(jedis);
        }

        if (set != null) {
            value = Sets.newHashSet();
            for (byte[] bs : set) {
                value.add(toObject(bs));
            }
            logger.debug("getObjectSet {} = {}", key, value);
        }
        return value;
    }

    /**
     * 设置Set缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    @Override
    public long setObjectSet(String key, Set<Object> value, int cacheSeconds) {
        long result = 0;
        ShardedJedis jedis = null;

        byte[] keybytes = getBytesKey(key);
        Set<byte[]> set = Sets.newHashSet();
        for (Object o : value) {
            set.add(toBytes(o));
        }

        try {
            jedis = getResource();
            if (jedis.exists(keybytes)) {
                jedis.del(key);
            }
            result = jedis.sadd(keybytes, (byte[][]) set.toArray());
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setObjectSet {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setObjectSet {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Set缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    @Override
    public long setSetObjectAdd(String key, Object... value) {
        long result = 0;
        ShardedJedis jedis = null;
        byte[] keybytes = getBytesKey(key);
        Set<byte[]> set = Sets.newHashSet();
        for (Object o : value) {
            set.add(toBytes(o));
        }
        try {
            jedis = getResource();
            result = jedis.rpush(keybytes, (byte[][]) set.toArray());
            logger.debug("setSetObjectAdd {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setSetObjectAdd {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 获取Map缓存
     *
     * @param key 键
     * @return 值
     */
    @Override
    public Map<String, Object> getObjectMap(String key) {
        Map<String, Object> value = null;
        ShardedJedis jedis = null;
        byte[] keybytes = getBytesKey(key);
        Map<byte[], byte[]> map = null;
        try {
            jedis = getResource();
            if (jedis.exists(keybytes)) {
                map = jedis.hgetAll(keybytes);
            }
        } catch (Exception e) {
            logger.warn("getObjectMap {}", key, e);
        } finally {
            returnResource(jedis);
        }

        if (map != null) {
            value = Maps.newHashMap();
            for (Map.Entry<byte[], byte[]> e : map.entrySet()) {
                value.put(StringUtils.toString(e.getKey()), toObject(e.getValue()));
            }
            logger.debug("getObjectMap {} = {}", key, value);
        }
        return value;
    }

    /**
     * 设置Map缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    @Override
    public String setObjectMap(String key, Map<String, Object> value, int cacheSeconds) {
        String result = null;
        ShardedJedis jedis = null;
        byte[] keybytes = getBytesKey(key);
        Map<byte[], byte[]> map = Maps.newHashMap();
        for (Map.Entry<String, Object> e : value.entrySet()) {
            map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
        }
        try {
            jedis = getResource();
            if (jedis.exists(keybytes)) {
                jedis.del(key);
            }
            result = jedis.hmset(keybytes, map);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setObjectMap {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setObjectMap {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Map缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    @Override
    public String mapObjectPut(String key, Map<String, Object> value) {
        String result = null;
        ShardedJedis jedis = null;
        byte[] keybytes = getBytesKey(key);
        Map<byte[], byte[]> map = Maps.newHashMap();
        for (Map.Entry<String, Object> e : value.entrySet()) {
            map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
        }
        try {
            jedis = getResource();
            result = jedis.hmset(keybytes, map);
            logger.debug("mapObjectPut {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("mapObjectPut {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 删除缓存
     *
     * 重写父类，删除方法，使其支持按 key正则方式删除缓存
     *
     * @param key 键
     * @return
     */
    @Override
    public long del(String key) {

        if (key.contains("*")) { // 判断key 是否包含模糊匹配
            long result = 0;
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = getResource();
                Collection<Jedis> jedises = shardedJedis.getAllShards();
                //TODO WARN 不建议直接获取 redis中的全部分片然后去删除某个key，待后期优化
                for (Jedis jedis : jedises) {
                    Set<String> keys = jedis.keys(key);
                    if (keys != null && keys.size() > 0) {
                        result = jedis.del(keys.toArray(new String[keys.size()]));
                        logger.debug("del {}", key);
                    } else {
                        logger.debug("del {} not exists", key);
                    }
                }
            } catch (Exception e) {
                logger.error("del {}", key, e);
            } finally {
                returnResource(shardedJedis);
            }
            return result;
        } else {
            return super.del(key);
        }
    }
}
